package nhf.gui;

import nhf.logic.RecipeBook;
import nhf.logic.UserManager;
import nhf.model.User;
import nhf.model.Recipe;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;

public class RecipeBookGUI extends JFrame {

    private final RecipeBook recipeBook;
    private final UserManager userManager;
    // az aktuális fő ablak tárolója
    private JTabbedPane mainTabbedPane;
    /* a táblázat modelljéttartalmazó változók */
    private JTable recipeTable;
    private RecipeTableModel tableModel;
    // recept leírsához
    private JTextArea detailTextArea;
    /* a filterhez használt változók */
    private JTextField searchField;
    private TableRowSorter<RecipeTableModel> sorter;

    /**
     * Konstruktor beállításokkal
     */
    public RecipeBookGUI(RecipeBook recipeBook, UserManager userManager) {
        this.recipeBook = recipeBook;
        this.userManager = userManager;
        setTitle("Recipe Book");
        // ikon az alkalmazásnak
        try {
            setIconImage(ImageIO.read(new File("res/icon.png")));
        } catch (Exception e) {
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);

        boolean loginSuccessful = selectUser();

        if (loginSuccessful) {
            setupMenuBar();
            mainTabbedPane = new JTabbedPane();
            JPanel recipeView = setupRecipeViewPanel();
            mainTabbedPane.addTab(" • Recipe List & Search", recipeView);
            JPanel menuView = setupWeeklyMenuPanel();
            mainTabbedPane.addTab(" • Weekly Menu", menuView);
            this.add(mainTabbedPane, BorderLayout.CENTER);
            setLocationRelativeTo(null);
            setupFavoriteListener();
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
    private JPanel setupRecipeViewPanel() {
        tableModel = new RecipeTableModel(recipeBook.getRecipes(), userManager);
        recipeTable = new JTable(tableModel);
        // sorter és filter:
        sorter = new TableRowSorter<>(tableModel);
        recipeTable.setRowSorter(sorter);
        // működjön a görgetés, ezért scroll-panelbe raktam
        JScrollPane tableScrollPane = new JScrollPane(recipeTable);

        // itt megjelenítem az aktuálisan kijelölt receptet
        detailTextArea = new JTextArea(20, 80);
        detailTextArea.setEditable(false);
        detailTextArea.setBorder(BorderFactory.createTitledBorder("Recipe Details"));
        JScrollPane detailScrollPane = new JScrollPane(detailTextArea);

        // wrapper a két scroll panelnak
        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
        mainContentPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainContentPanel.add(detailScrollPane, BorderLayout.SOUTH);
        setupRecipeSelectionListener();
        JPanel filterPanel = setupFilterPanel();
        mainContentPanel.add(filterPanel, BorderLayout.NORTH);

        return mainContentPanel;
    }

    private void setupRecipeSelectionListener() {
        recipeTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {

                // szimpla kattintásra betölti a részleteket alá
                if (evt.getClickCount() == 1) {
                    int selectedRow = recipeTable.getSelectedRow();
                    if (selectedRow != -1) {
                        int modelRow = recipeTable.convertRowIndexToModel(selectedRow);
                        Recipe selectedRecipe = tableModel.getRecipeAt(modelRow);
                        displayRecipeDetails(selectedRecipe);
                    }
                }
                // duplakattintásra szerkesztés
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
    }

    private void displayRecipeDetails(Recipe recipe) {
        // Összetevők formázása
        String ingredientDetails = recipe.getIngredients().stream().map(ing -> "  • " + ing.toString())
                .collect(Collectors.joining("\n"));

        String details = String.format(
                "Recipe: %s%n%n Time: %d min\t Servings: %d%n%n --- Ingredients ---%n%n%s%n%n --- Instructions ---%n%n%s",
                recipe.getName(),
                recipe.getPreparationTime(),
                recipe.getServings(),
                ingredientDetails,
                recipe.getInstructions());
        detailTextArea.setText(details);
        detailTextArea.setCaretPosition(0);
    }

    /* létrehozza a filter panelt */
    private JPanel setupFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(30);
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }
        });
        return filterPanel;
    }

    private void applyFilter() {
        String text = searchField.getText();
        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            try {
                // ha logikai értékkel kezdődik akkor csak az időre szűrök
                if (text.startsWith("<") || text.startsWith(">") || text.startsWith("=")) {
                    RowFilter<RecipeTableModel, Integer> numberFilter = createNumericFilter(text);
                    if (numberFilter != null) {
                        sorter.setRowFilter(numberFilter);
                        return;
                    }
                }
                // ha nem logikai értékkel kezdődik akkor stringre szűrök minden oszlopra
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            } catch (java.util.regex.PatternSyntaxException e) {
                sorter.setRowFilter(null);
            }
        }
    }

    private RowFilter<RecipeTableModel, Integer> createNumericFilter(String text) {
        String valueStr;
        RowFilter.ComparisonType type;

        // az időre való szűréshez az operátor eldöntése
        if (text.startsWith("<")) {
            type = RowFilter.ComparisonType.BEFORE;
            valueStr = text.substring(1).trim();
        } else if (text.startsWith(">")) {
            type = RowFilter.ComparisonType.AFTER;
            valueStr = text.substring(1).trim();
        } else if (text.startsWith("=")) {
            type = RowFilter.ComparisonType.EQUAL;
            valueStr = text.substring(1).trim();
        } else {
            return null;
        }
        // szöveg parsolása intbe
        try {
            int value = Integer.parseInt(valueStr);
            return RowFilter.numberFilter(type, value, 1);

        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void setupFavoriteListener() {
        tableModel.addTableModelListener(e -> {
            if (e.getColumn() == 4 && e.getType() == TableModelEvent.UPDATE) {
                int modelRow = e.getFirstRow();
                Recipe toggledRecipe = tableModel.getRecipeAt(modelRow);
                User user = userManager.getCurrentlyLoggedInUser();
                if (user != null) {
                    boolean isCurrentlyFavorite = user.getFavoriteRecipes().contains(toggledRecipe);
                    List<Recipe> favoriteRecipes = user.getFavoriteRecipes();
                    if (isCurrentlyFavorite) {
                        favoriteRecipes.remove(toggledRecipe);
                    } else {
                        favoriteRecipes.add(toggledRecipe);
                    }
                    try {
                        userManager.exportUsers();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error on saving favorite!", "Error",
                                JOptionPane.ERROR_MESSAGE);

                    }
                }
            }
        });

    }

    private JPanel setupWeeklyMenuPanel() {
        JPanel menuPanel = new JPanel(new BorderLayout());

        // TODO:
        menuPanel.add(new JLabel("Heti menü tervezés (Folyamatban...)"), BorderLayout.CENTER);

        return menuPanel;
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
            if (tableModel != null) {
                refreshRecipeTable(recipeBook.getRecipes());
            }
            return true;

        } else {
            return userManager.getCurrentlyLoggedInUser() != null;
        }
    }

    /*
     * frissíti az adatokat, a táblában a megfelelő metódus hívásával, csak egy
     * wrapper fv.
     */
    public void refreshRecipeTable(List<Recipe> recipes) {
        tableModel.setRecipes(recipes);
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