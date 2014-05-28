package org.esgi.orm.model;


import java.sql.Time;
import java.util.Date;

import org.esgi.orm.annotations.ORM_AUTO_INCREMENT;
import org.esgi.orm.annotations.ORM_PK;
import org.esgi.orm.annotations.ORM_TABLE;

@ORM_TABLE("absence")
public class Absence {
	
	
	@ORM_PK
	@ORM_AUTO_INCREMENT
	

	public int id_absence,id_eleve, id_matiere;
	public Date date_absence;
	public Time heure_debut, heure_fin;
	
	@Override
	public String toString() {
		return "Absence [date absence=" + date_absence + ", heure debut=" + heure_debut+", heure fin=" +heure_fin+ "]";
	}
}
