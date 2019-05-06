package fr.alten.ambroiseJEE.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.model.beans.SkillGraduated;
import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.beans.Skill;
import fr.alten.ambroiseJEE.model.beans.SkillsSheet;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.PersonRepository;
import fr.alten.ambroiseJEE.model.dao.SkillsSheetRepository;
import fr.alten.ambroiseJEE.model.dao.UserRepository;
import fr.alten.ambroiseJEE.utils.PersonRole;
import fr.alten.ambroiseJEE.utils.TokenIgnore;

/**
 *
 * @author Kylian Gehier
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SkillsheetRestIT {

	private static User userAdmin = new User();
	private static Person person = new Person();
	private static SkillsSheet skillsSheet = new SkillsSheet();
	private static Gson gson;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PersonRepository personRepository;
	@Autowired
	private SkillsSheetRepository skillsSheetRepository;

	@BeforeClass
	public static void beforeTests() {
		TokenIgnore.createDir();
		initAdminUser();
		initPerson();
		initSkillSheet();
		initGson();
	}

	private static void initAdminUser() {
		userAdmin.setForname("tempUserAdmin");
		userAdmin.setMail("tempUserAdmin@mail.com");
		userAdmin.setName("tempUserAdminName");
	}

	private static void initPerson() {
		person.setMail("person@mail.com");
		person.setRole(PersonRole.CONSULTANT);
	}

	private static void initSkillSheet() {
		skillsSheet.setName("skillsSheetName");
		skillsSheet.setMailPersonAttachedTo("person@mail.com");
		skillsSheet.setMailVersionAuthor(TokenIgnore.getTokenIgnoreMail());
		skillsSheet.setRolePersonAttachedTo(PersonRole.CONSULTANT);
	}

	private static void initGson() {
		final GsonBuilder builder = new GsonBuilder();
		gson = builder.create();
	}

	@Before
	public void beforeEachTest() {
		userRepository.insert(userAdmin);

		if (!mongoTemplate.collectionExists("skillsSheet")) {
			// Recreate the collection with the unique index "code" -> index beeing dropped
			// when the collection is dropped.
			mongoTemplate.createCollection(SkillsSheet.class);
		}
	}

	@After
	public void afterEachTest() {
		mongoTemplate.getDb().getCollection("user").drop();
		mongoTemplate.getDb().getCollection("person").drop();
		mongoTemplate.getDb().getCollection("skillsSheet").drop();

	}

	@AfterClass
	public static void afterTests() throws FileNotFoundException {
		TokenIgnore.deleteDir();
	}

	@Test
	public void create_skillsSheet_with_success() throws Exception {

		String newSkillsSheet = "{" + "  \"name\": \"skillsSheetName\","
				+ "  \"mailPersonAttachedTo\": \"person@mail.com\"," + "  \"rolePersonAttachedTo\": \"CONSULTANT\","
				+ "  \"skillsList\": [" + "    {" + "      \"skill\": {" + "        \"name\": \"skill1\","
				+ "        \"isSoft\": \" \"" + "      }," + "      \"grade\": \"1\"" + "    }," + "    {"
				+ "      \"skill\": {" + "        \"name\": \"skill2\"" + "      }," + "      \"grade\": \"2\""
				+ "    }" + "  ]" + "}";

		personRepository.insert(person);

		MvcResult result = this.mockMvc
				.perform(post("/skillsheet").contentType(MediaType.APPLICATION_JSON).content(newSkillsSheet))
				.andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("CreatedException"));

		Optional<SkillsSheet> skillsSheetOptional = this.skillsSheetRepository
				.findByNameIgnoreCaseAndMailPersonAttachedToIgnoreCaseAndVersionNumber("skillsSheetName",
						"person@mail.com", 1);

		assertTrue(skillsSheetOptional.isPresent());
		assertThat(skillsSheetOptional.get().getName()).isEqualTo("skillsSheetName");
		assertThat(skillsSheetOptional.get().getMailPersonAttachedTo()).isEqualTo("person@mail.com");
		assertThat(skillsSheetOptional.get().getMailVersionAuthor()).isEqualTo(TokenIgnore.getTokenIgnoreMail());
		assertThat(skillsSheetOptional.get().getRolePersonAttachedTo()).isEqualTo(PersonRole.CONSULTANT);
		assertThat(skillsSheetOptional.get().getVersionNumber()).isEqualTo(1);

		List<SkillGraduated> skillList = skillsSheetOptional.get().getSkillsList();

		for (int i = 1; i <= skillList.size(); i++) {
			SkillGraduated skill_i = skillList.get(i - 1);
			assertThat(skill_i.getName()).isEqualTo("skill" + i);
			assertThat(skill_i.getGrade()).isEqualByComparingTo(new Double(i));
			if (i == 1) {
				assertThat(skill_i.getSkill().getIsSoft()).isEqualTo(" ");
			} else {
				assertNull(skill_i.getSkill().getIsSoft());
			}
		}

	}

	@Test
	public void create_skillsSheet_with_conflict() throws Exception {

		String newSkillsSheet = "{" + "  \"name\": \"skillsSheetName\","
				+ "  \"mailPersonAttachedTo\": \"person@mail.com\"," + "  \"rolePersonAttachedTo\": \"CONSULTANT\","
				+ "  \"skillsList\": [" + "    {" + "      \"skill\": {" + "        \"name\": \"skill1\","
				+ "        \"isSoft\": \" \"" + "      }," + "      \"grade\": \"1\"" + "    }," + "    {"
				+ "      \"skill\": {" + "        \"name\": \"skill2\"" + "      }," + "      \"grade\": \"2\""
				+ "    }" + "  ]" + "}";

		skillsSheet.setVersionNumber(1);

		List<SkillGraduated> skillGraduatedList = new ArrayList<SkillGraduated>();
		// Skill
		Skill skill1 = new Skill();
		skill1.setIsSoft("");
		skill1.setName("skill1");
		SkillGraduated skillGraduated1 = new SkillGraduated(skill1, 1);
		skillGraduatedList.add(skillGraduated1);
		// SoftSkill
		Skill skill2 = new Skill();
		skill1.setName("skill2");
		SkillGraduated skillGraduated2 = new SkillGraduated(skill2, 2);
		skillGraduatedList.add(skillGraduated2);

		skillsSheet.setSkillsList(skillGraduatedList);
		skillsSheetRepository.insert(skillsSheet);
		assertTrue(skillsSheetRepository.findByNameIgnoreCaseAndMailPersonAttachedToIgnoreCaseAndVersionNumber(
				"skillsSheetName", "person@mail.com", 1).isPresent());

		personRepository.insert(person);

		MvcResult result = this.mockMvc
				.perform(post("/skillsheet").contentType(MediaType.APPLICATION_JSON).content(newSkillsSheet))
				.andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("ConflictException"));
	}

	@Test
	public void create_skillsSheet_with_resourceNotFound() throws Exception {

		String skillsSheetWithMailPersonAttachedToNotFoundInBase = "{" + "  \"name\": \"skillsSheetName\","
				+ "  \"mailPersonAttachedTo\": \"noOne@mail.com\"," + "  \"rolePersonAttachedTo\": \"CONSULTANT\","
				+ "  \"skillsList\": [" + "    {" + "      \"skill\": {" + "        \"name\": \"skill1\","
				+ "        \"isSoft\": \" \"" + "      }," + "      \"grade\": \"1\"" + "    }," + "    {"
				+ "      \"skill\": {" + "        \"name\": \"skill2\"" + "      }," + "      \"grade\": \"2\""
				+ "    }" + "  ]" + "}";

		String skillsSheetWithDemissionaireRole = "{" + "  \"name\": \"skillsSheetName\","
				+ "  \"mailPersonAttachedTo\": \"person@mail.com\"," + "  \"rolePersonAttachedTo\": \"DEMISSIONAIRE\","
				+ "  \"skillsList\": [" + "    {" + "      \"skill\": {" + "        \"name\": \"skill1\","
				+ "        \"isSoft\": \" \"" + "      }," + "      \"grade\": \"1\"" + "    }," + "    {"
				+ "      \"skill\": {" + "        \"name\": \"skill2\"" + "      }," + "      \"grade\": \"2\""
				+ "    }" + "  ]" + "}";

		personRepository.insert(person);

		MvcResult result1 = this.mockMvc.perform(post("/skillsheet").contentType(MediaType.APPLICATION_JSON)
				.content(skillsSheetWithMailPersonAttachedToNotFoundInBase)).andReturn();

		assertTrue(result1.getResponse().getContentAsString().contains("ResourceNotFoundException"));


		MvcResult result2 = this.mockMvc.perform(
				post("/skillsheet").contentType(MediaType.APPLICATION_JSON).content(skillsSheetWithDemissionaireRole))
				.andReturn();

		assertTrue(result2.getResponse().getContentAsString().contains("ResourceNotFoundException"));
	}

}
