package mirea.structures;

import org.junit.Test;

import static org.junit.Assert.*;

public class CustomSetTest {

    @Test
    public void add() {
        CustomSet<Double> set = new CustomSet<>();
        set.add(25.0);
        assertTrue(set.contains(25.0));
        boolean a = set.add(25.0);
        assertFalse(a);
        set.add(320.6);
        assertTrue(set.contains(320.6));
        assertFalse(set.contains(6.0));
    }

    @Test
    public void contains() {
    }
}