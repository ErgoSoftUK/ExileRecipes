package uk.co.ergosoft.ExileRecipes.models;

public class Property {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public Property setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Property setValue(String value) {
        this.value = value;
        return this;
    }
}
