/**
 * @author      Ginesty Thibault, TOULOUSE (31), FRANCE
 * @package     org.jelixeclipse.wizards
 * @version     0.0.3
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

public class WizardNewDaoPage extends WizardPage {

	private IProject currentProject;
	private ISelection selection;
	private Combo jelixComboAppli;
	private Combo jelixComboModule;
	private Button jelixOpenFile;
	private Text jelixTextDao;
	private Text jelixTextTable;
	private String appliJelix;
	private String moduleJelix;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public WizardNewDaoPage(ISelection selection) {
		super("wizardPage");
		setTitle("Nouveau DAO");
		setDescription("Cet assistant va generer un DAO");
		this.selection = selection;
		this.currentProject = JelixTools
				.currentProject((IStructuredSelection) this.selection);
		this.appliJelix = "";
		this.moduleJelix = "";

		/* La selection est un module ? */
		if (JelixToolsSelection
				.isJelixModule((IStructuredSelection) this.selection)) {
			this.moduleJelix = JelixToolsSelection
					.getJelixModuleName((IStructuredSelection) this.selection);
			this.appliJelix = JelixToolsSelection
					.getJelixApplicationByModule((IStructuredSelection) this.selection);
		} else {
			/* La selection est une appli ? */
			if (JelixToolsSelection
					.isJelixApplication((IStructuredSelection) this.selection)) {
				this.appliJelix = JelixToolsSelection
						.getJelixApplicationName((IStructuredSelection) this.selection);
			}
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
		GridData gd;

		/*
		 * listage des applis présente ds le projet si l'appli n'a pas pu être
		 * détectée à partir de la selection
		 */
		label = new Label(jelixAppliGroup, SWT.NULL);
		label.setText("&Nom de l'application :");
		if (this.appliJelix.equals("")) {
			List ll = new List();
			File f = new File(this.currentProject.getLocation().toOSString());
			ll = this.listerRepertoireAppli(f);
			jelixComboAppli = new Combo(jelixAppliGroup, SWT.READ_ONLY);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			jelixComboAppli.setLayoutData(gd);
			for (int k = 0; k < ll.getItemCount(); k++) {
				jelixComboAppli.add(ll.getItem(k).toString());
			}
			jelixComboAppli.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					remplirListeModule(jelixComboAppli.getText());
				}
			});
		} else {
			label = new Label(jelixAppliGroup, SWT.NULL);
			label.setText(this.appliJelix.toString());
		}

		/*
		 * Affichage du combo de module si le module n'a pas pu être détecté à
		 * partir de la selection
		 */
		label = new Label(jelixAppliGroup, SWT.NULL);
		label.setText("&Nom du module :");
		if (this.moduleJelix.equals("")) {
			jelixComboModule = new Combo(jelixAppliGroup, SWT.READ_ONLY);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			jelixComboModule.setLayoutData(gd);

			// si l'appli est déterminé, on rempli les modules dispos
			if (!this.appliJelix.equals("")) {
				remplirListeModule(this.appliJelix);
			}
		} else {
			label = new Label(jelixAppliGroup, SWT.NULL);
			label.setText(this.moduleJelix.toString());
		}

		/* Nom du DAO */
		label = new Label(jelixAppliGroup, SWT.NULL);
		label.setText("Nom du &Dao :");
		jelixTextDao = new Text(jelixAppliGroup, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		jelixTextDao.setLayoutData(gd);

		/* Nom de la table */
		label = new Label(jelixAppliGroup, SWT.NULL);
		label.setText("Nom de la &Table:");
		jelixTextTable = new Text(jelixAppliGroup, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		jelixTextTable.setLayoutData(gd);

		/* Ouverture du DAO ? */
		Group jelixOptionGroup = new Group(container, SWT.NONE);
		jelixOptionGroup.setLayout(new GridLayout());
		jelixOptionGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		jelixOptionGroup.setText("Option : ");
		
		//label = new Label(container, SWT.NULL);
		//label.setText("&Ouvrir le fichier Dao :");
		jelixOpenFile = new Button(jelixOptionGroup, SWT.CHECK);
		jelixOpenFile.setSelection(true);
		jelixOpenFile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		jelixOpenFile.setText("&Ouvrir le fichier Dao");

		setControl(container);
	}

	public String getJelixTextAppli() {
		if (this.appliJelix.equals("")) {
			return jelixComboAppli.getText();
		} else {
			return this.appliJelix.toString();
		}
	}

	public String getJelixTextModule() {
		if (this.moduleJelix.equals("")) {
			return jelixComboModule.getText();
		} else {
			return this.moduleJelix.toString();
		}
	}

	public String getJelixTextDao() {
		return jelixTextDao.getText();
	}

	public String getJelixTextTable() {
		return jelixTextTable.getText();
	}

	public Boolean getJelixOpenFile() {
		return jelixOpenFile.getSelection();
	}

	/*
	 * Liste les modules d'une application
	 */
	public List listerRepertoireModule(File repertoire) {
		File[] listefichiers;
		List listeModule = new List();

		int i;
		listefichiers = repertoire.listFiles();
		for (i = 0; i < listefichiers.length; i++) {
			if (listefichiers[i].isDirectory() == true) {
				listeModule.add(listefichiers[i].getName());
			}
		}

		return listeModule;
	}

	/*
	 * Liste les applications du projet
	 */
	public List listerRepertoireAppli(File repertoire) {
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

	/*
	 * Rempli la combo des modules en fonction de l'appli
	 */
	private void remplirListeModule(String appli) {
		List lll = new List();
		File ff = new File(this.currentProject.getLocation().toOSString()
				+ File.separator + appli + File.separator + "/modules");

		lll = this.listerRepertoireModule(ff);
		jelixComboModule.removeAll();
		for (int k = 0; k < lll.getItemCount(); k++) {
			jelixComboModule.add(lll.getItem(k).toString());
		}
	}
}