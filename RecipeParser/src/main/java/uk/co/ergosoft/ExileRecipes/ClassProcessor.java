package uk.co.ergosoft.ExileRecipes;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.co.ergosoft.ExileRecipes.models.ExileClass;
import uk.co.ergosoft.ExileRecipes.models.Item;
import uk.co.ergosoft.ExileRecipes.models.Property;
import uk.co.ergosoft.ExileRecipes.models.Recipe;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ClassProcessor {
    private List<ExileClass> classes = new ArrayList();
    private List<ExileClass> items;
    private List<ExileClass> recipes;

    public void execute(String[] files) {
        for(int i=0; i < files.length - 1; i++) {
            processFile(files[i]);
        }

        items = classes.stream().filter((c) -> "Exile_AbstractItem".equals(c.getSuperClass())).collect(Collectors.toList());
        recipes = classes.stream().filter((c) -> "Exile_AbstractCraftingRecipe".equals(c.getSuperClass())).collect(Collectors.toList());

        System.out.println("Items found: " + items.size());
        System.out.println("Recipes found: " + recipes.size());

        List<Recipe> r = recipes
            .stream()
            .map((c) -> classToRecipe(c))
            .collect(Collectors.toList());


        writeJson(files[files.length-1], r);
    }

    private void processFile(String file) {
        System.out.println("Processing file: " + file);
        try (Scanner s = new Scanner(new File(file))) {
            //s.useDelimiter("\n|\t|\\{|\\}|=|,");
            processClasses(s);
            System.out.println("Classes found: " + classes.size());
        } catch (Exception e) {
            System.err.println("Error scanning file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processClasses(Scanner s) {
        while(s.hasNext()) {
            String token = getNextToken(s);
            if (token.equals("class")) {
               parseClass(s);
            } else if (token.equals("#ifdef")) {
                System.err.println("#ifdef skipped");
                s.findWithinHorizon("#endif", 0);
            } else if (token.equals("#include")) {
                System.err.println("#include skipped");
                s.nextLine();
            } else {
                System.err.println("Error: [" + token + "]");
                System.exit(-1);
            }
        }
    }

    private ExileClass parseClass(Scanner s) {
        ExileClass ec = new ExileClass();
        classes.add(ec);
        String name = getNextToken(s);
        ec.setName(name.replace(":", ""));
        debug("Class: " + name);
        if (name.endsWith(":")) {
            String superClass = getNextToken(s);
            debug("\tSuper: " + superClass);
            ec.setSuperClass(superClass);
        }
        s.findWithinHorizon("[{]",0);

        String token = getNextToken(s);
        while (s.hasNext() && !"};".equals(token)) {
            if ("class".equals(token)) {
                parseClass(s);
            } else {
                String value = "";
                if (token.contains("=")) {
                    int i = token.indexOf("=");
                    value = token.substring(i + 1);
                    token = token.substring(0, i);
                } else {
                    getNextToken(s); // =
                }
                while (s.hasNext() && !value.endsWith(";")) {
                    value += " " + getNextToken(s);
                }
                ec.getProperties().add(
                    new Property().setName(token).setValue(value.replace("\"", "").replace(";", "").trim())
                );
                debug("\t- " + token + " = " + value);
            }
            token = getNextToken(s);
        }
        debug("Class end");
        return ec;
    }

    private String getNextToken(Scanner s) {
        String token = "";

        while (s.hasNext() && token.isEmpty()) {
            token = s.next();
            if (token.startsWith("/*")) {
                s.findWithinHorizon("[*]/", 0);
                token = "";
            } else {
                int i = token.indexOf("//");
                if (i >= 0) {
                    token = token.substring(0, i);
                    s.nextLine();
                }
            }
        }
        return token;
    }

    private void debug(String s) {
        // System.out.println(s);
    }

    private Recipe classToRecipe(ExileClass c) {
        Recipe r = new Recipe();
        r.setName(clean(c.getName()));
        for(Property p : c.getProperties()) {
            if ("pictureItem".equals(p.getName()))
                r.setPictureItem(p.getValue());
            if ("requiredInteractionModelGroup".equals(p.getName()))
                r.setInteractionGrp(clean(p.getValue()));
            if ("category".equals(p.getName()))
                r.setCategory(p.getValue());
            if ("requiresOcean".equals(p.getName()))
                r.setRequiresOcean("1".equals(p.getValue()));
            if ("requiresFire".equals(p.getName()))
                r.setRequiresFire("1".equals(p.getValue()));
            if ("requiresConcreteMixer".equals(p.getName()))
                r.setRequiresConcreteMixer("true".equals(p.getValue()));
            if ("returnedItems[]".equals(p.getName()))
                r.setReturnedItems(parseItemList(p.getValue()));
            if ("tools[]".equals(p.getName()))
                r.setTools(parseStringList(p.getValue()));
            if ("components[]".equals(p.getName()))
                r.setComponents(parseItemList(p.getValue()));
        }
        return r;
    }

    private List<Item> parseItemList(String itemText) {
        List<Item> result = new ArrayList<>();
        itemText = itemText.replace(" ", "");
        String[] itemTexts = itemText.split("[}][,][{]");
        for (String item : itemTexts) {
            item = item.replace("{", "").replace("}", "");
            String[] pair = item.split(",");
            result.add(
                new Item(clean(pair[1]), Integer.parseInt(pair[0]))
            );
        }
        return result;
    }

    private List<String> parseStringList(String itemText) {
        List<String> result =  Arrays.asList(itemText
            .replace("{", "")
            .replace("}", "")
            .split(",")
        );

        return result
            .stream()
            .map((i) -> i = clean(i.trim()))
            .collect(Collectors.toList());
    }

    private void writeJson(String outfile, List<Recipe> r) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outfile), r);
        } catch (Exception e) {
            System.err.println("Error generating json: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String clean(String s) {
        return s
            .replace("Exile_Item_", "")
            .replace("\"", "")
            .replace(";", "")
            .replace("_", " ")
            .replaceAll("([^ ][a-z])([A-Z])", "$1 $2");
    }
}
