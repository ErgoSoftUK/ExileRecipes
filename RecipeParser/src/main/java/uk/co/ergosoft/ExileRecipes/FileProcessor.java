package uk.co.ergosoft.ExileRecipes;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.co.ergosoft.ExileRecipes.models.Item;
import uk.co.ergosoft.ExileRecipes.models.Recipe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileProcessor {
    private List<Recipe> recipes = new ArrayList<>();
    private boolean inComment = false;

    public void execute(String infile, String outfile) {
        try (Scanner s = new Scanner(new File(infile))) {
            s.useDelimiter("\n|\t|\\{|\\}|=|,");
            findCraftingRecipesClass(s);
            processRecipes(s);
            System.out.println("Recipes found: " + recipes.size());
            writeJson(outfile);
        } catch (Exception e) {
            System.err.println("Error scanning file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void findCraftingRecipesClass(Scanner s) throws IOException {
        System.out.println("Searching for recipes class...");
        while (s.hasNext()) {
            String token = getNextToken(s);

            if ("class CfgCraftingRecipes".equals(token))
                return;
        }
        throw new RuntimeException("Could not find recipes class");
    }

    private void processRecipes(Scanner s) throws IOException {
        System.out.println("Processing recipes...");
        Recipe recipe = findNextRecipe(s);
        while (recipe != null) {
            recipes.add(recipe);
            processRecipe(recipe, s);
            recipe = findNextRecipe(s);
        }
    }

    private Recipe findNextRecipe(Scanner s) throws IOException {
        while (s.hasNext()) {
            String token = getNextToken(s);
            if (token.startsWith("class"))
                return new Recipe().setName(token);

            if (";".equals(token))  // End of recipes class
                return null;
        }
        throw new RuntimeException("Error finding recipe class, end of file reached");
    }

    private void processRecipe(Recipe recipe, Scanner s) throws IOException {
        System.out.println("Recipe: " + recipe.getName());

        while (s.hasNext()) {
            String token = getNextToken(s);

            if ("name".equals(token))
                recipe.setName(clean(getNextToken(s)));
            else if ("pictureItem".equals(token))
                recipe.setPictureItem(clean(getNextToken(s)));
            else if ("returnedItems[]".equals(token))
                processReturnedItems(recipe, s);
            else if ("tools[]".equals(token))
                processTools(recipe, s);
            else if ("components[]".equals(token))
                processComponents(recipe, s);
            else if ("requiredInteractionModelGroup".equals(token))
                recipe.setInteractionGrp(clean(getNextToken(s)));
            else if ("requiresOcean".equals(token))
                recipe.setRequiresOcean("1".equals(clean(getNextToken(s))));
            else if ("requiresFire".equals(token))
                recipe.setRequiresFire("1".equals(clean(getNextToken(s))));
            else if ("requiresConcreteMixer".equals(token))
                recipe.setRequiresConcreteMixer("true".equals(clean(getNextToken(s))));
            else if (";".equals(token))
                return; // End of recipe
            else
                System.err.println(token);
        }
    }

    private void processReturnedItems(Recipe recipe, Scanner s) throws IOException {
        while (s.hasNext()) {
            String qty = getNextToken(s);

            if (";".equals(qty))
                return; // End of items

            String item = getNextToken(s);

            recipe.getReturnedItems().add(new Item(clean(item), Integer.parseInt(qty)));
        }
        throw new RuntimeException("Error parsing returned items");
    }

    private void processTools(Recipe recipe, Scanner s) throws IOException {
        while (s.hasNext()) {
            String token = getNextToken(s);

            if (";".equals(token))
                return; // End of items

            recipe.getTools().add(clean(token));
        }
        throw new RuntimeException("Error parsing tools");
    }

    private void processComponents(Recipe recipe, Scanner s) throws IOException {
        while (s.hasNext()) {
            String qty = getNextToken(s);

            if (";".equals(qty))
                return; // End of items

            String item = getNextToken(s);

            recipe.getComponents().add(new Item(clean(item), Integer.parseInt(qty)));
        }
        throw new RuntimeException("Error parsing components");
    }

    private String getNextToken(Scanner s) {
        while (s.hasNext()) {
            String token = s.next().trim();

            int i;
            while((i=token.indexOf("/*")) >= 0 || inComment) {
                int j = token.indexOf("*/");
                if (j > i) {
                    inComment = false;
                    if (i >= 0)
                        token = token.substring(0, i) + token.substring(j + 2);
                    else
                        token = token.substring(j + 2);
                } else {
                    inComment = true;
                    token = s.next().trim();
                }
            }

            if (!token.isEmpty())
                return token;
        }

        throw new RuntimeException("No more tokens found before end of file!!");
    }

    private String clean(String s) {
        int i = s.indexOf("//");
        if (i>=0)
            s = s.substring(i+2);
        return s
            .replace("Exile_Item_", "")
            .replace("\"", "")
            .replace(";", "")
            .replace("_", " ")
            .replaceAll("([^ ][a-z])([A-Z])", "$1 $2");
    }

    private void writeJson(String outfile) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outfile), recipes);
        } catch (Exception e) {
            System.err.println("Error generating json: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
