package nhf.logic;

import nhf.model.User;
import nhf.io.JsonHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.File;

public class UserManager {

    private Map<String, User> users;
    private Optional<User> currentlyLoggedInUser;

    private final JsonHandler jsonHandler;
    private File userFile;

    public UserManager() {
        this.jsonHandler = new JsonHandler();
        this.currentlyLoggedInUser = Optional.empty();
        userFile = new File("users.json");
        try {
            this.users = jsonHandler.loadUsers(userFile);
        } catch (Exception e) {
            System.err.println("Hiba a felhasználók betöltésekor: " + e.getMessage() + ". Üres felhasználó lista.");
            this.users = new HashMap<>();
        }
    }

    public UserManager(File userFileOptional) {
        this.jsonHandler = new JsonHandler();
        this.currentlyLoggedInUser = Optional.empty();
        userFile = userFileOptional;
        try {
            this.users = jsonHandler.loadUsers(userFile);
        } catch (Exception e) {
            System.err.println("Hiba a felhasználók betöltésekor: " + e.getMessage() + ". Üres felhasználó lista.");
            this.users = new HashMap<>();
        }
    }

    /**
     * Új user létrehozása
     * 
     * @return ha sikeres akkor igazzal tér vissza
     */
    public boolean registerUser(String username) {
        if (users.containsKey(username)) {
            return false;
        }
        User newUser = new User(username);
        users.put(username, newUser);

        try {
            exportUsers();
        } catch (Exception e) {
            System.err.println("Hiba a felhasználók mentésekor: " + e.getMessage());
        }
        return true;
    }

    /**
     * Beállítja a jelenlegi felhasználót
     */
    public boolean loginUser(String username) {
        User user = users.get(username);
        if (user != null) {
            this.currentlyLoggedInUser = Optional.of(user);
            return true;
        }
        this.currentlyLoggedInUser = Optional.empty();
        return false;
    }

    public boolean deleteUser(String username) {
        if (!(users.containsKey(username))) {
            return false;
        }
        users.remove(username);
        try {
            exportUsers();
        } catch (Exception e) {
            System.err.println("Error while saving users: " + e.getMessage());
        }
        return true;

    }

    public void logout() {

        this.currentlyLoggedInUser = Optional.empty();

    }

    public Optional<User> getCurrentlyLoggedInUser() {
        return currentlyLoggedInUser;
    }

    /**
     * Standard export user szerializáló
     */
    public void exportUsers() throws Exception {
        jsonHandler.saveUsers(this.users, userFile);
    }

    /**
     * user deszerializáló
     */
    public void importUsers() throws Exception {
        this.users = jsonHandler.loadUsers(userFile);
    }

    public List<String> getAllUsernames() {
        return new ArrayList<>(users.keySet());
    }
}