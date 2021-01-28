package java.Model;

public class Recipe {

    private String name;
    private String date;

    public Recipe(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public String getName() { return name; }

    public String getDate() { return date; }
}
