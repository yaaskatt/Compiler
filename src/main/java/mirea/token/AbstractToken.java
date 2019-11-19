package mirea.token;

public abstract class AbstractToken {
    private String type;
    private String value;

    public AbstractToken(String type, String value) {
        this.type = type.toUpperCase();
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    public String getType() {
        return type;
    }
}
