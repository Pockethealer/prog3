package nhf.logic;

import nhf.model.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * JUnit tests for the RecipeBook business logic class.
 */
class RecipeBookTest {

    private RecipeBook recipeBook;
    private Recipe pizza;
    private Recipe cake;
    private Recipe applePie;

    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() {
        recipeBook = new RecipeBook(new File(tempDir, "temp.json"));
        pizza = new Recipe("Pepperoni Pizza", 45, 4, "Bake it.", new ArrayList<>());
        cake = new Recipe("Chocolate Cake", 60, 8, "Mix it.", new ArrayList<>());
        applePie = new Recipe("Grandma's Apple Pie", 90, 6, "Slice it.", new ArrayList<>());

        recipeBook.addRecipe(pizza);
        recipeBook.addRecipe(cake);
        recipeBook.addRecipe(applePie);
    }

    @Test
    void testAddRecipeAndInitialSize() {
        assertEquals(3, recipeBook.getRecipes().size(),
                "recipe size does not match new size");
    }

    @Test
    void testRemoveRecipe() {
        boolean removed = recipeBook.removeRecipe(cake);

        assertTrue(removed, "A süteményt sikeresen el kellett távolítani.");
        assertEquals(2, recipeBook.getRecipes().size(),
                "recipe ing. size does not match expected size after removal");
        assertFalse(recipeBook.getRecipes().contains(cake),
                "The removed recipe still there");
    }

    @Test
    void testFilterRecipesByName() {
        List<Recipe> filtered = recipeBook.filterRecipesByName("PizZa");
        assertEquals(1, filtered.size(), "There should only be one result");
        assertEquals(pizza, filtered.get(0), "it should match pizza");
    }

    @Test
    void testSortRecipesByTimeAscending() {
        recipeBook.sortRecipesByTime(true);
        List<Recipe> sorted = recipeBook.getRecipes();
        assertEquals(pizza, sorted.get(0), "Wrong sort");
        assertEquals(applePie, sorted.get(2), "The longes prep time ing. doesnt match");
    }

}