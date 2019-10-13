package mirea.interpreter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static mirea.interpreter.MockElement.newEl;
import static org.junit.Assert.*;

public class InterpreterTest {

    @Test
    public void scopesTest() throws Exception {
        Interpreter interpreter = new Interpreter();
        List<ElementInterface> inp = new ArrayList<>();
        inp.add(newEl("ADR", "a"));
        inp.add(newEl("DEF", "STRING"));
        inp.add(newEl("ADR", "a"));
        inp.add(newEl("STRING", "Hello from outer scope"));
        inp.add(newEl("OP", "="));
        inp.add(newEl("L_CB", "{"));
        inp.add(newEl("ADR", "a"));
        inp.add(newEl("DEF", "STRING"));
        inp.add(newEl("ADR", "a"));
        inp.add(newEl("STRING", "Hello from inner scope"));
        inp.add(newEl("OP", "="));
        inp.add(newEl("VAR", "a"));
        inp.add(newEl("OP", "print"));
        inp.add(newEl("R_CB", "}"));
        inp.add(newEl("VAR", "a"));
        inp.add(newEl("OP", "print"));
        assertEquals(0, interpreter.count(inp));
    }

    @Test
    public void containsTest() throws Exception {
        Interpreter interpreter = new Interpreter();
        List<ElementInterface> inp = new ArrayList<>();
        inp.add(newEl("ADR", "a"));
        inp.add(newEl("DEF", "Set"));
        inp.add(newEl("ADR", "a"));
        inp.add(newEl("INT", "5"));
        inp.add(newEl("OP", "contains"));
        inp.add(newEl("OP", "print"));
        inp.add(newEl("ADR", "a"));
        inp.add(newEl("INT", "5"));
        inp.add(newEl("OP", "add"));
        inp.add(newEl("ADR", "a"));
        inp.add(newEl("INT", "5"));
        inp.add(newEl("OP", "contains"));
        interpreter.count(inp);
    }
}