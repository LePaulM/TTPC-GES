package com.ttpc.ges.model;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class DatabaseManager {

	private static final String DB_PATH;

	static {
	    String userDir = System.getProperty("user.home");
	    File dbFolder = new File(userDir, "TTPC-GES");
	    if (!dbFolder.exists()) dbFolder.mkdirs();

	    DB_PATH = dbFolder.getAbsolutePath() + File.separator + "ttpc_ges.db";
	    System.out.println("Initialisation de DatabaseManager...");
	    System.out.println("DB_PATH = " + DB_PATH);

	}

    public DatabaseManager() {
    	try {
    		Class.forName("org.sqlite.JDBC");
    	
	        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

	
	        	try {
    stmt.executeUpdate("""
	        		    CREATE TABLE IF NOT EXISTS animaux (
	        		        id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	        		        numeroId TEXT NOT NULL,
	        		        nom TEXT NOT NULL,
	        		        espece TEXT,
	        		        sexe TEXT,
	        		        age INTEGER,
	        		        provenance TEXT,
	        		        description TEXT,
	        		        decede BOOLEAN DEFAULT 0
	        		    )
	        		""");
} catch (SQLException e) {
    JOptionPane.showMessageDialog(null,
        "Erreur SQL : " + e.getMessage(),
        "Erreur Base de Données",
        JOptionPane.ERROR_MESSAGE);
}
	
	            try {
    stmt.executeUpdate("""
					CREATE TABLE IF NOT EXISTS mouvements (
					    id INTEGER PRIMARY KEY AUTOINCREMENT,
					    animalId INTEGER NOT NULL,
					    typeMouvement TEXT NOT NULL,
					    dateMouvement DATE NOT NULL,
					    destination TEXT,
					    isDecede BOOLEAN DEFAULT 0,
					    FOREIGN KEY (animalId) REFERENCES animaux(id) ON DELETE CASCADE
					)
	                """);
} catch (SQLException e) {
    JOptionPane.showMessageDialog(null,
        "Erreur SQL : " + e.getMessage(),
        "Erreur Base de Données",
        JOptionPane.ERROR_MESSAGE);
}
	
	        } catch (SQLException e) {
	            System.out.println("Erreur lors de l'initialisation de la base :");
	            JOptionPane.showMessageDialog(null, "Une erreur est survenue : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
	        }
        } catch (ClassNotFoundException e) {
            System.out.println("[ERREUR] Driver SQLite non trouvé.");
            JOptionPane.showMessageDialog(null, "Une erreur est survenue : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
    }

    public void verifierBase() {
        System.out.println("[INFO] Vérification de la base...");
        System.out.println("[INFO] Chemin de la base : " + DB_PATH);
        try (Connection conn = connect()) {
            System.out.println("[OK] Connexion établie");
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='animaux'");
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("[OK] Table 'animaux' trouvée");
                ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM animaux");
                if (rs2.next()) {
                    System.out.println("[INFO] Nombre d'animaux : " + rs2.getInt(1));
                }
                rs2.close();
            } else {
                System.out.println("[WARN] Table 'animaux' non trouvée");
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("[ERREUR] Connexion ou lecture impossible :");
            JOptionPane.showMessageDialog(null, "Une erreur est survenue : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void ajouterAnimal(Animal animal) {
        String sql = """
            INSERT INTO animaux(numeroId, nom, espece, sexe, age, provenance, description, decede)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, animal.getNumeroId());
            pstmt.setString(2, animal.getNom());
            pstmt.setString(3, animal.getEspece());
            pstmt.setString(4, String.valueOf(animal.getSexe()));
            pstmt.setInt(5, animal.getAge());
            pstmt.setString(6, animal.getProvenance());
            pstmt.setString(7, animal.getDescription());
            pstmt.setBoolean(8, animal.isDecede());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("[ERREUR] Aucune ligne insérée.");
                return;
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    animal.setId(id);
                    System.out.println("ID généré : " + id);
                } else {
                    System.err.println("[ERREUR] Impossible de récupérer l'ID généré.");
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Une erreur est survenue : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }



    public List<Animal> getTousLesAnimaux() {
        List<Animal> animaux = new ArrayList<>();
        String sql = "SELECT * FROM animaux";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Animal animal = new Animal(
                        rs.getInt("id"),
                        rs.getString("numeroId"),
                        rs.getString("nom"),
                        rs.getString("espece"),
                        rs.getString("sexe").charAt(0),
                        rs.getInt("age"),
                        rs.getString("provenance"),
                        rs.getString("description"),
                        rs.getBoolean("decede")
                );
                animaux.add(animal);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Une erreur est survenue : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return animaux;
    }

    public List<Mouvement> getMouvementsParAnimal(int idAnimal) {
        List<Mouvement> mouvements = new ArrayList<>();
        String sql = "SELECT * FROM mouvements WHERE animalId = ? ORDER BY dateMouvement";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAnimal);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Mouvement m = new Mouvement(
                            rs.getInt("id"),
                            rs.getInt("animalId"),
                            rs.getString("typeMouvement"),
                            rs.getDate("dateMouvement"),
                            rs.getBoolean("isDecede"),
                            rs.getString("destination")
                    );
                    mouvements.add(m);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Une erreur est survenue : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            JOptionPane.showMessageDialog(null,
                "Une erreur est survenue lors de la récupération des mouvements.",
                "Erreur base de données",
                JOptionPane.ERROR_MESSAGE);
        }

        return mouvements;
    }

    
    public List<Mouvement> getTousLesMouvements() {
        List<Mouvement> mouvements = new ArrayList<>();
        String sql = "SELECT * FROM mouvements ORDER BY dateMouvement";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Mouvement m = new Mouvement(
                    rs.getInt("id"),
                    rs.getInt("animalId"),
                    rs.getString("typeMouvement"),
                    rs.getDate("dateMouvement"),
                    rs.getBoolean("isDecede"),
                    rs.getString("destination")
                );
                mouvements.add(m);
                
                System.out.println("Mouvement trouvé pour animal ID " + rs.getInt("animalId"));

            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Une erreur est survenue : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        return mouvements;
    }



    public void ajouterMouvement(Mouvement m) {
        String sql = "INSERT INTO mouvements(animalId, typeMouvement, dateMouvement, destination, isDecede) " +
                     "VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, m.getAnimalId());
            stmt.setString(2, m.getTypeMouvement());
            stmt.setDate(3, m.getDateMouvement());
            stmt.setString(4, m.getDestination());
            stmt.setBoolean(5, m.isDecede());

            stmt.executeUpdate();
            System.out.println("Mouvement ajouté : " + m);

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du mouvement : " + e.getMessage());
        }
    }


    
    public void mettreAJourStatutDeces(int animalId, boolean estDecede) {
        String sql = "UPDATE animaux SET decede = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, estDecede);
            pstmt.setInt(2, animalId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Une erreur est survenue : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Animal getAnimalById(int id) {
        String sql = "SELECT * FROM animaux WHERE id = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
            	Animal a = new Animal(
            		    rs.getString("numeroId"),
            		    rs.getString("nom"),
            		    rs.getString("espece"),
            		    rs.getString("sexe").charAt(0),
            		    rs.getInt("age"),
            		    rs.getString("provenance"),
            		    rs.getString("description"),
            		    rs.getBoolean("decede")
            		);
            		a.setId(rs.getInt("id")); // ← IMPORTANT
            		return a;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Une erreur est survenue : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    public Animal getAnimalByNumeroId(String id) {
        String sql = "SELECT * FROM animaux WHERE numeroId = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
            	Animal a = new Animal(
            		    rs.getString("numeroId"),
            		    rs.getString("nom"),
            		    rs.getString("espece"),
            		    rs.getString("sexe").charAt(0),
            		    rs.getInt("age"),
            		    rs.getString("provenance"),
            		    rs.getString("description"),
            		    rs.getBoolean("decede")
            		);
            		a.setId(rs.getInt("id")); // ← IMPORTANT
            		return a;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Une erreur est survenue : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    public void updateAnimal(Animal a) {
        String sql = "UPDATE animaux SET numeroId=?, nom=?, espece=?, sexe=?, age=?, provenance=?, description=?, decede=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, a.getNumeroId());
            pstmt.setString(2, a.getNom());
            pstmt.setString(3, a.getEspece());
            pstmt.setString(4, String.valueOf(a.getSexe()));
            pstmt.setInt(5, a.getAge());
            pstmt.setString(6, a.getProvenance());
            pstmt.setString(7, a.getDescription());
            pstmt.setBoolean(8, a.isDecede());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Une erreur est survenue : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateMouvement(Mouvement mouvement) {
        String sql = "UPDATE mouvements SET animalId = ?, typeMouvement = ?, dateMouvement = ?, destination = ?, isDecede = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, mouvement.getAnimalId());
            pstmt.setString(2, mouvement.getTypeMouvement());
            pstmt.setDate(3, mouvement.getDateMouvement());
            pstmt.setString(4, mouvement.getDestination());
            pstmt.setBoolean(5, mouvement.isDecede());
            pstmt.setInt(6, mouvement.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Une erreur est survenue : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    public void deleteAnimal(int id) {
        String sql = "DELETE FROM animaux WHERE id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                System.out.println("Animal supprimé (id=" + id + ")");
            } else {
                System.out.println("Aucun animal trouvé avec l'id " + id);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Une erreur est survenue : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    public void deleteMouvement(int id) {
        String sql = "DELETE FROM mouvements WHERE id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Une erreur est survenue : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
        
    public boolean estPresent(int animalId) {
        List<Mouvement> mouvements = getMouvementsParAnimal(animalId);
        if (mouvements.isEmpty()) return false;

        Mouvement dernier = mouvements.get(mouvements.size() - 1);
        return "entree".equalsIgnoreCase(dernier.getTypeMouvement());
    }
    
    public static java.sql.Date parseDateSafely(String input) {
        if (input == null || input.trim().isEmpty()) return null;

        input = input.trim();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);

        try {
            // Si c’est une vraie date JJ/MM/AAAA
            java.util.Date parsed = sdf.parse(input);
            return new java.sql.Date(parsed.getTime());

        } catch (ParseException e) {
            try {
                // Sinon, essaie de le parser comme un timestamp
                long timestamp = Long.parseLong(input);
                return new java.sql.Date(timestamp);
            } catch (NumberFormatException ex) {
                System.out.println("Erreur parsing date : \"" + input + "\" n'est ni une date ni un timestamp.");
                return null;
            }
        }
    }
    
    public void supprimerAnimalEtMouvements(int idAnimal) {
        String deleteMouvementsSQL = "DELETE FROM mouvements WHERE animalId = ?";
        String deleteAnimalSQL = "DELETE FROM animaux WHERE id = ?";
        
        try (Connection conn = connect();
             PreparedStatement delMouv = conn.prepareStatement(deleteMouvementsSQL);
             PreparedStatement delAnimal = conn.prepareStatement(deleteAnimalSQL)) {
            
            delMouv.setInt(1, idAnimal);
            delMouv.executeUpdate();

            delAnimal.setInt(1, idAnimal);
            delAnimal.executeUpdate();

            System.out.println("[INFO] Animal et mouvements supprimés pour ID=" + idAnimal);
        } catch (SQLException e) {
            System.out.println("[ERREUR] Suppression animal+mouvements : " + e.getMessage());
        }
    }

    public Mouvement getMouvementById(int id) {
        String sql = "SELECT * FROM mouvements WHERE id = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int animalId = rs.getInt("animalId");
                String type = rs.getString("typeMouvement");
                Date date = rs.getDate("dateMouvement");
                String destination = rs.getString("destination");
                boolean decede = rs.getBoolean("isDecede");

                return new Mouvement(animalId, type, date, destination, decede);
            }

        } catch (SQLException e) {
            System.out.println("[ERREUR] Impossible de recuperer le mouvement ID=" + id);
            JOptionPane.showMessageDialog(null, "Une erreur est survenue : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }


}
