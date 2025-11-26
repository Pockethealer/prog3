package nhf.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/* Ez a fő recepteket kezelő class, amiben megtalálhatóak privát változóként a recept tulajdonságai */
public class Recipe {

    private String name;
    private int preparationTime;
    private int servings;
    private String instructions;
    private List<RecipeIngredient> ingredients;
    private List<String> tags;

    /* default constructor a deszerializáláshoz */
    public Recipe() {
        this.ingredients = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    /**
     * Új receptek konstruktora
     * 
     * @param name            A recept neve
     * @param preparationTime A recept elkészítési ideje percben
     * @param servings        Hány adagra elég
     * @param instructions    Elkészítés leírása
     * @param ingredients     Lista az összetevőkről
     */
    public Recipe(String name, int preparationTime, int servings, String instructions,
            List<RecipeIngredient> ingredients, List<String> tags) {
        this.name = name;
        this.preparationTime = preparationTime;
        this.servings = servings;
        this.instructions = instructions;
        this.ingredients = new ArrayList<>(ingredients);
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    // Getterek

    public String getName() {
        return name;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public int getServings() {
        return servings;
    }

    public String getInstructions() {
        return instructions;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    // Setterek

    public void setName(String name) {
        this.name = name;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    /* Összetevőt hozzáadó fv. */
    public void addIngredient(RecipeIngredient ingredient) {
        this.ingredients.add(ingredient);
    }

    public double getTotalCalories() {
        return this.ingredients.stream()
                .mapToDouble(RecipeIngredient::getTotalCalories)
                .sum();
    }

    public double getCaloriesPerServing() {
        double totalCalories = getTotalCalories();
        if (servings <= 0)
            return 0.0;
        return totalCalories / servings;
    }

    // Ez a kettő csak azért kell mert később szerializálás során külön objektum
    // referenciák keletkeznek ugyanahoz a recepthez és a helyes logikához kell hogy
    // akkor is megtalálják őket a metódusok
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true; // Ha ugyanaz a referencia
        if (o == null || getClass() != o.getClass())
            return false;
        Recipe recipe = (Recipe) o;
        return name.equalsIgnoreCase(recipe.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }

    @Override
    public String toString() {
        return this.name;
    }

    public List<String> getTags() {
        return tags != null ? tags : new ArrayList<>();
    }

    public void setTags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }
}