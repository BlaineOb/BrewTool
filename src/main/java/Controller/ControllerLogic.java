package Controller;

import Service.DataService;
import org.json.JSONObject;

import Model.Ingredient;
import Model.Recipe;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ControllerLogic {

    private final DataService dataService;

    public ControllerLogic(DataService dataService) {
        this.dataService = dataService;
    }

    public Ingredient createIngredient(String clientInput) {
        JSONObject jsonOb = new JSONObject(clientInput);
        return new Ingredient(
                jsonOb.getString("recId"),
                jsonOb.getString("name"),
                jsonOb.getString("amount"),
                jsonOb.getString("type"));
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
        rs.close();
    }

    public void insertRecipe(ResultSet rs, Recipe recipe) throws SQLException {
        String recName = recipe.getName();
        String recDate = recipe.getDate();
        int recipeNameId = getRecipeNameId(rs, recName);
        int recId = getNextId("RECIPE", "RECIPE_ID");
        dataService.updateDatabase("INSERT INTO RECIPE (recipe_id, recipe_name_id, date) VALUES "
                + "(" + recId + "," + recipeNameId + ",\'" + recDate + "\')");
    }

    private int getRecipeNameId(ResultSet rs, String recName) throws SQLException {
        int recNameId = 0;
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
        dataService.updateDatabase("INSERT INTO RECIPE_NAME (name, recipe_name_id) VALUES (\'" + recName + "\'," + recipeNameId + ")");
    }

    private void createNewIngredientEntry(Ingredient ingredient) throws SQLException {
        int nextId = getNextId("INGREDIENT", "ID");
        dataService.updateDatabase("INSERT INTO INGREDIENT VALUES (\'" + ingredient.getName() + "\'," + nextId + ",\'" + ingredient.getType() + "\')");
        dataService.updateDatabase("INSERT INTO RECIPE_INGREDIENT VALUES (" + ingredient.getRecipeId()+ "," + nextId + ",\'" + ingredient.getAmount() + "\')");
    }

    private int getNextId(String table, String idColumnName) throws SQLException {
        int default_id = 0;
        ResultSet rs = dataService.queryDatabase("SELECT MAX(" + idColumnName + ") FROM " + table);
        rs.next();
        default_id = rs.getInt("MAX") + 1;
        rs.close();
        return default_id;
    }

    private void updateExistingIngredient(ResultSet rs, Ingredient ingredient) throws SQLException {
        String sql;
        int ingId = rs.getInt("ID");
        sql = "INSERT INTO RECIPE_INGREDIENT VALUES (" + ingredient.getRecipeId() + "," + ingId + ",\'" + ingredient.getAmount() + "\')";
        dataService.updateDatabase(sql);
    }

    public String fixEscapedName(String inputName) {
        String correctName = inputName.replaceAll("\\$", "%");
        return correctName.replaceAll("&", "/");
    }

    public void removeIngredient(String recID, String amount, ResultSet rs) throws SQLException {
        if (rs.next()) {
            int ing_ID = rs.getInt("ID");
            String sql = "DELETE FROM RECIPE_INGREDIENT WHERE RECIPE_ID = " + recID
                    + " AND INGREDIENT_ID = " + ing_ID + " AND AMOUNT = \'" + amount + "\'";
            dataService.updateDatabase(sql);
        }
        rs.close();
    }

    public void deleteRecipe(String recName, String correct_date) throws SQLException {
        int nameID = getNameID(recName);
        int recID = getRecID(correct_date, nameID);
        deleteRecipeFromTables(recID);
    }

    private void deleteRecipeFromTables(int recID) {
        String[] tableDeletions = {
                "DELETE FROM RECIPE_INGREDIENT WHERE RECIPE_ID = ",
                "DELETE FROM MASH WHERE RECIPE_ID = ",
                "DELETE FROM BOIL WHERE RECIPE_ID = ",
                "DELETE FROM STATS WHERE RECIPE_ID = ",
                "DELETE FROM MISCELLANEOUS WHERE RECIPE_ID = ",
                "DELETE FROM RECIPE WHERE RECIPE_ID = "
        };
        for (String tableDeletion : tableDeletions) {
            String sql = tableDeletion + recID;
            dataService.updateDatabase(sql);
        }
    }

    private int getRecID(String correct_date, int nameID) throws SQLException {
        ResultSet rs;
        String sql = "SELECT RECIPE_ID FROM RECIPE WHERE RECIPE_NAME_ID = " + nameID + " AND DATE = \'" + correct_date + "\'";
        rs = dataService.queryDatabase(sql);
        rs.next();
        int recID = rs.getInt("RECIPE_ID");
        rs.close();
        return recID;
    }

    private int getNameID(String recName) throws SQLException {
        String sql = "SELECT RECIPE_NAME_ID FROM RECIPE_NAME WHERE NAME = \'" + recName + "\'";
        ResultSet rs = dataService.queryDatabase(sql);
        rs.next();
        int nameID = rs.getInt("RECIPE_NAME_ID");
        rs.close();
        return nameID;
    }
}
