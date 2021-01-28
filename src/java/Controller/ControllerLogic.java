package java.Controller;

import Service.DataService;
import org.json.JSONObject;

import javax.json.JsonObject;
import java.Model.Ingredient;
import java.Model.Recipe;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ControllerLogic {

    private DataService dataService;

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

    public void insertIngredient(ResultSet rs, Ingredient ingredient) {
        try {
            if (rs.next()) {
                updateExistingIngredient(rs, ingredient);
            } else {
                createNewIngredientEntry(ingredient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertRecipe(ResultSet rs, Recipe recipe) {
        String recName = recipe.getName();
        String recDate = recipe.getDate();
        int recipeNameId = getRecipeNameId(rs, recName);
        int recId = getRecipeId();
        String sql = "INSERT INTO RECIPE (recipe_id, recipe_name_id, date) VALUES "
                + "(" + recId + "," + recipeNameId + ",\'" + recDate + "\')";
        dataService.updateDatabase(sql);
    }

    private int getRecipeId() {
        int recipeId = 0;
        try {
            recipeId = getNextId("RECIPE", "RECIPE_ID");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipeId;
    }

    private int getRecipeNameId(ResultSet rs, String recName) {
        int recNameId = 0;
        try {
            if(rs.next()) {
                recNameId = rs.getInt("RECIPE_NAME_ID");
            } else {
                recNameId = getNextId("RECIPE_NAME", "RECIPE_NAME_ID");
                insertRecipeName(recName, recNameId);
            }
            rs.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return recNameId;
    }

    private void insertRecipeName(String recName, int recipeNameId) {
        String sql = "INSERT INTO RECIPE_NAME (name, recipe_name_id) VALUES (\'" + recName + "\'," + recipeNameId + ")";
        dataService.updateDatabase(sql);
    }

    private void createNewIngredientEntry(Ingredient ingredient) throws SQLException {
        String sql;
        int nextId = getNextId("INGREDIENT", "ID");
        sql = "INSERT INTO INGREDIENT VALUES (\'" + ingredient.getName() + "\'," + nextId + ",\'" + ingredient.getType() + "\')";
        dataService.updateDatabase(sql);
        sql = "INSERT INTO RECIPE_INGREDIENT VALUES (" + ingredient.getRecipeId()+ "," + nextId + ",\'" + ingredient.getAmount() + "\')";
        dataService.updateDatabase(sql);
    }

    private int getNextId(String table, String idColumnName) throws SQLException {
        String sql;
        sql = "SELECT MAX(" + idColumnName + ") FROM " + table;
        ResultSet rs = dataService.queryDatabase(sql);
        rs.next();
        return rs.getInt("MAX") + 1;
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

    public void removeIngredient(String recID, String amount, ResultSet rs) {
        try {
            if (rs.next()) {
                int ing_ID = rs.getInt("ID");
                String sql = "DELETE FROM RECIPE_INGREDIENT WHERE RECIPE_ID = " + recID
                        + " AND INGREDIENT_ID = " + ing_ID + " AND AMOUNT = \'" + amount + "\'";
                dataService.updateDatabase(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
