package model;

public class Animal {
    private int id;
    private String nom;
	private String espece;
    private char sexe;
    private int age;
    private String provenance;
    private String description;
    private boolean decede;
    private String numeroId;

    public Animal(String numeroId, String nom, String espece, char sexe, int age, String provenance, String description, boolean decede) {
		super();
		this.nom = nom;
		this.espece = espece;
		this.sexe = sexe;
		this.age = age;
		this.provenance = provenance;
		this.description = description;
		this.decede = decede;
		this.numeroId = numeroId;
	}

	public Animal(int id, String nom, String espece, char sexe, int age, String provenance, String description, boolean decede) {
        this.id = id;
        this.nom = nom;
        this.espece = espece;
        this.sexe = sexe;
        this.age = age;
        this.provenance = provenance;
        this.description = description;
        this.decede = decede;
    }

    public Animal(String nom, String espece, char sexe, int age, String provenance, String description, boolean decede) {
        this.nom = nom;
        this.espece = espece;
        this.sexe = sexe;
        this.age = age;
        this.provenance = provenance;
        this.description = description;
        this.decede = decede;
    }

    public Animal(String numeroId, String nom, String espece, char sexe, int age, String provenance, String description) {
		// TODO Auto-generated constructor stub
    	this.numeroId = numeroId;
        this.nom = nom;
        this.espece = espece;
        this.sexe = sexe;
        this.age = age;
        this.provenance = provenance;
        this.description = description;
        this.decede = false;
        
        //System.out.println("Animal Constructeur");
        //System.out.println(this.toString());
	}

	public Animal(int id,String numeroId, String nom, String espece, char sexe, int age, String provenance, String description, boolean decede) {
        this.id = id;
        this.numeroId = numeroId;
        this.nom = nom;
        this.espece = espece;
        this.sexe = sexe;
        this.age = age;
        this.provenance = provenance;
        this.description = description;
        this.decede = decede;
    }

	public int getId() {
		// System.out.println("id : "+this.id);
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getEspece() {
        return espece;
    }

    public char getSexe() {
        return sexe;
    }

    public int getAge() {
        return age;
    }

    public String getProvenance() {
        return provenance;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDecede() {
        return decede;
    }
    
    public boolean isPresent(DatabaseManager dbManager) {
        return dbManager.estPresent(this.id);
    }

	public String getNumeroId() {
		return numeroId;
	}

	public void setNumeroId(String numeroId) {
		this.numeroId = numeroId;
	}
	
    @Override
	public String toString() {
    	return this.nom;
	}
    
    public String toStringLong() {
		return "Animal [id=" + id + ", nom=" + nom + ", espece=" + espece + ", sexe=" + sexe + ", age=" + age
				+ ", provenance=" + provenance + ", description=" + description + ", decede=" + decede + ", numeroId="
				+ numeroId + "]";
    }

	public void setId(int id) {
		this.id = id;
	}
	
}
