/**
* @author      Ginesty Thibault, TOULOUSE (31), FRANCE
* @package     jelixeclipse.preferences
* @version     1.0
* @date        25/06/2007
* @link        http://www.jelix.org
* @licence     GNU General Public Licence see LICENCE file or http://www.gnu.org/licenses/gpl.html
*/

package org.jelixeclipse.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

//import org.jelixeclipse.PreferenceConstants;


import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.jelixeclipse.Activator;



/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class JelixPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	protected FileFieldEditor blanc;
	
	public JelixPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
	       
		Composite composite = getFieldEditorParent();
		composite.setBounds(new Rectangle(8, 10, 1000, 42));
		
		GridLayout lg = new GridLayout();
		composite.setLayout(lg);
		lg.numColumns = 1;
		lg.verticalSpacing = 9;

		addField(new FileFieldEditor(PreferenceConstants.P_PATH_JELIX_PHP, "* Ex�cutable &PHP :", composite));
	    addField(new FileFieldEditor(PreferenceConstants.P_PATH_JELIX_MYSQL, "* Ex�cutable &MySQL :", composite)); 
	    addField (new StringFieldEditor(PreferenceConstants.P_NAME_JELIX_ZIP, "&Version JELIX :", composite));


	        
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}