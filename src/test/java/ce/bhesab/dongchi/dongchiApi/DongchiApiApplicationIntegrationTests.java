package ce.bhesab.dongchi.dongchiApi;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class DongchiApiApplicationIntegrationTests {

	@Autowired
	private MockMvc mockMvc;


	@Test
	public void testRegistration() throws Exception {
		String registrationRequest = """
				{"username":"username123", "password":"password123","email":"email@domain.com"}
				""";

		mockMvc.perform(MockMvcRequestBuilders.post("/user/registration")
				.content(registrationRequest)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void testAuthentication() throws Exception {
		// Assuming there is a user in the database with the following details
		testRegistration();
		String authenticationRequest = """
					{"username":"username123", "password":"password123"}
				""";

		mockMvc.perform(MockMvcRequestBuilders.post("/user/authentication")
				.content(authenticationRequest)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(header().exists("Authorization"));
	}

}
