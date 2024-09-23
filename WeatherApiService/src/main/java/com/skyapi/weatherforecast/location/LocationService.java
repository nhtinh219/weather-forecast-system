package com.skyapi.weatherforecast.location;

import org.springframework.stereotype.Service;

import com.skyapi.weatherforecast.common.Location;

@Service
public class LocationService {

	private LocationRepository repository;

	public LocationService(LocationRepository repository) {
		super();
		this.repository = repository;
	}
	
	public Location addLocation(Location location) {
		return repository.save(location);
	}
	
}
