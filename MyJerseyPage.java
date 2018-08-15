package p1;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import javax.json.*;
import java.util.ArrayList;
import org.json.*;

@Path("/home")
public class MyJerseyPage {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.postgresql.Driver";  
	static final String DB_URL = "jdbc:postgresql://localhost:5433/BrewTool";

	//  Database credentials
	static final String USER = "dod";
	static final String PASS = "1";
	
	@GET
	@Path("/hello")
	@Produces(MediaType.TEXT_HTML)
	public String sayHtmlHello() {
		return "Hello from Jersey";
	}
	
	@GET
	@Path("/get_ingredient")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ingredients() {
		Connection conn = null;
		Statement stmt = null;
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		ArrayList<JsonObject> ingreds = new ArrayList<JsonObject>(); 
		int b_id = 0;
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("org.postgresql.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, USER, PASS);
		      System.out.println("Connected database successfully...");
		      
		      //STEP 4: Execute a query
		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();

		      String sql = "SELECT * FROM INGREDIENT";
		      ResultSet rs = stmt.executeQuery(sql);
		      //STEP 5: Extract data from result set
		      while(rs.next()){
		         //Retrieve by column name
		    	  int id = rs.getInt("ID");
		    	  String ing_name = rs.getString("NAME");
		    	  JsonObject json = factory.createObjectBuilder()
		    	      .add("ingName",ing_name)
		    	      .add("ingId",id)
		    	      .build();
		    	  ingreds.add(json);
		      }
		      rs.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException se){
		      }// do nothing
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		   String json_list = ingreds.toString();
		   System.out.println("Goodbye!");
		   
	    return Response.ok() //200
				.entity(json_list)
				.header("Access-Control-Allow-Origin", "*").build();
	}
	

	@GET
	@Path("/get_recipe")
	@Produces(MediaType.APPLICATION_JSON)
	public Response recipes() {
		Connection conn = null;
		Statement stmt = null;
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		ArrayList<JsonObject> recipes = new ArrayList<JsonObject>(); 
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("org.postgresql.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, USER, PASS);
		      System.out.println("Connected database successfully...");
		      
		      //STEP 4: Execute a query
		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();

		      String sql = "SELECT RECIPE_NAME.NAME, RECIPE.DATE FROM RECIPE INNER JOIN "
		      		+ "RECIPE_NAME ON RECIPE_NAME.RECIPE_NAME_ID = RECIPE.RECIPE_NAME_ID"
		      		+ " ORDER BY RECIPE.RECIPE_ID DESC";
		      ResultSet rs = stmt.executeQuery(sql);
		      
		      //STEP 5: Extract data from result set
		      while(rs.next()){
		         //Retrieve by column name
		    	  String rec_name = rs.getString("NAME");
		    	  String rec_date = rs.getString("DATE");
		    	  JsonObject json = factory.createObjectBuilder()
		    	      .add("recName",rec_name)
		    	      .add("recDate",rec_date)
		    	      .build();
		    	  recipes.add(json);
		      }
		      rs.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException se){
		      }// do nothing
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		   String json_list = recipes.toString();
		   System.out.println(json_list);
		   System.out.println("Goodbye!");
		   
	    return Response.ok() //200
				.entity(json_list)
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	@GET
	@Path("/get_recipe_date/{recipe_date}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response recipe_name_id(@PathParam("recipe_name") String recipe_name) {
		Connection conn = null;
		Statement stmt = null;
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		ArrayList<JsonObject> ID = new ArrayList<JsonObject>();
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("org.postgresql.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, USER, PASS);
		      System.out.println("Connected database successfully...");
		      
		      //STEP 4: Execute a query
		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();

		      String sql = "SELECT RECIPE_NAME_ID FROM RECIPE_NAME WHERE NAME = \'" + recipe_name + "\'";
		      ResultSet rs = stmt.executeQuery(sql);
		      
		      //STEP 5: Extract data from result set
		      while(rs.next()){
		         //Retrieve by column name
		    	  String rec_id = rs.getString("RECIPE_NAME_ID");
		    	  JsonObject json = factory.createObjectBuilder()
		    	      .add("recNameID",rec_id)
		    	      .build();
		    	  ID.add(json);
		      }
		      rs.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException se){
		      }// do nothing
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		   String jsonS = ID.toString();
		   System.out.println("Goodbye!");
		   
	    return Response.ok() //200
				.entity(jsonS)
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	@GET
	@Path("/get_boil/{recipe_date}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response boil(@PathParam("recipe_date") String recipe_date) {
		Connection conn = null;
		Statement stmt = null;
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		ArrayList<JsonObject> boil = new ArrayList<JsonObject>();
		
		
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("org.postgresql.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, USER, PASS);
		      System.out.println("Connected database successfully...");
		      
		      //STEP 4: Execute a query
		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      
		      String sql = "SELECT BOIL_ID FROM RECIPE WHERE DATE = \'" + recipe_date + "\'";
		      System.out.println(sql);
		      ResultSet rs = stmt.executeQuery(sql);
		      rs.next();
		      String boil_id = Integer.toString(rs.getInt("BOIL_ID"));
		      
		      sql = "SELECT TIME,ACTION FROM BOIL WHERE BOIL_ID = " + boil_id;
		      rs = stmt.executeQuery(sql);
		      
		      //STEP 5: Extract data from result set
		      while(rs.next()){
		         //Retrieve by column name
		    	  String time = rs.getString("TIME");
		    	  String action = rs.getString("ACTION");
		    	  JsonObject json = factory.createObjectBuilder()
		    	      .add("time",time)
		    	      .add("action",action)
		    	      .build();
		    	  boil.add(json);
		      }
		      rs.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException se){
		      }// do nothing
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		   String json_list = boil.toString();
		   System.out.println("Goodbye!");
		   
	    return Response.ok() //200
				.entity(json_list)
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	@POST
	@Path("/post_recipe")
	@Produces(MediaType.APPLICATION_JSON)
	public Response recipes(String clientStuff) {

		Connection conn = null;
		Statement stmt = null;
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		ArrayList<JsonObject> recipes = new ArrayList<JsonObject>(); 
		JSONObject jsonOb = new JSONObject(clientStuff);
		
		System.out.println(jsonOb.toString());
		String nm = jsonOb.getString("name");
		String dt = jsonOb.getString("date");
		System.out.println(nm + " " + dt);
		int b_id = 0;
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("org.postgresql.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, USER, PASS);
		      System.out.println("Connected database successfully...");
		      
		      //STEP 4: Execute a query
		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();

		      String sql = "SELECT * FROM RECIPE_NAME";
		      ResultSet rs = stmt.executeQuery(sql);
		      boolean found = false;
		      int curr_id = 1;
		      
		      //STEP 5: Extract data from result set
		      while(rs.next()){
		         //Check if name exists already
		    	  String rec_name = rs.getString("NAME");
		    	  if (nm.equals(rec_name)) {
		    		  found = true;
		    		  curr_id = rs.getInt("RECIPE_NAME_ID");
		    		  break;
		    	  }
		    	  curr_id++;
		      }
		      rs.close();
		      
		      //Get total rows in recipe table so far
	    	  int total = -1;
	    	  sql = "SELECT COUNT (*) FROM RECIPE";
	    	  rs = stmt.executeQuery(sql);
	    	  rs.next();
	    	  total = rs.getInt("COUNT") + 1;
		      
		      if (found) {
		    	  
		    	  //Create new Recipe entry with correct recipe_name_id and recipe_id
		    	  sql = "INSERT INTO RECIPE (recipe_id, recipe_name_id, date) VALUES "
		    	  		+ "(" + total + "," + curr_id + ",\'" + dt + "\')";
		    	  stmt.executeUpdate(sql);	    	  
		    	  
		      } else {
		    	  
		    	  //Insert new Recipe_Name entry
		    	  sql = "INSERT INTO RECIPE_NAME (name, recipe_name_id) VALUES (\'" + nm + "\'," + curr_id + ")";
		    	  System.out.println(sql);
		    	  stmt.executeUpdate(sql);
		    	  
		    	  //Create new Recipe entry with correct recipe_name_id and recipe_id
		    	  sql = "INSERT INTO RECIPE (recipe_id, recipe_name_id, date) VALUES "
			    	  		+ "(" + total + "," + curr_id + ",\'" + dt + "\')";
		    	  System.out.println(sql);
		    	  stmt.executeUpdate(sql);	 
		      }
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException se){
		      }// do nothing
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		   String json_list = recipes.toString();
		   System.out.println("Goodbye!");
		   
	    return Response.ok() //200
	    		.entity(clientStuff)
				.header("Access-Control-Allow-Origin", "*").build();
	}
}