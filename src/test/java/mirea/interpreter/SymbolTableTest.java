package mirea.interpreter;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class SymbolTableTest {

    @Test
    public void enterScope() {
        SymbolTable symbolTable = new SymbolTable();
        Assert.assertEquals(0, symbolTable.position);
        symbolTable.enterScope();
        Assert.assertEquals(1, symbolTable.position);
    }

    @Test
    public void exitScope() {
        SymbolTable symbolTable = new SymbolTable();
        Assert.assertEquals(0, symbolTable.position);
        symbolTable.exitScope();
        Assert.assertEquals(-1, symbolTable.position);
    }

    @Test
    public void insertSymbol() {
        SymbolTable symbolTable = new SymbolTable();
        Record test = new Record("a", "1", "int");
        symbolTable.insertSymbol(test);
        assertEquals(test, symbolTable.tables.get(0).get("a"));
    }

    @Test
    public void lookup() {
        SymbolTable symbolTable = new SymbolTable();
        symbolTable.insertSymbol(new Record("a", "1", "int"));
        symbolTable.insertSymbol(new Record("b", "1", "int"));
        symbolTable.enterScope();
        symbolTable.insertSymbol(new Record("b", "2", "int"));
        assertEquals("1", symbolTable.lookup("a").getValue());
        assertEquals("2", symbolTable.lookup("b").getValue());
        symbolTable.exitScope();
        assertEquals("1", symbolTable.lookup("b").getValue());
    }

    @Test
    public void localLookup() {
        SymbolTable symbolTable = new SymbolTable();
        symbolTable.enterScope();
        Record test = new Record("a", "1", "int");
        symbolTable.insertSymbol(test);
        assertEquals( test, symbolTable.lookup("a"));
        assertNull(symbolTable.lookup("b"));
    }
}