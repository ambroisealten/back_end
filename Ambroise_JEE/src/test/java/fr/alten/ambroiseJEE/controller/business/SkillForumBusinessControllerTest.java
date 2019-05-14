package fr.alten.ambroiseJEE.controller.business;

import java.util.ArrayList;
import java.util.List;

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

import fr.alten.ambroiseJEE.model.beans.SkillForum;
import fr.alten.ambroiseJEE.model.entityControllers.SkillForumEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * @author Andy Chabalier
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SkillForumBusinessControllerTest {

	@InjectMocks
	@Spy
	private final SkillForumBusinessController skillForumBusinessController = new SkillForumBusinessController();

	@Mock
	private SkillForumEntityController skillForumEntityController;

	@Mock
	private JsonNode mockedJsonNode;;

	@Mock
	private SkillForum mockedSkillForum;

	@Mock
	private HttpException mockedHttpException;

	@Test
	public void createSkillForum() {
		// setup
		Mockito.doReturn(true).when(this.skillForumBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.skillForumEntityController.createSkillForum(ArgumentMatchers.any(JsonNode.class)))
				.thenReturn(this.mockedHttpException);

		Assertions
				.assertThat(this.skillForumBusinessController.createSkillForum(mockedJsonNode, UserRole.MANAGER_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	@Test
	public void createSkillForum_with_forbiddenException() {
		// setup
		Mockito.doReturn(false).when(this.skillForumBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));

		Assertions
				.assertThat(this.skillForumBusinessController.createSkillForum(mockedJsonNode, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);
	}

	@Test
	public void deleteSkillForum() {
		// setup
		Mockito.doReturn(true).when(this.skillForumBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.skillForumEntityController.deleteSkillForum(ArgumentMatchers.any(JsonNode.class)))
				.thenReturn(this.mockedHttpException);

		Assertions
				.assertThat(this.skillForumBusinessController.deleteSkillForum(mockedJsonNode, UserRole.MANAGER_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	@Test
	public void deleteSkillForum_with_forbiddenException() {
		// setup
		Mockito.doReturn(false).when(this.skillForumBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));

		Assertions
				.assertThat(this.skillForumBusinessController.deleteSkillForum(mockedJsonNode, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);
	}

	@Test
	public void getSkillForum() {
		// setup
		Mockito.doReturn(true).when(this.skillForumBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.skillForumEntityController.getSkillForum(ArgumentMatchers.any(JsonNode.class)))
				.thenReturn(this.mockedSkillForum);

		Assertions.assertThat(this.skillForumBusinessController.getSkillForum(mockedJsonNode, UserRole.MANAGER_ADMIN))
				.isEqualTo(mockedSkillForum);
	}

	@Test(expected = ForbiddenException.class)
	public void getSkillForum_with_forbiddenException() {
		// setup
		Mockito.doReturn(false).when(this.skillForumBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));

		this.skillForumBusinessController.getSkillForum(mockedJsonNode, UserRole.MANAGER_ADMIN);
	}

	@Test
	public void getSkillsForum() {
		// setup
		Mockito.doReturn(true).when(this.skillForumBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.skillForumEntityController.getSkillsForum()).thenReturn(new ArrayList<SkillForum>());

		Assertions.assertThat(this.skillForumBusinessController.getSkillsForum(UserRole.MANAGER_ADMIN))
				.isInstanceOf(List.class);
	}

	@Test(expected = ForbiddenException.class)
	public void getSkillsForum_with_forbiddenException() {
		// setup
		Mockito.doReturn(false).when(this.skillForumBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));

		this.skillForumBusinessController.getSkillsForum(UserRole.MANAGER_ADMIN);
	}

	@Test
	public void updateSkillForum() {
		// setup
		Mockito.doReturn(true).when(this.skillForumBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.skillForumEntityController.updateSkillForum(ArgumentMatchers.any(JsonNode.class)))
				.thenReturn(this.mockedHttpException);

		Assertions
				.assertThat(this.skillForumBusinessController.updateSkillForum(mockedJsonNode, UserRole.MANAGER_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	@Test
	public void updateSkillForum_with_forbiddenException() {
		// setup
		Mockito.doReturn(false).when(this.skillForumBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));

		Assertions
				.assertThat(this.skillForumBusinessController.updateSkillForum(mockedJsonNode, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);
	}

}
