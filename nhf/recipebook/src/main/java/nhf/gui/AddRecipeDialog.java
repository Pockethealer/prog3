package nhf.gui;

import nhf.logic.RecipeBook;
import nhf.model.RecipeIngredient;
import nhf.model.IngredientTemplate;
import nhf.model.Recipe;
import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddRecipeDialog extends JDialog {
    private static final int DEFAULT_SERVING_SIZE = 4;
    private static final int DEFAULT_PREP_TIME = 90;

    private final RecipeBook recipeBook;
    private final RecipeBookGUI parent;
    private final Recipe originalRecipe;
    private final List<RecipeIngredient> currentIngredients = new ArrayList<>();

    // UI elemek
    private JTextField nameField;
    private JFormattedTextField timeField;
    private JFormattedTextField servingField;
    private JTextField tagsField;
    private JTextArea instructionsArea;
    private JList<RecipeIngredient> ingredientList;
    private DefaultListModel<RecipeIngredient> listModel;

    // megfelelő gombok(szerkesztés vagy létrehozás nézet)
    private JButton removeIngredientButton;
    private JButton saveButton;
    private JButton deleteRecipeButton;
    private final Map<String, IngredientTemplate> templateMap;

    public AddRecipeDialog(RecipeBookGUI parent, RecipeBook recipeBook, Recipe recipeToEdit,
            Map<String, IngredientTemplate> templateMap) {
        super(parent, "Add new recipe", true);
        this.recipeBook = recipeBook;
        this.parent = parent;
        this.originalRecipe = recipeToEdit;
        this.templateMap = templateMap;
        setTitle(recipeToEdit == null ? "Add new recipe" : "Edit recipe: " + recipeToEdit.getName());
        setupGUI();
        if (recipeToEdit != null) {
            loadRecipeData(recipeToEdit);
        }
        pack();
        setLocationRelativeTo(parent);
    }

    private void setupGUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(20);
        tagsField = new JTextField(20);

        instructionsArea = new JTextArea(5, 20);
        // kikényszerítem már gui szinten a helyes bevitelt, hogy később ne legyen gond
        // az integerré alakítással, így létrehozok egy formattert és utána egy
        // jformattedtextfield-nek továbbadva létrehozom a mezőt
        NumberFormatter formatter = new NumberFormatter(NumberFormat.getIntegerInstance());
        formatter.setValueClass(Integer.class);
        formatter.setAllowsInvalid(false);
        formatter.setMinimum(0);
        timeField = new JFormattedTextField(formatter);
        servingField = new JFormattedTextField(formatter);
        // Alapértelmezett értékek
        timeField.setValue(DEFAULT_PREP_TIME);
        servingField.setValue(DEFAULT_SERVING_SIZE);

        // Név
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(new JLabel("Name of the recipe:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        infoPanel.add(nameField, gbc);

        // Idő
        gbc.gridx = 0;
        gbc.gridy = 1;
        infoPanel.add(new JLabel("Preparation time:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        infoPanel.add(timeField, gbc);

        // Adag
        gbc.gridx = 0;
        gbc.gridy = 2;
        infoPanel.add(new JLabel("Number of servings:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        infoPanel.add(servingField, gbc);

        // tagek
        gbc.gridx = 0;
        gbc.gridy = 3;
        infoPanel.add(new JLabel("Tags:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        infoPanel.add(new JScrollPane(tagsField), gbc);

        // Elkészítés
        gbc.gridx = 0;
        gbc.gridy = 4;
        infoPanel.add(new JLabel("Instructions:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        infoPanel.add(new JScrollPane(instructionsArea), gbc);

        add(infoPanel, BorderLayout.NORTH);

        // összetevők panel
        JPanel ingredientPanel = new JPanel(new BorderLayout());
        listModel = new DefaultListModel<>();
        ingredientList = new JList<>(listModel);
        JPanel ingredientButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton addIngredientButton = new JButton("Add ingredient");
        addIngredientButton.addActionListener(e -> showAddIngredientDialog());
        removeIngredientButton = new JButton("Delete ingredient");
        removeIngredientButton.addActionListener(e -> removeSelectedIngredient());
        updateIngredientButtons();
        ingredientButtonPanel.add(addIngredientButton);
        ingredientButtonPanel.add(removeIngredientButton);
        ingredientPanel.add(new JLabel("Ingredients:"), BorderLayout.NORTH);
        ingredientPanel.add(new JScrollPane(ingredientList), BorderLayout.CENTER);
        ingredientPanel.add(ingredientButtonPanel, BorderLayout.SOUTH);

        add(ingredientPanel, BorderLayout.CENTER);

        // mentés törlés gomb panel
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton(originalRecipe == null ? "Recept Mentése" : "Módosítások Mentése");
        saveButton.addActionListener(e -> saveRecipe());
        deleteRecipeButton = new JButton("Recept Törlése");
        deleteRecipeButton.addActionListener(this::deleteRecipe);
        bottomButtonPanel.add(saveButton);
        if (originalRecipe != null) {
            bottomButtonPanel.add(deleteRecipeButton);
        }
        add(bottomButtonPanel, BorderLayout.SOUTH);
    }

    private void updateIngredientButtons() {
        boolean listHasElements = !listModel.isEmpty();
        removeIngredientButton.setEnabled(listHasElements);
    }

    private void loadRecipeData(Recipe recipe) {
        nameField.setText(recipe.getName());
        timeField.setValue(recipe.getPreparationTime());
        servingField.setValue(recipe.getServings());
        instructionsArea.setText(recipe.getInstructions());
        currentIngredients.addAll(recipe.getIngredients());
        currentIngredients.forEach(ing -> listModel.addElement(ing));
        updateIngredientButtons();
        if (!recipe.getTags().isEmpty()) {
            tagsField.setText(String.join(", ", recipe.getTags()));

        }
    }

    /* az add ingredient dialog hívása */
    private void showAddIngredientDialog() {
        AddIngredientDialog dialog = new AddIngredientDialog(parent, templateMap);
        dialog.setVisible(true);

        RecipeIngredient newIngredient = dialog.getRecipeIngredient();
        if (newIngredient != null) {
            currentIngredients.add(newIngredient);
            listModel.addElement(newIngredient);
            updateIngredientButtons();
        }
    }

    /**
     * Törli a kijelölt összetevőt a receptből
     */
    private void removeSelectedIngredient() {
        int selectedIndex = ingredientList.getSelectedIndex();

        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Select an ingredient to proceed!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentIngredients.remove(selectedIndex);
        listModel.remove(selectedIndex);
        updateIngredientButtons();
        JOptionPane.showMessageDialog(this, "Ingredient successfully removed from recipe!",
                "Success", JOptionPane.INFORMATION_MESSAGE);

    }

    /* Recept mentése a dialog-ról */
    private void saveRecipe() {
        try {
            // Adatok ellenőrzése és kinyerése
            String name = nameField.getText().trim();
            int time = (Integer) timeField.getValue();
            int servings = (Integer) servingField.getValue();
            String tagsInput = tagsField.getText().trim();
            List<String> newTags = tagsInput.isEmpty() ? new ArrayList<>()
                    : Arrays.stream(tagsInput.split(","))
                            .map(String::trim)
                            .filter(tag -> !tag.isEmpty())
                            .collect(Collectors.toList());
            String instructions = instructionsArea.getText().trim();

            if (name.isEmpty() || currentIngredients.isEmpty()) {
                JOptionPane.showMessageDialog(this, "You need to name the recipe and give it one ingredient", "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            // névellenőrzés, ha új recept, vagy ha nem ugyanaz a neve mint a réginek
            if (recipeBook.recipeNameExists(name)
                    && (originalRecipe == null || !name.equalsIgnoreCase(originalRecipe.getName()))) {
                JOptionPane.showMessageDialog(
                        this, "Error, the'" + name + "' name already exits. Choose an other name!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            Recipe newRecipe = new Recipe(name, time, servings, instructions, currentIngredients, newTags);
            if (originalRecipe == null) {
                recipeBook.addRecipe(newRecipe);
            } else {
                recipeBook.updateRecipe(originalRecipe, newRecipe);
            }
            parent.refreshRecipeTable(recipeBook.getRecipes());
            JOptionPane.showMessageDialog(this, "Recipe successfully saved", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Something went wrong" + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRecipe(ActionEvent event) {
        if (originalRecipe == null) {
            return;
        }
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete:'" + originalRecipe.getName(),
                "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                recipeBook.removeRecipe(originalRecipe);
                parent.refreshRecipeTable(recipeBook.getRecipes());
                JOptionPane.showMessageDialog(this, "'" + originalRecipe.getName() + "' successfully deleted",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Something went wrong with the deletion: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
