/**
 * 
 */
package org.jelixeclipse.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.IFolder;
import java.util.*;

/**
 * @author vincent
 * 
 */
public class JelixTools {

	/**
	 * Télécharge un document
	 * 
	 * @param urlDocument
	 *            url du document à télécharger
	 * @param path
	 *            dossier ou enregistrer le document
	 */
	public static void download(String urlDocument, IPath path) {
		java.io.FileOutputStream destinationFile = null;
		try {
			URL url = new URL(urlDocument);
			
			File destination = new File(dirpath(path) + new java.io.File(urlDocument).getName());

			destination.createNewFile();

			URLConnection connection = url.openConnection();
			connection.setDoOutput(false);
			destinationFile = new java.io.FileOutputStream(destination);

			java.io.InputStream is = url.openStream();

			byte[] buffer = new byte[512 * 1024];
			int reads;

			while ((reads = is.read(buffer)) != -1) {
				destinationFile.write(buffer, 0, reads);
			}
		} catch (java.io.IOException ioe) {
			System.out.println("I/O Error on Navigation.download()");
		} catch (Exception e) {
		} finally {
			try {
				destinationFile.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * Retourne le projet en cours d'utilisation
	 * 
	 * @param selection
	 * @return
	 */
	public static IProject currentProject(IStructuredSelection selection) {
		Object element = selection.getFirstElement();
		
		if (element instanceof IResource)
			return ((IResource) element).getProject();
		
		if (!(element instanceof IAdaptable))
			return null;
		IAdaptable adaptable = (IAdaptable) element;
		Object adapter = adaptable.getAdapter(IResource.class);
		return ((IResource) adapter).getProject();
		
/*		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection ss = (IStructuredSelection) selection;
		Object element = ss.getFirstElement();
		if (element instanceof IResource)
			return ((IResource) element).getProject();
		if (!(element instanceof IAdaptable))
			return null;
		IAdaptable adaptable = (IAdaptable) element;
		Object adapter = adaptable.getAdapter(IResource.class);
		return ((IResource) adapter).getProject();*/
	}

	public static boolean unzip(File archive, IPath destination) {
		boolean success = true;

		try {
			ZipFile zf = new ZipFile(archive);
			Enumeration zipEnum = zf.entries();
			String dir = dirpath(destination);
			

			while (zipEnum.hasMoreElements()) {
				ZipEntry item = (ZipEntry) zipEnum.nextElement();

				if (item.isDirectory()) // Directory
				{
					File newdir = new File(dir + item.getName());
					newdir.mkdir();
				} else // File
				{
					String newfile = dir + item.getName();

					InputStream is = zf.getInputStream(item);
					FileOutputStream fos = new FileOutputStream(newfile);

					int ch;
					while ((ch = is.read()) != -1) {
						fos.write(ch);
					}

					is.close();
					fos.close();
				}
			}

			zf.close();
		} catch (Exception e) {
			System.err.println(e);
			success = false;
		}
		return success;
	}
	
	public static String dirpath(IPath dir){
		String path = dir.toOSString();
		if (!path.endsWith(File.separator)) {
			path = path + File.separator;
		}
		return path;
	}
	
	/*
	 * Définit si la selection est une application
	 */
	public static boolean isJelixApplication(IStructuredSelection selection){
		Object element = selection.getFirstElement();
		if (element instanceof IResource){
			IResource r = (IResource)element;
			IContainer container = (IContainer) r;			
			Vector v = new Vector();
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
	 * Retourne le nom des repertoires que doit contenir
	 * une application Jelix
	 */
	public static Vector getContenuJelixApplication(){
		Vector v = new Vector(5);
		v.add("modules");
		v.add("plugins");
		v.add("responses");
		v.add("var");
		v.add("www");
		return v;
	}
}
