/**
 * 
 */
package fr.alten.ambroiseJEE.controller.rest;

import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.alten.ambroiseJEE.controller.business.SkillForumBusinessController;
import fr.alten.ambroiseJEE.model.beans.SkillForum;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * @author Andy Chabalier
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SkillForumRestControllerTest {

	@InjectMocks
	@Spy
	private final SkillForumRestController skillForumRestController = new SkillForumRestController();

	@Spy
	private JsonNode spiedJsonNode;

	@Mock
	private SkillForumBusinessController mockedSkillForumBusinessController;

	@Mock
	private HttpException mockedHttpException;

	private final ObjectMapper mapper = new ObjectMapper();

	@Test
	public void createSkill_with_unprocessableEntityException() throws Exception {
		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.mockedSkillForumBusinessController)
				.createSkillForum(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"name\":\"name\"}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.skillForumRestController.createSkill(this.spiedJsonNode, "", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.skillForumRestController.createSkill(this.spiedJsonNode, "", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	@Test
	public void deleteSkill_checkJsonIntegrity() throws Exception {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.mockedSkillForumBusinessController)
				.deleteSkillForum(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"name\":\"name\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.skillForumRestController.deleteSkill(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.skillForumRestController.deleteSkill(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	@Test
	public void updateSkill_checkJsonIntegrity() throws Exception {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.mockedSkillForumBusinessController)
				.updateSkillForum(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"name\":\"name\"," + "\"oldName\":\"oldName\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.skillForumRestController.updateSkill(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{" + "\"oldName\":\"oldName\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.skillForumRestController.updateSkill(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing oldName
		final String missingOldName = "{" + "\"name\":\"name\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingOldName);
		// assert missing oldName
		Assertions.assertThat(this.skillForumRestController.updateSkill(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing oldNameAndName
		final String oldNameAndName = "{}";

		this.spiedJsonNode = this.mapper.readTree(oldNameAndName);
		// assert missing oldName
		Assertions.assertThat(this.skillForumRestController.updateSkill(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	@Test
	public void getSkill_expectingString() throws Exception {

		// setup : all needed fields present
		final String valid = "{\"name\":\"name\"}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// setup
		Mockito.when(this.mockedSkillForumBusinessController.getSkillForum(ArgumentMatchers.any(),
				ArgumentMatchers.any(UserRole.class))).thenReturn(new SkillForum());

		// assert
		Assertions.assertThat(this.skillForumRestController.getSkill(spiedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(String.class);
	}

	@Test(expected = UnprocessableEntityException.class)
	public void getSkill_with_unprocessableEntityException() throws Exception {
		// assert
		this.skillForumRestController.getSkill(spiedJsonNode, "mail", UserRole.CDR_ADMIN);
	}

	@Test
	public void getSkills_expectingString() {

		// setup
		Mockito.when(this.mockedSkillForumBusinessController.getSkillsForum(ArgumentMatchers.any(UserRole.class)))
				.thenReturn(new ArrayList<SkillForum>());

		// assert
		Assertions.assertThat(this.skillForumRestController.getSkills("mail", UserRole.CDR_ADMIN))
				.isInstanceOf(String.class);
	}
}
