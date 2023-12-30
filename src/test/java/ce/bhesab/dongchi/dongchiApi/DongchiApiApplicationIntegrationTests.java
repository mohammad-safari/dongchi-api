package ce.bhesab.dongchi.dongchiApi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ce.bhesab.dongchi.dongchiApi.service.group.model.GroupModel;
import ce.bhesab.dongchi.dongchiApi.service.user.model.UserModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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

	@Test
	@Transactional
	public void testGroupCreationWithOtherMemebers() throws Exception {
		// Assuming there is a user in the database with the following details
		UserModel creatorUser = UserModel.builder()
				.username("username123")
				.email("email@domain.com")
				.password(passwordEncoder.encode("password123")).build();
		entityManager.persist(creatorUser);
		UserModel otherUser = UserModel.builder()
				.username("username1234")
				.email("email2@domain.com")
				.password(passwordEncoder.encode("password1234")).build();
		entityManager.persist(otherUser);
		entityManager.flush();
		entityManager.clear();

		var creationRequest = """
					{"groupName":"group123", "description":"test group", "otherMembers":["username1234"]}
				""";

		mockMvc.perform(MockMvcRequestBuilders.post("/group")
				.header("Authorization", "Basic " +
						Base64.getEncoder().encodeToString("username123:password123".getBytes()))
				.content(creationRequest)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		entityManager.contains(GroupModel.builder()
				.groupName("group123")
				.description("test group")
				.members(Set.of(creatorUser, otherUser))
				.build());
	}

	@Test
	@Transactional
	public void testManyToManyRelationship() {
		// Create a user
		UserModel user = UserModel.builder()
				.username("username123")
				.email("email@domain.com")
				.groups(new HashSet<>())
				.password(passwordEncoder.encode("password123"))
				.build();

		// Create a group
		GroupModel group = GroupModel.builder()
				.groupName("group123")
				.description("test group")
				.members(new HashSet<>())
				.build();

		// Establish the many-to-many relationship
		user.getGroups().add(group);
		group.getMembers().add(user);

		// Persist entities
		entityManager.persist(user);
		entityManager.persist(group);
		entityManager.flush();
		entityManager.clear();

		// Retrieve entities from the database
		UserModel retrievedUser = entityManager.find(UserModel.class, user.getId());
		GroupModel retrievedGroup = entityManager.find(GroupModel.class, group.getId());

		// Perform assertions
		assertNotNull(retrievedUser);
		assertNotNull(retrievedGroup);

		// Ensure the many-to-many relationship is properly established
		assertEquals(1, retrievedUser.getGroups().size());
		assertEquals(1, retrievedGroup.getMembers().size());
		assertTrue(retrievedUser.getGroups().contains(retrievedGroup));
		assertTrue(retrievedGroup.getMembers().contains(retrievedUser));
	}

	@Test
	@Transactional
	public void testRetreiveUserGroups() throws Exception {
		// Assuming there is a user in the database with the following details
		UserModel userModel = UserModel.builder()
				.username("username123")
				.email("email@domain.com")
				.password(passwordEncoder.encode("password123")).build();
		entityManager.persist(userModel);
		entityManager.persist(GroupModel.builder()
				.groupName("group123")
				.description("test group")
				.members(Set.of(userModel))
				.build());
		entityManager.flush();
		entityManager.clear();

		mockMvc.perform(MockMvcRequestBuilders.get("/group")
				.header("Authorization", "Basic " +
						Base64.getEncoder().encodeToString("username123:password123".getBytes())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.groupName == 'group123')]").exists())
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
	}

	// TODO test cases for group join and join code endpoints

}
