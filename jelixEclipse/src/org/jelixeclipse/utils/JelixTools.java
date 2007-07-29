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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

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

	public static IProject currentProject(ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection ss = (IStructuredSelection) selection;
		Object element = ss.getFirstElement();
		if (element instanceof IResource)
			return ((IResource) element).getProject();
		if (!(element instanceof IAdaptable))
			return null;
		IAdaptable adaptable = (IAdaptable) element;
		Object adapter = adaptable.getAdapter(IResource.class);
		return ((IResource) adapter).getProject();
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
}