package com.skyapi.weatherforecast.location;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skyapi.weatherforecast.common.Location;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/locations")
public class LocationApiController {

	private LocationService locationService;

	public LocationApiController(LocationService locationService) {
		super();
		this.locationService = locationService;
	}
	
	@PostMapping
	public ResponseEntity<Location> addLocation(@RequestBody @Valid Location location){
		Location addedLocation = locationService.add(location);
		URI uri = URI.create("/v1/locations/" + addedLocation.getCode());
		
		return ResponseEntity.created(uri).body(addedLocation);
	}
	
	@GetMapping
	public ResponseEntity<?> listLocations() {
		List<Location> listLocations = locationService.list();
		if (listLocations.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return new ResponseEntity<>(listLocations, HttpStatus.OK);
	}
	
	@GetMapping("/{code}")
	public ResponseEntity<Location> getLocation(@PathVariable String code) {
		Location location = locationService.get(code);
		if (location == null) {
			return ResponseEntity.notFound().build();
		}
		return new ResponseEntity<Location>(location, HttpStatus.OK);
	}
	
	@PutMapping
	public ResponseEntity<Location> updateLocation(@RequestBody @Valid Location location){
		try {
			Location updatedLocation = locationService.update(location);
			
			return new ResponseEntity<Location>(updatedLocation, HttpStatus.OK);
		} catch (LocationNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@DeleteMapping("/{code}")
	public ResponseEntity<?> deleteLocation(@PathVariable String code) {
		try {
			locationService.delete(code);
			
			return ResponseEntity.noContent().build();
		} catch (LocationNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
}
