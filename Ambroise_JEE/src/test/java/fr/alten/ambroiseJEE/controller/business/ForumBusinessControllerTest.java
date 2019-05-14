package fr.alten.ambroiseJEE.controller.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.entityControllers.ForumEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
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

	@Test
	public void createForum_as_non_consultantOrDeactivated() {
		// setup
		Mockito.when(this.forumBusinessController.isNot_ConsultantOrDeactivated(any(UserRole.class))).thenReturn(true);
		Mockito.when(this.forumEntityController.createForum(jForum)).thenReturn(mockedHttpException);
		
		// assert
		assertThat(this.forumBusinessController.createForum(jForum, UserRole.DEACTIVATED)).isEqualTo(mockedHttpException);
	}
	
	@Test
	public void createForum_as_consultantOrDeactivated() {
		
	}
	
	@Test
	public void deleteForum_as_admin() {
		
	}
	
	@Test
	public void deleteForum_as_non_admin() {
		
	}
	
	@Test
	public void updateForum_as_admin() {
		
	}
	
	@Test
	public void updateForum_as_non_admin() {
		
	}
	
	@Test
	public void getForum_as_non_consultantOrDeactivated() {
		
	}
	
	@Test
	public void getForum_as_consultantOrDeactivated() {
		
	}
	
	@Test
	public void getForums_as_non_consultantOrDeactivated() {
		
	}
	
	@Test
	public void getForums_as_consultantOrDeactivated() {
		
	}
}
