package Controller;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JsonObjectExtractor {

    private List<JsonObject> jsonList = new ArrayList<>();
    private JsonBuilderFactory factory = Json.createBuilderFactory(null);
    private JsonObjectBuilder builder = factory.createObjectBuilder();

    public List<JsonObject> extractJSONList(ResultSet rs,String[] columnNames, String[] propertyNames) {
        try {
            while(rs.next())
                buildJSONObject(rs, columnNames, propertyNames);

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonList;
    }

    private void buildJSONObject(ResultSet rs, String[] columnNames, String[] propertyNames) throws SQLException {
        for (int i = 0; i < columnNames.length; i++)
            builder.add(propertyNames[i], rs.getString(columnNames[i]));

        JsonObject jsonObject = builder.build();
        jsonList.add(jsonObject);
    }
}
