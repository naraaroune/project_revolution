package org.esgi.orm.model;


import java.sql.Time;
import java.util.Date;

import org.esgi.orm.annotations.ORM_AUTO_INCREMENT;
import org.esgi.orm.annotations.ORM_PK;
import org.esgi.orm.annotations.ORM_TABLE;

@ORM_TABLE("retard")
public class Retard {
	
	
	@ORM_PK
	@ORM_AUTO_INCREMENT
	

	public int id_retard,id_eleve, id_matiere;
	public Date date_retard;
	public Time heure_debut, heure_fin;
	
	@Override
	public String toString() {
		return "Retard [date retard=" + date_retard + ", heure debut=" + heure_debut+", heure fin=" +heure_fin+ "]";
	}
}
