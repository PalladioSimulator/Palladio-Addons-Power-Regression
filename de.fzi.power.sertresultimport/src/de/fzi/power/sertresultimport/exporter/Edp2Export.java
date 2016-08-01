package de.fzi.power.sertresultimport.exporter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.measure.Measure;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentDataFactory;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentGroup;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentSetting;
import org.palladiosimulator.edp2.models.ExperimentData.Measurement;
import org.palladiosimulator.edp2.models.ExperimentData.MeasurementRange;
import org.palladiosimulator.edp2.models.ExperimentData.MeasuringType;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPointRepository;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringpointFactory;
import org.palladiosimulator.edp2.models.measuringpoint.StringMeasuringPoint;
import org.palladiosimulator.edp2.util.MeasurementsUtility;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.measurementframework.TupleMeasurement;
import org.palladiosimulator.metricspec.MetricSetDescription;

import de.fzi.power.sertresultimport.SertResultConstants;
import de.fzi.power.sertresultimport.data.IntervalResultData;
import de.fzi.power.sertresultimport.data.NumericResultData;
import de.fzi.power.sertresultimport.data.SuiteResultData;
import de.fzi.power.sertresultimport.data.WorkletResultData;
import de.fzi.power.sertresultimport.data.WorkloadResultData;

/***
 * Objects of this class export the results of a benchmark suite run into an
 * local EDP2 repository.
 *
 * @author Daniel Hassler
 *
 */
public class Edp2Export implements IExport {

	private static final String METRIC_SPEC_MODELS = "pathmap://METRIC_SPEC_MODELS/models/commonMetrics.metricspec";
	private static final MeasuringpointFactory MEASURING_POINT_FACTORY = MeasuringpointFactory.eINSTANCE;
	private static final ExperimentDataFactory EXPERIMENT_DATA_FACTORY = ExperimentDataFactory.eINSTANCE;

	private Resource resource;
	private ExperimentGroup group;
	private final Map<Unit<?>, MeasuringType> typeMap = new HashMap<Unit<?>, MeasuringType>();
	private Repository repo;

	/**
	 * Sets the repository into which the data will be exported. The repository
	 * must be set before the export method is called.
	 *
	 * @param repo
	 *            the EDP2 repository
	 */
	public void setRepository(Repository repo) {
		this.repo = repo;
	}

	/**
	 * Returns the target repository.
	 * 
	 * @return the Repository object of the target repository.
	 */
	public Repository getRepository() {
		return repo;
	}

	/**
	 * 
	 * 
	 * This method exports the results from the parameter suiteResult into the
	 * repository.
	 *
	 * @param suiteResult
	 *            the suite result data
	 * @throws IOException
	 *             if the edp2 models cannot be loaded
	 */
	@Override
	public void export(SuiteResultData suiteResult) throws IOException {
		initPathmaps();
		group = EXPERIMENT_DATA_FACTORY.createExperimentGroup();
		group.setPurpose("Experiment run on SuT");
		group.setRepository(repo);
		for (SertResultConstants.Type type : SertResultConstants.Type.values()) {
			create_measurement_type(type);
		}
		for (final WorkloadResultData workloadResult : suiteResult.getSubResults()) {
			for (final WorkletResultData workletResult : workloadResult.getSubResults()) {
				final ExperimentSetting settings = EXPERIMENT_DATA_FACTORY.createExperimentSetting(group,
						"Worklet (" + workletResult.getName() + ")");
				settings.getMeasuringTypes().addAll(typeMap.values());
				for (final IntervalResultData intervalResult : workletResult.getSubResults()) {
					// create a single run for each interval
					final ExperimentRun run = EXPERIMENT_DATA_FACTORY.createExperimentRun(settings);
					settings.getExperimentRuns().add(run);
					run.setStartTime(intervalResult.getTimings().getStart());
					final long duration = intervalResult.getTimings().getEnd().getTime()
							- intervalResult.getTimings().getStart().getTime();
					run.setDuration(Measure.valueOf(duration, SI.MILLI(SI.SECOND)));
					for (final NumericResultData providerResult : intervalResult.getSubResults()) {
						store_measurements_in_repo(run, duration, providerResult);
					}
				}
			}
		}
	}

	/**
	 * This method extracts the measurement data of the given provider and
	 * stores it into the EDP2 repository.
	 * 
	 * @param run
	 *            the experiment run
	 * @param duration
	 *            the duration of the run
	 * @param providerResult
	 *            the numeric measurement result
	 */
	private void store_measurements_in_repo(final ExperimentRun run, final long duration,
			final NumericResultData providerResult) {
		final MeasuringType mType = typeMap.get(providerResult.getUnit());
		if (mType != null) {
			final Measurement measurement = create_measurement(mType);
			run.getMeasurement().add(measurement);
			for (MeasurementRange range : measurement.getMeasurementRanges()) {
				MeasurementsUtility.createDAOsForRawMeasurements(EXPERIMENT_DATA_FACTORY.createRawMeasurements(range));
			}
			double second = 0.0;
			double increment = (duration / 1000.0) / providerResult.getRawValues().size();
			for (double rawValue : providerResult.getRawValues()) {
				if (providerResult.getUnit() == NonSI.PERCENT) {
					// for EDP2 percentages are expected to be between 0 and 100
					rawValue *= 100;
				}
				Measure<?,?> rawMeasure = javax.measure.Measure.valueOf(rawValue, providerResult.getUnit());
				final MeasuringValue m1 = new TupleMeasurement((MetricSetDescription) mType.getMetric(),
						javax.measure.Measure.valueOf(second, SI.SECOND), rawMeasure);
				second += increment;
				MeasurementsUtility.storeMeasurement(measurement, m1);
			}
		}
	}

	/**
	 * Creates a Measurement from the MeasuringType.
	 * 
	 * @param mType
	 *            the measuring type
	 * @return the measurement.
	 */
	private Measurement create_measurement(MeasuringType mType) {
		final Measurement measurement = EXPERIMENT_DATA_FACTORY.createMeasurement(mType);
		final MeasurementRange typeMeasurementRange = EXPERIMENT_DATA_FACTORY.createMeasurementRange(measurement);
		measurement.getMeasurementRanges().add(typeMeasurementRange);
		return measurement;
	}

	/**
	 * Creates the measurement type in the EDP2 Repository.
	 * 
	 * @param type
	 *            the measurement type to be created.
	 */
	private void create_measurement_type(SertResultConstants.Type type) {
		if (typeMap.get(type.getUnit()) != null || type.getModelSpecId() == null) {
			// type already in map
			return;
		}
		final MetricSetDescription descr = (MetricSetDescription) resource.getEObject(type.getModelSpecId());
		if (descr != null) {
			final StringMeasuringPoint mPoint = MEASURING_POINT_FACTORY.createStringMeasuringPoint();
			final MeasuringPointRepository measuringPointRepo = MEASURING_POINT_FACTORY
					.createMeasuringPointRepository();
			mPoint.setMeasuringPoint(type.getName());
			mPoint.setMeasuringPointRepository(measuringPointRepo);
			group.getMeasuringPointRepositories().add(measuringPointRepo);
			final MeasuringType mType = EXPERIMENT_DATA_FACTORY.createMeasuringType(mPoint, descr);
			mType.setExperimentGroup(group);
			typeMap.put(type.getUnit(), mType);
		}
	}

	/**
	 * This prepares the metric spec models.
	 * 
	 * @throws IOException
	 *             if the model cannot be loaded.
	 */
	private void initPathmaps() throws IOException {
		final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		final Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("metricspec", new XMIResourceFactoryImpl());
		final ResourceSet resourceSet = new ResourceSetImpl();
		resource = resourceSet.createResource(URI.createURI(METRIC_SPEC_MODELS, true));
		resource.load(Collections.EMPTY_MAP);

	}
}
