package nhf.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tesztek a receptek ellenőrzésére
 */
class RecipeTest {
    List<Ingredient> ingredients;
    Recipe recipe;

    @BeforeEach
    public void setup() {
        ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Water", 200, "ml"));
        ingredients.add(new Ingredient("Coffee", 10, "g"));
        recipe = new Recipe("Simple Coffee", 5, 1,
                "Boil water, mix.", ingredients);
    }

    @Test
    void testGetPreparationTime() {
        assertEquals(5, recipe.getPreparationTime(),
                "Wrong prep time");
    }

    @Test
    void testIngredientCollectionSize() {
        assertEquals(2, recipe.getIngredients().size(),
                "wrong ing. list size");
    }

    @Test
    void testRecipeName() {
        assertEquals("Simple Coffee", recipe.getName(),
                "The recipe name is not properly set");
    }
}