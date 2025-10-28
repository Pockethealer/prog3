package nhf.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tesztek az összetevők tesztelésére
 */
class IngredientTest {

    @Test
    void testIngredientCreationAndQuantity() {
        Ingredient ingredient = new Ingredient("Sugar", 150.5, "g");
        assertEquals(150.5, ingredient.getQuantity(), 0.001,
                "The quantity is wrong");
    }

    @Test
    void testIngredientUnitChange() {
        Ingredient ingredient = new Ingredient("Salt", 5.0, "g");
        ingredient.setUnit("teaspoon");

        assertEquals("teaspoon", ingredient.getUnit(),
                "The unit property failed to set.");
    }
}