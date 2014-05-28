package org.esgi.orm.model;


import org.esgi.orm.annotations.ORM_AUTO_INCREMENT;
import org.esgi.orm.annotations.ORM_PK;
import org.esgi.orm.annotations.ORM_TABLE;

@ORM_TABLE("prof")
public class Prof {
	
	
	@ORM_PK
	@ORM_AUTO_INCREMENT
	

	public int id_prof,id_classe;
	public String nom_prof;
	
	@Override
	public String toString() {
		return "Prof [nom=" + nom_prof + "]";
	}
}
