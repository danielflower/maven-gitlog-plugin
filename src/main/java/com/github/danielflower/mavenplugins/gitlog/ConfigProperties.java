package com.github.danielflower.mavenplugins.gitlog;


import java.util.ResourceBundle;




public class ConfigProperties
  

{
	private static final ResourceBundle params = ResourceBundle.getBundle ("configuration");
  
	 public static String getProperty(String cle_)
	 {
	      return params.getString(cle_);
	 }
	 public static String getExistingProperty(String cle_)
	 {
	      String valeur = getProperty(cle_);
	      if (valeur == null || valeur.length() == 0)
	      {
	         throw new IllegalStateException("Configuration - Parametre '" + cle_ + "' introuvable ou non renseigne");
	      }
	      return valeur;
	 }

}

