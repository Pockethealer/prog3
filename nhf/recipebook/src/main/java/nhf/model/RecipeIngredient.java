package nhf.model;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RecipeIngredient {

    private String templateName;
    private double quantity;
    private Unit unitType;
    // szerializálás miatt egyszerűbb így
    private IngredientTemplate template;

    /* default constructor a deszerializáláshoz */
    public RecipeIngredient() {
    }

    public RecipeIngredient(String templateName, double quantity, Unit unitType) {
        this.templateName = templateName;
        this.quantity = quantity;
        this.unitType = unitType;
    }

    public String getName() {
        return templateName;
    }

    public void setName(String templateName) {
        this.templateName = templateName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Unit getUnit() {
        return unitType;
    }

    public void setUnit(Unit unitType) {
        this.unitType = unitType;
    }

    public void setTemplate(IngredientTemplate template) {
        this.template = template;
    }

    /*
     * segédfüggvények kalória számoláshoz, jsonignore csak azért kell hogy szebb
     * legyen a keletkező json, mert ezek nem kellenek bele
     */
    @JsonIgnore
    public double getTotalCalories() {
        if (template == null || template.getCaloriesPer100g() <= 0) {
            return 0.0;
        }
        String unitName = this.unitType.name();
        Double factor = template.getUnitToGramFactors().get(unitName);
        if (factor == null)
            return 0.0;
        double totalGrams = this.quantity * factor;
        double calorieFactor = totalGrams / 100.0;
        return template.getCaloriesPer100g() * calorieFactor;
    }

    @JsonIgnore
    public Map<String, Double> getNutritionalValues() {
        if (template == null || template.getCaloriesPer100g() < 0) {
            return Collections.emptyMap();
        }
        String unitName = this.unitType.name();
        Double factorPerUnit = template.getUnitToGramFactors().get(unitName);
        if (factorPerUnit == null) {
            return Collections.emptyMap();
        }
        double totalGrams = this.quantity * factorPerUnit;
        double nutrientFactor = totalGrams / 100.0;
        return Map.of(
                "calories", template.getCaloriesPer100g() * nutrientFactor,
                "protein", template.getProteinPer100g() * nutrientFactor,
                "fat", template.getFatPer100g() * nutrientFactor,
                "carb", template.getCarbPer100g() * nutrientFactor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RecipeIngredient that = (RecipeIngredient) o;
        return Objects.equals(templateName.toLowerCase(), that.templateName.toLowerCase()) && unitType == that.unitType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(templateName.toLowerCase(), unitType);
    }

    @Override
    public String toString() {
        return templateName + " (" + quantity + " " + unitType.toString() + ")";
    }

    @JsonIgnore
    public Map<String, Object> getMetricConversion() {
        return this.unitType.convertToMetric(this.quantity);
    }
}