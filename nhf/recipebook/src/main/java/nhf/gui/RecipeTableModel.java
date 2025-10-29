package nhf.gui;

import nhf.logic.UserManager;
import nhf.model.Recipe;
import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Custom TableModel, kapcsolat a recept modell és a tablemodell között
 */
public class RecipeTableModel extends AbstractTableModel {

    private List<Recipe> recipes;
    private List<Recipe> userFavorites;
    private final UserManager userManager;
    private final String[] columnNames = { "Name", "Time (min)", "Servings", "Ingredients #", "Favorite" };

    public RecipeTableModel(List<Recipe> recipes, UserManager userManager) {
        this.recipes = recipes;
        this.userManager = userManager;
        this.userFavorites = userManager.getCurrentlyLoggedInUser().getFavoriteRecipes();
    }

    public void setRecipes(List<Recipe> newRecipes) {
        this.recipes = newRecipes;
        this.userFavorites = userManager.getCurrentlyLoggedInUser().getFavoriteRecipes();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return recipes.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Recipe recipe = recipes.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> recipe.getName();
            case 1 -> recipe.getPreparationTime();
            case 2 -> recipe.getServings();
            case 3 -> recipe.getIngredients().size();
            case 4 -> userFavorites.contains(recipe);
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 4;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 4 && aValue instanceof Boolean) {
            fireTableCellUpdated(rowIndex, columnIndex);
            // innentől átveszi a gui listener
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> String.class;
            case 1, 2, 3 -> Integer.class;
            case 4 -> Boolean.class;
            default -> super.getColumnClass(columnIndex);
        };
    }

    public Recipe getRecipeAt(int rowIndex) {
        return recipes.get(rowIndex);
    }
}