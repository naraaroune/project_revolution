package org.esgi.orm.model;


import org.esgi.orm.annotations.ORM_AUTO_INCREMENT;
import org.esgi.orm.annotations.ORM_PK;
import org.esgi.orm.annotations.ORM_TABLE;

@ORM_TABLE("eleve")
public class Eleve {
	
	
	@ORM_PK
	@ORM_AUTO_INCREMENT
	

	public int id_eleve,id_classe;
	public String nom_eleve,prenom_eleve;
	
	@Override
	public String toString() {
		return "Eleve [nom=" + nom_eleve + ", prenom=" + prenom_eleve+ "]";
	}
}
