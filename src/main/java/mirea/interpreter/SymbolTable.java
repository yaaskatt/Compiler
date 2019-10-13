package mirea.interpreter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Symbol table with basic operations
 * Holds object - List of HashMap with Records
 *
 * @see Record
 */
class SymbolTable {
    ArrayList<HashMap<String, Record>> tables = new ArrayList<>();
    int position = -1;

    /*
     * Creating new scope on init
     */
    SymbolTable(){
        enterScope();
    }
    /**
     * Increase nesting level
     */
    void enterScope(){
        if (tables.size() <= ++position)
            tables.add(new HashMap<>());
    }
    /**
     * Decrease nesting level
     */
    void exitScope(){
        tables.remove(position--);
    }
    /**
     * Inserts symbol into current scope
     * @param record structure to insert
     */
    void insertSymbol(Record record){
        tables.get(position).put(record.getName(), record);
    }
    /**
     * Searches for symbol occurrence in all scopes containing local
     * @param name name of symbol to search
     * @return {@code null} if not found, {@link Record} if found</>
     */
    Record lookup(String name){
        Record result;
        for (int i = position; i >= 0; i--){
            if ((result = tables.get(i).get(name)) != null) return result;
        }
        return  null;
    }
    /**
     * Searches for symbol occurrence in local scope
     * @param name name of symbol to search
     * @return {@code null} if not found, {@link Record} if found</>
     */
    Record localLookup(String name) {
        return tables.get(position).get(name);
    }
}
