/**
* @author      Ginesty Thibault, TOULOUSE (31), FRANCE
* @package     jelixeclipse.wizards
* @version     1.0
* @date        25/06/2007
* @link        http://www.jelix.org
* @licence     GNU General Public Licence see LICENCE file or http://www.gnu.org/licenses/gpl.html
*/

package org.jelixeclipse.wizards.pages;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (php).
 */

public class newModuleWizardPage extends WizardPage {
	
	private Text jelixTextModule;

	private ISelection selection;
	
	private Button jelixOpenFile;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public newModuleWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("Nouveau Module");
		setDescription("Cet assistant va g�n�rer le squelette d'un module JELIX.");
		this.selection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("&Nom du module :");

		
		jelixTextModule = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		jelixTextModule.setLayoutData(gd);
		
		label = new Label(container, SWT.NULL);
		label.setText("&Ouvrir le fichier Controller :");
		jelixOpenFile = new Button(container, SWT.CHECK);
		jelixOpenFile.setSelection(true);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		jelixOpenFile.setLayoutData(gd);
		
		initialize();
		//dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) {
				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer) obj;
				else
					container = ((IResource) obj).getParent();
			}
		}
	}

	
	public String getjelixTextModule() {
		return jelixTextModule.getText();
	}
	
	public Boolean getJelixOpenFile() {
		return jelixOpenFile.getSelection();
	}
}