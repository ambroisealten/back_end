package fr.alten.ambroiseJEE.model.entityControllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.alten.ambroiseJEE.model.beans.Forum;
import fr.alten.ambroiseJEE.model.dao.ForumRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * 
 * @author Kylian Gehier
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ForumEntityControllerTest {

	@InjectMocks
	@Spy
	private final ForumEntityController forumEntityController = new ForumEntityController();

	@Mock
	private ForumRepository forumRepository;

	@Spy
	private JsonNode jForum;
	@Spy
	private static ArrayList<Forum> spiedForumList = new ArrayList<>();
	@SpyBean
	private Forum spiedForum = new Forum();

	private ObjectMapper mapper = new ObjectMapper();

	@BeforeClass
	public static void initForumList() {
		spiedForumList.add(new Forum());
	}
	
	@Test
	public void createForum_with_success() throws IOException {

		// setup
		String newForum = "{" + "\"name\":\"forumName\"," + "\"date\":\"forumDate\"," + "\"place\":\"forumPlace\""
				+ "}";
		jForum = mapper.readTree(newForum);
		Optional<Forum> forumOptional = Optional.ofNullable(null);
		Mockito.when(this.forumRepository.findByNameAndDateAndPlace(anyString(), anyString(), anyString()))
				.thenReturn(forumOptional);

		// assert
		assertThat(this.forumEntityController.createForum(jForum)).isInstanceOf(CreatedException.class);

		// verify
		verify(this.forumRepository, times(1)).save(any(Forum.class));

	}

	@Test
	public void createForum_with_conflict() throws IOException {

		// setup
		String newForum = "{" + "\"name\":\"forumName\"," + "\"date\":\"forumDate\"," + "\"place\":\"forumPlace\""
				+ "}";
		jForum = mapper.readTree(newForum);
		Optional<Forum> forumOptional = Optional.of(spiedForum);
		Mockito.when(this.forumRepository.findByNameAndDateAndPlace(anyString(), anyString(), anyString()))
				.thenReturn(forumOptional);

		// assert
		assertThat(this.forumEntityController.createForum(jForum)).isInstanceOf(ConflictException.class);

		// verify
		verify(this.forumRepository, never()).save(any(Forum.class));

	}

	@Test
	public void deleteForum_with_success() throws IOException {
		// setup
		String newForum = "{" + "\"name\":\"forumName\"," + "\"date\":\"forumDate\"," + "\"place\":\"forumPlace\""
				+ "}";
		jForum = mapper.readTree(newForum);
		Optional<Forum> forumOptional = Optional.of(spiedForum);
		Mockito.when(this.forumRepository.findByNameAndDateAndPlace(anyString(), anyString(), anyString()))
				.thenReturn(forumOptional);
		
		// assert
		assertThat(this.forumEntityController.deleteForum(jForum)).isInstanceOf(OkException.class);

		// verify
		verify(this.forumRepository, times(1)).delete(any(Forum.class));
	}

	@Test
	public void deleteForum_with_rnfe() throws IOException {
		// setup
		String newForum = "{" + "\"name\":\"forumName\"," + "\"date\":\"forumDate\"," + "\"place\":\"forumPlace\""
				+ "}";
		jForum = mapper.readTree(newForum);
		Optional<Forum> forumOptional = Optional.ofNullable(null);
		Mockito.when(this.forumRepository.findByNameAndDateAndPlace(anyString(), anyString(), anyString()))
				.thenReturn(forumOptional);
		
		// assert
		assertThat(this.forumEntityController.deleteForum(jForum)).isInstanceOf(ResourceNotFoundException.class);

		// verify
		verify(this.forumRepository, never()).delete(any(Forum.class));
	}

	@Test
	public void getForum_with_success() throws IOException {
		// setup
		String newForum = "{" + "\"name\":\"forumName\"," + "\"date\":\"forumDate\"," + "\"place\":\"forumPlace\""
				+ "}";
		jForum = mapper.readTree(newForum);
		Optional<Forum> forumOptional = Optional.of(spiedForum);
		Mockito.when(this.forumRepository.findByNameAndDateAndPlace(anyString(), anyString(), anyString()))
				.thenReturn(forumOptional);
		
		// assert
		assertThat(this.forumEntityController.getForum(anyString(), anyString(), anyString())).isInstanceOf(Forum.class);
		
		// verify
		verify(this.forumRepository, times(1)).findByNameAndDateAndPlace(anyString(), anyString(), anyString());
	}

	@Test(expected = ResourceNotFoundException.class)
	public void getForum_with_rnfe() throws IOException {
		// setup
		String newForum = "{" + "\"name\":\"forumName\"," + "\"date\":\"forumDate\"," + "\"place\":\"forumPlace\""
				+ "}";
		jForum = mapper.readTree(newForum);
		Optional<Forum> forumOptional = Optional.ofNullable(null);
		Mockito.when(this.forumRepository.findByNameAndDateAndPlace(anyString(), anyString(), anyString()))
				.thenReturn(forumOptional);
		
		// assert
		assertThat(this.forumEntityController.getForum(anyString(), anyString(), anyString())).isInstanceOf(OkException.class);

	}

	@Test
	public void getForums() {
		// setup
		Mockito.when(this.forumRepository.findAll()).thenReturn(spiedForumList);
		
		// assert
		assertThat(this.forumEntityController.getForums()).isInstanceOf(List.class);
		assertThat(this.forumEntityController.getForums().get(0)).isInstanceOf(Forum.class);
	}

	@Test
	public void updateForum_with_success() throws IOException {
		// setup
		String newForum = "{" + "\"name\":\"forumName\"," + "\"date\":\"forumDate\"," + "\"place\":\"forumPlace\","
				+ "\"oldName\":\"oldName\","+ "\"oldDate\":\"oldDate\","+ "\"oldPlace\":\"oldPlace\""+ "}";
		jForum = mapper.readTree(newForum);
		Optional<Forum> forumOptional = Optional.of(spiedForum);
		Mockito.when(this.forumRepository.findByNameAndDateAndPlace(anyString(), anyString(), anyString()))
				.thenReturn(forumOptional);

		// assert
		assertThat(this.forumEntityController.updateForum(jForum)).isInstanceOf(OkException.class);

		// verify
		verify(this.forumRepository, times(1)).save(any(Forum.class));
	}

	@Test
	public void updateForum_with_conflict() throws IOException {
		// setup
		String newForum = "{" + "\"name\":\"forumName\"," + "\"date\":\"forumDate\"," + "\"place\":\"forumPlace\","
				+ "\"oldName\":\"oldName\","+ "\"oldDate\":\"oldDate\","+ "\"oldPlace\":\"oldPlace\""+ "}";
		jForum = mapper.readTree(newForum);
		Optional<Forum> forumOptional = Optional.ofNullable(null);
		Mockito.when(this.forumRepository.findByNameAndDateAndPlace(anyString(), anyString(), anyString()))
				.thenReturn(forumOptional);

		// assert
		assertThat(this.forumEntityController.updateForum(jForum)).isInstanceOf(ConflictException.class);

		// verify
		verify(this.forumRepository, never()).save(any(Forum.class));
	}

	@Test
	public void updateForum_with_rnfe() throws IOException {
		// setup
		String newForum = "{" + "\"name\":\"forumName\"," + "\"date\":\"forumDate\"," + "\"place\":\"forumPlace\","
				+ "\"oldName\":\"oldName\","+ "\"oldDate\":\"oldDate\","+ "\"oldPlace\":\"oldPlace\""+ "}";
		jForum = mapper.readTree(newForum);
		Optional<Forum> forumOptional = Optional.of(spiedForum);
		Optional<Forum> emptyForumOptional = Optional.ofNullable(null);
		Mockito.when(this.forumRepository.findByNameAndDateAndPlace(anyString(), anyString(), anyString()))
				.thenReturn(forumOptional)
				.thenReturn(emptyForumOptional);

		// assert
		assertThat(this.forumEntityController.updateForum(jForum)).isInstanceOf(ResourceNotFoundException.class);

		// verify
		verify(this.forumRepository, never()).save(any(Forum.class));
	}

}
