package nhf.io;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import nhf.model.Recipe;
import nhf.model.User;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * A szerializáló és deszerializáló metódusok gyűjteménye, jackson libraryt
 * használ a json-object mapeléshez
 */
public class JsonHandler {

    private final ObjectMapper mapper;

    public JsonHandler() {
        this.mapper = JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();
    }

    // --- SAVE / EXPORT Metódusok ---

    public void saveRecipes(List<Recipe> recipes, File file) throws Exception {
        mapper.writeValue(file, recipes);
    }

    public void saveUsers(Map<String, User> users, File file) throws Exception {
        mapper.writeValue(file, users);
    }

    // --- LOAD / IMPORT Metódusok ---

    public List<Recipe> loadRecipes(File file) throws Exception {
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        // Mivel listát olvasunk futási időben ezért kell a Typefactory construct
        // collection metódusa
        return mapper.readValue(file,
                mapper.getTypeFactory().constructCollectionType(List.class, Recipe.class));
    }

    public Map<String, User> loadUsers(File file) throws Exception {
        if (!file.exists() || file.length() == 0) {
            return new HashMap<>();
        }
        return mapper.readValue(file,
                mapper.getTypeFactory().constructMapType(Map.class, String.class,
                        User.class));
    }

}