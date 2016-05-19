package de.fzi.power.sertresultimport.wizard.pages;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

/**
 * This class implements a WizardPage that lets the user load a XML file.
 * 
 * @author Daniel Hassler
 *
 */
public class XmlSelectorPage extends WizardPage {

	private Button discardDataButton;
	private Button filterBadSamplesButton;
	private String filename;
	private final String[] extensionFilter;
	private final int fileDialogStyle;
	private final boolean setOverwrite;
	protected NewFileFieldEditor editor;

	public XmlSelectorPage(String pageName, String description) {
		super(pageName);
		setDescription(description);
		setOverwrite = false;
		fileDialogStyle = SWT.OPEN;
		extensionFilter = new String[] { "*.xml" };
	}

	private class NewFileFieldEditor extends StringButtonFieldEditor {
		public NewFileFieldEditor(String name, String labelText, Composite parent) {
			super(name, labelText, parent);
		}

		@Override
		protected String changePressed() {
			FileDialog dialog = new FileDialog(getShell(), fileDialogStyle);
			dialog.setFilterExtensions(extensionFilter);
			dialog.setOverwrite(setOverwrite);
			return dialog.open();
		}
	}

	@Override
	public void createControl(Composite parent) {
		final Composite fileSelectionArea = new Composite(parent, SWT.NONE);
		GridData fileSelectionData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		fileSelectionArea.setLayoutData(fileSelectionData);
		setControl(fileSelectionArea);

		GridLayout fileSelectionLayout = new GridLayout();
		fileSelectionLayout.numColumns = 3;
		fileSelectionLayout.makeColumnsEqualWidth = false;
		fileSelectionLayout.marginWidth = 0;
		fileSelectionLayout.marginHeight = 0;
		fileSelectionArea.setLayout(fileSelectionLayout);

		editor = new NewFileFieldEditor("Select File", "Select File: ", fileSelectionArea);
		editor.getTextControl(fileSelectionArea).addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				IPath path = new Path(editor.getStringValue());
				setFilename(path.makeAbsolute().toString());
				setPageComplete(true);
			}

		});
		final GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gridData.horizontalSpan = 3;
		discardDataButton = new Button(fileSelectionArea, SWT.CHECK);
		filterBadSamplesButton = new Button(fileSelectionArea, SWT.CHECK);
		discardDataButton.setText("Discard data containing bad samples");
		discardDataButton.setLayoutData(gridData);
		discardDataButton.addSelectionListener(new SelectionListener() {
			private void disable_filter_button() {
				boolean selected = discardDataButton.getSelection();
				filterBadSamplesButton.setEnabled(!selected);
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				disable_filter_button();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				disable_filter_button();
			}
		});
		filterBadSamplesButton.setLayoutData(gridData);
		filterBadSamplesButton.setText("Filter bad samples");
		filterBadSamplesButton.setSelection(true);
		fileSelectionArea.pack();
	}

	public boolean isDiscardData() {
		return discardDataButton.getSelection();
	}

	public boolean isFilterBadSamples() {
		return filterBadSamplesButton.getSelection();
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}
