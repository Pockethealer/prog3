package nhf.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Tárolja az összetevők standard tápanyagértékeit 100 grammonként,
 * valamint a sűrűségfaktort a térfogat/tömeg konverzióhoz.
 */
public class IngredientTemplate {

    private String name;
    private double caloriesPer100g = 0.0;
    private double proteinPer100g = 0.0;
    private double fatPer100g = 0.0;
    private double carbPer100g = 0.0;
    private Map<String, Double> unitToGramFactors;

    /* Konstruktorok */

    public IngredientTemplate() {
        this.unitToGramFactors = new HashMap<>();
    }

    public IngredientTemplate(String name, double caloriesPer100g, double proteinPer100g,
            double fatPer100g, double carbPer100g, Map<String, Double> unitToGramFactors) {
        this.name = name;
        this.caloriesPer100g = caloriesPer100g;
        this.proteinPer100g = proteinPer100g;
        this.fatPer100g = fatPer100g;
        this.carbPer100g = carbPer100g;
        this.unitToGramFactors = unitToGramFactors != null ? unitToGramFactors : new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCaloriesPer100g() {
        return caloriesPer100g;
    }

    public void setCaloriesPer100g(double caloriesPer100g) {
        this.caloriesPer100g = caloriesPer100g;
    }

    public double getProteinPer100g() {
        return proteinPer100g;
    }

    public void setProteinPer100g(double proteinPer100g) {
        this.proteinPer100g = proteinPer100g;
    }

    public double getFatPer100g() {
        return fatPer100g;
    }

    public void setFatPer100g(double fatPer100g) {
        this.fatPer100g = fatPer100g;
    }

    public double getCarbPer100g() {
        return carbPer100g;
    }

    public void setCarbPer100g(double carbPer100g) {
        this.carbPer100g = carbPer100g;
    }

    public Map<String, Double> getUnitToGramFactors() {
        if (this.unitToGramFactors == null) {
            this.unitToGramFactors = new HashMap<>();
        }
        return unitToGramFactors;
    }

    public void setUnitToGramFactors(Map<String, Double> unitToGramFactors) {
        this.unitToGramFactors = unitToGramFactors;
    }

    public Set<String> getAllUnitsOfIngredient() {
        return unitToGramFactors.keySet();
    }

    /* az összehasonlítás felülírása */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        IngredientTemplate that = (IngredientTemplate) o;
        return Objects.equals(name.toLowerCase(), that.name.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }

    @Override
    public String toString() {
        return name;
    }
}
