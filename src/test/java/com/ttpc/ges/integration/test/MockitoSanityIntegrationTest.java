
package com.ttpc.ges.integration.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MockitoSanityIntegrationTest {

    public interface Service {
        String getMessage();
    }

    @Mock
    Service mockService;

    @Test
    public void testMockReturnsExpectedValue() {
        when(mockService.getMessage()).thenReturn("Hello Mockito");

        String result = mockService.getMessage();

        assertEquals("Hello Mockito", result);
        verify(mockService).getMessage();
    }
}
