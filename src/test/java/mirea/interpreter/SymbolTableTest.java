package mirea.interpreter;

import mirea.parser.ParserTokenType;
import mirea.table.Record;
import mirea.table.SymbolTable;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class SymbolTableTest {

    @Test
    public void enterScope() {
        SymbolTable symbolTable = new SymbolTable();
        Assert.assertEquals(0, symbolTable.position());
        symbolTable.enterScope();
        Assert.assertEquals(1, symbolTable.position());
    }

    @Test
    public void exitScope() {
        SymbolTable symbolTable = new SymbolTable();
        Assert.assertEquals(0, symbolTable.position());
        symbolTable.exitScope();
        Assert.assertEquals(-1, symbolTable.position());
    }

    @Test
    public void insertSymbol() {
        SymbolTable symbolTable = new SymbolTable();
        Record test = new Record("a", "1", ParserTokenType.INT);
        symbolTable.insertSymbol(test);
        assertEquals(test, symbolTable.tables().get(0).get("a"));
    }

    @Test
    public void lookup() {
        SymbolTable symbolTable = new SymbolTable();
        symbolTable.insertSymbol(new Record("a", "1", ParserTokenType.INT));
        symbolTable.insertSymbol(new Record("b", "1", ParserTokenType.INT));
        symbolTable.enterScope();
        symbolTable.insertSymbol(new Record("b", "2", ParserTokenType.INT));
        assertEquals("1", symbolTable.lookup("a").getValue());
        assertEquals("2", symbolTable.lookup("b").getValue());
        symbolTable.exitScope();
        assertEquals("1", symbolTable.lookup("b").getValue());
    }

    @Test
    public void localLookup() {
        SymbolTable symbolTable = new SymbolTable();
        symbolTable.enterScope();
        Record test = new Record("a", "1", ParserTokenType.INT);
        symbolTable.insertSymbol(test);
        assertEquals( test, symbolTable.lookup("a"));
        assertNull(symbolTable.lookup("b"));
    }

    @Test
    public void flatten() {
        SymbolTable table = new SymbolTable();
        table.insertSymbol(new Record("a", null, null));
        table.enterScope();
        table.insertSymbol(new Record("b", null, null));
        SymbolTable flatten = new SymbolTable();
        flatten.insertAll(table.flatten().values());
        Assert.assertNotNull("a null", flatten.lookup("a"));
        Assert.assertNotNull("b null", flatten.lookup("b"));
    }
}