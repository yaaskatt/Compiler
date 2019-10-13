package mirea.interpreter;

/**
 * Basic record for SymbolTable class.
 * Contains name, value and type of variable.
 * @see SymbolTable
 */
class Record {
    private String name;
    private Object value;
    private String type;

    Record(String name, Object value, String type){
        this.name = name;
        this.value = value;
        this.type = type;
    }

    String getName() {
        return name;
    }

    Object getValue() {
        return value;
    }

    void setValue(Object value) {
        this.value = value;
    }

    String getType() {
        return type;
    }
}
