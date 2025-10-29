package nhf.model;

import java.util.Objects;

/* Az összetevőket reprezentáló class */
public class Ingredient {

    private String name;
    private double quantity;
    private String unit;

    /* default constructor a deszerializáláshoz */
    public Ingredient() {
    }

    /**
     * Full constructor for an Ingredient.
     * 
     * @param name     The name of the ingredient (e.g., "Sugar").
     * @param quantity The amount of the ingredient.
     * @param unit     The unit of measurement (e.g., "g").
     */
    public Ingredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    // --- Getters ---

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    // --- Setters ---

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return name + " (" + quantity + " " + unit + ")";
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
        Ingredient ing = (Ingredient) o;
        return name.equalsIgnoreCase(ing.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }
}