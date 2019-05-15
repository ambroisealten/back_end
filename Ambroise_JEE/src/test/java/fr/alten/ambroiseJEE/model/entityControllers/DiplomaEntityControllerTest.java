package fr.alten.ambroiseJEE.model.entityControllers;

import java.text.ParseException;
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

import fr.alten.ambroiseJEE.model.beans.Diploma;
import fr.alten.ambroiseJEE.model.dao.DiplomaRepository;
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
public class DiplomaEntityControllerTest {

    @InjectMocks
    @Spy
    private final DiplomaEntityController diplomaEntityController = new DiplomaEntityController();

    @Mock
    private DiplomaRepository diplomaRepository;
    @Mock
    private CreatedException mockedCreatedException;
    @Mock
    private ConflictException mockedConflictException;
    @Mock
    private DuplicateKeyException mockedDuplicateKeyException;
    @Mock
    private JsonNode mockedJDiploma;
    @MockBean
    private Diploma mockedDiploma;

    
	/**
	 * @test create a {@link Diploma}
	 * @context {@link Diploma} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * 
     * @author Thomas Decamp
	 * @throws ParseException 
	 */
	@Test
	public void createDiploma_with_conflict() throws ParseException {

		// setup
		Mockito.doReturn(this.mockedJDiploma).when(this.mockedJDiploma).get(ArgumentMatchers.anyString());
		Mockito.doThrow(this.mockedDuplicateKeyException).when(this.diplomaRepository)
				.save(ArgumentMatchers.any(Diploma.class));
		// assert
		Assertions.assertThat(this.diplomaEntityController.createDiploma(this.mockedJDiploma))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.diplomaRepository, Mockito.times(1)).save(ArgumentMatchers.any(Diploma.class));
	}

	/**
	 * @test create a {@link Diploma}
	 * @context sucess
	 * @expected {@link CreatedException} save() call only once
	 * 
     * @author Thomas Decamp
	 * @throws ParseException 
	 */
	@Test
	public void createDiploma_with_success() throws ParseException {

		// setup
		Mockito.doReturn(this.mockedJDiploma).when(this.mockedJDiploma).get(ArgumentMatchers.anyString());
		Mockito.doReturn(this.mockedDiploma).when(this.diplomaRepository).save(ArgumentMatchers.any(Diploma.class));
		// assert
		Assertions.assertThat(this.diplomaEntityController.createDiploma(this.mockedJDiploma))
				.isInstanceOf(CreatedException.class);
		// verify
		Mockito.verify(this.diplomaRepository, Mockito.times(1)).save(ArgumentMatchers.any(Diploma.class));
	}

	/**
	 * @test delete a {@link Diploma}
	 * @context {@link Diploma} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * 
     * @author Thomas Decamp
	 */
	@Test
	public void deleteDiploma_with_resourceNotFound() {

		final Optional<Diploma> emptyDiplomaOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJDiploma).when(this.mockedJDiploma).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJDiploma).textValue();
		Mockito.doReturn(emptyDiplomaOptional).when(this.diplomaRepository).findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.diplomaEntityController.deleteDiploma(this.mockedJDiploma))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.diplomaRepository, Mockito.never()).save(ArgumentMatchers.any(Diploma.class));
	}

	/**
	 * @test delete a {@link Diploma}
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * 
     * @author Thomas Decamp
	 */
	@Test
	public void deleteDiploma_with_success() {

		Optional<Diploma> diplomaoptional = Optional.of(new Diploma());
		Mockito.doReturn(this.mockedJDiploma).when(this.mockedJDiploma).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJDiploma).textValue();
		Mockito.doReturn(diplomaoptional).when(this.diplomaRepository).findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.diplomaEntityController.deleteDiploma(this.mockedJDiploma)).isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.diplomaRepository, Mockito.times(1)).save(ArgumentMatchers.any(Diploma.class));
	}

	/**
	 * @test get all {@link Diploma}
	 * @expected return instance of {@link List} save() call only once
	 * 
     * @author Thomas Decamp
	 */
	@Test
	public void getDiplomas() {

		// assert
		Assertions.assertThat(this.diplomaEntityController.getDiplomas()).isInstanceOf(List.class);
		// verify
		Mockito.verify(this.diplomaRepository, Mockito.times(1)).findAll();
	}

	/**
	 * @test get a {@link Diploma} by name
	 * @context {@link Diploma} not found in base
	 * @expected {@link ResourceNotFoundException}
	 * 
     * @author Thomas Decamp
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void getDiploma_with_ResourceNotFound() {

		// setup
		final Optional<Diploma> emptyDiplomaOptional = Optional.ofNullable(null);
		Mockito.when(this.diplomaRepository.findByName("name")).thenReturn(emptyDiplomaOptional);
		// throw
		this.diplomaEntityController.getDiplomaByName("name");
	}

	/**
	 * @test get a {@link Diploma} by name
	 * @context success
	 * @expected return instance of {@link Diploma}
	 * 
     * @author Thomas Decamp
	 */
	@Test
	public void getDiploma_with_success() {

		// setup
		final Optional<Diploma> notEmptyDiplomaOptional = Optional.of(new Diploma());
		Mockito.when(this.diplomaRepository.findByName("name")).thenReturn(notEmptyDiplomaOptional);
		// assert
		Assertions.assertThat(this.diplomaEntityController.getDiplomaByName("name")).isInstanceOf(Diploma.class);
		// verify
		Mockito.verify(this.diplomaRepository, Mockito.times(1)).findByName("name");
	}

	/**
	 * @test update a {@link Diploma}
	 * @context {@link Diploma} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * 
     * @author Thomas Decamp
	 * @throws ParseException 
	 */
	@Test
	public void updateDiploma_with_Conflict() throws ParseException {

		Mockito.doReturn(this.mockedJDiploma).when(this.mockedJDiploma).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJDiploma).textValue();
		final Optional<Diploma> notEmptyDiplomaOptional = Optional.of(new Diploma());
		Mockito.doReturn(notEmptyDiplomaOptional).when(this.diplomaRepository).findByName(ArgumentMatchers.anyString());
		
		Mockito.when(this.diplomaRepository.save(ArgumentMatchers.any(Diploma.class)))
				.thenThrow(this.mockedDuplicateKeyException);
		// assert
		Assertions.assertThat(this.diplomaEntityController.updateDiploma(this.mockedJDiploma))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.diplomaRepository, Mockito.times(1)).save(ArgumentMatchers.any(Diploma.class));
	}

	/**
	 * @test update a {@link Diploma}
	 * @context {@link Diploma} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * 
     * @author Thomas Decamp
	 * @throws ParseException 
	 */
	@Test
	public void updateDiploma_with_resourceNotFound() throws ParseException {

		Mockito.doReturn(this.mockedJDiploma).when(this.mockedJDiploma).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJDiploma).textValue();
		// assert
		Assertions.assertThat(this.diplomaEntityController.updateDiploma(this.mockedJDiploma))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.diplomaRepository, Mockito.never()).save(ArgumentMatchers.any(Diploma.class));
	}

	/**
	 * @test update a {@link Diploma}
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * 
     * @author Thomas Decamp
	 * @throws ParseException 
	 */
	@Test
	public void updateDiploma_with_success() throws ParseException {

		final Optional<Diploma> notEmptyDiplomaOptional = Optional.of(new Diploma());
		Mockito.doReturn(this.mockedJDiploma).when(this.mockedJDiploma).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJDiploma).textValue();
		Mockito.doReturn(notEmptyDiplomaOptional).when(this.diplomaRepository).findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.diplomaEntityController.updateDiploma(this.mockedJDiploma)).isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.diplomaRepository, Mockito.times(1)).save(ArgumentMatchers.any(Diploma.class));
	}
}
