package com.zainab.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ApplicationTest {
    
    @Test
    public void contextLoads() {
        System.out.println("=== Test contextLoads exécuté ===");
        assertTrue(true);
    }
    
    @Test
    public void testApplication() {
        System.out.println("=== Test testApplication exécuté par Jenkins! ===");
        assertTrue(true, "Application doit fonctionner");
    }
    
    @Test
    public void testAddition() {
        System.out.println("=== Test addition ===");
        assertTrue(2 + 2 == 4);
    }
}