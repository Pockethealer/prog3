
package nhf.model;

/* TODO mértékegység konvertáló metrikusra */
public enum Unit {
    G("g"),
    KG("kg"),
    ML("ml"),
    L("l"),
    PIECES("pc"),
    PINCH("pinch"),
    TEASPOON("ts"),
    TABLESPOON("tbs");

    private final String displayValue;

    Unit(String displayValue) {
        this.displayValue = displayValue;
    }

    @Override
    public String toString() {
        return displayValue;
    }
}