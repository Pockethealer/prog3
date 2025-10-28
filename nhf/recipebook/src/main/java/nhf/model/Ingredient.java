package nhf.model;

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
}