package Service;

import Model.Ingredient;
import Model.Recipe;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BrewToolService {

    private final DataService dataService;
    private final JsonObjectExtractor extractor = new JsonObjectExtractor();

    public BrewToolService(DataService dataService) {
        this.dataService = dataService;
    }

    public String getIngredients(String recipeId) throws SQLException {
        ResultSet rs = dataService.queryDatabase("SELECT INGREDIENT.NAME, RECIPE_INGREDIENT.AMOUNT, INGREDIENT.TYPE FROM "
                + "INGREDIENT INNER JOIN RECIPE_INGREDIENT ON INGREDIENT.ID = RECIPE_INGREDIENT.INGREDIENT_ID "
                + "AND RECIPE_ID = " + recipeId + " ORDER BY INGREDIENT.TYPE");
        String[] columnNames = {"name","amount","type"};
        return extractor.extractJsonString(rs,columnNames,columnNames);
    }

    public void postIngredient(String ingredientData) throws SQLException {
        Ingredient ingredient = createIngredient(ingredientData);
        String sql = "SELECT ID FROM INGREDIENT WHERE NAME = '" + ingredient.getName() + "'";
        ResultSet rs = dataService.queryDatabase(sql);
        insertIngredient(rs,ingredient);
    }

    public Ingredient createIngredient(String ingredientData) {
        JSONObject jsonOb = new JSONObject(ingredientData);
        return new Ingredient(
                jsonOb.getString("recId"),
                jsonOb.getString("name"),
                jsonOb.getString("amount"),
                jsonOb.getString("type"));
    }

    public void deleteIngredient(String recipeId, String ingName, String amount) throws SQLException {
        String correctName = fixEscapedName(ingName);
        ResultSet rs = dataService.queryDatabase("SELECT ID FROM INGREDIENT WHERE NAME = '" + correctName + "'");
        removeIngredient(recipeId, amount, rs);
    }

    public String getRecipes() throws SQLException {
        ResultSet rs = dataService.queryDatabase("SELECT RECIPE_NAME.NAME, RECIPE.DATE, RECIPE.RECIPE_ID FROM RECIPE INNER JOIN "
                + "RECIPE_NAME ON RECIPE_NAME.RECIPE_NAME_ID = RECIPE.RECIPE_NAME_ID"
                + " ORDER BY RECIPE.RECIPE_ID DESC");
        String[] columnNames = {"NAME","DATE","RECIPE_ID"};
        String[] propertyNames = {"recName","recDate","recID"};
        return extractor.extractJsonString(rs,columnNames,propertyNames);
    }

    public Recipe createRecipe(String clientInput) {
        JSONObject jsonOb = new JSONObject(clientInput);
        return new Recipe(
                jsonOb.getString("name"),
                jsonOb.getString("date"));
    }

    public void insertIngredient(ResultSet rs, Ingredient ingredient) throws SQLException {
        if (rs.next()) {
            updateExistingIngredient(rs, ingredient);
        } else {
            createNewIngredientEntry(ingredient);
        }
    }

    public void postRecipe(String recipeData) throws SQLException {
        Recipe recipe = createRecipe(recipeData);
        ResultSet rs = dataService.queryDatabase("SELECT * FROM RECIPE_NAME WHERE NAME='" + recipe.getName() + "'");
        insertRecipe(rs,recipe);
    }

    public void insertRecipe(ResultSet rs, Recipe recipe) throws SQLException {
        String recName = recipe.getName();
        String recDate = recipe.getDate();
        int recipeNameId = getRecipeNameId(rs, recName);
        int recId = getNextId("RECIPE", "RECIPE_ID");
        dataService.updateDatabase("INSERT INTO RECIPE (recipe_id, recipe_name_id, date) VALUES "
                + "(" + recId + "," + recipeNameId + ",'" + recDate + "')");
    }

    private int getRecipeNameId(ResultSet rs, String recName) throws SQLException {
        int recNameId;
        if(rs.next()) {
            recNameId = rs.getInt("RECIPE_NAME_ID");
        } else {
            recNameId = getNextId("RECIPE_NAME", "RECIPE_NAME_ID");
            insertRecipeName(recName, recNameId);
        }
        rs.close();
        return recNameId;
    }

    private void insertRecipeName(String recName, int recipeNameId) {
        dataService.updateDatabase("INSERT INTO RECIPE_NAME (name, recipe_name_id) VALUES ('" + recName + "'," + recipeNameId + ")");
    }

    private void createNewIngredientEntry(Ingredient ingredient) throws SQLException {
        int nextId = getNextId("INGREDIENT", "ID");
        dataService.updateDatabase("INSERT INTO INGREDIENT VALUES ('" + ingredient.getName() + "'," + nextId + ",'" + ingredient.getType() + "')");
        dataService.updateDatabase("INSERT INTO RECIPE_INGREDIENT VALUES (" + ingredient.getRecipeId()+ "," + nextId + ",'" + ingredient.getAmount() + "')");
    }

    private int getNextId(String table, String idColumnName) throws SQLException {
        ResultSet rs = dataService.queryDatabase("SELECT MAX(" + idColumnName + ") FROM " + table);
        rs.next();
        return rs.getInt("MAX") + 1;
    }

    private void updateExistingIngredient(ResultSet rs, Ingredient ingredient) throws SQLException {
        int ingredientId = rs.getInt("ID");
        dataService.updateDatabase("INSERT INTO RECIPE_INGREDIENT VALUES (" + ingredient.getRecipeId() + "," + ingredientId + ",'" + ingredient.getAmount() + "')");
    }

    public String fixEscapedName(String inputName) {
        String correctName = inputName.replaceAll("\\$", "%");
        return correctName.replaceAll("&", "/");
    }

    public void removeIngredient(String recID, String amount, ResultSet rs) throws SQLException {
        if (rs.next()) {
            int ing_ID = rs.getInt("ID");
            dataService.updateDatabase("DELETE FROM RECIPE_INGREDIENT WHERE RECIPE_ID = " + recID
                    + " AND INGREDIENT_ID = " + ing_ID + " AND AMOUNT = '" + amount + "'");
        }
    }

    public int getRecipeId(String recName, String recDate) throws SQLException {
        int nameID = getNameID(recName);
        String correct_date = recDate.replaceAll("@","/");
        ResultSet rs = dataService.queryDatabase("SELECT RECIPE_ID FROM RECIPE WHERE RECIPE_NAME_ID = " + nameID + " AND DATE = '" + correct_date + "'");
        rs.next();
        int recID = rs.getInt("RECIPE_ID");
        rs.close();
        return recID;
    }

    private int getNameID(String recName) throws SQLException {
        ResultSet rs = dataService.queryDatabase("SELECT RECIPE_NAME_ID FROM RECIPE_NAME WHERE NAME = '" + recName  + "'");
        rs.next();
        return rs.getInt("RECIPE_NAME_ID");
    }

    public void deleteRecipe(String recDate, String recName) throws SQLException {
        int recipeId = getRecipeId(recDate, recName);
        dataService.updateDatabase("DELETE FROM RECIPE_INGREDIENT WHERE RECIPE_ID = " + recipeId);
        dataService.updateDatabase("DELETE FROM MASH WHERE RECIPE_ID = " + recipeId);
        dataService.updateDatabase("DELETE FROM BOIL WHERE RECIPE_ID = " + recipeId);
        dataService.updateDatabase("DELETE FROM STATS WHERE RECIPE_ID = " + recipeId);
        dataService.updateDatabase("DELETE FROM MISCELLANEOUS WHERE RECIPE_ID = " + recipeId);
        dataService.updateDatabase("DELETE FROM RECIPE WHERE RECIPE_ID = " + recipeId);
    }

    public String getBoil(String recipeId) throws SQLException {
        ResultSet rs = dataService.queryDatabase("SELECT TIME,ACTION FROM BOIL WHERE RECIPE_ID = " + recipeId);
        String[] columnNames = {"TIME", "ACTION"};
        String[] propertyNames = {"time", "action"};
        return extractor.extractJsonString(rs, columnNames, propertyNames);
    }

    public void postBoil(String boilData) {
        JSONObject jsonOb = new JSONObject(boilData);
        String id = jsonOb.getString("id");
        String time = jsonOb.getString("time");
        String action = jsonOb.getString("action");
        dataService.updateDatabase("INSERT INTO BOIL VALUES "
                + "('" + time + "','" + action + "'," + id + ")");
    }

    public void deleteBoil(String recipeId, String time, String action) {
        String correctTime = time.replaceAll("\\$","%");
        String correctAction = action.replaceAll("\\$","%");
        String betterTime = correctTime.replaceAll("&","/");
        String betterAction = correctAction.replaceAll("&","/");
        dataService.updateDatabase("DELETE FROM BOIL WHERE RECIPE_ID = " + recipeId + " AND TIME = '"
                + betterTime + "' AND ACTION = '" + betterAction + "'");
    }

    public String getStats(String recipeId) throws SQLException {
        ResultSet rs = dataService.queryDatabase("SELECT * FROM STATS WHERE RECIPE_ID = " + recipeId);
        String[] columnNames = {"OG", "FG", "ABV", "ATTENUATION"};
        String[] propertyNames = {"og", "fg", "abv", "atten", "id"};
        return extractor.extractJsonString(rs, columnNames, propertyNames);
    }

    public void postStats(String statsData) {
        JSONObject jsonOb = new JSONObject(statsData);
        String id = jsonOb.getString("id");
        String og = jsonOb.getString("og");
        String fg = jsonOb.getString("fg");
        String abv = jsonOb.getString("abv");
        String atten = jsonOb.getString("atten");
        dataService.updateDatabase("INSERT INTO STATS VALUES " + "('" + og + "','" + fg + "','" + abv + "','" + atten + "'," + id + ")");
    }

    public void deleteStats(String recipeId){
        dataService.updateDatabase("DELETE FROM STATS WHERE RECIPE_ID = " + recipeId);
    }

    public String getMash(String recipeId) throws SQLException {
        ResultSet rs = dataService.queryDatabase("SELECT * FROM MASH WHERE RECIPE_ID = " + recipeId);
        String[] columnNames = {"MISTRIKE", "MITARGET", "MIACTUAL", "MOSTRIKE", "MOTARGET", "MOACTUAL"};
        String[] propertyNames = {"miStrike", "miTarget", "miActual", "moStrike", "moTarget", "moActual"};
        return extractor.extractJsonString(rs, columnNames, propertyNames);
    }

    public void postMash(String mashData) {
        JSONObject jsonOb = new JSONObject(mashData);
        String id = jsonOb.getString("id");
        String miStrike = jsonOb.getString("miStrike");
        String miTarg = jsonOb.getString("miTarg");
        String miAct = jsonOb.getString("miAct");
        String moStrike = jsonOb.getString("moStrike");
        String moTarg = jsonOb.getString("moTarg");
        String moAct = jsonOb.getString("moAct");
        dataService.updateDatabase("INSERT INTO MASH VALUES "
                + "(" + id + ",'" + miStrike + "','" + miTarg + "','" + miAct + "','"
                + moStrike + "','" + moTarg + "','" + moAct + "')");
    }

    public void deleteMash(String recipeId) {
        dataService.updateDatabase("DELETE FROM MASH WHERE RECIPE_ID = " + recipeId);
    }

    public String getMisc(String recipeId) throws SQLException {
        ResultSet rs = dataService.queryDatabase("SELECT * FROM MISCELLANEOUS WHERE RECIPE_ID = " + recipeId);
        String[] columnNames = {"COMMENT"};
        String[] propertyNames = {"comment"};
        return extractor.extractJsonString(rs, columnNames, propertyNames);
    }

    public void postMisc(String miscData) {
        JSONObject jsonOb = new JSONObject(miscData);
        String id = jsonOb.getString("id");
        String com = jsonOb.getString("com");
        dataService.updateDatabase("INSERT INTO MISCELLANEOUS VALUES "
                + "('" + com + "'," + id + ")");
    }

    public void deleteMisc(String recipeId, String comment) {
        String fixedComment = fixEscapedName(comment);
        dataService.updateDatabase("DELETE FROM MISCELLANEOUS WHERE RECIPE_ID = " + recipeId + " AND COMMENT = '" + fixedComment + "'");
    }

    public String getEvents() throws SQLException {
        ResultSet rs = dataService.queryDatabase("SELECT * FROM EVENT");
        String[] columnNames = {"EVENT_ID", "EVENT_NAME", "START_DATE", "END_DATE"};
        String[] propertyNames = {"id", "title", "start", "end"};
        return extractor.extractJsonString(rs, columnNames, propertyNames);
    }

    public void postEvent(String eventData) {
        JSONObject jsonOb = new JSONObject(eventData);
        int id = jsonOb.getInt("id");
        String title = jsonOb.getString("title");
        String start = jsonOb.getString("start");
        String end = jsonOb.getString("end");
        dataService.updateDatabase("INSERT INTO EVENT VALUES "
                + "(" + id + ",'" + title + "','"+ start + "','" + end + "')");
    }

    public void deleteEvent(String eventId) {
        dataService.updateDatabase("DELETE FROM EVENT WHERE EVENT_ID = " + eventId);
    }
}
