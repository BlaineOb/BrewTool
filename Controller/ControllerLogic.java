package Controller;

import Model.Ingredient;
import Service.DataService;
import org.json.JSONObject;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ControllerLogic {

    DataService dataService;

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

    private void createNewIngredientEntry(Ingredient ingredient) throws SQLException {
        String sql;
        int nextId = getNextId();
        sql = "INSERT INTO INGREDIENT VALUES (\'" + ingredient.getName() + "\'," + nextId + ",\'" + ingredient.getType() + "\')";
        dataService.updateDatabase(sql);
        sql = "INSERT INTO RECIPE_INGREDIENT VALUES (" + ingredient.getRecipeId()+ "," + nextId + ",\'" + ingredient.getAmount() + "\')";
        dataService.updateDatabase(sql);
    }

    private int getNextId() throws SQLException {
        String sql;
        sql = "SELECT MAX(ID) FROM INGREDIENT";
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
