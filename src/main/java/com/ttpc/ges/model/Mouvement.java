package com.ttpc.ges.model;

import java.sql.Date;
import java.time.Instant;

public class Mouvement {
    private int id;
    private int animalId;
    private String typeMouvement; 
    private Date dateMouvement;
    private String destination;
    private boolean isDecede = false;

    // Constructeurs
    public Mouvement() {}
    
    public Mouvement(int animalId, String typeMouvement, Date dateMouvement, String destination, boolean isDecede) {
        this.animalId = animalId;
        this.typeMouvement = typeMouvement;
        this.dateMouvement = dateMouvement;
        this.setDecede(isDecede);
    }

    public Mouvement(int animalId, String typeMouvement, Date dateMouvement) {
        this.animalId = animalId;
        this.typeMouvement = typeMouvement;
        this.dateMouvement = dateMouvement;
    }
    
    public Mouvement(int id, int animalId, String typeMouvement, Date dateMouvement) {
        this.id = id;
        this.animalId = animalId;
        this.typeMouvement = typeMouvement;
        this.dateMouvement = dateMouvement;
    }

    public Mouvement(int id, int animalId, String typeMouvement, Date dateMouvement, boolean isDecede, String destination) {
        this.id = id;
        this.animalId = animalId;
        this.typeMouvement = typeMouvement;
        this.dateMouvement = dateMouvement != null ? dateMouvement : new java.sql.Date(Instant.now().toEpochMilli());
        this.isDecede = isDecede;
        this.destination = destination;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAnimalId() { return animalId; }
    public void setAnimalId(int animalId) { this.animalId = animalId; }

    public String getTypeMouvement() { return typeMouvement; }
    public void setTypeMouvement(String typeMouvement) { this.typeMouvement = typeMouvement; }

    public Date getDateMouvement() { return dateMouvement; }
    public void setDateMouvement(Date dateMouvement) { this.dateMouvement = dateMouvement; }

	public boolean isDecede() {
		return isDecede;
	}

	public void setDecede(boolean isDecede) {
		this.isDecede = isDecede;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	@Override
	public String toString() {
		return "Mouvement [id=" + id + ", animalId=" + animalId + ", typeMouvement=" + typeMouvement
				+ ", dateMouvement=" + dateMouvement + ", destination=" + destination + ", isDecede=" + isDecede + "]";
	}
	
	
}

