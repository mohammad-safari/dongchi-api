package ce.bhesab.dongchi.dongchiApi;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import ce.bhesab.dongchi.dongchiApi.service.group.model.GroupModel;
import ce.bhesab.dongchi.dongchiApi.service.user.model.UserModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
public class DongchiApiApplicationIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	public void testRegistration() throws Exception {
		var registrationRequest = """
				{"username":"username123", "password":"password123","email":"email@domain.com"}
				""";

		mockMvc.perform(MockMvcRequestBuilders.post("/user/registration")
				.content(registrationRequest)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		entityManager.contains(UserModel.builder()
				.username("username123")
				.password(passwordEncoder.encode("password123")).build());
	}

	@Test
	@Transactional
	public void testAuthentication() throws Exception {
		// Assuming there is a user in the database with the following details
		entityManager.persist(UserModel.builder()
				.username("username123")
				.email("email@domain.com")
				.password(passwordEncoder.encode("password123")).build());
		entityManager.flush();
		entityManager.clear();

		var authenticationRequest = """
					{"username":"username123", "password":"password123"}
				""";

		mockMvc.perform(MockMvcRequestBuilders.post("/user/authentication")
				.content(authenticationRequest)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(header().exists("Authorization"))
				.andExpect(header().string("Authorization", "Basic " +
						Base64.getEncoder().encodeToString("username123:password123".getBytes())));
	}

	@Test
	@Transactional
	public void testGroupCreation() throws Exception {
		// Assuming there is a user in the database with the following details
		entityManager.persist(UserModel.builder()
				.username("username123")
				.email("email@domain.com")
				.password(passwordEncoder.encode("password123")).build());
		entityManager.flush();
		entityManager.clear();

		var creationRequest = """
					{"groupName":"group123", "description":"test group"}
				""";

		mockMvc.perform(MockMvcRequestBuilders.post("/group")
				.header("Authorization", "Basic " +
						Base64.getEncoder().encodeToString("username123:password123".getBytes()))
				.content(creationRequest)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		entityManager.contains(GroupModel.builder()
				.groupName("group123")
				.description("test group").build());

	}
}
