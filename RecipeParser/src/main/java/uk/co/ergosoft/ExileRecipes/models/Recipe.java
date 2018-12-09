package uk.co.ergosoft.ExileRecipes.models;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private String name;
    private String pictureItem;
    private String interactionGrp;
    private List<Item> returnedItems = new ArrayList<>();
    private List<String> tools = new ArrayList<>();
    private List<Item> components = new ArrayList<>();
    private boolean requiresOcean;
    private boolean requiresFire;
    private boolean requiresConcreteMixer;
    private String category;

    public String getName() {
        return name;
    }

    public Recipe setName(String name) {
        this.name = name;
        return this;
    }

    public String getPictureItem() {
        return pictureItem;
    }

    public Recipe setPictureItem(String pictureItem) {
        this.pictureItem = pictureItem;
        return this;
    }

    public String getInteractionGrp() {
        return interactionGrp;
    }

    public Recipe setInteractionGrp(String interactionGrp) {
        this.interactionGrp = interactionGrp;
        return this;
    }

    public List<Item> getReturnedItems() {
        return returnedItems;
    }

    public List<String> getTools() {
        return tools;
    }

    public List<Item> getComponents() {
        return components;
    }

    public Recipe setReturnedItems(List<Item> returnedItems) {
        this.returnedItems = returnedItems;
        return this;
    }

    public Recipe setTools(List<String> tools) {
        this.tools = tools;
        return this;
    }

    public Recipe setComponents(List<Item> components) {
        this.components = components;
        return this;
    }

    public boolean isRequiresOcean() {
        return requiresOcean;
    }

    public Recipe setRequiresOcean(boolean requiresOcean) {
        this.requiresOcean = requiresOcean;
        return this;
    }

    public boolean isRequiresFire() {
        return requiresFire;
    }

    public Recipe setRequiresFire(boolean requiresFire) {
        this.requiresFire = requiresFire;
        return this;
    }

    public boolean isRequiresConcreteMixer() {
        return requiresConcreteMixer;
    }

    public Recipe setRequiresConcreteMixer(boolean requiresConcreteMixer) {
        this.requiresConcreteMixer = requiresConcreteMixer;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public Recipe setCategory(String category) {
        this.category = category;
        return this;
    }
}
