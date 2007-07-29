package jelixeclipse.wizards;

public class JelixIni {
	private String entete = "";
	private String definition = "";
	private String commentaire = "";
	private String pdo = "";
	private String cr = "\r\n";
	
	/* constructeur */
	public JelixIni(){
		// entete
		this.entete = ";<?php die(''); ?>" + this.cr;
		this.entete += ";for security reasons, don't remove or modify the first line" + this.cr;
		this.entete += this.cr;
		
		// definition
		this.definition = "; nom de la connexion utilisée par défaut" + this.cr;
		this.definition += "default = ";
		
		// commentaire
		this.commentaire = "; chaque section correspond à une connexion" + this.cr;
		this.commentaire += "; le nom de la section est le nom de la connexion utilisé dans jDb et jDao" + this.cr;
		this.commentaire += "; la liste des paramètres dépend du driver." + this.cr;
		this.commentaire += this.cr;
		
		// pdo
		this.pdo = "; pour pdo :" + this.cr;
		this.pdo += ";driver=pdo" + this.cr;
		this.pdo += ";dsn=mysql:host=localhost;dbname=test" + this.cr;
		this.pdo += ";user=" + this.cr;
		this.pdo += ";password=";
	}
	
	public String getEntete(){
		return this.entete;
	}
	
	public String getDefinition(String nomConnexion){
		return this.definition + nomConnexion + this.cr + this.cr;
	}
	
	public String getCommentaire(){
		return this.commentaire;
	}
	
	public String getConnexion(String nomConnexion, String host, String db, String user, String pwd, Boolean persistent){
		String res = "";
		
		res = "[" + nomConnexion + "]" + this.cr;
		res += "; pour la plupart des drivers :" + this.cr;
		res += "driver=\"mysql\"" + this.cr;
		res += "database=\"" + db + "\"" + this.cr;
		res += "host=\"" + host + "\"" + this.cr;
		res += "user=\"" + user + "\"" + this.cr;
		res += "password=\"" + pwd + "\"" + this.cr;
		if (persistent){
			res += "persistent= on" + this.cr;
		}else{
			res += "persistent= off" + this.cr;
		}
		res += this.cr;
		
		return res;
	}
	
	public String getPdo(){
		return this.pdo;
	}
	
	
	
}
