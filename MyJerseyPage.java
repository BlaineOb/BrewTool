package p1;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
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
	@Path("/get_ingredient/{recID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ingredients(@PathParam("recID") String recID) {
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

		      String sql = "SELECT INGREDIENT.NAME, RECIPE_INGREDIENT.AMOUNT, INGREDIENT.TYPE FROM "
		      		+ "INGREDIENT INNER JOIN RECIPE_INGREDIENT ON INGREDIENT.ID = RECIPE_INGREDIENT.INGREDIENT_ID "
		      		+ "AND RECIPE_ID = " + recID + " ORDER BY INGREDIENT.TYPE";
		      System.out.println(sql);
		      ResultSet rs = stmt.executeQuery(sql);
		      //STEP 5: Extract data from result set
		      while(rs.next()){
		         //Retrieve by column name
		    	  String name = rs.getString("name");
		    	  String amount = rs.getString("amount");
		    	  String type = rs.getString("type");
		    	  JsonObject json = factory.createObjectBuilder()
		    	      .add("name",name)
		    	      .add("amount",amount)
		    	      .add("type",type)
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
	
	@POST
	@Path("/post_ingredient")
	@Produces(MediaType.APPLICATION_JSON)
	public Response post_ingredient(String clientStuff) {

		Connection conn = null;
		Statement stmt = null;
		JSONObject jsonOb = new JSONObject(clientStuff);
		
		System.out.println(jsonOb.toString());
		String recId = jsonOb.getString("recId");
		String name = jsonOb.getString("name");
		String amount = jsonOb.getString("amount");
		String type = jsonOb.getString("type");
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
		      
		      String sql = "SELECT ID FROM INGREDIENT WHERE NAME = \'" + name + "\'";
		      ResultSet rs = stmt.executeQuery(sql);
		      if (rs.next()) {
		    	  int ing_ID = rs.getInt("ID");
		    	  sql = "INSERT INTO RECIPE_INGREDIENT VALUES (" + recId + "," + ing_ID + ",\'" + amount + "\')";
		    	  stmt.executeUpdate(sql);
		      } else {
		    	  sql = "SELECT MAX(ID) FROM INGREDIENT";
		    	  rs = stmt.executeQuery(sql);
		    	  rs.next();
		    	  int next_id = rs.getInt("MAX") + 1;
		    	  sql = "INSERT INTO INGREDIENT VALUES (\'" + name + "\'," + next_id + ",\'" + type + "\')";
		    	  stmt.executeUpdate(sql);
		    	  sql = "INSERT INTO RECIPE_INGREDIENT VALUES (" + recId + "," + next_id + ",\'" + amount + "\')";
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
		   System.out.println("Goodbye!");
		   
	    return Response.ok() //200
				.header("Access-Control-Allow-Origin", "*").build();
	}
		
	@POST
	@Path("/delete_ingredient/{recID}/{ingName}/{amount}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete_ingredients(@PathParam("recID") String recID,
									   @PathParam("ingName") String ingName,
									   @PathParam("amount") String amount) {

		Connection conn = null;
		Statement stmt = null;
		String correctName = ingName.replaceAll("\\$","%");
		String betterName = correctName.replaceAll("&","/");

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
		      
		      String sql = "SELECT ID FROM INGREDIENT WHERE NAME = \'" + betterName + "\'";
		      ResultSet rs = stmt.executeQuery(sql);
		      System.out.println(sql);
		      
		      if (rs.next()) {
		    	int ing_ID = rs.getInt("ID");
		    	sql = "DELETE FROM RECIPE_INGREDIENT WHERE RECIPE_ID = " + recID 
		    			+ " AND INGREDIENT_ID = " + ing_ID + " AND AMOUNT = \'" + amount + "\'";
			    stmt.executeUpdate(sql);
			    System.out.println(sql);
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
		   System.out.println("Goodbye!");
		   
	    return Response.ok() //200
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

		      String sql = "SELECT RECIPE_NAME.NAME, RECIPE.DATE, RECIPE.RECIPE_ID FROM RECIPE INNER JOIN "
		      		+ "RECIPE_NAME ON RECIPE_NAME.RECIPE_NAME_ID = RECIPE.RECIPE_NAME_ID"
		      		+ " ORDER BY RECIPE.RECIPE_ID DESC";
		      ResultSet rs = stmt.executeQuery(sql);
		      
		      //STEP 5: Extract data from result set
		      while(rs.next()){
		         //Retrieve by column name
		    	  String rec_name = rs.getString("NAME");
		    	  String rec_date = rs.getString("DATE");
		    	  String rec_id = rs.getString("RECIPE_ID");
		    	  JsonObject json = factory.createObjectBuilder()
		    	      .add("recName",rec_name)
		    	      .add("recDate",rec_date)
		    	      .add("recID",rec_id)
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
		      int rec_name_id = 1;
		      
		      //STEP 5: Extract data from result set
		      while(rs.next()){
		         //Check if name exists already
		    	  String rec_name = rs.getString("NAME");
		    	  if (nm.equals(rec_name)) {
		    		  found = true;
		    		  rec_name_id = rs.getInt("RECIPE_NAME_ID");
		    		  break;
		    	  }
		      }
		      rs.close();
		      
		      //Get max id in recipe table so far
	    	  int rec_id = -1;
	    	  sql = "SELECT MAX(RECIPE_ID) FROM RECIPE";
	    	  rs = stmt.executeQuery(sql);
	    	  rs.next();
	    	  rec_id = rs.getInt("MAX") + 1;
		      
		      if (found) {		    	  
		    	  //Create new Recipe entry with correct recipe_name_id and recipe_id
		    	  sql = "INSERT INTO RECIPE (recipe_id, recipe_name_id, date) VALUES "
		    	  		+ "(" + rec_id + "," + rec_name_id + ",\'" + dt + "\')";
		    	  stmt.executeUpdate(sql);	    	  	    	  
		      } else {		    	  
		    	  sql = "SELECT MAX(RECIPE_NAME_ID) FROM RECIPE_NAME";
		    	  rs = stmt.executeQuery(sql);
		    	  rs.next();
		    	  rec_name_id = rs.getInt("MAX") + 1;		    	  
		    	  //Insert new Recipe_Name entry
		    	  sql = "INSERT INTO RECIPE_NAME (name, recipe_name_id) VALUES (\'" + nm + "\'," + rec_name_id + ")";
		    	  System.out.println(sql);
		    	  stmt.executeUpdate(sql);		    	  
		    	  //Create new Recipe entry with correct recipe_name_id and recipe_id
		    	  sql = "INSERT INTO RECIPE (recipe_id, recipe_name_id, date) VALUES "
			    	  		+ "(" + rec_id + "," + rec_name_id + ",\'" + dt + "\')";
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
	
	@POST
	@Path("/delete_recipe/{recName}/{recDate}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response recipes(@PathParam("recName") String recName,
							@PathParam("recDate") String recDate) {

		Connection conn = null;
		Statement stmt = null;
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		String correct_date = recDate.replaceAll("@","/");
		

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

		      String sql = "SELECT RECIPE_NAME_ID FROM RECIPE_NAME WHERE NAME = \'" + recName  + "\'";
		      ResultSet rs = stmt.executeQuery(sql);
		      rs.next();
		      int nameID = rs.getInt("RECIPE_NAME_ID");
		      
		      sql = "SELECT RECIPE_ID FROM RECIPE WHERE RECIPE_NAME_ID = " + nameID + " AND DATE = \'" + correct_date + "\'";
		      rs = stmt.executeQuery(sql);
		      rs.next();
		      int recID = rs.getInt("RECIPE_ID");
		      System.out.println(recID);
		      
		      //STEP 5: Delete recipe data from all tables
		      sql = "DELETE FROM RECIPE_INGREDIENT WHERE RECIPE_ID = " + recID;
		      stmt.executeUpdate(sql);
		      sql = "DELETE FROM MASH WHERE RECIPE_ID = " + recID;
		      stmt.executeUpdate(sql);
		      sql = "DELETE FROM BOIL WHERE RECIPE_ID = " + recID;
		      stmt.executeUpdate(sql);
		      sql = "DELETE FROM STATS WHERE RECIPE_ID = " + recID;
		      stmt.executeUpdate(sql);
		      sql = "DELETE FROM MISCELLANEOUS WHERE RECIPE_ID = " + recID;
		      stmt.executeUpdate(sql);
		      sql = "DELETE FROM RECIPE WHERE RECIPE_ID = " + recID;
		      stmt.executeUpdate(sql);
		      
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
		   System.out.println("Goodbye!");
		   
	    return Response.ok() //200
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	@GET
	@Path("/get_boil/{recipe_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response boil(@PathParam("recipe_id") String recipe_id) {
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
		      
		      String sql = "SELECT TIME,ACTION FROM BOIL WHERE RECIPE_ID = " + recipe_id;
		      ResultSet rs = stmt.executeQuery(sql);
		      
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
	@Path("/post_boil")
	@Produces(MediaType.APPLICATION_JSON)
	public Response post_boil(String clientStuff) {

		Connection conn = null;
		Statement stmt = null;
		JSONObject jsonOb = new JSONObject(clientStuff);
		
		System.out.println(jsonOb.toString());
		String id = jsonOb.getString("id");
		String time = jsonOb.getString("time");
		String action = jsonOb.getString("action");
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

		      String sql = "INSERT INTO BOIL VALUES "
		      		+ "(\'" + time + "\',\'" + action + "\'," + id + ")";
		      System.out.println(sql);
		      stmt.executeUpdate(sql);
		      
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
		   System.out.println("Goodbye!");
		   
	    return Response.ok() //200
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	@POST
	@Path("/delete_boil/{recID}/{time}/{action}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete_boil(@PathParam("recID") String recID,
								@PathParam("time") String time,
								@PathParam("action") String action) {

		Connection conn = null;
		Statement stmt = null;	
		String correctTime = time.replaceAll("\\$","%");
		String correctAction = action.replaceAll("\\$","%");
		String betterTime = correctTime.replaceAll("&","/");
		String betterAction = correctAction.replaceAll("&","/");

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

		      String sql = "DELETE FROM BOIL WHERE RECIPE_ID = " + recID + " AND TIME = \'" 
		    		  	+ betterTime + "\' AND ACTION = \'" + betterAction + "\'";
		      System.out.println(sql);
		      stmt.executeUpdate(sql);
		      
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
		   System.out.println("Goodbye!");
		   
	    return Response.ok() //200
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	@GET
	@Path("/get_stats/{recipe_ID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_stats(@PathParam("recipe_ID") String recipe_id) {
		Connection conn = null;
		Statement stmt = null;
		JsonObject json_built = null;
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		
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
		      
		      String sql = "SELECT * FROM STATS WHERE RECIPE_ID = " + recipe_id;
		      ResultSet rs = stmt.executeQuery(sql);
		      if (rs.next()) {	      
			      //STEP 5: Extract data from result set
		    	  String og = rs.getString("OG");
			      String fg = rs.getString("FG");
			      String abv = rs.getString("ABV");
			      String atten = rs.getString("ATTENUATION");
			      JsonObject json = factory.createObjectBuilder()
			    	      .add("og",og)
			    	      .add("fg",fg)
			    	      .add("abv",abv)
			    	      .add("atten",atten)
			    	      .add("id",recipe_id)
			    	      .build();
			      json_built = json; 
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
		   System.out.println("Goodbye!");
		   String json_string = new String("[]");
		   if (json_built != null) {
			   json_string = json_built.toString();
		   }
		   
	    return Response.ok() //200
				.entity(json_string)
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	@POST
	@Path("/post_stats")
	@Produces(MediaType.APPLICATION_JSON)
	public Response post_stats(String clientStuff) {

		Connection conn = null;
		Statement stmt = null;
		JSONObject jsonOb = new JSONObject(clientStuff);
		
		System.out.println(jsonOb.toString());
		String id = jsonOb.getString("id");
		String og = jsonOb.getString("og");
		String fg = jsonOb.getString("fg");
		String abv = jsonOb.getString("abv");
		String atten = jsonOb.getString("atten");
		System.out.println(id + " " + og + " " + fg + " " + abv + " " + atten);
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

		      String sql = "INSERT INTO STATS VALUES "
		      		+ "(\'" + og + "\',\'" + fg + "\',\'" + abv + "\',\'" + atten + "\'," + id + ")";
		      System.out.println(sql);
		      stmt.executeUpdate(sql);
		      
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
		   System.out.println("Goodbye!");
		   
	    return Response.ok() //200
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	@POST
	@Path("/delete_stats/{recID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete_stats(@PathParam("recID") String recID) {

		Connection conn = null;
		Statement stmt = null;		

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

		      String sql = "DELETE FROM STATS WHERE RECIPE_ID = " + recID;
		      System.out.println(sql);
		      stmt.executeUpdate(sql);
		      
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
		   System.out.println("Goodbye!");
		   
	    return Response.ok() //200
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	
	@GET
	@Path("/get_mash/{recipe_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_mash(@PathParam("recipe_id") String recipe_id) {
		Connection conn = null;
		Statement stmt = null;
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		ArrayList<JsonObject> mash = new ArrayList<JsonObject>(); 
		
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
		      
		      String sql = "SELECT * FROM MASH WHERE RECIPE_ID = " + recipe_id;
		      ResultSet rs = stmt.executeQuery(sql);
		      while (rs.next()) {
		    	  String miStrike = rs.getString("MISTRIKE");
		    	  String miTarget = rs.getString("MITARGET");
		    	  String miActual = rs.getString("MIACTUAL");
		    	  String moStrike = rs.getString("MOSTRIKE");
		    	  String moTarget = rs.getString("MOTARGET");
		    	  String moActual = rs.getString("MOACTUAL");
		    	  JsonObject json = factory.createObjectBuilder()
		    			  .add("miStrike",miStrike)
		    			  .add("miTarget",miTarget)
		    			  .add("miActual",miActual)
		    			  .add("moStrike",moStrike)
		    			  .add("moTarget",moTarget)
		    			  .add("moActual",moActual)
		    			  .build();
		    	  mash.add(json); 
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
		   System.out.println("Goodbye!");
		   String json_string = mash.toString();
		   
	    return Response.ok() //200
				.entity(json_string)
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	@POST
	@Path("/post_mash")
	@Produces(MediaType.APPLICATION_JSON)
	public Response post_mash(String clientStuff) {

		Connection conn = null;
		Statement stmt = null;
		JSONObject jsonOb = new JSONObject(clientStuff);
		
		System.out.println(jsonOb.toString());
		String id = jsonOb.getString("id");
		String miStrike = jsonOb.getString("miStrike");
		String miTarg = jsonOb.getString("miTarg");
		String miAct = jsonOb.getString("miAct");
		String moStrike = jsonOb.getString("moStrike");
		String moTarg = jsonOb.getString("moTarg");
		String moAct = jsonOb.getString("moAct");
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

		      String sql = "INSERT INTO MASH VALUES "
		      		+ "(" + id + ",\'" + miStrike + "\',\'" + miTarg + "\',\'" + miAct + "\',\'" 
		    		+ moStrike + "\',\'" + moTarg + "\',\'" + moAct + "\')";
		      System.out.println(sql);
		      stmt.executeUpdate(sql);
		      
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
		   System.out.println("Goodbye!");
		   
	    return Response.ok() //200
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	@POST
	@Path("/delete_mash/{recID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete_mash(@PathParam("recID") String recID) {

		Connection conn = null;
		Statement stmt = null;		

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

		      String sql = "DELETE FROM MASH WHERE RECIPE_ID = " + recID;
		      System.out.println(sql);
		      stmt.executeUpdate(sql);
		      
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
		   System.out.println("Goodbye!");
		   
	    return Response.ok() //200
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	@GET
	@Path("/get_misc/{recipe_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_misc(@PathParam("recipe_id") String recipe_id) {
		Connection conn = null;
		Statement stmt = null;
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		ArrayList<JsonObject> comments = new ArrayList<JsonObject>(); 
		
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
		      
		      String sql = "SELECT * FROM MISCELLANEOUS WHERE RECIPE_ID = " + recipe_id;
		      ResultSet rs = stmt.executeQuery(sql);
		      while (rs.next()) {
		    	  String com = rs.getString("COMMENT");
		    	  JsonObject json = factory.createObjectBuilder()
		    			  .add("comment",com)
		    			  .build();
		    	  comments.add(json); 
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
		   System.out.println("Goodbye!");
		   String json_string = comments.toString();
		   
	    return Response.ok() //200
				.entity(json_string)
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	@POST
	@Path("/post_misc")
	@Produces(MediaType.APPLICATION_JSON)
	public Response post_misc(String clientStuff) {

		Connection conn = null;
		Statement stmt = null;
		JSONObject jsonOb = new JSONObject(clientStuff);
		
		System.out.println(jsonOb.toString());
		String id = jsonOb.getString("id");
		String com = jsonOb.getString("com");
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

		      String sql = "INSERT INTO MISCELLANEOUS VALUES "
		      		+ "(\'" + com + "\'," + id + ")";
		      System.out.println(sql);
		      stmt.executeUpdate(sql);
		      
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
		   System.out.println("Goodbye!");
		   
	    return Response.ok() //200
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	@POST
	@Path("/delete_misc/{recID}/{comment}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete_misc(@PathParam("recID") String recID,
								 @PathParam("comment") String comment) {

		Connection conn = null;
		Statement stmt = null;
		String correctComment = comment.replaceAll("\\$","%");
		String betterComment = correctComment.replaceAll("&","/");

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

		      String sql = "DELETE FROM MISCELLANEOUS WHERE RECIPE_ID = " + recID + " AND COMMENT = \'" + betterComment + "\'";
		      System.out.println(sql);
		      stmt.executeUpdate(sql);
		      
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
		   System.out.println("Goodbye!");
		   
	    return Response.ok() //200
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	@GET
	@Path("/get_event")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_event() {
		Connection conn = null;
		Statement stmt = null;
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		ArrayList<JsonObject> events = new ArrayList<JsonObject>(); 
		
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
		      
		      String sql = "SELECT * FROM EVENT";
		      ResultSet rs = stmt.executeQuery(sql);
		      while (rs.next()) {
		    	  int id = rs.getInt("EVENT_ID");
		    	  String title = rs.getString("EVENT_NAME");
		    	  String start = rs.getString("START_DATE");
		    	  String end = rs.getString("END_DATE");
		    	  
		    	  String[] start_split = start.split("/");
		    	  String[] end_split = end.split("/");
		    	  int[] tmp = new int[start_split.length];
		    	  
		    	  for (int i = 0; i < start_split.length; i++) {
		    	         String numberAsString = start_split[i];
		    	         tmp[i] = Integer.parseInt(numberAsString);
		    	  }
		    	  String startt = "new Date(" + tmp[2] + "," + (tmp[0] - 1) + "," + tmp[1] + ",0,0,0)";
		    	  for (int i = 0; i < end_split.length; i++) {
		    	         String numberAsString = end_split[i];
		    	         tmp[i] = Integer.parseInt(numberAsString);
		    	  }
		    	  String endt = "new Date(" + tmp[2] + "," + (tmp[0] - 1) + "," + tmp[1] + ",0,0,0)";
		    	  
		    	  JsonObject json = factory.createObjectBuilder()
		    			  .add("id",id)
		    			  .add("title",title)
		    			  .add("start",start)
		    			  .add("end",end)
		    			  .build();
		    	  events.add(json); 
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
		   System.out.println("Goodbye!");
		   String json_string = events.toString();
		   
	    return Response.ok() //200
				.entity(json_string)
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	@POST
	@Path("/post_event")
	@Produces(MediaType.APPLICATION_JSON)
	public Response post_event(String clientStuff) {

		Connection conn = null;
		Statement stmt = null;
		JSONObject jsonOb = new JSONObject(clientStuff);
		
		int id = jsonOb.getInt("id");
		String title = jsonOb.getString("title");
		String start = jsonOb.getString("start");
		String end = jsonOb.getString("end");
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

		      String sql = "INSERT INTO EVENT VALUES "
		      		+ "(" + id + ",\'" + title + "\',\'"+ start + "\',\'" + end + "\')";
		      System.out.println(sql);
		      stmt.executeUpdate(sql);
		      
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
		   System.out.println("Goodbye!");
		   
	    return Response.ok() //200
				.header("Access-Control-Allow-Origin", "*").build();
	}
	
	@POST
	@Path("/delete_event/{evID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete_event(@PathParam("evID") String evID) {

		Connection conn = null;
		Statement stmt = null;		

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

		      String sql = "DELETE FROM EVENT WHERE EVENT_ID = " + evID;
		      System.out.println(sql);
		      stmt.executeUpdate(sql);
		      
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
		   System.out.println("Goodbye!");
		   
	    return Response.ok() //200
				.header("Access-Control-Allow-Origin", "*").build();
	}
}
