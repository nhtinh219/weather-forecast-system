package com.skyapi.weatherforecast.location;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.common.Location;

@WebMvcTest(LocationApiController.class)
public class LocationApiControllerTest {

	private final static String END_POINT_PATH = "/v1/locations";

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper mapper;

	@MockBean
	LocationService locationService;

	@Test
	public void testAddShouldReturn400BadRequest() throws Exception {
		Location location = new Location();

		String bodyContent = mapper.writeValueAsString(location);

		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
				.andExpect(status().isBadRequest())
				.andDo(print());
	}

	@Test
	public void testAddShouldReturn201Created() throws Exception {

		Location location = new Location();
		location.setCityName("Ho Chi Minh");
		location.setCode("HCM");
		location.setRegionName("SEA");
		location.setCountryCode("VN");
		location.setCountryName("Viet Nam");
		location.setEnabled(true);

		Mockito.when(locationService.add(location)).thenReturn(location);

		String bodyContent = mapper.writeValueAsString(location);

		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
		.andExpect(status().isCreated())
		.andExpect(content().contentType("application/json"))
		.andExpect(jsonPath("$.code", is("HCM")))
		.andExpect(jsonPath("$.city_name", is("Ho Chi Minh")))
		.andExpect(header().string("Location", "/v1/locations/HCM"))
		.andDo(print());
	}
	
	@Test
	public void testListShouldReturn204NoContent() throws Exception {
		
		Mockito.when(locationService.list()).thenReturn(Collections.emptyList());
		
		mockMvc.perform(get(END_POINT_PATH))
		.andExpect(status().isNoContent())
		.andDo(print());
	}
	
	@Test
	public void testListShouldReturn200Ok() throws Exception {
		
		Location location1 = new Location();
		location1.setCityName("Ho Chi Minh");
		location1.setCode("HCM");
		location1.setRegionName("SEA");
		location1.setCountryCode("VN");
		location1.setCountryName("Viet Nam");
		location1.setEnabled(true);
		
		Location location2 = new Location();
		location2.setCityName("New York");
		location2.setCode("NYK");
		location2.setRegionName("USA");
		location2.setCountryCode("USA");
		location2.setCountryName("United State");
		location2.setEnabled(true);
		
		Location location3 = new Location();
		location3.setCityName("Shanghai");
		location3.setCode("SHH");
		location3.setRegionName("ASIA");
		location3.setCountryCode("CN");
		location3.setCountryName("China");
		location3.setEnabled(true);
		location3.setTrashed(true);
		
		Mockito.when(locationService.list()).thenReturn(List.of(location1, location2));
		
		mockMvc.perform(get(END_POINT_PATH))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$[0].code", is("HCM")))
			.andExpect(jsonPath("$[0].city_name", is("Ho Chi Minh")))
			.andExpect(jsonPath("$[1].code", is("NYK")))
			.andExpect(jsonPath("$[1].city_name", is("New York")))
			.andDo(print());
			
	}

	@Test
	public void testGetShouldReturn405MethodNotAllowed() throws Exception {
		String requestURI = END_POINT_PATH + "/AAAAA";
		
		mockMvc.perform(post(requestURI))
			.andExpect(status().isMethodNotAllowed())
			.andDo(print());
	}
	
	@Test
	public void testGetShouldReturn404NotFound() throws Exception {
		String requestURI = END_POINT_PATH + "/AAAAA";
				
		mockMvc.perform(get(requestURI))
			.andExpect(status().isNotFound())
			.andDo(print());
	}
	
	@Test
	public void testGetShouldReturn200Ok() throws Exception {
		String code = "DN";
		String requestURI = END_POINT_PATH + "/" + code;
		
		Location location = new Location();
		location.setCityName("Da Nang");
		location.setCode("DN");
		location.setRegionName("SEA");
		location.setCountryCode("VN");
		location.setCountryName("Viet Nam");
		location.setEnabled(true);
		
		Mockito.when(locationService.get(code)).thenReturn(location);
				
		mockMvc.perform(get(requestURI))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.code", is("DN")))
			.andExpect(jsonPath("$.city_name", is("Da Nang")))
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn404NotFound() throws Exception {
		Location location = new Location();
		location.setCode("ABC");
		location.setCityName("Da Nang");
		location.setRegionName("SEA");
		location.setCountryCode("VN");
		location.setCountryName("Viet Nam");
		location.setEnabled(true);
		
		Mockito.when(locationService.update(location)).thenThrow(new LocationNotFoundException("No location found"));
		
		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
			.andExpect(status().isNotFound())
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn400BadRequest() throws Exception {
		Location location = new Location();
		location.setCityName("Da Nang");
		location.setRegionName("SEA");
		location.setCountryCode("VN");
		location.setCountryName("Viet Nam");
		location.setEnabled(true);
				
		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturn200Ok() throws Exception {

		Location location = new Location();
		location.setCityName("Da Nag");
		location.setCode("DN");
		location.setRegionName("ASIA");
		location.setCountryCode("VNs");
		location.setCountryName("VietNam");
		location.setEnabled(false);

		Mockito.when(locationService.update(location)).thenReturn(location);

		String bodyContent = mapper.writeValueAsString(location);

		mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
		.andExpect(status().isOk())
		.andExpect(content().contentType("application/json"))
		.andExpect(jsonPath("$.code", is("DN")))
		.andExpect(jsonPath("$.city_name", is("Da Nag")))
		.andExpect(jsonPath("$.region_name", is("ASIA")))
		.andExpect(jsonPath("$.country_name", is("VietNam")))
		.andExpect(jsonPath("$.country_code", is("VNs")))
		.andExpect(jsonPath("$.enabled", is(false)))
		.andDo(print());
	}
	
	@Test
	public void testDeleteShouldReturn404NotFound() throws Exception {
		String code = "AAA";
		String requestURI = END_POINT_PATH + "/" + code;
		
		Mockito.doThrow(LocationNotFoundException.class).when(locationService).delete(code);
		
		mockMvc.perform(delete(requestURI))
			.andExpect(status().isNotFound())
			.andDo(print());
	}
	
	@Test
	public void testDeleteShouldReturn204NoContent() throws Exception {
		String code = "DN";
		String requestURI = END_POINT_PATH + "/" + code;
		
		Mockito.doNothing().when(locationService).delete(code);
		
		mockMvc.perform(delete(requestURI))
			.andExpect(status().isNoContent())
			.andDo(print());
	}
}
