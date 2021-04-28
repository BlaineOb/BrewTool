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
    private final JsonBuilderFactory factory = Json.createBuilderFactory(null);
    private final JsonObjectBuilder builder = factory.createObjectBuilder();

    public String extractJsonString(ResultSet rs, String[] columnNames, String[] propertyNames) throws SQLException {
        List<JsonObject> jsonList = new ArrayList<>();
        while(rs.next())
            buildJSONObject(rs, columnNames, propertyNames, jsonList);
        rs.close();
        return jsonList.toString();
    }

    private void buildJSONObject(ResultSet rs, String[] columnNames, String[] propertyNames, List<JsonObject> jsonList) throws SQLException {
        for (int i = 0; i < columnNames.length; i++)
            builder.add(propertyNames[i], rs.getString(columnNames[i]));
        JsonObject jsonObject = builder.build();
        jsonList.add(jsonObject);
    }
}
