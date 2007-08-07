/**
 * @author      Ginesty Thibault, TOULOUSE (31), FRANCE
 * @package     jelixeclipse.perspectives
 * @version     1.0
 * @date        25/06/2007
 * @link        http://www.jelix.org
 * @licence     GNU General Public Licence see LICENCE file or http://www.gnu.org/licenses/gpl.html
 */

package org.jelixeclipse.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.*;

import org.eclipse.ui.console.*;

public class PerspectiveFactory1 implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		IFolderLayout topLeft = layout.createFolder("topleft",
				IPageLayout.LEFT, 0.25f, editorArea);

		IFolderLayout bottom = layout.createFolder("bottom",
				IPageLayout.BOTTOM, 0.66f, editorArea);
		topLeft.addView(IPageLayout.ID_RES_NAV);// L'explorateur de ressources.
		topLeft.addView(IPageLayout.ID_OUTLINE);// La vue "outline".

		bottom.addView(IPageLayout.ID_TASK_LIST);// La vue "Tasks".
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);// La vue "probleme".
		// bottom.addView(net.sourceforge.phpeclipse.webbrowser.views.BrowserView.ID_BROWSER);//
		// La vue "browser".

		bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);

	}

}
