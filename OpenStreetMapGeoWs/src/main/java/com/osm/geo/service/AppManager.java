package com.osm.geo.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.osm.geo.model.ResponseWs;

@Consumes("application/json")
@Produces("application/json")
public interface AppManager {
	@GET
	@Path("/getAll")
	public ResponseWs fetchEntityById();

	@GET
	@Path("/updateRestaurant/{id}/{value}")
	public boolean updateEntity(@PathParam("id") int id,
			@PathParam("value") double prix);
}
