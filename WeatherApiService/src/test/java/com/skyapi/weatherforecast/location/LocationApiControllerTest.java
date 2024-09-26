package com.skyapi.weatherforecast.location;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

}
