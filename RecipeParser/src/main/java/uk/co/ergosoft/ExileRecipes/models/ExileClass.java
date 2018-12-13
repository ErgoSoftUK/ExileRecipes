package uk.co.ergosoft.ExileRecipes.models;

import java.util.ArrayList;
import java.util.List;

public class ExileClass {
    private String name;
    private String superClass;
    private List<Property> properties = new ArrayList();
//    private List<ExileClass> internalClass = new ArrayList();

    public String getName() {
        return name;
    }

    public ExileClass setName(String name) {
        this.name = name;
        return this;
    }

    public String getSuperClass() {
        return superClass;
    }

    public ExileClass setSuperClass(String superClass) {
        this.superClass = superClass;
        return this;
    }

    public List<Property> getProperties() {
        return properties;
    }

//    public List<ExileClass> getInternalClass() {
//        return internalClass;
//    }
}
