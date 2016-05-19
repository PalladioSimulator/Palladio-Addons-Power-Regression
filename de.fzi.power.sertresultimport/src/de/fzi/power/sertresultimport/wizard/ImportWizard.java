/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.fzi.power.sertresultimport.wizard;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.signedcontent.InvalidContentException;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.palladiosimulator.edp2.EDP2Plugin;
import org.palladiosimulator.edp2.impl.RepositoryManager;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.models.Repository.RepositoryFactory;

import de.fzi.power.sertresultimport.data.SuiteResultData;
import de.fzi.power.sertresultimport.exporter.CSVExport;
import de.fzi.power.sertresultimport.exporter.Edp2Export;
import de.fzi.power.sertresultimport.importer.xml.XmlPowerParser;
import de.fzi.power.sertresultimport.importer.xml.XmlResultParser;
import de.fzi.power.sertresultimport.importer.xml.XmlUtilizationParser;
import de.fzi.power.sertresultimport.ui.wizards.datasource.DiscoverLocalDirectoryPage;
import de.fzi.power.sertresultimport.wizard.pages.CSVDataSinkPage;
import de.fzi.power.sertresultimport.wizard.pages.SelectDataSinkPage;
import de.fzi.power.sertresultimport.wizard.pages.XmlSelectorPage;

/**
 * This is a eclipse import wizard that can be used to import the SERT XML
 * results into and EDP2-Repository or a CSV file.
 * 
 * @author Daniel Hassler
 *
 */
public class ImportWizard extends Wizard implements IImportWizard {

	private final XmlSelectorPage xmlImportPage;
	private final SelectDataSinkPage selectDataSinkPage;
	private final CSVDataSinkPage csvDataSinkPage;
	private final DiscoverLocalDirectoryPage discoverLocalFilePage;
	private boolean discardData;
	private boolean filterBadSamples;

	/**
	 * Creates a new {@link ImportWizard} object.
	 */
	public ImportWizard() {
		super();
		xmlImportPage = new XmlSelectorPage("Import XML file", "Import XML results file from the local file system.");
		csvDataSinkPage = new CSVDataSinkPage("Select CSV direcotry",
				"Select or create CSV target direcotry for data export.");
		selectDataSinkPage = new SelectDataSinkPage("Select data sink");
		discoverLocalFilePage = new DiscoverLocalDirectoryPage();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					String filename = xmlImportPage.getFilename();
					if (filename == null) {
						return;
					}
					if (selectDataSinkPage.getSelection().equals(SelectDataSinkPage.FILE_DATA_SINK)) {
						final Repository repo = discoverLocalFilePage.getRepositoryOnFinish();
						exportToRepo(repo, filename, monitor);
					} else if (selectDataSinkPage.getSelection().equals(SelectDataSinkPage.IN_MEMORY_DATA_SINK)) {
						final Repository repo = RepositoryFactory.eINSTANCE.createLocalMemoryRepository();
						exportToRepo(repo, filename, monitor);
					} else if (selectDataSinkPage.getSelection().equals(SelectDataSinkPage.CSV_DATA_SINK)) {
						SuiteResultData data = importXml(filename, monitor);
						CSVExport export = new CSVExport();
						export.setPath(Paths.get(csvDataSinkPage.getPath().toString()));
						try {
							export.export(data);
						} catch (IOException e) {
							throw new RuntimeException("Error while exporting data to CSV file: " + e.getMessage());
						}
					} else {
						// This line should never be reached. Otherwise there
						// likely are
						// unaccounted data source
						// types.
						assert(false);
						return;
					}
				}
			});
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return true;
	}

	@Override
	public boolean canFinish() {
		boolean canFinish = false;
		canFinish = xmlImportPage.getFilename() != null;
		if (canFinish) {
			if (selectDataSinkPage.getSelection().equals(SelectDataSinkPage.CSV_DATA_SINK)) {
				canFinish = canFinish && (csvDataSinkPage.getPath() != null);
			} else if (selectDataSinkPage.getSelection().equals(SelectDataSinkPage.FILE_DATA_SINK)) {
				canFinish = canFinish && (discoverLocalFilePage.getRepositoryOnFinish() != null);
			} else if (!selectDataSinkPage.getSelection().equals(SelectDataSinkPage.IN_MEMORY_DATA_SINK)) {
				canFinish = false;
			}
		}
		return canFinish;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("File Import Wizard");
		setNeedsProgressMonitor(true);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage nextPage = null;
		if (page == xmlImportPage && xmlImportPage.isPageComplete()) {
			nextPage = selectDataSinkPage;
			discardData = xmlImportPage.isDiscardData();
			filterBadSamples = xmlImportPage.isFilterBadSamples();
		} else if (page == selectDataSinkPage && selectDataSinkPage.isPageComplete()) {
			String selection = selectDataSinkPage.getSelection();
			switch (selection) {
			case SelectDataSinkPage.CSV_DATA_SINK:
				nextPage = csvDataSinkPage;
				break;
			case SelectDataSinkPage.FILE_DATA_SINK:
				nextPage = discoverLocalFilePage;
				break;
			}
		}
		return nextPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		super.addPages();
		addPage(xmlImportPage);
		addPage(selectDataSinkPage);
		addPage(csvDataSinkPage);
		addPage(discoverLocalFilePage);
	}

	private void exportToRepo(Repository repo, String filename, IProgressMonitor monitor) {
		SuiteResultData data = importXml(filename, monitor);
		RepositoryManager.addRepository(EDP2Plugin.INSTANCE.getRepositories(), repo);
		Edp2Export export = new Edp2Export();
		export.setRepository(repo);
		try {
			export.export(data);
		} catch (IOException e) {
			throw new RuntimeException("Error while exporting data to EDP2 repository: " + e.getMessage());
		}
	}

	private SuiteResultData importXml(String filename, IProgressMonitor monitor) {
		SuiteResultData data = null;
		final XmlResultParser parser = new XmlResultParser();
		parser.getProviderResultParsers().add(new XmlUtilizationParser());
		parser.getProviderResultParsers().add(new XmlPowerParser());
		parser.setDiscardBadSamples(discardData);
		parser.setFilterBadSamples(filterBadSamples);
		try {
			data = parser.parseResults(Paths.get(filename), monitor);
		} catch (InvalidContentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

}
