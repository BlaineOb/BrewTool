package Model;

public class Ingredient {

    private String recipeId;
    private String name;
    private String amount;
    private String type;

    public Ingredient(String recipeId, String name, String amount, String type) {
        this.recipeId = recipeId;
        this.name = name;
        this.amount = amount;
        this.type = type;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }
}
