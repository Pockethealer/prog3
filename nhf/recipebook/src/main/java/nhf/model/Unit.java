
package nhf.model;

import java.util.Map;

public enum Unit {
    G("g", "g", 1.0),
    KG("kg", "g", 1000.0),
    ML("ml", "ml", 1.0),
    L("l", "ml", 1000.0),
    PIECES("pc", "pc", 1.0),
    PINCH("pinch", "g", 3.0),
    TEASPOON("ts", "ml", 5.0),
    TABLESPOON("tbs", "ml", 15.0),
    CUP("cup", "ml", 240.0),
    FLUID_OUNCE("fl oz", "ml", 29.57),
    OUNCE("oz", "g", 28.35),
    POUND("lb", "g", 453.59);

    private final String displayValue;
    private final String metricUnit;
    private final double conversionFactor;

    Unit(String displayValue, String metricUnit, double conversionFactor) {
        this.displayValue = displayValue;
        this.metricUnit = metricUnit;
        this.conversionFactor = conversionFactor;
    }

    @Override
    public String toString() {
        return displayValue;
    }

    /* bevásárló listához */
    public Map<String, Object> convertToMetric(double quantity) {
        if (metricUnit.equals("g") || metricUnit.equals("ml")) {
            double convertedQuantity = quantity * conversionFactor;
            return Map.of("amount", convertedQuantity, "unit", metricUnit);
        } else {
            return Map.of("amount", quantity, "unit", metricUnit);
        }
    }

    public static Unit getUnitFromDisplayValue(String displayValue) {
        String search = displayValue.trim().toLowerCase();
        for (Unit unit : Unit.values()) {
            if (unit.displayValue.equalsIgnoreCase(search)) {
                return unit;
            }
        }
        // default gram
        return Unit.G;
    }

    public static Unit[] getAllUnits() {
        return Unit.values();
    }
}