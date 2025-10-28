package nhf.gui;

import nhf.logic.RecipeBook;
import nhf.logic.UserManager;
import nhf.model.Recipe;

import javax.swing.*;
import java.awt.*;

public class RecipeBookGUI extends JFrame {

    private final RecipeBook recipeBook;
    private final UserManager userManager;

    // GUI Komponensek
    private JTable recipeTable;
    private RecipeTableModel tableModel;

    /**
     * Konstruktor beállításokkal
     */
    public RecipeBookGUI(RecipeBook recipeBook, UserManager userManager) {
        this.recipeBook = recipeBook;
        this.userManager = userManager;
        setTitle("Recipe Book");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);

        setupMenuBar();
        setupRecipeTablePanel();
        setLocationRelativeTo(null);
        boolean loginSuccessful = selectUser();

        if (loginSuccessful) {
            setVisible(true);
        } else {
            System.exit(0);
        }
    }

    /**
     * Létrehozza a fő menüsort
     */
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        /*
         * A jövőben ha hozzáadok speciális importot illetve szerver side
         * szinkronizálást
         * JMenu fileMenu = new JMenu("File");
         * JMenuItem importItem = new JMenuItem("Import Recipes (JSON)");
         * JMenuItem exportItem = new JMenuItem("Export Recipes (JSON)");
         * fileMenu.add(importItem);
         * fileMenu.add(exportItem);
         */

        JMenu userMenu = new JMenu("User");
        JMenuItem loginItem = new JMenuItem("Select user");
        loginItem.addActionListener(e -> selectUser());
        userMenu.add(loginItem);

        JMenu recipeMenu = new JMenu("Recipes");
        JMenuItem addItem = new JMenuItem("Add New Recipe...");
        addItem.addActionListener(e -> showAddRecipeDialog());
        // Filter name, tag és összetevő alapján, és sort TODO
        recipeMenu.add(addItem);

        /* menuBar.add(fileMenu); */
        menuBar.add(userMenu);
        menuBar.add(recipeMenu);

        setJMenuBar(menuBar);
    }

    /**
     * Létrehozza a recept táblázatot.
     */
    private void setupRecipeTablePanel() {
        tableModel = new RecipeTableModel(recipeBook.getRecipes());
        recipeTable = new JTable(tableModel);
        // event bindolása kattintásra
        recipeTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // duplakatt-ra
                if (evt.getClickCount() == 2) {
                    int selectedRow = recipeTable.getSelectedRow();
                    if (selectedRow != -1) {
                        int modelRow = recipeTable.convertRowIndexToModel(selectedRow);
                        Recipe selectedRecipe = tableModel.getRecipeAt(modelRow);
                        showAddRecipeDialog(selectedRecipe);
                    }
                }
            }
        });
        // működjön a görgetés, ezért scroll-panelbe raktam
        JScrollPane scrollPane = new JScrollPane(recipeTable);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RecipeBook recipeBook = new RecipeBook();
            UserManager userManager = new UserManager();
            new RecipeBookGUI(recipeBook, userManager);
        });
    }

    /**
     * Megnyitja a LoginDialógust felhasználóválasztáshoz/bejelentkezéshez.
     * 
     * @return true, ha a bejelentkezés/felh.váltás sikeres
     */
    private boolean selectUser() {
        LoginDialog loginDialog = new LoginDialog(userManager, this);
        loginDialog.setVisible(true);
        String newUsername = loginDialog.getSelectedUsername();

        if (newUsername != null) {
            userManager.loginUser(newUsername);
            setTitle("Recipe Book - User: " + newUsername);
            // Frissítés/újrarajzolása a user dolgoknak TODO
            return true;

        } else {
            return userManager.getCurrentlyLoggedInUser().isPresent();
        }
    }

    /*
     * frissíti az adatokat, a táblában a megfelelő metódus hívásával, csak egy
     * wrapper fv.
     */
    public void refreshRecipeTable() {
        tableModel.setRecipes(recipeBook.getRecipes());
    }

    /**
     * Megnyitja az addRecipe dialog-ot
     */
    private void showAddRecipeDialog() {
        AddRecipeDialog dialog = new AddRecipeDialog(this, recipeBook, null);
        dialog.setVisible(true);
    }

    /**
     * Megnyitja az addRecipe dialog-ot és betölti a receptet
     */
    private void showAddRecipeDialog(Recipe selectedRecipe) {
        AddRecipeDialog dialog = new AddRecipeDialog(this, recipeBook, selectedRecipe);
        dialog.setVisible(true);
    }
}