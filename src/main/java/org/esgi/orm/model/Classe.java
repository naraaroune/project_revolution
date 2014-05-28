package org.esgi.orm.model;


import org.esgi.orm.annotations.ORM_AUTO_INCREMENT;
import org.esgi.orm.annotations.ORM_PK;
import org.esgi.orm.annotations.ORM_TABLE;

@ORM_TABLE("classe")
public class Classe {
	
	
	@ORM_PK
	@ORM_AUTO_INCREMENT
	

	public int id_classe;
	public String libelle_classe;
	
	@Override
	public String toString() {
		return "Classe [libelle=" + libelle_classe +"]";
	}
}
