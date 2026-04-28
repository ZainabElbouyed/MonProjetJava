package com.zainab.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {
    
    @Test
    public void contextLoads() {
        assertTrue(true);
    }
    
    @Test
    public void testApplication() {
        System.out.println("Test exécuté par Jenkins!");
        assertTrue("Application doit fonctionner", true);
    }
}