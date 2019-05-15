package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.DuplicateKeyException;

import fr.alten.ambroiseJEE.model.beans.SkillForum;
import fr.alten.ambroiseJEE.model.dao.SkillForumRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * @author Andy Chabalier
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SkillForumEntityControllerTest {

	@InjectMocks
	@Spy
	private final SkillForumEntityController skillForumEntityController = new SkillForumEntityController();

	@Mock
	private SkillForumRepository skillForumRepository;

	@Mock
	private JsonNode mockedJSkillForum;

	@Mock
	private CreatedException mockedCreatedException;

	@Mock
	private ConflictException mockedConflictException;

	@Mock
	private DuplicateKeyException mockedDuplicateKeyException;

	@MockBean
	private SkillForum mockedSkillForum;

	@Test
	public void createSkillForum() {
		// setup
		Mockito.doReturn(this.mockedJSkillForum).when(this.mockedJSkillForum).get(ArgumentMatchers.anyString());
		Mockito.doReturn(new SkillForum()).when(this.skillForumRepository).save(ArgumentMatchers.any(SkillForum.class));
		// assert
		Assertions.assertThat(this.skillForumEntityController.createSkillForum(this.mockedJSkillForum))
				.isInstanceOf(CreatedException.class);
		// verify
		Mockito.verify(this.skillForumRepository, Mockito.times(1)).save(ArgumentMatchers.any(SkillForum.class));
	}

	@Test
	public void createSkillForum_with_conflict() {
		// setup
		Mockito.doReturn(this.mockedJSkillForum).when(this.mockedJSkillForum).get(ArgumentMatchers.anyString());
		Mockito.doThrow(this.mockedDuplicateKeyException).when(this.skillForumRepository)
				.save(ArgumentMatchers.any(SkillForum.class));
		// assert
		Assertions.assertThat(this.skillForumEntityController.createSkillForum(this.mockedJSkillForum))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.skillForumRepository, Mockito.times(1)).save(ArgumentMatchers.any(SkillForum.class));
	}

	@Test
	public void deleteSkillForum_with_Conflict() {

		// setup
		Mockito.doReturn(this.mockedJSkillForum).when(this.mockedJSkillForum).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJSkillForum).textValue();
		Mockito.doReturn(Optional.of(new SkillForum())).when(this.skillForumRepository)
				.findByName(ArgumentMatchers.anyString());
		Mockito.when(this.skillForumRepository.save(ArgumentMatchers.any(SkillForum.class)))
				.thenThrow(this.mockedDuplicateKeyException);
		// assert
		Assertions.assertThat(this.skillForumEntityController.deleteSkillForum(this.mockedJSkillForum))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.skillForumRepository, Mockito.times(1)).save(ArgumentMatchers.any(SkillForum.class));
	}

	@Test
	public void deleteSkillForum_with_resourceNotFound() {

		// setup
		Mockito.doReturn(this.mockedJSkillForum).when(this.mockedJSkillForum).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJSkillForum).textValue();
		Mockito.doReturn(Optional.empty()).when(this.skillForumRepository).findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.skillForumEntityController.deleteSkillForum(this.mockedJSkillForum))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.skillForumRepository, Mockito.never()).save(ArgumentMatchers.any(SkillForum.class));
	}

	@Test
	public void deleteSkillForum_with_success() {

		// setup
		Mockito.doReturn(this.mockedJSkillForum).when(this.mockedJSkillForum).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJSkillForum).textValue();
		Mockito.doReturn(Optional.of(new SkillForum())).when(this.skillForumRepository)
				.findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.skillForumEntityController.deleteSkillForum(this.mockedJSkillForum))
				.isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.skillForumRepository, Mockito.times(1)).save(ArgumentMatchers.any(SkillForum.class));
	}

	@Test
	public void getSkillForum() {
		// assert
		Assertions.assertThat(this.skillForumEntityController.getSkillsForum()).isInstanceOf(List.class);
		// verify
		Mockito.verify(this.skillForumRepository, Mockito.times(1)).findAll();
	}

	@Test(expected = ResourceNotFoundException.class)
	public void getSkillForum_with_ResourceNotFound() {

		Mockito.doReturn(this.mockedJSkillForum).when(this.mockedJSkillForum).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJSkillForum).textValue();
		// setup
		Mockito.when(this.skillForumRepository.findByName(ArgumentMatchers.anyString()))
				.thenReturn(Optional.ofNullable(null));
		// throw
		this.skillForumEntityController.getSkillForum(this.mockedJSkillForum);
	}

	@Test
	public void getSkillForum_with_success() {
		// setup
		Mockito.doReturn(this.mockedJSkillForum).when(this.mockedJSkillForum).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJSkillForum).textValue();
		Mockito.doReturn(Optional.of(new SkillForum())).when(this.skillForumRepository)
				.findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.skillForumEntityController.getSkillForum(this.mockedJSkillForum))
				.isInstanceOf(SkillForum.class);
		// verify
		Mockito.verify(this.skillForumRepository, Mockito.times(1)).findByName("anyString");
	}

	@Test
	public void updateSkillForum_with_Conflict() {

		// setup
		Mockito.doReturn(this.mockedJSkillForum).when(this.mockedJSkillForum).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJSkillForum).textValue();
		Mockito.doReturn(Optional.of(new SkillForum())).when(this.skillForumRepository)
				.findByName(ArgumentMatchers.anyString());
		Mockito.when(this.skillForumRepository.save(ArgumentMatchers.any(SkillForum.class)))
				.thenThrow(this.mockedDuplicateKeyException);
		// assert
		Assertions.assertThat(this.skillForumEntityController.updateSkillForum(this.mockedJSkillForum))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.skillForumRepository, Mockito.times(1)).save(ArgumentMatchers.any(SkillForum.class));
	}

	@Test
	public void updateSkillForum_with_resourceNotFound() {

		// setup
		Mockito.doReturn(this.mockedJSkillForum).when(this.mockedJSkillForum).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJSkillForum).textValue();
		Mockito.doReturn(Optional.empty()).when(this.skillForumRepository).findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.skillForumEntityController.updateSkillForum(this.mockedJSkillForum))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.skillForumRepository, Mockito.never()).save(ArgumentMatchers.any(SkillForum.class));
	}

	@Test
	public void updateSkillForum_with_success() {

		// setup
		Mockito.doReturn(this.mockedJSkillForum).when(this.mockedJSkillForum).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJSkillForum).textValue();
		Mockito.doReturn(Optional.of(new SkillForum())).when(this.skillForumRepository)
				.findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.skillForumEntityController.updateSkillForum(this.mockedJSkillForum))
				.isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.skillForumRepository, Mockito.times(1)).save(ArgumentMatchers.any(SkillForum.class));
	}
}
