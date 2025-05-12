
package com.ttpc.ges.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DatabaseManagerTest {

    private DatabaseManager dbManager;

    @BeforeEach
    public void setup() {
        dbManager = new DatabaseManager();
    }

    @Test
    public void testConnectionIsEstablished() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            assertNotNull(conn);
            assertFalse(conn.isClosed());
        } catch (SQLException e) {
            fail("La connexion à la base en mémoire a échoué : " + e.getMessage());
        }
    }

    @Test
    public void testTableCreationWithoutError() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS test (id INTEGER PRIMARY KEY)");
        } catch (SQLException e) {
            fail("Erreur lors de la création de table : " + e.getMessage());
        }
    }
}
