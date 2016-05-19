package de.fzi.power.sertresultimport.wizard.pages;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;

public class CSVDataSinkPage extends WizardPage {

	protected NewFileFieldEditor editor;
	private IPath path;
	private int fileDialogStyle;
	private Composite dirSelectionArea;

	public CSVDataSinkPage(String pageName, String description) {
		super(pageName);
		setTitle(pageName);
		setDescription(description);
		fileDialogStyle = SWT.OPEN | SWT.SAVE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.dialogs.WizardNewFileCreationPage#createAdvancedControls(
	 * org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		dirSelectionArea = new Composite(parent, SWT.NONE);
		GridData fileSelectionData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		dirSelectionArea.setLayoutData(fileSelectionData);
		setControl(dirSelectionArea);

		GridLayout dirSelectionLayout = new GridLayout();
		dirSelectionLayout.numColumns = 3;
		dirSelectionLayout.makeColumnsEqualWidth = false;
		dirSelectionLayout.marginWidth = 0;
		dirSelectionLayout.marginHeight = 0;
		dirSelectionArea.setLayout(dirSelectionLayout);

		editor = new NewFileFieldEditor("Select directory", "Select directory: ", dirSelectionArea);
		editor.getTextControl(dirSelectionArea).addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				IPath path = new Path(editor.getStringValue());
				setPath(path);
				setPageComplete(true);
			}

		});
		dirSelectionArea.moveAbove(null);
	}

	/**
	 * Returns the selected path where the CSV files should be saved.
	 * 
	 * @return the path of the selected directory.
	 */
	public IPath getPath() {
		return path;
	}

	/**
	 * Sets the path where the CSV files should be saved.
	 * 
	 * @param path
	 *            the path the the desired directory.
	 */
	public void setPath(IPath path) {
		this.path = path;
	}

	private class NewFileFieldEditor extends StringButtonFieldEditor {
		public NewFileFieldEditor(String name, String labelText, Composite parent) {
			super(name, labelText, parent);
		}

		@Override
		protected String changePressed() {
			DirectoryDialog dialog = new DirectoryDialog(getShell(), fileDialogStyle);
			return dialog.open();
		}
	}
}
