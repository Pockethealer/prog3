package nhf.gui;

import nhf.model.Ingredient;
import nhf.model.Unit;
import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.Locale;

public class AddIngredientDialog extends JDialog {

    private Ingredient ingredient;
    private JTextField nameField;
    private JFormattedTextField quantityField;
    private JComboBox<Unit> unitComboBox;

    public AddIngredientDialog(JFrame parent) {
        super(parent, "Add Ingredient", true);
        setupGUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void setupGUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        /*
         * A recptekhez hasonlóan a mezők megfelelő értékeinek kikényszerítése, és
         * locale beállítás hogy vesszőt használjon, ne pontot
         */
        NumberFormat doubleFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("hu-HU"));
        doubleFormat.setMinimumFractionDigits(1);
        doubleFormat.setGroupingUsed(false);
        NumberFormatter formatter = new NumberFormatter(doubleFormat);
        formatter.setValueClass(Double.class);
        formatter.setAllowsInvalid(false);
        formatter.setMinimum(0.0);
        quantityField = new JFormattedTextField(formatter);
        quantityField.setValue(1.0);
        /* enum felsorolás létrehozása az egységes mértékegységekért */
        unitComboBox = new JComboBox<>(Unit.values());
        nameField = new JTextField(15);

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel("Unit:"));
        panel.add(unitComboBox);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(this::addIngredient);

        add(panel, BorderLayout.CENTER);
        add(addButton, BorderLayout.SOUTH);
    }

    private void addIngredient(ActionEvent event) {
        try {
            String name = nameField.getText().trim();
            Double quantity = (Double) quantityField.getValue();
            Unit unit = (Unit) unitComboBox.getSelectedItem();
            if (name.isEmpty() || quantity == null || quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Please fill out all the fields!", "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            this.ingredient = new Ingredient(name, quantity, unit.toString());
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Something went wrong: " + e.getMessage(), "Hiba",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public Ingredient getIngredient() {
        return ingredient;
    }
}