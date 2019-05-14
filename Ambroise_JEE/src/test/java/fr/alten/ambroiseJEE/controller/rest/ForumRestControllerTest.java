package fr.alten.ambroiseJEE.controller.rest;

import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.alten.ambroiseJEE.controller.business.ForumBusinessController;
import fr.alten.ambroiseJEE.model.beans.Forum;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * 
 * @author Kylian Gehier
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ForumRestControllerTest {

	@InjectMocks
	@Spy
	private ForumRestController forumRestController = new ForumRestController();

	@Spy
	private JsonNode spiedJsonNode;

	@Mock
	private ForumBusinessController forumBusinessController;
	@Mock
	private JsonNode mockedJsonNode;
	@Mock
	private HttpException mockedHttpException;
	@Spy
	private static List<Forum> spiedForumList = new ArrayList<Forum>();
	@SpyBean
	private static Forum spiedForum = new Forum();

	private ObjectMapper mapper = new ObjectMapper();

	@BeforeClass
	public static void initList() {
		spiedForumList.add(new Forum());
	}

	@Test
	public void createForum_checkJsonIntegrity() throws Exception {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.forumBusinessController)
				.createForum(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"name\":\"name\"," + "\"date\":\"date\"," + "\"place\":\"place\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.forumRestController.createForum(this.spiedJsonNode, UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{" + "\"date\":\"date\"," + "\"place\":\"place\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.forumRestController.createForum(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingDate = "{" + "\"name\":\"name\"," + "\"place\":\"place\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingDate);
		// assert missing name
		Assertions.assertThat(this.forumRestController.createForum(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingPlace = "{" + "\"name\":\"name\"," + "\"date\":\"date\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingPlace);
		// assert missing name
		Assertions.assertThat(this.forumRestController.createForum(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	@Test
	public void createForum_with_rightParam() throws Exception {

		// setup
		Mockito.doReturn(true).when(this.forumRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.forumBusinessController.createForum(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.forumRestController.createForum(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	@Test
	public void createForum_with_wrongParam() throws Exception {

		// setup
		Mockito.doReturn(false).when(this.forumRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.forumRestController.createForum(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	@Test
	public void deleteForum_checkJsonIntegrity() throws Exception {
		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.forumBusinessController)
				.deleteForum(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"name\":\"name\"," + "\"date\":\"date\"," + "\"place\":\"place\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.forumRestController.deleteForum(this.spiedJsonNode, UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{" + "\"date\":\"date\"," + "\"place\":\"place\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.forumRestController.deleteForum(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingDate = "{" + "\"name\":\"name\"," + "\"place\":\"place\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingDate);
		// assert missing name
		Assertions.assertThat(this.forumRestController.deleteForum(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingPlace = "{" + "\"name\":\"name\"," + "\"date\":\"date\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingPlace);
		// assert missing name
		Assertions.assertThat(this.forumRestController.deleteForum(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	@Test
	public void deleteForum_with_rightParam() throws Exception {

		// setup
		Mockito.doReturn(true).when(this.forumRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.forumBusinessController.deleteForum(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.forumRestController.deleteForum(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	@Test
	public void deleteForum_with_wrongParam() throws Exception {

		// setup
		Mockito.doReturn(false).when(this.forumRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.forumRestController.deleteForum(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	@Test
	public void updateForum_checkJsonIntegrity() throws Exception {
		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.forumBusinessController)
				.updateForum(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"name\":\"name\"," + "\"date\":\"date\"," + "\"place\":\"place\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.forumRestController.updateForum(this.spiedJsonNode, UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{" + "\"date\":\"date\"," + "\"place\":\"place\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.forumRestController.updateForum(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingDate = "{" + "\"name\":\"name\"," + "\"place\":\"place\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingDate);
		// assert missing name
		Assertions.assertThat(this.forumRestController.updateForum(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingPlace = "{" + "\"name\":\"name\"," + "\"date\":\"date\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingPlace);
		// assert missing name
		Assertions.assertThat(this.forumRestController.updateForum(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	@Test
	public void updateForum_with_rightParam() throws Exception {

		// setup
		Mockito.doReturn(true).when(this.forumRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.forumBusinessController.updateForum(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.forumRestController.updateForum(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	@Test
	public void updateForum_with_wrongParam() throws Exception {

		// setup
		Mockito.doReturn(false).when(this.forumRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.forumRestController.updateForum(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	@Test
	public void getForum_expectingString() {
		// setup
		Mockito.when(this.forumBusinessController.getForum(anyString(), anyString(), anyString(),
				ArgumentMatchers.any(UserRole.class))).thenReturn(spiedForum);

		// assert
		Assertions.assertThat(this.forumRestController.getForum("", "", "", UserRole.CDR_ADMIN))
				.isInstanceOf(String.class);
	}

	@Test
	public void getForums_expectingString() {
		// setup
		Mockito.when(this.forumBusinessController.getForums(ArgumentMatchers.any(UserRole.class)))
				.thenReturn(spiedForumList);

		// assert
		Assertions.assertThat(this.forumRestController.getForums(UserRole.CDR_ADMIN)).isInstanceOf(String.class);
	}

}
