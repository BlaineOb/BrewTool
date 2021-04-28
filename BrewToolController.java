package Controller;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;

import Service.BrewToolService;
import Service.DataService;

@Path("/home")
public class BrewToolController {

	private final DataService dataService = new DataService();
	private final BrewToolService brewToolService = new BrewToolService(dataService);

	private Response getBasicSuccessResponse() {
		return Response.ok().header("Access-Control-Allow-Origin", "*").build();
	}

	private Response getSuccessResponseWithEntity(String entity) {
		return Response.ok().entity(entity).header("Access-Control-Allow-Origin", "*").build();
	}

	@GET
	@Path("/hello")
	@Produces(MediaType.TEXT_HTML)
	public String sayHtmlHello() {
		return "Hello from BrewTool!";
	}
	
	@GET
	@Path("/get_ingredient/{recipeId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIngredients(@PathParam("recipeId") String recipeId) throws SQLException {
		String ingredients = brewToolService.getIngredients(recipeId);
		return getSuccessResponseWithEntity(ingredients);
	}

	@POST
	@Path("/post_ingredient")
	@Produces(MediaType.APPLICATION_JSON)
	public Response postIngredient(String ingredientData) throws SQLException {
		brewToolService.postIngredient(ingredientData);
		return getBasicSuccessResponse();
	}

	@POST
	@Path("/delete_ingredient/{recipeId}/{ingName}/{amount}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteIngredients(@PathParam("recipeId") String recipeId,
									  @PathParam("ingName") String ingName,
									  @PathParam("amount") String amount) throws SQLException {
		brewToolService.deleteIngredient(recipeId, ingName, amount);
		return getBasicSuccessResponse();
	}

	@GET
	@Path("/get_recipe")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRecipes() throws SQLException {
		String recipes = brewToolService.getRecipes();
		return getSuccessResponseWithEntity(recipes);
	}

	@POST
	@Path("/post_recipe")
	@Produces(MediaType.APPLICATION_JSON)
	public Response postRecipe(String recipeData) throws SQLException {
		brewToolService.postRecipe(recipeData);
	    return getSuccessResponseWithEntity(recipeData);
	}
	
	@POST
	@Path("/delete_recipe/{recName}/{recDate}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteRecipe(@PathParam("recName") String recName,
							@PathParam("recDate") String recDate) throws SQLException {
		brewToolService.deleteRecipe(recDate, recName);
	    return getBasicSuccessResponse();
	}

	@GET
	@Path("/get_boil/{recipeId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBoil(@PathParam("recipeId") String recipeId) throws SQLException {
	    return getSuccessResponseWithEntity(brewToolService.getBoil(recipeId));
	}

	@POST
	@Path("/post_boil")
	@Produces(MediaType.APPLICATION_JSON)
	public Response postBoil(String boilData) {
		brewToolService.postBoil(boilData);
	    return getBasicSuccessResponse();
	}
	
	@POST
	@Path("/delete_boil/{recipeId}/{time}/{action}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteBoil(@PathParam("recipeId") String recipeId,
							   @PathParam("time") String time,
							   @PathParam("action") String action) {
		brewToolService.deleteBoil(recipeId, time, action);
	    return getBasicSuccessResponse();
	}
	
	@GET
	@Path("/get_stats/{recipeId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStats(@PathParam("recipeId") String recipeId) throws SQLException {
	    return getSuccessResponseWithEntity(brewToolService.getStats(recipeId));
	}
	
	@POST
	@Path("/post_stats")
	@Produces(MediaType.APPLICATION_JSON)
	public Response postStats(String statsData) {
		brewToolService.postStats(statsData);
	    return getBasicSuccessResponse();
	}
	
	@POST
	@Path("/delete_stats/{recipeId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteStats(@PathParam("recipeId") String recipeId) {
		brewToolService.deleteStats(recipeId);
	    return getBasicSuccessResponse();
	}
	
	@GET
	@Path("/get_mash/{recipeId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMash(@PathParam("recipeId") String recipeId) throws SQLException {
		String mash = brewToolService.getMash(recipeId);
	    return getSuccessResponseWithEntity(mash);
	}
	
	@POST
	@Path("/post_mash")
	@Produces(MediaType.APPLICATION_JSON)
	public Response postMash(String mashData) {
		brewToolService.postMash(mashData);
	    return getBasicSuccessResponse();
	}
	
	@POST
	@Path("/delete_mash/{recipeId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteMash(@PathParam("recipeId") String recipeId) {
		brewToolService.deleteMash(recipeId);
	    return getBasicSuccessResponse();
	}
	
	@GET
	@Path("/get_misc/{recipeId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMisc(@PathParam("recipeId") String recipeId) throws SQLException {
		String misc = brewToolService.getMisc(recipeId);
	    return getSuccessResponseWithEntity(misc);
	}
	
	@POST
	@Path("/post_misc")
	@Produces(MediaType.APPLICATION_JSON)
	public Response postMisc(String miscData) {
		brewToolService.postMisc(miscData);
	    return getBasicSuccessResponse();
	}
	
	@POST
	@Path("/delete_misc/{recipeId}/{comment}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteMisc(@PathParam("recipeId") String recipeId,
							   @PathParam("comment") String comment) {
		brewToolService.deleteMisc(recipeId, comment);
	    return getBasicSuccessResponse();
	}
	
	@GET
	@Path("/get_event")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEvents() throws SQLException {
		String events = brewToolService.getEvents();
	    return getSuccessResponseWithEntity(events);
	}
	
	@POST
	@Path("/post_event")
	@Produces(MediaType.APPLICATION_JSON)
	public Response postEvent(String eventData) {
		brewToolService.postEvent(eventData);
	    return getBasicSuccessResponse();
	}
	
	@POST
	@Path("/delete_event/{eventID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEvent(@PathParam("eventID") String eventID) {
		brewToolService.deleteEvent(eventID);
	    return getBasicSuccessResponse();
	}
}