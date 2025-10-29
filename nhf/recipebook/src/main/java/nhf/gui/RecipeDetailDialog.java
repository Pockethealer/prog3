package nhf.gui;

import nhf.logic.RecipeBook;
import nhf.model.Recipe;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class RecipeDetailDialog extends JDialog {

    private final RecipeBookGUI parentGUI;
    private final RecipeBook recipeBook;
    private Recipe currentRecipe;

    // UI komponensek
    private JTextField nameField, timeField, servingField;
    private JTextArea instructionsArea;
    private JList<String> ingredientList;
    private JButton saveButton, deleteButton;

    /**
     * Konstruktor a kiválasztott recepttel.
     */
    public RecipeDetailDialog(RecipeBookGUI parentGUI, RecipeBook recipeBook, Recipe recipe) {
        super(parentGUI, "Recipe details: " + recipe.getName(), true);
        this.parentGUI = parentGUI;
        this.recipeBook = recipeBook;
        this.currentRecipe = recipe;

        setupGUI();
        loadRecipeData(recipe);

        pack();
        setLocationRelativeTo(parentGUI);
        setVisible(true);
    }

    private void setupGUI() {
        setLayout(new BorderLayout(10, 10));

        // --- Információk panel (GridBagLayout) ---
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mezők
        nameField = new JTextField(20);
        timeField = new JTextField(10); // Ide kell a JFormattedTextField, ha befejezed!
        servingField = new JTextField(10);
        instructionsArea = new JTextArea(5, 20);

        // Címkék és Mezők elhelyezése...
        // ... (név, idő, adag, útmutató címkéinek elhelyezése, mint az
        // AddRecipeDialogban) ...

        add(infoPanel, BorderLayout.NORTH);

        // --- Összetevők megjelenítése ---
        ingredientList = new JList<>();
        add(new JScrollPane(ingredientList), BorderLayout.CENTER);

        // --- Gombok panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        saveButton = new JButton("Recept Módosítása (Mentés)");
        saveButton.addActionListener(this::saveModifiedRecipe);

        deleteButton = new JButton("Recept Törlése");
        deleteButton.addActionListener(this::deleteRecipe);

        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Betölti a megadott recept adatait a UI mezőkbe.
     */
    private void loadRecipeData(Recipe recipe) {
        nameField.setText(recipe.getName());
        // Mivel a time/serving mezők JTextField-ek (vagy FormattedTextField-ek)
        timeField.setText(String.valueOf(recipe.getPreparationTime()));
        servingField.setText(String.valueOf(recipe.getServings()));
        instructionsArea.setText(recipe.getInstructions());

        // Összetevők listázása a JList-be
        List<String> ingredientStrings = recipe.getIngredients().stream()
                .map(ing -> ing.getName() + " (" + ing.getQuantity() + " " + ing.getUnit() + ")")
                .toList();
        ingredientList.setListData(ingredientStrings.toArray(new String[0]));
    }

    /**
     * Kezeli a recept törlését.
     */
    private void deleteRecipe(ActionEvent event) {
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Biztosan törölni szeretnéd a '" + currentRecipe.getName() + "' receptet?",
                "Törlés Megerősítés", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            recipeBook.removeRecipe(currentRecipe); // Logika hívása
            parentGUI.refreshRecipeTable(recipeBook.getRecipes()); // GUI frissítése
            dispose(); // Ablak bezárása
        }
    }

    /**
     * Kezeli a módosított adatok mentését.
     */
    private void saveModifiedRecipe(ActionEvent event) {
        try {
            // 1. ÚJ ADATOK KIOLVASÁSA (ITT VÉGEZD EL A VALIDEÁCIÓT!)
            String newName = nameField.getText().trim();
            int newTime = Integer.parseInt(timeField.getText().trim()); // Ha JFormattedTextField, más a kinyerés!
            int newServings = Integer.parseInt(servingField.getText().trim());
            String newInstructions = instructionsArea.getText().trim();

            // 2. ÚJ RECIPE OBJEKTUM LÉTREHOZÁSA (az eredeti összetevőlistával)
            Recipe modifiedRecipe = new Recipe(newName, newTime, newServings, newInstructions,
                    currentRecipe.getIngredients());

            // 3. LOGIKA HÍVÁSA: Módosítás (Ezt a metódust implementálnod kell a
            // RecipeBook-ban!)
            recipeBook.updateRecipe(currentRecipe, modifiedRecipe);

            // 4. GUI FRISSÍTÉSE
            parentGUI.refreshRecipeTable(recipeBook.getRecipes());

            JOptionPane.showMessageDialog(this, "Recept sikeresen módosítva!", "Siker",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Érvénytelen számformátum az időben/adagban.", "Hiba",
                    JOptionPane.ERROR_MESSAGE);
        }
        // ... itt kezelheted a Duplikált Név hibát is, amit a RecipeBook dob ...
    }
}