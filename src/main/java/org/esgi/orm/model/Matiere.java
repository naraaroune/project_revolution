package org.esgi.orm.model;


import org.esgi.orm.annotations.ORM_AUTO_INCREMENT;
import org.esgi.orm.annotations.ORM_PK;
import org.esgi.orm.annotations.ORM_TABLE;

@ORM_TABLE("matiere")
public class Matiere {
	
	
	@ORM_PK
	@ORM_AUTO_INCREMENT
	

	public int id_matiere,id_prof;
	public String libelle_matiere;
	
	@Override
	public String toString() {
		return "Matiere [libelle=" + libelle_matiere + "]";
	}
}
