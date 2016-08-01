package de.fzi.power.sertresultimport.wizard.pages;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * This pages lets the user select one of three data sinks to which the SERT
 * results can be exported. This class is heavily based on
 * {@link org.palladiosimulator.edp2.ui.wizards.datasource.SelectDataSourceTypePage}
 * .
 * 
 * @author Daniel Hassler
 *
 */
public class SelectDataSinkPage extends WizardPage {

	public static final String IN_MEMORY_DATA_SINK = "In-Memory data sink";
	public static final String FILE_DATA_SINK = "File data sink";
	public static final String CSV_DATA_SINK = "CSV data sink";

	private String selection;

	public SelectDataSinkPage(String pageName) {
		super(pageName);
		setTitle("Select Type of Data Sink");
		setDescription("Please select the desired data sink type.");

	}

	@Override
	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);
		setControl(container);

		final Label label = new Label(container, SWT.NONE);
		label.setText("Type of data sink: ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		final Combo selectedDataTypeCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		selectedDataTypeCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent evt) {
				widgetSelected(evt);
			}

			@Override
			public void widgetSelected(final SelectionEvent evt) {
				selection = selectedDataTypeCombo.getText();
				setPageComplete(true);
			}
		});
		populateComboBox(selectedDataTypeCombo);
		// The additional spacing (default is 5,5) is for the decorations
		GridLayoutFactory.swtDefaults().numColumns(2).spacing(10, 5).generateLayout(container);
	}

	/**
	 * Populates the combo box with all selectable data source types.
	 *
	 * @param selectedDataType
	 *            Combo box to be populated.
	 */
	private void populateComboBox(final Combo selectedDataType) {
		selectedDataType.add(IN_MEMORY_DATA_SINK);
		selectedDataType.select(0);
		selectedDataType.add(FILE_DATA_SINK);
		selectedDataType.add(CSV_DATA_SINK);
		this.selection = IN_MEMORY_DATA_SINK;
	}

	/**
	 * Get the current selected data type. Returns an empty string if no data
	 * source type is selected.
	 *
	 * @return The string representing the selection.
	 */
	public String getSelection() {
		return this.selection;
	}

}
