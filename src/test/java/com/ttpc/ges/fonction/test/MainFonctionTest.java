package com.ttpc.ges.fonction.test;

import org.junit.jupiter.api.Test;

import com.ttpc.ges.Main;

import static org.junit.jupiter.api.Assertions.*;

public class MainFonctionTest {

    @Test
    public void testMainLancementSansException() {
        assertDoesNotThrow(() -> {
            String[] args = {};
            Main.main(args);
        }, "L'exécution de Main.main ne doit pas générer d'exception");
    }
}