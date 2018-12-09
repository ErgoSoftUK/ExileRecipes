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

    public void execute(String[] args) {
        for(int i=0; i < args.length - 1; i++) {
            try (Scanner s = new Scanner(new File(args[i]))) {
                s.useDelimiter("\n|\t|\\{|\\}|=|,");
                processRecipes(s);
                System.out.println("Recipes found: " + recipes.size());
            } catch (Exception e) {
                System.err.println("Error scanning file: " + e.getMessage());
                e.printStackTrace();
            }
        }
        writeJson(args[args.length - 1]);
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
            if (token.contains(": Exile_AbstractCraftingRecipe"))
                return new Recipe().setName(token);
        }
        return null;
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
            else if ("category".equals(token))
                recipe.setCategory(clean(getNextToken(s)));
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

            i = token.indexOf("//");
            if (i>=0)
                token = token.substring(0, i);

            if (!token.isEmpty())
                return token;
        }

        throw new RuntimeException("No more tokens found before end of file!!");
    }

    private String clean(String s) {
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
