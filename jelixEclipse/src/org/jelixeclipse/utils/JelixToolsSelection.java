/**
 * @author      Ginesty Thibault, TOULOUSE (31), FRANCE
 * @package     org.jelixeclipse.utils
 * @version     1.0
 * @date        30/07/2007
 * @link        http://www.jelix.org
 * @licence     GNU General Public Licence see LICENCE file or http://www.gnu.org/licenses/gpl.html
 */

package org.jelixeclipse.utils;

import java.util.Vector;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;

public class JelixToolsSelection {
	/*
	 * Définit si la selection est une application
	 */
	public static boolean isJelixApplication(IStructuredSelection selection) {
		Object element = selection.getFirstElement();
		if (element instanceof IResource) {
			IResource r = (IResource) element;

			/* si declenché sur le repertoire "modules" */
			if (r.getName().equals("modules")) { //$NON-NLS-1$
				r = r.getParent();
			}
			IContainer container = (IContainer) r;
			Vector<String> v = new Vector<String>();
			v = getContenuJelixApplication();
			for (int i = 0; i < v.size(); i++) {
				if (!container.getFolder(new Path(v.elementAt(i).toString()))
						.exists()) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

	/*
	 * Définit si la selection est un module
	 */
	public static boolean isJelixModule(IStructuredSelection selection) {
		Object element = selection.getFirstElement();
		if (element instanceof IResource) {
			IResource r = (IResource) element;

			/* si declenché sur le repertoire "daos" */
			if (r.getName().equals("daos")) { //$NON-NLS-1$
				r = r.getParent();
			}
			IContainer container = (IContainer) r;
			Vector<String> v = new Vector<String>();
			v = getContenuJelixModule();
			for (int i = 0; i < v.size(); i++) {
				if (!container.getFolder(new Path(v.elementAt(i).toString()))
						.exists()) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

	/*
	 * Renvoi le nom de l'application relatif à la sélection
	 */
	public static String getJelixApplicationName(IStructuredSelection selection) {
		Object element = selection.getFirstElement();
		if (element instanceof IResource) {
			IResource r = (IResource) element;

			/* si declenché sur le repertoire "modules" */
			if (r.getName().equals("modules")) { //$NON-NLS-1$
				r = r.getParent();
			}
			return r.getName();
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	/*
	 * Renvoi le nom du module relatif à la sélection
	 */
	public static String getJelixModuleName(IStructuredSelection selection) {
		Object element = selection.getFirstElement();
		if (element instanceof IResource) {
			IResource r = (IResource) element;

			/* si declenché sur le repertoire "daos" */
			if (r.getName().equals("daos")) { //$NON-NLS-1$
				r = r.getParent();
			}
			return r.getName();
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	/*
	 * Renvoi l'application du module
	 */
	public static String getJelixApplicationByModule(
			IStructuredSelection selection) {
		Object element = selection.getFirstElement();
		if (element instanceof IResource) {
			IResource r = (IResource) element;

			/* si declenché sur le repertoire "daos" */
			if (r.getName().equals("daos")) { //$NON-NLS-1$
				r = r.getParent();
			}
			return r.getParent().getParent().getName();
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	/*
	 * Retourne le nom des repertoires que doit contenir une application Jelix
	 */
	public static Vector<String> getContenuJelixApplication() {
		Vector<String> v = new Vector<String>(5);
		v.add("modules"); //$NON-NLS-1$
		v.add("plugins"); //$NON-NLS-1$
		v.add("responses"); //$NON-NLS-1$
		v.add("var"); //$NON-NLS-1$
		v.add("www"); //$NON-NLS-1$
		return v;
	}

	/*
	 * Retourne le nom des repertoires que doit contenir un module Jelix
	 */
	public static Vector<String> getContenuJelixModule() {
		Vector<String> v = new Vector<String>(5);
		v.add("classes"); //$NON-NLS-1$
		v.add("controllers"); //$NON-NLS-1$
		v.add("daos"); //$NON-NLS-1$
		v.add("locales"); //$NON-NLS-1$
		v.add("templates"); //$NON-NLS-1$
		v.add("zones"); //$NON-NLS-1$
		return v;
	}
}
