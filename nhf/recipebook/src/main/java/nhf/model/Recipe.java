package nhf.model;

import java.util.ArrayList;
import java.util.List;

/* Ez a fő recepteket kezelő class, amiben megtalálhatóak privát változóként a recept tulajdonságai */
public class Recipe {

    private String name;
    private int preparationTime;
    private int servings;
    private String instructions;
    private List<Ingredient> ingredients;

    /* default constructor a deszerializáláshoz */
    public Recipe() {
        this.ingredients = new ArrayList<>();
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
            List<Ingredient> ingredients) {
        this.name = name;
        this.preparationTime = preparationTime;
        this.servings = servings;
        this.instructions = instructions;
        this.ingredients = new ArrayList<>(ingredients);
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

    public List<Ingredient> getIngredients() {
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

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    /* Összetevőt hozzáadó fv. */
    public void addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
    }
}