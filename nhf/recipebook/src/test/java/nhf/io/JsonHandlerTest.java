package nhf.io;

import nhf.model.Ingredient;
import nhf.model.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * a szerializációt tesztelő osztály
 */
class JsonHandlerTest {

    private JsonHandler jsonHandler;
    private List<Recipe> testRecipes;

    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() {
        jsonHandler = new JsonHandler();
        testRecipes = new ArrayList<>();
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Víz", 200, "ml"));
        ingredients.add(new Ingredient("Kávé", 10, "g"));

        testRecipes.add(new Recipe("Simple Coffee", 5, 1, "Boil water, mix.", ingredients));
        testRecipes.add(new Recipe("Tea", 3, 1, "Steep the bag.", new ArrayList<>()));
    }

    @Test
    void testSaveAndLoadRecipes() throws Exception {
        File tempFile = new File(tempDir, "recipes.json");
        jsonHandler.saveRecipes(testRecipes, tempFile);
        assertTrue(tempFile.exists(), "The new save file should exist");
        assertTrue(tempFile.length() > 0, "The new save file should not be empty");

        List<Recipe> loadedRecipes = jsonHandler.loadRecipes(tempFile);
        assertEquals(testRecipes.size(), loadedRecipes.size(),
                "The number of recipes loaded are wrong");

        assertEquals(2, loadedRecipes.get(0).getIngredients().size(),
                "The nested ingredient list have incorrect size");
    }

    @Test
    void testLoadEmptyFile() throws Exception {
        File nonExistentFile = new File(tempDir, "non_existent.json");
        List<Recipe> loaded = jsonHandler.loadRecipes(nonExistentFile);
        assertNotNull(loaded, "The returned listfrom an empty file should be an empty list, not null");
        assertTrue(loaded.isEmpty(), "The returned listfrom an empty file should be an empty list");
    }
}