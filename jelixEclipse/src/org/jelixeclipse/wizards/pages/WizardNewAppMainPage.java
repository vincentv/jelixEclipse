/**
 * 
 */
package org.jelixeclipse.wizards.pages;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFolderMainPage;

/**
 * @author vincent
 *
 */
public class WizardNewAppMainPage extends WizardNewFolderMainPage {

	public WizardNewAppMainPage(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);

		Composite composite = (Composite) super.getControl();
		
		//createOptions(composite);

		setControl(composite);
	}
}
