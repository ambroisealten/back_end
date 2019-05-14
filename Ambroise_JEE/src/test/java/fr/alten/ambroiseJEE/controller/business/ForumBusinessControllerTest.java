package fr.alten.ambroiseJEE.controller.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Forum;
import fr.alten.ambroiseJEE.model.entityControllers.ForumEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * 
 * @author Kylian Gehier
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ForumBusinessControllerTest {

	@InjectMocks
	@Spy
	private ForumBusinessController forumBusinessController = new ForumBusinessController();

	@Mock
	private ForumEntityController forumEntityController;
	@Mock
	private HttpException mockedHttpException;
	@Mock
	private JsonNode jForum;
	@Mock
	private List<Forum> mockedForumList;
	@MockBean
	private Forum mockedForum;

	
	@Test
	public void createForum_as_non_consultantOrDeactivated() {
		// setup
		Mockito.when(this.forumBusinessController.isNot_ConsultantOrDeactivated(any(UserRole.class))).thenReturn(true);
		Mockito.when(this.forumEntityController.createForum(jForum)).thenReturn(mockedHttpException);

		// assert
		assertThat(this.forumBusinessController.createForum(jForum, UserRole.DEACTIVATED))
				.isEqualTo(mockedHttpException);
	}

	@Test
	public void createForum_as_consultantOrDeactivated() {
		// setup
		Mockito.when(this.forumBusinessController.isNot_ConsultantOrDeactivated(any(UserRole.class))).thenReturn(false);

		// assert
		assertThat(this.forumBusinessController.createForum(jForum, UserRole.DEACTIVATED))
				.isInstanceOf(ForbiddenException.class);
	}

	@Test
	public void deleteForum_as_admin() {
		// setup
		Mockito.when(this.forumBusinessController.isAdmin(any(UserRole.class))).thenReturn(true);
		Mockito.when(this.forumEntityController.deleteForum(jForum)).thenReturn(mockedHttpException);

		// assert
		assertThat(this.forumBusinessController.deleteForum(jForum, UserRole.DEACTIVATED))
				.isEqualTo(mockedHttpException);
	}

	@Test
	public void deleteForum_as_non_admin() {
		// setup
		Mockito.when(this.forumBusinessController.isAdmin(any(UserRole.class))).thenReturn(false);

		// assert
		assertThat(this.forumBusinessController.deleteForum(jForum, UserRole.DEACTIVATED))
				.isInstanceOf(ForbiddenException.class);
	}

	@Test
	public void updateForum_as_admin() {
		// setup
		Mockito.when(this.forumBusinessController.isAdmin(any(UserRole.class))).thenReturn(true);
		Mockito.when(this.forumEntityController.updateForum(jForum)).thenReturn(mockedHttpException);

		// assert
		assertThat(this.forumBusinessController.updateForum(jForum, UserRole.DEACTIVATED))
				.isEqualTo(mockedHttpException);
	}

	@Test
	public void updateForum_as_non_admin() {
		// setup
		Mockito.when(this.forumBusinessController.isAdmin(any(UserRole.class))).thenReturn(false);

		// assert
		assertThat(this.forumBusinessController.updateForum(jForum, UserRole.DEACTIVATED))
				.isInstanceOf(ForbiddenException.class);
	}

	@Test
	public void getForum_as_non_consultantOrDeactivated() {
		// setup
		Mockito.when(this.forumBusinessController.isNot_ConsultantOrDeactivated(any(UserRole.class))).thenReturn(true);
		Mockito.when(this.forumEntityController.getForum("", "", "")).thenReturn(mockedForum);

		// assert
		assertThat(this.forumBusinessController.getForum("", "", "", UserRole.DEACTIVATED)).isEqualTo(mockedForum);
	}

	@Test(expected = ForbiddenException.class)
	public void getForum_as_consultantOrDeactivated() {
		// setup
		Mockito.when(this.forumBusinessController.isNot_ConsultantOrDeactivated(any(UserRole.class))).thenReturn(false);

		// throw
		this.forumBusinessController.getForum("", "", "", UserRole.DEACTIVATED);
	}

	@Test
	public void getForums_as_non_consultantOrDeactivated() {
		// setup
		Mockito.when(this.forumBusinessController.isNot_ConsultantOrDeactivated(any(UserRole.class))).thenReturn(true);
		Mockito.when(this.forumEntityController.getForums()).thenReturn(mockedForumList);

		// assert
		assertThat(this.forumBusinessController.getForums(UserRole.DEACTIVATED)).isEqualTo(mockedForumList);
	}

	@Test(expected = ForbiddenException.class)
	public void getForums_as_consultantOrDeactivated() {
		// setup
		Mockito.when(this.forumBusinessController.isNot_ConsultantOrDeactivated(any(UserRole.class))).thenReturn(false);

		// throw
		this.forumBusinessController.getForums(UserRole.DEACTIVATED);
	}
}
