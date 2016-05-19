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
import org.eclipse.swt.widgets.FileDialog;

public class DataSelectorPage extends WizardPage {

	protected NewFileFieldEditor editor;
	private String filename;
	private final int fileDialogStyle;
	private final String[] extensionFilter;
	private final boolean setOverwrite;

	public DataSelectorPage(String pageName, String description) {
		super(pageName);
		setTitle(pageName);
		setDescription(description);
		extensionFilter = new String[] { "*.*" };
		setOverwrite = true;
		fileDialogStyle = SWT.OPEN | SWT.SAVE;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.dialogs.WizardNewFileCreationPage#createAdvancedControls(
	 * org.eclipse.swt.widgets.Composite)
	 */
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
		fileSelectionArea.moveAbove(null);
	}

	/**
	 * Returns the filename of the selected file.
	 * 
	 * @return the filename of the selected file as String.
	 */
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}
