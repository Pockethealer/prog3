package nhf.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a simplified user, storing their name, favorites (List),
 * and weekly menu (Map) directly.
 */
public class User {

    private String username;
    private List<Recipe> favoriteRecipes;

    // A heti menü: String (nap neve) -> List<Recipe>
    private Map<String, List<Recipe>> weeklyMenu;

    // 💡 Szükséges a Jackson deszerializációhoz!
    public User() {
        this.favoriteRecipes = new ArrayList<>();
        this.weeklyMenu = initializeWeeklyMenu();
    }

    public User(String username) {
        this.username = username;
        this.favoriteRecipes = new ArrayList<>();
        this.weeklyMenu = initializeWeeklyMenu();
    }

    private Map<String, List<Recipe>> initializeWeeklyMenu() {
        Map<String, List<Recipe>> menu = new HashMap<>();
        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
        for (String day : days) {
            menu.put(day, new ArrayList<>());
        }
        return menu;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Recipe> getFavoriteRecipes() {
        return favoriteRecipes;
    }

    public void setFavoriteRecipes(List<Recipe> favoriteRecipes) {
        this.favoriteRecipes = (favoriteRecipes != null) ? favoriteRecipes : new ArrayList<>();
    }

    public Map<String, List<Recipe>> getWeeklyMenu() {
        return weeklyMenu;
    }

    public void setWeeklyMenu(Map<String, List<Recipe>> weeklyMenu) {
        this.weeklyMenu = (weeklyMenu != null) ? weeklyMenu : initializeWeeklyMenu();
    }

    public void addFavoriteRecipe(Recipe recipe) {
        if (recipe != null && !favoriteRecipes.contains(recipe)) {
            favoriteRecipes.add(recipe);
        }
    }

    public void addRecipeToDay(String day, Recipe recipe) {
        if (weeklyMenu.containsKey(day) && recipe != null) {
            weeklyMenu.get(day).add(recipe);
        }
    }

    public void clearDay(String day) {
        if (weeklyMenu.containsKey(day)) {
            weeklyMenu.get(day).clear();
        }
    }
}