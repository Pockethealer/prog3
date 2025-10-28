package nhf.logic;

import nhf.model.Ingredient;
import nhf.model.Recipe;
import nhf.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A usermanager osztály tesztjei
 */
class UserManagerTest {

    private UserManager userManager;

    @TempDir
    File tempDir;

    private Recipe recipe1;
    private Recipe recipe2;

    @BeforeEach
    void setUp() {
        Ingredient ingredient = new Ingredient("Víz", 100, "ml");
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredient);
        recipe1 = new Recipe("Teszt Kávé", 5, 1, "asdf", ingredients);
        recipe2 = new Recipe("Teszt Tea", 3, 1, "1223", new ArrayList<>());
        userManager = new UserManager(new File(tempDir, "temp.json"));
    }

    @Test
    void testRegisterAndLoginSuccess() {
        String username = "Test";
        assertTrue(userManager.registerUser(username), "Failed to register user");
        assertTrue(userManager.loginUser(username), "failed to set current user");
        Optional<User> currentUser = userManager.getCurrentlyLoggedInUser();
        assertTrue(currentUser.isPresent(), "There should be a current user.");
        assertEquals(username, currentUser.get().getUsername(), "The current users name is wrong");
    }

    @Test
    void testDuplicateRegistrationFails() {
        String username = "Test2";
        userManager.registerUser(username);
        assertFalse(userManager.registerUser(username), "Two registration with the same name is imposibble");
    }

    @Test
    void testLoginNonExistentUserFails() {
        assertFalse(userManager.loginUser("123"), "Non existent user shouldnt be able to log in");
        assertTrue(userManager.getCurrentlyLoggedInUser().isEmpty(), "No current user since the login was invalid");
    }

    @Test
    void testAddFavoriteRecipe() {
        String username = "TestElek";
        userManager.registerUser(username);
        userManager.loginUser(username);
        User user = userManager.getCurrentlyLoggedInUser().get();
        user.addFavoriteRecipe(recipe1);
        user.addFavoriteRecipe(recipe1);

        assertEquals(1, user.getFavoriteRecipes().size(),
                "There should only be one recipe after adding the same recipe twice");
        assertTrue(user.getFavoriteRecipes().contains(recipe1),
                "The added recipe should be in the list of favorites");
    }

    @Test
    void testWeeklyMenuManagement() {
        String username = "Test";
        userManager.registerUser(username);
        userManager.loginUser(username);
        User user = userManager.getCurrentlyLoggedInUser().get();
        String day = "Tuesday";
        user.addRecipeToDay(day, recipe1);
        user.addRecipeToDay(day, recipe2);

        Map<String, List<Recipe>> menu = user.getWeeklyMenu();
        assertTrue(menu.containsKey(day), "The day should be in the map keyset");
        assertEquals(2, menu.get(day).size(), "There should be two recipes on the day");
        assertTrue(menu.get(day).contains(recipe2), "The added recipe should be in the list of the given day");
        user.clearDay(day);
        assertEquals(0, menu.get(day).size(), "The list is not empty after clearing the day");
    }

}
