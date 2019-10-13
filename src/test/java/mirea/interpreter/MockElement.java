package mirea.interpreter;

public class MockElement implements ElementInterface {

    String type;
    String value;

    MockElement(String type, String value){
        this.type = type;
        this.value = value;
    }

    static MockElement newEl(String type, String value){
        return new MockElement(type, value);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getValue() {
        return value;
    }
}
