package uk.co.ergosoft.ExileRecipes;

public class RecipeParser {

    public static void main(String[] args) {
        System.out.println("Exile recipe parser");
        System.out.println("===================");
        System.out.println();

        if (args.length < 2) {
            System.err.println("Use: RecipeParser <input file(s)...> <output file>");
            System.exit(1);
        }

        new ClassProcessor().execute(args);
    }
}
