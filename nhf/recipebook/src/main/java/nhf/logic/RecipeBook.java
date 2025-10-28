package nhf.logic;

import nhf.model.Recipe;
import nhf.io.JsonHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/* A program logikájának az alapja, a receptkönyv class */
public class RecipeBook {
    JsonHandler jsonHandler;
    private List<Recipe> recipes;
    private File recipeFile;

    /**
     * Konstruktor, létrehoz egy új listát a recepteknek
     */
    public RecipeBook() {
        recipeFile = new File("recipes.json");
        jsonHandler = new JsonHandler();
        try {
            importRecipes();
        } catch (Exception e) {
            System.err.println("Error on loading recipes: " + e.getMessage());
            this.recipes = new ArrayList<>();
        }
    }

    public RecipeBook(File recipeFileOptional) {
        recipeFile = recipeFileOptional;
        jsonHandler = new JsonHandler();
        this.recipes = new ArrayList<>();
        try {
            importRecipes();
        } catch (Exception e) {
            System.err.println("Error on loading recipes: " + e.getMessage());
            this.recipes = new ArrayList<>();
        }
    }

    /**
     * Hozzáad egy receptet a könyvhöz
     * 
     * @param recipe A hozzáadni kívánt recept
     */
    public void addRecipe(Recipe recipe) {
        if (recipe != null) {
            this.recipes.add(recipe);
        }
        try {
            exportRecipes();
        } catch (Exception e) {
            System.err.println("Error on saving recipe: " + e.getMessage());
        }
    }

    /**
     * Töröl egy receptet a könyvhöz
     * 
     * @param recipe A törölni kívánt recept
     */
    public boolean removeRecipe(Recipe recipe) {
        boolean success = this.recipes.remove(recipe);
        try {
            exportRecipes();
        } catch (Exception e) {
            System.err.println("Error on removing recipe: " + e.getMessage());
        }
        return success;
    }

    /**
     * Standard getter
     * 
     * @return Visszaadja a receptek másolatát, hogy ne tudják kívülről módosítani a
     *         listát
     */
    public List<Recipe> getRecipes() {
        return new ArrayList<>(recipes);
    }

    // Logikai metódusok

    /**
     * A filtereléshez használt metódus
     * 
     * @param partialName A részleges név kereséshez
     * @return Visszaad egy listát ami a részleges nevet tartalmazó recepteket
     *         tartalmazza
     */
    public List<Recipe> filterRecipesByName(String partialName) {
        String query = partialName.toLowerCase();

        return recipes.stream()
                .filter(recipe -> recipe.getName().toLowerCase().contains(query))
                .toList();
    }

    /**
     * Sorts recipes based on preparation time.
     * 
     * @param ascending Ha igaz akkor növekvő, egyébként csökkenő sorban adja vissza
     *                  a recepteket
     */
    public void sortRecipesByTime(boolean ascending) {
        Comparator<Recipe> comparator = Comparator.comparing(Recipe::getPreparationTime);
        if (!ascending) {
            comparator = comparator.reversed();
        }
        recipes.sort(comparator);
    }

    public void exportRecipes() throws Exception {
        jsonHandler.saveRecipes(this.recipes, recipeFile);
    }

    public void importRecipes() throws Exception {
        this.recipes = jsonHandler.loadRecipes(recipeFile);
    }

    /**
     * Ellenőrzi, hogy a megadott receptnév már létezik-e
     * 
     * @param name a keresett név
     * @return true, ha már létezik
     */
    public boolean recipeNameExists(String name) {
        final String searchName = name.trim().toLowerCase();
        return recipes.stream()
                .anyMatch(r -> r.getName().toLowerCase().equals(searchName));
    }

    public void updateRecipe(Recipe oldRecipe, Recipe newRecipe) {
        int index = recipes.indexOf(oldRecipe);
        if (index != -1) {
            recipes.set(index, newRecipe);
            try {
                exportRecipes();
            } catch (Exception e) {
                System.err.println("Error updating recipe: " + e.getMessage());
            }
        }
    }
}