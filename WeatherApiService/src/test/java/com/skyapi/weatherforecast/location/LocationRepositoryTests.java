package com.skyapi.weatherforecast.location;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.skyapi.weatherforecast.common.Location;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class LocationRepositoryTests {
	
	@Autowired
	private LocationRepository repository;
	
	@Test
	public void testAddSuccess() {
		Location location = new Location();
		location.setCityName("Da Nang");
		location.setCode("DN");
		location.setRegionName("SEA");
		location.setCountryCode("VN");
		location.setCountryName("Viet Nam");
		location.setEnabled(true);
		
		Location savedLocation = repository.save(location);
		
		assertThat(savedLocation).isNotNull();
		assertThat(savedLocation.getCode()).isEqualTo("DN");
	}
	
	@Test
	public void testListSuccess() {
		List<Location> listLocations = repository.findUntrashed();
		
		assertThat(listLocations).isNotEmpty();
		
		listLocations.forEach(System.out::println);
	}
	
	@Test
	public void testGetNotFound() {
		String code = "GG";
		Location location = repository.findByCode(code);
		
		assertThat(location).isNull();
	}
	
	@Test
	public void testGetFound() {
		String code = "SHH";
		Location location = repository.findByCode(code);
		
		assertThat(location).isNotNull();
		assertThat(location.getCode()).isEqualTo("SHH");
	}
	
	@Test
	public void testTrashSuccess() {
		String code = "DN";
		repository.trashByCode(code);
		
		Location location = repository.findByCode(code);
		
		assertThat(location).isNull();
	}

}
