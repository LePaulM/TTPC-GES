
package com.ttpc.ges.unit.view;

import org.junit.jupiter.api.Test;

import com.ttpc.ges.view.MainFrame;

import javax.swing.JFrame;

import static org.junit.jupiter.api.Assertions.*;

public class MainFrameTest {

    @Test
    public void testMainFrameInitialization() {
        MainFrame frame = new MainFrame();
        assertNotNull(frame);
        assertTrue(frame instanceof JFrame);
    }
}
