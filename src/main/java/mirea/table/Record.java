package mirea.table;

public class Record {
    private String name;
    private Object value;
    private String type;

    public Record(String name, Object value, String type){
        this.name = name;
        this.value = value;
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }
}