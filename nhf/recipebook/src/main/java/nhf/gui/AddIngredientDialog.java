package nhf.gui;

import nhf.model.IngredientTemplate;
import nhf.model.RecipeIngredient;
import nhf.model.Unit;
import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class AddIngredientDialog extends JDialog {
    private RecipeIngredient recipeIngredient;
    private final Map<String, IngredientTemplate> templateMap;
    private JComboBox<String> templateComboBox;
    private JComboBox<String> unitComboBox;
    private JFormattedTextField quantityField;

    public AddIngredientDialog(JFrame parent, Map<String, IngredientTemplate> templateMap) {
        super(parent, "Add Ingredient", true);
        this.templateMap = templateMap;
        setupGUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void setupGUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        // formázó a hibák elkerülésére
        NumberFormat doubleFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("hu-HU"));
        doubleFormat.setMinimumFractionDigits(1);
        doubleFormat.setGroupingUsed(false);
        NumberFormatter formatter = new NumberFormatter(doubleFormat);
        formatter.setValueClass(Double.class);
        formatter.setAllowsInvalid(false);
        formatter.setMinimum(0.0);

        String[] templateNames = templateMap.keySet().toArray(new String[0]);
        templateComboBox = new JComboBox<>(templateNames);
        templateComboBox.addActionListener(e -> updateUnitComboBox());

        panel.add(new JLabel("1. Ingredient:"));
        panel.add(templateComboBox);
        unitComboBox = new JComboBox<>();
        panel.add(new JLabel("2. Unit:"));
        panel.add(unitComboBox);
        quantityField = new JFormattedTextField(formatter);
        quantityField.setValue(1.0);
        panel.add(new JLabel("3. Quantity:"));
        panel.add(quantityField);
        JButton addButton = new JButton("Add Ingredient");
        addButton.addActionListener(this::addIngredient);
        updateUnitComboBox();
        add(panel, BorderLayout.CENTER);
        add(addButton, BorderLayout.SOUTH);
    }

    private void updateUnitComboBox() {
        String selectedTemplateName = (String) templateComboBox.getSelectedItem();
        unitComboBox.removeAllItems();

        if (selectedTemplateName == null) {
            unitComboBox.setEnabled(false);
            return;
        }
        IngredientTemplate selectedTemplate = templateMap.get(selectedTemplateName);
        if (selectedTemplate != null) {
            Set<String> validUnits = selectedTemplate.getAllUnitsOfIngredient();
            for (String unitName : validUnits) {
                unitComboBox.addItem(unitName);
            }
            unitComboBox.setEnabled(true);
        }
    }

    private void addIngredient(ActionEvent event) {
        try {
            String templateName = (String) templateComboBox.getSelectedItem();
            String unitString = (String) unitComboBox.getSelectedItem();
            Double quantity = (Double) quantityField.getValue();
            if (templateName == null || unitString == null || quantity == null || quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Please select template unit, and quantity!", "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            Unit unitType = Unit.valueOf(unitString.toUpperCase());
            this.recipeIngredient = new RecipeIngredient(templateName, quantity, unitType);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Something went wrong: " + e.getMessage(), "Hiba",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public RecipeIngredient getRecipeIngredient() {
        return recipeIngredient;
    }
}