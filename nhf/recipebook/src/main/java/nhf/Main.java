package nhf;

import nhf.logic.RecipeBook;
import nhf.logic.UserManager;
import nhf.gui.RecipeBookGUI;
import nhf.gui.LoginDialog;
import javax.swing.SwingUtilities;

// nhf.Main.java (Csak a main metódus módosítása)

public class Main {
    public static void main(String[] args) {
        // 1. Üzleti logika inicializálása
        RecipeBook recipeBook = new RecipeBook();
        UserManager userManager = new UserManager();

        SwingUtilities.invokeLater(() -> {

            new RecipeBookGUI(recipeBook, userManager);
        });
    }
}