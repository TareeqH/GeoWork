package com.osm.geo.model;

import java.util.List;

public class ResponseWs {

	List<Restaurant> restaurants;
	String error;

	public List<Restaurant> getRestaurants() {
		return restaurants;
	}

	public void setRestaurants(List<Restaurant> restaurants) {
		this.restaurants = restaurants;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
