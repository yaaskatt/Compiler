package mirea.table;

import java.util.ArrayList;
import java.util.HashMap;


public class SymbolTable {
    private ArrayList<HashMap<String, Record>> tables = new ArrayList<>();
    private int position = -1;

    public SymbolTable(){
        enterScope();
    }

    public void enterScope(){
        if (tables.size() <= ++position)
            tables.add(new HashMap<>());
    }

    public void exitScope(){
        tables.remove(position--);
    }

    public void insertSymbol(Record record){
        tables.get(position).put(record.getName(), record);
    }

    public void deleteSymbol(Record record) {
        tables.get(position).remove(record.getName());
    }

    public Record lookup(String name){
        Record result;
        for (int i = position; i >= 0; i--){
            if ((result = tables.get(i).get(name)) != null) return result;
        }
        return  null;
    }

    public Record localLookup(String name) {
        return tables.get(position).get(name);
    }

    public ArrayList<HashMap<String, Record>> tables() {
        return tables;
    }
}
