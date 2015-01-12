package com.osm.geo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.osm.geo.dao.DAOServices;
import com.osm.geo.model.ResponseWs;
import com.osm.geo.model.Restaurant;

@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class AppManagerImpl implements AppManager {
	@Autowired
	DAOServices daoServices;

	public DAOServices getDaoServices() {
		return daoServices;
	}

	public void setDaoServices(DAOServices daoServices) {
		this.daoServices = daoServices;
	}

	@Override
	public ResponseWs fetchEntityById() {
		ResponseWs response = new ResponseWs();
		try {
			response.setRestaurants(daoServices.getEntities(Restaurant.class));
		} catch (Exception e) {
			e.printStackTrace();
			response.setError(e.getMessage());
		}
		return response;
	}

	@Override
	public boolean updateEntity(int id, double prix) {
		Restaurant restaurant = daoServices.getEntityById(Restaurant.class, id);
		restaurant.setPrix(prix);
		try {
			daoServices.updateEntity(restaurant);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
