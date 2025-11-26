package nhf.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tesztek a receptek ellenőrzésére
 */
class RecipeTest {
    List<RecipeIngredient> ingredients;
    Recipe recipe;

    @BeforeEach
    public void setup() {
        ingredients = new ArrayList<>();
        IngredientTemplate waterTemplate = new IngredientTemplate("Víz", 0, 0, 0, 0, Map.of("ML", 1.0));
        IngredientTemplate coffeeTemplate = new IngredientTemplate("Kávé", 100, 5, 0, 15, Map.of("G", 1.0));
        RecipeIngredient water = new RecipeIngredient("Víz", 200, Unit.ML);
        water.setTemplate(waterTemplate);
        ingredients.add(water);
        RecipeIngredient coffee = new RecipeIngredient("Kávé", 10, Unit.G);
        coffee.setTemplate(coffeeTemplate);
        ingredients.add(coffee);

        List<String> tags = new ArrayList<>();
        tags.add("easy");
        tags.add("fast");
        recipe = new Recipe("Simple Coffee", 5, 1,
                "Boil water, mix.", ingredients, tags);
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