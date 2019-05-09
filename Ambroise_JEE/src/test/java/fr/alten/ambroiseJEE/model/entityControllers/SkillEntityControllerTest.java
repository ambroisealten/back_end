package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

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
import org.springframework.dao.DuplicateKeyException;

import fr.alten.ambroiseJEE.model.beans.Skill;
import fr.alten.ambroiseJEE.model.dao.SkillRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * 
 * @author Thomas Decamp
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class SkillEntityControllerTest {

    @InjectMocks
    @Spy
    private final SkillEntityController skillEntityController = new SkillEntityController();

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private CreatedException mockedCreatedException;
    @Mock
    private ConflictException mockedConflictException;
    @Mock
    private DuplicateKeyException mockedDuplicateKeyException;
    @Mock
    private JsonNode mockedJSkill;
    @MockBean
    private Skill mockedSkill;

    
	/**
	 * @test create a {@link Skill}
	 * @context {@link Skill} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * 
     * @author Thomas Decamp
	 */
	@Test
	public void createSkill_with_conflict() {

		// setup
		Mockito.doReturn(this.mockedJSkill).when(this.mockedJSkill).get(ArgumentMatchers.anyString());
		Mockito.doThrow(this.mockedDuplicateKeyException).when(this.skillRepository)
				.save(ArgumentMatchers.any(Skill.class));
		// assert
		Assertions.assertThat(this.skillEntityController.createSkill(this.mockedJSkill))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.skillRepository, Mockito.times(1)).save(ArgumentMatchers.any(Skill.class));
	}

	/**
	 * @test create a {@link Skill}
	 * @context sucess
	 * @expected {@link CreatedException} save() call only once
	 * 
     * @author Thomas Decamp
	 */
	@Test
	public void createSkill_with_success() {

		// setup
		Mockito.doReturn(this.mockedJSkill).when(this.mockedJSkill).get(ArgumentMatchers.anyString());
		Mockito.doReturn(this.mockedSkill).when(this.skillRepository).save(ArgumentMatchers.any(Skill.class));
		// assert
		Assertions.assertThat(this.skillEntityController.createSkill(this.mockedJSkill))
				.isInstanceOf(CreatedException.class);
		// verify
		Mockito.verify(this.skillRepository, Mockito.times(1)).save(ArgumentMatchers.any(Skill.class));
	}

	/**
	 * @test delete a {@link Skill}
	 * @context {@link Skill} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * 
     * @author Thomas Decamp
	 */
	@Test
	public void deleteSkill_with_resourceNotFound() {

		final Optional<Skill> emptySkillOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJSkill).when(this.mockedJSkill).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJSkill).textValue();
		Mockito.doReturn(emptySkillOptional).when(this.skillRepository).findByNameIgnoreCase(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.skillEntityController.deleteSkill(this.mockedJSkill))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.skillRepository, Mockito.never()).save(ArgumentMatchers.any(Skill.class));
	}

	/**
	 * @test delete a {@link Skill}
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * 
     * @author Thomas Decamp
	 */
	@Test
	public void deleteSkill_with_success() {

		Optional<Skill> skilloptional = Optional.of(new Skill());
		Mockito.doReturn(this.mockedJSkill).when(this.mockedJSkill).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJSkill).textValue();
		Mockito.doReturn(skilloptional).when(this.skillRepository).findByNameIgnoreCase(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.skillEntityController.deleteSkill(this.mockedJSkill)).isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.skillRepository, Mockito.times(1)).save(ArgumentMatchers.any(Skill.class));
	}

	/**
	 * @test get all {@link Skill}
	 * @expected return instance of {@link List} save() call only once
	 * 
     * @author Thomas Decamp
	 */
	@Test
	public void getSkills() {

		// assert
		Assertions.assertThat(this.skillEntityController.getSkills()).isInstanceOf(List.class);
		// verify
		Mockito.verify(this.skillRepository, Mockito.times(1)).findAll();
	}

	/**
	 * @test get a {@link Skill} by name
	 * @context {@link Skill} not found in base
	 * @expected {@link ResourceNotFoundException}
	 * 
     * @author Thomas Decamp
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void getSkill_with_ResourceNotFound() {

		// setup
		final Optional<Skill> emptySkillOptional = Optional.ofNullable(null);
		Mockito.when(this.skillRepository.findByNameIgnoreCase("name")).thenReturn(emptySkillOptional);
		// throw
		this.skillEntityController.getSkill("name");
	}

	/**
	 * @test get a {@link Skill} by name
	 * @context success
	 * @expected return instance of {@link Skill}
	 * 
     * @author Thomas Decamp
	 */
	@Test
	public void getSkill_with_success() {

		// setup
		final Optional<Skill> notEmptySkillOptional = Optional.of(new Skill());
		Mockito.when(this.skillRepository.findByNameIgnoreCase("name")).thenReturn(notEmptySkillOptional);
		// assert
		Assertions.assertThat(this.skillEntityController.getSkill("name")).isInstanceOf(Skill.class);
		// verify
		Mockito.verify(this.skillRepository, Mockito.times(1)).findByNameIgnoreCase("name");
	}

	/**
	 * @test update a {@link Skill}
	 * @context {@link Skill} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * 
     * @author Thomas Decamp
	 */
	@Test
	public void updateSkill_with_Conflict() {

		Mockito.doReturn(this.mockedJSkill).when(this.mockedJSkill).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJSkill).textValue();
		final Optional<Skill> notEmptySkillOptional = Optional.of(new Skill());
		//Mockito.when(this.skillRepository.findByNameIgnoreCase(ArgumentMatchers.anyString())).thenReturn(notEmptySkillOptional);
		Mockito.doReturn(notEmptySkillOptional).when(this.skillRepository).findByNameIgnoreCase(ArgumentMatchers.anyString());
		
		Mockito.when(this.skillRepository.save(ArgumentMatchers.any(Skill.class)))
				.thenThrow(this.mockedDuplicateKeyException);
		// assert
		Assertions.assertThat(this.skillEntityController.updateSkill(this.mockedJSkill))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.skillRepository, Mockito.times(1)).save(ArgumentMatchers.any(Skill.class));
	}

	/**
	 * @test update a {@link Skill}
	 * @context {@link Skill} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * 
     * @author Thomas Decamp
	 */
	@Test
	public void updateSkill_with_resourceNotFound() {

		Mockito.doReturn(this.mockedJSkill).when(this.mockedJSkill).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJSkill).textValue();
		// assert
		Assertions.assertThat(this.skillEntityController.updateSkill(this.mockedJSkill))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.skillRepository, Mockito.never()).save(ArgumentMatchers.any(Skill.class));
	}

	/**
	 * @test update a {@link Skill}
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * 
     * @author Thomas Decamp
	 */
	@Test
	public void updateSkill_with_success() {

		final Optional<Skill> notEmptySkillOptional = Optional.of(new Skill());
		Mockito.doReturn(this.mockedJSkill).when(this.mockedJSkill).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJSkill).textValue();
		Mockito.doReturn(notEmptySkillOptional).when(this.skillRepository).findByNameIgnoreCase(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.skillEntityController.updateSkill(this.mockedJSkill)).isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.skillRepository, Mockito.times(1)).save(ArgumentMatchers.any(Skill.class));
	}
}
