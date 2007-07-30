/**
 * @author      Ginesty Thibault, TOULOUSE (31), FRANCE
 * @package     jelixeclipse.wizards
 * @version     1.0
 * @date        25/06/2007
 * @link        http://www.jelix.org
 * @licence     GNU General Public Licence see LICENCE file or http://www.gnu.org/licenses/gpl.html
 */

package org.jelixeclipse.wizards.pages;

import java.awt.List;
import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jelixeclipse.utils.JelixTools;
import org.jelixeclipse.utils.JelixToolsSelection;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (php).
 */

public class WizardNewModulePage extends WizardPage {

	private Text jelixTextModule;
	private ISelection selection;
	private IProject currentProject;
	private Button jelixOpenFile;
	private Combo jelixComboAppli;
	private String appliJelix;

	/**
	 * Constructeur.
	 * 
	 */
	public WizardNewModulePage(ISelection selection) {
		super("wizardPage");
		setTitle("Nouveau Module");
		setDescription("Cet assistant va g�n�rer le squelette d'un module JELIX.");
		this.selection = selection;
		this.currentProject = JelixTools
				.currentProject((IStructuredSelection) this.selection);
		this.appliJelix = "";

		/* La selection est une appli ? */
		if (JelixToolsSelection
				.isJelixApplication((IStructuredSelection) this.selection)) {
			this.appliJelix = JelixToolsSelection
					.getJelixApplicationName((IStructuredSelection) this.selection);
		}
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 9;

		GridLayout layout2 = new GridLayout();
		layout2.numColumns = 2;
		layout2.verticalSpacing = 9;
		
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(layout);

		Composite jelixAppliGroup = new Composite(container, SWT.NULL);
		jelixAppliGroup.setLayout(layout2);
		jelixAppliGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label label;
		
		/*
		 * listage des applis présente ds le projet si l'application n'a pas pu
		 * être déterminée
		 */
		
		label = new Label(jelixAppliGroup, SWT.NULL);
		label.setText("&Nom de l'application :");
		if (this.appliJelix.equals("")) {
			List ll = new List();
			File f = new File(this.currentProject.getLocation().toOSString());
			ll = this.listerRepertoire(f);
			jelixComboAppli = new Combo(jelixAppliGroup, SWT.READ_ONLY);
			jelixComboAppli
					.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			for (int k = 0; k < ll.getItemCount(); k++) {
				jelixComboAppli.add(ll.getItem(k).toString());
			}
		} else {
			label = new Label(jelixAppliGroup, SWT.NULL);
			label.setText(this.appliJelix.toString());
		}

		/* Nom du module */
		label = new Label(jelixAppliGroup, SWT.NULL);
		label.setText("&Nom du module :");
		jelixTextModule = new Text(jelixAppliGroup, SWT.BORDER | SWT.SINGLE);
		jelixTextModule.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		/* Ouverture du fichier après création */
		Group jelixOptionGroup = new Group(container, SWT.NONE);
		jelixOptionGroup.setLayout(new GridLayout());
		jelixOptionGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		jelixOptionGroup.setText("Option : ");

		jelixOpenFile = new Button(jelixOptionGroup, SWT.CHECK);
		jelixOpenFile.setSelection(true);
		jelixOpenFile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		jelixOpenFile.setText("&Ouvrir le fichier Controller");

		setControl(container);
	}

	public String getjelixTextModule() {
		return jelixTextModule.getText();
	}

	public Boolean getJelixOpenFile() {
		return jelixOpenFile.getSelection();
	}

	public String getJelixTextAppli() {
		if (this.appliJelix.equals("")) {
			return jelixComboAppli.getText();
		} else {
			return this.appliJelix;
		}
	}

	/*
	 * Liste les répertoires du chemin passé en paramètre
	 */
	public List listerRepertoire(File repertoire) {
		File[] listefichiers;
		List listeAppli = new List();

		int i;
		listefichiers = repertoire.listFiles();
		for (i = 0; i < listefichiers.length; i++) {
			if (listefichiers[i].isDirectory() == true) {
				if (!listefichiers[i].getName().equals("lib")
						&& !listefichiers[i].getName().equals("temp")) {
					listeAppli.add(listefichiers[i].getName());
				}
			}
		}
		return listeAppli;
	}

}