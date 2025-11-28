package nhf.logic;

import nhf.model.Recipe;
import nhf.model.IngredientTemplate;
import nhf.io.JsonHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/* A program logikájának az alapja, a receptkönyv class */
public class RecipeBook {
    JsonHandler jsonHandler;
    private List<Recipe> recipes;
    private File recipeFile;
    private File ingredientFile;
    private Map<String, IngredientTemplate> ingredientTemplates;

    /**
     * Konstruktor, létrehoz egy új listát a recepteknek
     */
    public RecipeBook() {
        recipeFile = new File("recipes.json");
        ingredientFile = new File("ingredients.json");
        jsonHandler = new JsonHandler();
        this.ingredientTemplates = new HashMap<>();
        try {
            importRecipes();
        } catch (Exception e) {
            System.err.println("Error on loading recipes: " + e.getMessage());
            this.recipes = new ArrayList<>();
        }
    }

    public RecipeBook(File recipeFileOptional, File ingredientFileOptional) {
        recipeFile = recipeFileOptional;
        ingredientFile = ingredientFileOptional;
        jsonHandler = new JsonHandler();
        this.recipes = new ArrayList<>();
        this.ingredientTemplates = new HashMap<>();
        try {
            importRecipes();
        } catch (Exception e) {
            System.err.println("Error on loading recipes: " + e.getMessage());
            this.recipes = new ArrayList<>();
            this.ingredientTemplates = new HashMap<>();
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
    // TODO remove not used methods
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

    // mivel egyszer importálunk ezért ezt összevontam az összetevőkkel
    public void importRecipes() throws Exception {
        List<IngredientTemplate> templates = jsonHandler.loadIngredientTemplates(ingredientFile);
        this.recipes = jsonHandler.loadRecipes(recipeFile);
        templates.forEach(t -> ingredientTemplates.put(t.getName().toLowerCase(), t));
        linkRecipeIngredientsToTemplates();
    }

    // összekapcsolja a templátot a tényleges összetevővel
    private void linkRecipeIngredientsToTemplates() {
        this.recipes.stream()
                .flatMap(recipe -> recipe.getIngredients().stream())
                .forEach(recipeIngredient -> {
                    String templateName = recipeIngredient.getName().toLowerCase();
                    IngredientTemplate template = ingredientTemplates.get(templateName);
                    if (template != null) {
                        recipeIngredient.setTemplate(template);
                    } else {
                        System.err.println(
                                "ingredient template not found: " + recipeIngredient.getName());
                    }
                });
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

    public Map<String, IngredientTemplate> getIngredientTemplates() {
        return Collections.unmodifiableMap(this.ingredientTemplates);
    }

    public void addTemplate(IngredientTemplate template) {
        if (template == null)
            return;
        this.ingredientTemplates.put(template.getName().toLowerCase(), template);
        try {
            exportTemplates();
        } catch (Exception e) {
            System.err.println("Error saving ingredient templates: " + e.getMessage());
        }
    }

    public boolean removeTemplate(String templateName) {
        if (templateName == null)
            return false;
        boolean wasRemoved = this.ingredientTemplates.remove(templateName.toLowerCase()) != null;
        if (wasRemoved) {
            try {
                exportTemplates();
            } catch (Exception e) {
                System.err.println("Error saving ingredient templates after removal: " + e.getMessage());
            }
        }
        return wasRemoved;
    }

    public void exportTemplates() throws Exception {
        List<IngredientTemplate> templatesToSave = new ArrayList<>(this.ingredientTemplates.values());
        jsonHandler.saveIngredientTemplates(templatesToSave, ingredientFile);
    }

    public Optional<Recipe> getRecipeByName(String name) {
        return recipes.stream()
                .filter(r -> r.getName().equalsIgnoreCase(name))
                .findFirst(); // 💡 Visszatér Optional<Recipe>
    }
}