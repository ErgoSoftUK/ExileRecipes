package uk.co.ergosoft.ExileRecipes;

public class RecipeParser {

    public static void main(String[] arg) {
        System.out.println("Exile recipe parser");
        System.out.println("===================");
        System.out.println();

        if (arg.length != 2) {
            System.err.println("Use: RecipeParser <config.cpp input file> <json output file>");
            System.exit(1);
        }
        System.out.println("Input file: " + arg[0]);
        System.out.println("Output file: " + arg[1]);

        new FileProcessor().execute(arg[0], arg[1]);
    }
}
