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
	public static boolean isJelixApplication(IStructuredSelection selection){
		Object element = selection.getFirstElement();
		if (element instanceof IResource){
			IResource r = (IResource)element;
			IContainer container = (IContainer) r;			
			Vector<String> v = new Vector<String>();
			v = getContenuJelixApplication();
			for (int i=0; i<v.size(); i++){
				if (!container.getFolder(new Path(v.elementAt(i).toString())).exists()){
					return false;
				}
			}
		}else{
			return false;
		}
		return true;
	}
	
	/*
	 * Définit si la selection est un module
	 */
	public static boolean isJelixModule(IStructuredSelection selection){
		Object element = selection.getFirstElement();
		if (element instanceof IResource){
			IResource r = (IResource)element;
			IContainer container = (IContainer) r;			
			Vector<String> v = new Vector<String>();
			v = getContenuJelixModule();
			for (int i=0; i<v.size(); i++){
				if (!container.getFolder(new Path(v.elementAt(i).toString())).exists()){
					return false;
				}
			}
		}else{
			return false;
		}
		return true;
	}
	
	/*
	 * Renvoi le nom de l'application relatif à la sélection
	 */
	public static String getJelixApplicationName(IStructuredSelection selection){
		Object element = selection.getFirstElement();
		if (element instanceof IResource){
			IResource r = (IResource)element;
			return r.getName();
		}else{
			return "";
		}
	}
	
	/*
	 * Renvoi le nom du module relatif à la sélection
	 */
	public static String getJelixModuleName(IStructuredSelection selection){
		Object element = selection.getFirstElement();
		if (element instanceof IResource){
			IResource r = (IResource)element;
			return r.getName();
		}else{
			return "";
		}
	}
	
	/*
	 * Renvoi l'application du module
	 */
	public static String getJelixApplicationByModule(IStructuredSelection selection){
		Object element = selection.getFirstElement();
		if (element instanceof IResource){
			IResource r = (IResource)element;
			return r.getParent().getParent().getName();
		}else{
			return "";
		}
	}
	
	/*
	 * Retourne le nom des repertoires que doit contenir
	 * une application Jelix
	 */
	public static Vector<String> getContenuJelixApplication(){
		Vector<String> v = new Vector<String>(5);
		v.add("modules");
		v.add("plugins");
		v.add("responses");
		v.add("var");
		v.add("www");
		return v;
	}
	
	/*
	 * Retourne le nom des repertoires que doit contenir
	 * un module Jelix
	 */
	public static Vector<String> getContenuJelixModule(){
		Vector<String> v = new Vector<String>(5);
		v.add("classes");
		v.add("controllers");
		v.add("daos");
		v.add("locales");
		v.add("templates");
		v.add("zones");
		return v;
	}
}
