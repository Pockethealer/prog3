package nhf.gui;

import nhf.logic.RecipeBook;
import nhf.logic.UserManager;
import nhf.model.User;
import nhf.model.Recipe;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableRowSorter;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

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
    /* tageknek használt változók */
    private Set<String> selectedTags = new HashSet<>();
    private JPanel tagsPanel;
    // bevásárló lista
    private Map<String, JCheckBox> dayCheckboxes;
    private JTextArea shoppingListArea;
    private Map<String, JTextArea> nutrientAreas;

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
        recipeMenu.add(addItem);

        JMenuItem addTemplateItem = new JMenuItem("Add Ingredient Template...");
        addTemplateItem.addActionListener(e -> new AddTemplateDialog(this, recipeBook).setVisible(true));

        JMenuItem deleteTemplateItem = new JMenuItem("Delete Ingredient Template...");
        deleteTemplateItem.addActionListener(e -> deleteTemplateAction());

        recipeMenu.add(addTemplateItem);
        recipeMenu.add(deleteTemplateItem);
        /* menuBar.add(fileMenu); */
        menuBar.add(userMenu);
        menuBar.add(recipeMenu);

        setJMenuBar(menuBar);
    }

    private void deleteTemplateAction() {
        String[] templateNames = recipeBook.getIngredientTemplates().keySet().toArray(new String[0]);
        if (templateNames.length == 0) {
            JOptionPane.showMessageDialog(this, "There are no available ingredient.", "Warning",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String selectedName = (String) JOptionPane.showInputDialog(
                this,
                "Choose one:",
                "Delete ingredient",
                JOptionPane.WARNING_MESSAGE,
                null,
                templateNames,
                templateNames[0]);

        if (selectedName != null) {
            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    "Do you want to delete '" + selectedName + "'?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.YES_OPTION) {
                try {
                    boolean success = recipeBook.removeTemplate(selectedName.toLowerCase());
                    if (success) {
                        JOptionPane.showMessageDialog(this, "'" + selectedName + "' succesfully deleted.", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Something went wrong", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "i/o error. " + e.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
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

        // recipe hozzáadása a heti menühöz
        JButton addToMenuButton = new JButton("Add Selected to Weekly Menu");
        addToMenuButton.addActionListener(e -> showDaySelectionPopup(addToMenuButton));
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.add(addToMenuButton, BorderLayout.SOUTH);
        actionPanel.add(tableScrollPane, BorderLayout.CENTER);

        // wrapper a két scroll panelnak
        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
        mainContentPanel.add(actionPanel, BorderLayout.CENTER);
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

    private void showDaySelectionPopup(JButton invoker) {
        int selectedRow = recipeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Select a recipe to add!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        JPopupMenu popupMenu = new JPopupMenu("Select Day");
        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
        for (String day : days) {
            JMenuItem menuItem = new JMenuItem(day);
            menuItem.addActionListener(e -> addRecipeToDayAction(day));
            popupMenu.add(menuItem);
        }
        popupMenu.show(invoker, 0, invoker.getHeight());
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
        tagsPanel = buildTags();
        filterPanel.add(tagsPanel);
        return filterPanel;
    }

    private JPanel buildTags() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Tags"));
        Set<String> uniqueTags = recipeBook.getRecipes().stream()
                .flatMap(recipe -> recipe.getTags().stream())
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        for (String tag : uniqueTags) {
            JLabel tagLabel = new JLabel("#" + tag);
            tagLabel.setOpaque(true);
            tagLabel.setBackground(Color.LIGHT_GRAY);
            tagLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY, 1),
                    BorderFactory.createEmptyBorder(2, 5, 2, 5)));
            tagLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (selectedTags.contains(tag)) {
                        selectedTags.remove(tag);
                        tagLabel.setBackground(Color.LIGHT_GRAY);
                    } else {
                        selectedTags.add(tag);
                        tagLabel.setBackground(new Color(135, 206, 250));
                    }
                    applyFilter();
                }
            });
            panel.add(tagLabel);
        }
        return panel;
    }

    private void applyFilter() {
        String text = searchField.getText().trim();
        RowFilter<Object, Object> finalCombinedFilter = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends Object, ? extends Object> entry) {
                boolean textOrNumericPasses = true;
                if (!text.isEmpty()) {
                    if (text.startsWith("<") || text.startsWith(">") || text.startsWith("=")) {
                        RowFilter numericRowFilter = createNumericFilter(text);
                        if (numericRowFilter != null) {
                            textOrNumericPasses = numericRowFilter.include(entry);
                        } else {
                            textOrNumericPasses = false;
                        }

                    } else {
                        RowFilter regexFilter = RowFilter.regexFilter("(?i)" + text);
                        textOrNumericPasses = regexFilter.include(entry);
                    }
                }
                if (!textOrNumericPasses)
                    return false;
                if (!selectedTags.isEmpty()) {
                    String tagsString = (String) entry.getValue(3);
                    List<String> recipeTags = Arrays.stream(tagsString.split(","))
                            .map(String::trim).filter(t -> !t.isEmpty())
                            .map(String::toLowerCase).collect(Collectors.toList());

                    for (String selectedTag : selectedTags) {
                        if (!recipeTags.contains(selectedTag)) {
                            return false; // Hiányzik egy kötelező címke
                        }
                    }
                }
                return true;
            }
        };

        if (text.isEmpty() && selectedTags.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            try {
                sorter.setRowFilter(finalCombinedFilter);
            } catch (Exception e) {
                sorter.setRowFilter(null);
            }
        }
    }

    private RowFilter<RecipeTableModel, Integer> createNumericFilter(String text) {
        String content;
        RowFilter.ComparisonType type;

        if (text.startsWith("<")) {
            type = RowFilter.ComparisonType.BEFORE;
            content = text.substring(1).trim();
        } else if (text.startsWith(">")) {
            type = RowFilter.ComparisonType.AFTER;
            content = text.substring(1).trim();
        } else if (text.startsWith("=")) {
            type = RowFilter.ComparisonType.EQUAL;
            content = text.substring(1).trim();
        } else {
            return null;
        }
        String valueStr;
        int columnIndex;
        if (content.toLowerCase().startsWith("t")) {
            columnIndex = 1;
            valueStr = content.substring(1).trim();
        } else if (content.toLowerCase().startsWith("k")) {
            columnIndex = 4;
            valueStr = content.substring(1).trim();
        } else {
            columnIndex = 1;
            valueStr = content;
        }
        try {
            int value = Integer.parseInt(valueStr);
            return RowFilter.numberFilter(type, value, columnIndex);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void setupFavoriteListener() {
        tableModel.addTableModelListener(e -> {
            if (e.getColumn() == 5 && e.getType() == TableModelEvent.UPDATE) {
                int modelRow = e.getFirstRow();
                Recipe toggledRecipe = tableModel.getRecipeAt(modelRow);
                User user = userManager.getCurrentlyLoggedInUser();
                if (user != null) {
                    boolean isCurrentlyFavorite = user.getFavoriteRecipes().contains(toggledRecipe.getName());
                    List<String> favoriteRecipes = user.getFavoriteRecipes();
                    if (isCurrentlyFavorite) {
                        favoriteRecipes.remove(toggledRecipe.getName());
                    } else {
                        favoriteRecipes.add(toggledRecipe.getName());
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
        nutrientAreas = new HashMap<>();
        JPanel shoppingListPanel = setupShoppingListPanel();
        User currentUser = userManager.getCurrentlyLoggedInUser();
        Map<String, List<Recipe>> weeklyMenu = convertWeeklyMenuNamesToObjects(currentUser.getWeeklyMenu());
        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
        JPanel daysGridPanel = new JPanel(new GridLayout(1, 7, 10, 10));

        for (String day : days) {
            List<Recipe> recipesForDay = weeklyMenu.get(day);
            JPanel dayPanel = createDayPanel(day, recipesForDay);
            daysGridPanel.add(dayPanel);
        }
        JPanel menuPanel = new JPanel(new BorderLayout(10, 10));
        menuPanel.add(daysGridPanel, BorderLayout.CENTER);
        menuPanel.add(shoppingListPanel, BorderLayout.SOUTH);
        updateShoppingList();

        return menuPanel;
    }

    private Map<String, Double> aggregateDayNutrients(List<Recipe> recipes) {
        Map<String, Double> totalNutrients = new HashMap<>();
        recipes.stream()
                .flatMap(recipe -> recipe.getIngredients().stream())
                .forEach(ingredient -> {
                    Map<String, Double> nutrientMap = ingredient.getNutritionalValues();
                    nutrientMap.forEach((key, value) -> {
                        totalNutrients.merge(key, value, Double::sum);
                    });
                });
        // kompatibilitási okokból sok helyen null volt, ezért kell:
        totalNutrients.putIfAbsent("calories", 0.0);
        totalNutrients.putIfAbsent("protein", 0.0);
        totalNutrients.putIfAbsent("fat", 0.0);
        totalNutrients.putIfAbsent("carb", 0.0);
        return totalNutrients;
    }

    private JPanel setupShoppingListPanel() {
        JPanel listWrapper = new JPanel(new BorderLayout(5, 5));
        listWrapper.setBorder(BorderFactory.createTitledBorder("Shopping List"));
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dayCheckboxes = new LinkedHashMap<>();
        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
        for (String day : days) {
            JCheckBox dayBox = new JCheckBox(day);
            dayBox.setSelected(true);
            dayBox.addActionListener(e -> updateShoppingList());
            dayCheckboxes.put(day, dayBox);
            selectorPanel.add(dayBox);
        }
        shoppingListArea = new JTextArea(15, 30);
        shoppingListArea.setEditable(false);
        listWrapper.add(selectorPanel, BorderLayout.NORTH);
        listWrapper.add(new JScrollPane(shoppingListArea), BorderLayout.CENTER);
        return listWrapper;
    }

    private void updateShoppingList() {
        Set<String> daysToInclude = dayCheckboxes.entrySet().stream()
                .filter(entry -> entry.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        User currentUser = userManager.getCurrentlyLoggedInUser();
        Map<String, List<Recipe>> weeklyMenu = convertWeeklyMenuNamesToObjects(currentUser.getWeeklyMenu());

        Map<String, Double> shoppingList = new HashMap<>();

        daysToInclude.forEach(day -> {
            List<Recipe> recipes = weeklyMenu.getOrDefault(day, Collections.emptyList());
            recipes.stream()
                    .flatMap(recipe -> recipe.getIngredients().stream())
                    .forEach(ingredient -> {
                        Map<String, Object> converted = ingredient.getMetricConversion();
                        double convertedAmount = (double) converted.get("amount");
                        String convertedUnit = (String) converted.get("unit");
                        String key = ingredient.getName().toLowerCase() + " (" + convertedUnit + ")";
                        shoppingList.merge(key, convertedAmount, Double::sum);
                    });
        });

        StringBuilder sb = new StringBuilder();
        shoppingList.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    sb.append(String.format("%.2f %s%n", entry.getValue(), entry.getKey()));
                });

        shoppingListArea.setText(sb.toString());
        shoppingListArea.setCaretPosition(0);
    }

    private void refreshWeeklyMenuTab() {
        int weeklyMenuIndex = mainTabbedPane.indexOfTab(" • Weekly Menu");

        if (weeklyMenuIndex != -1) {
            mainTabbedPane.remove(weeklyMenuIndex);
            JPanel newMenuView = setupWeeklyMenuPanel();
            mainTabbedPane.insertTab(" • Weekly Menu", null, newMenuView, null, weeklyMenuIndex);
        }
    }

    private JPanel createDayPanel(String day, List<Recipe> recipesForDay) {
        JPanel dayPanel = new JPanel(new BorderLayout());
        dayPanel.add(new JLabel(day, SwingConstants.CENTER), BorderLayout.NORTH);
        DefaultListModel<Recipe> listModel = new DefaultListModel<>();
        recipesForDay.forEach(listModel::addElement);
        JList<Recipe> recipeList = new JList<>(listModel);
        recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dayPanel.add(new JScrollPane(recipeList), BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton removeButton = new JButton("Remove Selected");
        removeButton.addActionListener(e -> removeRecipeFromDay(day, recipeList, listModel));
        bottomPanel.add(removeButton, BorderLayout.SOUTH);
        JTextArea nutrientArea = new JTextArea(4, 20);
        nutrientAreas.put(day, nutrientArea);
        updateNutrientArea(day, recipesForDay);
        bottomPanel.add(nutrientArea, BorderLayout.NORTH);
        dayPanel.add(bottomPanel, BorderLayout.SOUTH);

        return dayPanel;
    }

    private void updateNutrientArea(String day, List<Recipe> recipesForDay) {
        Map<String, Double> dayTotals = aggregateDayNutrients(recipesForDay);
        nutrientAreas.get(day).setEditable(false);
        String nutrientSummary = String.format(
                "Calories: %.0f kcal%n" +
                        "Protein: %.1f g%n" +
                        "Fat: %.1f g%n" +
                        "Carbs: %.1f g",
                dayTotals.get("calories"),
                dayTotals.get("protein"),
                dayTotals.get("fat"),
                dayTotals.get("carb"));
        nutrientAreas.get(day).setText(nutrientSummary);
    }

    private void removeRecipeFromDay(String day, JList<Recipe> recipeList, DefaultListModel<Recipe> listModel) {
        Recipe selectedRecipe = recipeList.getSelectedValue();
        int selectedIndex = recipeList.getSelectedIndex();

        if (selectedRecipe == null) {
            JOptionPane.showMessageDialog(this, "Please select a recipe to remove!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        User currentUser = userManager.getCurrentlyLoggedInUser();
        currentUser.getWeeklyMenu().get(day).remove(selectedRecipe);
        listModel.remove(selectedIndex);
        updateShoppingList();
        updateNutrientArea(day, convertWeeklyMenuNamesToObjects(currentUser.getWeeklyMenu()).get(day));
        try {
            userManager.exportUsers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error trying to save weakly menu!", "I/O Hiba",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addRecipeToDayAction(String day) {
        int selectedRow = recipeTable.getSelectedRow();
        int modelRow = recipeTable.convertRowIndexToModel(selectedRow);
        Recipe selectedRecipe = tableModel.getRecipeAt(modelRow);
        User currentUser = userManager.getCurrentlyLoggedInUser();
        if (!currentUser.getWeeklyMenu().get(day).contains(selectedRecipe)) {
            currentUser.addRecipeToDay(day, selectedRecipe);

        }

        try {
            userManager.exportUsers();
            refreshWeeklyMenuTab();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Hiba a menü mentésekor!", "I/O Hiba", JOptionPane.ERROR_MESSAGE);
        }
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
            if (tableModel != null) {
                refreshRecipeTable(recipeBook.getRecipes());
                refreshWeeklyMenuTab();
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
        // tagek frissítése
        JPanel parent = (JPanel) tagsPanel.getParent();
        if (parent != null) {
            parent.remove(tagsPanel);
            tagsPanel = buildTags();
            parent.add(tagsPanel);
            parent.revalidate();
            parent.repaint();
        }
    }

    /**
     * Megnyitja az addRecipe dialog-ot
     */
    private void showAddRecipeDialog() {
        AddRecipeDialog dialog = new AddRecipeDialog(this, recipeBook, null, recipeBook.getIngredientTemplates());
        dialog.setVisible(true);
    }

    /**
     * Megnyitja az addRecipe dialog-ot és betölti a receptet
     */
    private void showAddRecipeDialog(Recipe selectedRecipe) {
        AddRecipeDialog dialog = new AddRecipeDialog(this, recipeBook, selectedRecipe,
                recipeBook.getIngredientTemplates());
        dialog.setVisible(true);
    }

    private Map<String, List<Recipe>> convertWeeklyMenuNamesToObjects(Map<String, List<String>> weeklyMenuNames) {
        if (weeklyMenuNames == null || weeklyMenuNames.isEmpty()) {
            return Collections.emptyMap();
        }
        return weeklyMenuNames.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(recipeBook::getRecipeByName)
                                .toList()));
    }
}