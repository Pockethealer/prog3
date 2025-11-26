package nhf.gui;

import nhf.logic.RecipeBook;
import nhf.model.IngredientTemplate;
import nhf.model.Unit;
import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class AddTemplateDialog extends JDialog {

    private final RecipeBook recipeBook;
    private JTextField nameField;
    private JFormattedTextField caloriesField, proteinField, fatField, carbField;
    private JFormattedTextField densityField;
    private JPanel factorsPanel;
    private Map<String, JFormattedTextField> unitFactorFields;

    public AddTemplateDialog(JFrame parent, RecipeBook recipeBook) {
        super(parent, "Add New Ingredient Template", true);
        this.recipeBook = recipeBook;
        this.unitFactorFields = new HashMap<>();

        setupGUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void setupGUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel nutrientPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        NumberFormat doubleFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("hu-HU"));
        doubleFormat.setGroupingUsed(false);
        NumberFormatter formatter = new NumberFormatter(doubleFormat);
        formatter.setValueClass(Double.class);
        formatter.setAllowsInvalid(false);
        formatter.setMinimum(0.0);
        nameField = new JTextField(20);
        caloriesField = new JFormattedTextField(formatter);
        caloriesField.setValue(0.0);
        proteinField = new JFormattedTextField(formatter);
        proteinField.setValue(0.0);
        fatField = new JFormattedTextField(formatter);
        fatField.setValue(0.0);
        carbField = new JFormattedTextField(formatter);
        carbField.setValue(0.0);
        densityField = new JFormattedTextField(formatter);
        densityField.setValue(1.0);
        nutrientPanel.add(new JLabel("Name:"));
        nutrientPanel.add(nameField);
        nutrientPanel.add(new JLabel("Kcal (per 100g):"));
        nutrientPanel.add(caloriesField);
        nutrientPanel.add(new JLabel("Protein (per 100g):"));
        nutrientPanel.add(proteinField);
        nutrientPanel.add(new JLabel("Fat (per 100g):"));
        nutrientPanel.add(fatField);
        nutrientPanel.add(new JLabel("Carbs (per 100g):"));
        nutrientPanel.add(carbField);

        factorsPanel = setupUnitFactorsPanel(formatter);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(nutrientPanel, BorderLayout.NORTH);
        centerPanel.add(factorsPanel, BorderLayout.CENTER);

        JButton addButton = new JButton("Add Template");
        addButton.addActionListener(this::addTemplate);

        add(centerPanel, BorderLayout.CENTER);
        add(addButton, BorderLayout.SOUTH);
    }

    private JPanel setupUnitFactorsPanel(NumberFormatter formatter) {
        JPanel panel = new JPanel(new GridLayout(0, 4, 5, 5)); // 0 sor, 4 oszlop (Unit neve, input mező)
        panel.setBorder(BorderFactory.createTitledBorder("Unit to Gram Factors"));
        for (Unit unit : Unit.getAllUnits()) {
            String unitName = unit.name();
            JFormattedTextField field = new JFormattedTextField(formatter);
            field.setValue(0.0);
            panel.add(new JLabel(unitName + " (g):"));
            panel.add(field);
            unitFactorFields.put(unitName, field);
        }
        return panel;
    }

    private void addTemplate(ActionEvent event) {
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Template name cannot be empty.");
            }
            if (recipeBook.getIngredientTemplates().containsKey(name.toLowerCase())) {
                throw new IllegalArgumentException("Template '" + name + "' already exists.");
            }
            double calories = ((Number) caloriesField.getValue()).doubleValue();
            double protein = ((Number) proteinField.getValue()).doubleValue();
            double fat = ((Number) fatField.getValue()).doubleValue();
            double carb = ((Number) carbField.getValue()).doubleValue();
            Map<String, Double> factors = new HashMap<>();

            for (Map.Entry<String, JFormattedTextField> entry : unitFactorFields.entrySet()) {
                double value = ((Number) entry.getValue().getValue()).doubleValue();
                if (value > 0.0) {
                    factors.put(entry.getKey(), value);
                }
            }

            IngredientTemplate newTemplate = new IngredientTemplate(name, calories, protein, fat, carb, factors);
            recipeBook.addTemplate(newTemplate);
            JOptionPane.showMessageDialog(this, "Template added successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "An unexpected error occurred while saving the template: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}