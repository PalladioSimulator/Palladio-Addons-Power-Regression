package de.fzi.power.sertresultimport;

import javax.measure.unit.Unit;

/**
 * 
 * This class contains constants used in the project.
 * 
 * @author Daniel Hassler
 *
 */
public final class SertResultConstants {

	/**
	 * This holds some measures used in the project and offers a mapping to the
	 * corresponding model specific id of the EDP2 framework.
	 * 
	 * @author Daniel Hassler
	 *
	 */
	public static enum Type {
		VOLTS("volts", javax.measure.unit.SI.VOLT, null), 
		AMPS("amps", javax.measure.unit.SI.AMPERE, null),
		WATTS("watts", javax.measure.unit.SI.WATT, "_EZBPQV91EeSUTcC2MkYv_Q"),
		TEMPERATURE("temperature", javax.measure.unit.SI.CELSIUS, null),
		CPULOAD("cpu_load", javax.measure.unit.NonSI.PERCENT, "_mhws4SkUEeSuf8LV7cHLgA");

		private String name;
		private Unit<?> unit;
		private String modelSpecId;

		private Type(String name, Unit<?> unit, String modelSpecId) {
			this.name = name;
			this.unit = unit;
			this.modelSpecId = modelSpecId;
		}

		public String getName() {
			return name;
		}

		public Unit<?> getUnit() {
			return unit;
		}

		public String getModelSpecId() {
			return modelSpecId;
		}
	}
}
