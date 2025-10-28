package nhf.gui;

import nhf.model.Recipe;
import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Custom TableModel, kapcsolat a recept modell és a tablemodell között
 */
public class RecipeTableModel extends AbstractTableModel {

    private List<Recipe> recipes;
    private final String[] columnNames = { "Name", "Time (min)", "Servings", "Ingredients #" };

    public RecipeTableModel(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public void setRecipes(List<Recipe> newRecipes) {
        this.recipes = newRecipes;
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
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Recipe getRecipeAt(int rowIndex) {
        return recipes.get(rowIndex);
    }
}