package org.jelixeclipse.utils;

import java.io.BufferedInputStream;
import java.io.InputStream;

import org.eclipse.jface.preference.IPreferenceStore;
import org.jelixeclipse.preferences.PreferenceConstants;

import java.io.File;

public class JelixShell {
	
	private String commande;
	private String erreur;
	private IPreferenceStore store;
	private String separateur = File.separator;
	
	/*
	 * Constructeur
	 */
	public JelixShell(String cmd, IPreferenceStore s){
		this.setCommande(cmd);
		this.setStore(s);
		this.setErreur("");
	}
	
	/*
	 * Getter/Setters
	 */
	public String getCommande(){
		return this.commande;
	}
	
	public String getErreur(){
		return this.erreur;
	}
	
	public IPreferenceStore getStore(){
		return this.store;
	}
	
	public String getSeparateur(){
		return this.separateur;
	}
	
	public void setCommande(String c){
		this.commande = c;
	}
	
	public void setErreur(String e){
		this.erreur = e;
	}
	
	public void setStore(IPreferenceStore s){
		this.store = s;
	}
	
	public void setSeparateur(String s){
		this.separateur = s;
	}
	
	/*
	 * Permet de lancer le shell
	 */
	public boolean play(){
		/* on recupere le chemin de php*/
		String cheminPhp = this.store.getString(PreferenceConstants.P_PATH_JELIX_PHP);
		cheminPhp = this.verifFormatChemin(cheminPhp);
		
		/* on recupere le chemin du fichier jelix.php */
		String cheminScript = this.store.getString(PreferenceConstants.P_PATH_JELIX_SCRIPT);
		cheminScript = this.verifFormatChemin(cheminScript);		
		if (!cheminScript.endsWith("/") && !cheminScript.endsWith("\\")){
			cheminScript += this.getSeparateur();
		}
		cheminScript += "lib" + this.getSeparateur() + "jelix-scripts" + this.getSeparateur() + "jelix.php";
		
		String cmd = this.getCommande();
		cmd = this.verifFormatChemin(cmd);
		
		/* on lance l'execution du script */
		try{	
			String sc = "\"" + cheminPhp + "\" \"" + cheminScript + "\"" + cmd;
			Process p = Runtime.getRuntime().exec(sc);
			p.waitFor();
			
			// on recupere le retour du script
		    InputStream out=new BufferedInputStream(p.getInputStream());
		    byte[] b=new byte[1024];
		    int n=out.read(b);
		    
		    // s'il y a un message, on le stocke
		    if (n > 0){
		    	String err = "";
		    	for (int i=0; i<n; i++){
		    		err += (char)b[i];
		    	}
		    	this.setErreur(err);
		    }
		}catch(Exception e){
			this.setErreur(e.getMessage());
		}
		return this.getErreur().equals("");
	}
	
	/*
	 * Reformate le chemin avec des guillements et les bons slash ou anti-slash
	 */
	public String verifFormatChemin(String c){
		c.replace("/", this.getSeparateur());
		c.replace("\\", this.getSeparateur());
		return c;
	}
		
}
