package Controller;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import Model.Ingredient;
import Model.Recipe;
import java.sql.*;
import javax.json.*;
import java.util.ArrayList;

import Service.DataService;
import org.json.*;

import static Utils.Constants.*;

@Path("/home")
public class BrewToolController {

	private final DataService dataService = new DataService();
	private final JsonObjectExtractor extractor = new JsonObjectExtractor();
	private final ControllerLogic logic = new ControllerLogic(dataService);
	
	@GET @Path("/hello") @Produces(MediaType.TEXT_HTML)
	public String healthCheck() {
		return "Hello from Jersey";
	}
	
	@GET @Path("/get_ingredient/{recID}") @Produces(MediaType.APPLICATION_JSON)
	public Response getIngredients(@PathParam("recID") String recID) {
		String sql = "SELECT INGREDIENT.NAME, RECIPE_INGREDIENT.AMOUNT, INGREDIENT.TYPE FROM "
			   + "INGREDIENT INNER JOIN RECIPE_INGREDIENT ON INGREDIENT.ID = RECIPE_INGREDIENT.INGREDIENT_ID "
			   + "AND RECIPE_ID = " + recID + " ORDER BY INGREDIENT.TYPE";
		ResultSet rs = dataService.queryDatabase(sql);
		String[] columnNames = {"name","amount","type"};
		String json_list = extractor.extractJSONList(rs,columnNames,columnNames).toString();
		return Response.ok().entity(json_list).header("Access-Control-Allow-Origin", "*").build();
	}

	@POST @Path("/post_ingredient") @Produces(MediaType.APPLICATION_JSON)
	public Response postIngredient(String clientInput) throws SQLException {
		Ingredient ingredient = logic.createIngredient(clientInput);
		String sql = "SELECT ID FROM INGREDIENT WHERE NAME = \'" + ingredient.getName() + "\'";
		ResultSet rs = dataService.queryDatabase(sql);
		logic.insertIngredient(rs,ingredient);
		return Response.ok().header("Access-Control-Allow-Origin", "*").build();
	}
		
	@POST @Path("/delete_ingredient/{recID}/{ingName}/{amount}") @Produces(MediaType.APPLICATION_JSON)
	public Response deleteIngredients(@PathParam("recID") String recID,
									  @PathParam("ingName") String ingName,
									  @PathParam("amount") String amount) throws SQLException {
		String correctName = logic.fixEscapedName(ingName);
		String sql = "SELECT ID FROM INGREDIENT WHERE NAME = \'" + correctName + "\'";
		ResultSet rs = dataService.queryDatabase(sql);
		logic.removeIngredient(recID, amount, rs);
		return Response.ok().header("Access-Control-Allow-Origin", "*").build();
	}

	@GET @Path("/get_recipe") @Produces(MediaType.APPLICATION_JSON)
	public Response getRecipe() {
		String sql = "SELECT RECIPE_NAME.NAME, RECIPE.DATE, RECIPE.RECIPE_ID FROM RECIPE INNER JOIN "
				+ "RECIPE_NAME ON RECIPE_NAME.RECIPE_NAME_ID = RECIPE.RECIPE_NAME_ID"
				+ " ORDER BY RECIPE.RECIPE_ID DESC";
		ResultSet rs = dataService.queryDatabase(sql);
		String[] columnNames = {"NAME","DATE","RECIPE_ID"};
		String[] propertyNames = {"recName","recDate","recID"};
		String json_list = extractor.extractJSONList(rs,columnNames,propertyNames).toString();
		return Response.ok().entity(json_list).header("Access-Control-Allow-Origin", "*").build();
	}

	@POST @Path("/post_recipe") @Produces(MediaType.APPLICATION_JSON)
	public Response postRecipe(String clientInput) throws SQLException {
		Recipe recipe = logic.createRecipe(clientInput);
		String sql = "SELECT * FROM RECIPE_NAME WHERE NAME=\'" + recipe.getName() + "\'"; //TODO: check this works
		ResultSet rs = dataService.queryDatabase(sql);
		logic.insertRecipe(rs,recipe);
	    return Response.ok().entity(clientInput).header("Access-Control-Allow-Origin", "*").build();
	}
	
	@POST @Path("/delete_recipe/{recName}/{recDate}") @Produces(MediaType.APPLICATION_JSON)
	public Response deleteRecipe(@PathParam("recName") String recName,
								 @PathParam("recDate") String recDate) throws SQLException {
		String correct_date = recDate.replaceAll("@","/");
		logic.deleteRecipe(recName, correct_date);
	    return Response.ok().header("Access-Control-Allow-Origin", "*").build();
	}

	@GET @Path("/get_boil/{recipe_id}") @Produces(MediaType.APPLICATION_JSON)
	public Response getBoil(@PathParam("recipe_id") String recipe_id) {
		Connection conn = null;
		Statement stmt = null;
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		ArrayList<JsonObject> boil = new ArrayList<JsonObject>();
		
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
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
	
	@POST @Path("/post_boil") @Produces(MediaType.APPLICATION_JSON)
	public Response postBoil(String clientStuff) {

		Connection conn = null;
		Statement stmt = null;
		JSONObject jsonOb = new JSONObject(clientStuff);
		
		System.out.println(jsonOb.toString());
		String id = jsonOb.getString("id");
		String time = jsonOb.getString("time");
		String action = jsonOb.getString("action");
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
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
	
	@POST @Path("/delete_boil/{recID}/{time}/{action}") @Produces(MediaType.APPLICATION_JSON)
	public Response deleteBoil(@PathParam("recID") String recID,
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
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
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
	
	@GET @Path("/get_stats/{recipe_ID}") @Produces(MediaType.APPLICATION_JSON)
	public Response getStats(@PathParam("recipe_ID") String recipe_id) {
		Connection conn = null;
		Statement stmt = null;
		JsonObject json_built = null;
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
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
	
	@POST @Path("/post_stats") @Produces(MediaType.APPLICATION_JSON)
	public Response postStats(String clientStuff) {

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
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
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
	
	@POST @Path("/delete_stats/{recID}") @Produces(MediaType.APPLICATION_JSON)
	public Response deleteStats(@PathParam("recID") String recID) {

		Connection conn = null;
		Statement stmt = null;		

		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
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
	
	
	@GET @Path("/get_mash/{recipe_id}") @Produces(MediaType.APPLICATION_JSON)
	public Response getMash(@PathParam("recipe_id") String recipe_id) {
		Connection conn = null;
		Statement stmt = null;
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		ArrayList<JsonObject> mash = new ArrayList<JsonObject>(); 
		
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
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
	
	@POST @Path("/post_mash") @Produces(MediaType.APPLICATION_JSON)
	public Response postMash(String clientStuff) {

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
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
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
	
	@POST @Path("/delete_mash/{recID}") @Produces(MediaType.APPLICATION_JSON)
	public Response deleteMash(@PathParam("recID") String recID) {

		Connection conn = null;
		Statement stmt = null;		

		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
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
	
	@GET @Path("/get_misc/{recipe_id}") @Produces(MediaType.APPLICATION_JSON)
	public Response getMisc(@PathParam("recipe_id") String recipe_id) {
		Connection conn = null;
		Statement stmt = null;
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		ArrayList<JsonObject> comments = new ArrayList<JsonObject>(); 
		
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
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
	
	@POST @Path("/post_misc") @Produces(MediaType.APPLICATION_JSON)
	public Response postMisc(String clientStuff) {

		Connection conn = null;
		Statement stmt = null;
		JSONObject jsonOb = new JSONObject(clientStuff);
		
		System.out.println(jsonOb.toString());
		String id = jsonOb.getString("id");
		String com = jsonOb.getString("com");
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
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
	
	@POST @Path("/delete_misc/{recID}/{comment}") @Produces(MediaType.APPLICATION_JSON)
	public Response deleteMisc(@PathParam("recID") String recID,
							   @PathParam("comment") String comment) {

		Connection conn = null;
		Statement stmt = null;
		String betterComment = logic.fixEscapedName(comment);

		try{
		      //STEP 2: Register JDBC driver
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
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
	
	@GET @Path("/get_event") @Produces(MediaType.APPLICATION_JSON)
	public Response getEvent() {
		Connection conn = null;
		Statement stmt = null;
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		ArrayList<JsonObject> events = new ArrayList<JsonObject>(); 
		
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
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
	
	@POST @Path("/post_event") @Produces(MediaType.APPLICATION_JSON)
	public Response postEvent(String clientStuff) {

		Connection conn = null;
		Statement stmt = null;
		JSONObject jsonOb = new JSONObject(clientStuff);
		
		int id = jsonOb.getInt("id");
		String title = jsonOb.getString("title");
		String start = jsonOb.getString("start");
		String end = jsonOb.getString("end");
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
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
	
	@POST @Path("/delete_event/{evID}") @Produces(MediaType.APPLICATION_JSON)
	public Response deleteEvent(@PathParam("evID") String evID) {

		Connection conn = null;
		Statement stmt = null;		

		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
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
