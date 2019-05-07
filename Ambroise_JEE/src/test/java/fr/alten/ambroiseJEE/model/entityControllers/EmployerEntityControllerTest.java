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
import org.springframework.dao.DuplicateKeyException;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Employer;
import fr.alten.ambroiseJEE.model.dao.EmployerRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * 
 * @author Lucas Royackkers
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class EmployerEntityControllerTest {
	
	@InjectMocks
	@Spy
	private final EmployerEntityController employerEntityController = new EmployerEntityController();

	@Mock
	private EmployerRepository employerRepository;
	@Mock
	private CreatedException mockedCreatedException;
	@Mock
	private ConflictException mockedConflictException;
	@Mock
	private DuplicateKeyException mockedDuplicateKeyException;
	@Mock
	private JsonNode mockedJEmployer;
	@MockBean
	private Employer mockedEmployer;
	
	/**
	 * @test create a {@link Employer}
	 * @context {@link Employer} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Lucas Royackkers
	 */
	@Test
	public void createEmployer_with_conflict() {
		//setup
		Mockito.doReturn(this.mockedJEmployer).when(this.mockedJEmployer).get(ArgumentMatchers.anyString());
		Mockito.doThrow(this.mockedDuplicateKeyException).when(this.employerRepository)
				.save(ArgumentMatchers.any(Employer.class));
		// assert
		Assertions.assertThat(this.employerEntityController.createEmployer(this.mockedJEmployer))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.employerRepository, Mockito.times(1)).save(ArgumentMatchers.any(Employer.class));
	}
	
	/**
	 * @test create a {@link Employer}
	 * @context sucess
	 * @expected {@link CreatedException} save() call only once
	 * @author Lucas Royackkers
	 */
	@Test
	public void createEmployer_with_success() {

		// setup
		Mockito.doReturn(this.mockedJEmployer).when(this.mockedJEmployer).get(ArgumentMatchers.anyString());
		Mockito.doReturn(this.mockedEmployer).when(this.employerRepository).save(ArgumentMatchers.any(Employer.class));
		// assert
		Assertions.assertThat(this.employerEntityController.createEmployer(this.mockedJEmployer))
				.isInstanceOf(CreatedException.class);
		// verify
		Mockito.verify(this.employerRepository, Mockito.times(1)).save(ArgumentMatchers.any(Employer.class));
	}

	/**
	 * @test delete a {@link Employer}
	 * @context {@link Employer} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteEmployer_with_Conflict() {

		// setup
		final Optional<Employer> notEmptyEmployerOptional = Optional.of(new Employer());
		Mockito.doReturn(this.mockedJEmployer).when(this.mockedJEmployer).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJEmployer).textValue();
		Mockito.doReturn(notEmptyEmployerOptional).when(this.employerRepository).findByName(ArgumentMatchers.anyString());
		Mockito.when(this.employerRepository.save(ArgumentMatchers.any(Employer.class)))
				.thenThrow(this.mockedDuplicateKeyException);
		// assert
		Assertions.assertThat(this.employerEntityController.deleteEmployer(this.mockedJEmployer))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.employerRepository, Mockito.times(1)).save(ArgumentMatchers.any(Employer.class));
	}

	/**
	 * @test delete a {@link Employer}
	 * @context {@link Employer} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteEmployer_with_resourceNotFound() {

		// setup
		final Optional<Employer> emptyEmployerOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJEmployer).when(this.mockedJEmployer).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJEmployer).textValue();
		Mockito.doReturn(emptyEmployerOptional).when(this.employerRepository).findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.employerEntityController.deleteEmployer(this.mockedJEmployer))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.employerRepository, Mockito.never()).save(ArgumentMatchers.any(Employer.class));
	}

	/**
	 * @test delete a {@link Employer}
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteEmployer_with_success() {

		// setup
		final Optional<Employer> notEmptyEmployerOptional = Optional.of(new Employer());
		Mockito.doReturn(this.mockedJEmployer).when(this.mockedJEmployer).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJEmployer).textValue();
		Mockito.doReturn(notEmptyEmployerOptional).when(this.employerRepository).findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.employerEntityController.deleteEmployer(this.mockedJEmployer)).isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.employerRepository, Mockito.times(1)).save(ArgumentMatchers.any(Employer.class));
	}

	/**
	 * @test get all {@link Employer}
	 * @expected return instance of {@link List} save() call only once
	 * @author Lucas Royackkers
	 */
	@Test
	public void getEmployers() {

		// assert
		Assertions.assertThat(this.employerEntityController.getEmployers()).isInstanceOf(List.class);
		// verify
		Mockito.verify(this.employerRepository, Mockito.times(1)).findAll();
	}

	/**
	 * @test get a {@link Employer} by name
	 * @context {@link Employer} not found in base
	 * @expected {@link ResourceNotFoundException}
	 * @author Lucas Royackkers
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void getEmployer_with_ResourceNotFound() {

		// setup
		final Optional<Employer> emptyEmployerOptional = Optional.ofNullable(null);
		Mockito.when(this.employerRepository.findByName("name")).thenReturn(emptyEmployerOptional);
		// throw
		this.employerEntityController.getEmployer("name");
	}

	/**
	 * @test get a {@link Employer} by name
	 * @context success
	 * @expected return instance of {@link Employer}
	 * @author Lucas Royackkers
	 */
	@Test
	public void getEmployer_with_success() {

		// setup
		final Optional<Employer> notEmptyEmployerOptional = Optional.of(new Employer());
		//Mockito.when(this.employerRepository.findByName("name")).thenReturn(notEmptyEmployerOptional);
		Mockito.doReturn(notEmptyEmployerOptional).when(this.employerRepository).findByName("name");
		// assert
		Assertions.assertThat(this.employerEntityController.getEmployer("name")).isInstanceOf(Employer.class);
		// verify
		Mockito.verify(this.employerRepository, Mockito.times(1)).findByName("name");
	}

	/**
	 * @test update a {@link Employer}
	 * @context {@link Employer} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Lucas Royackkers
	 */
	@Test
	public void updateEmployer_with_Conflict() {

		// setup
		final Optional<Employer> notEmptyEmployerOptional = Optional.of(new Employer());
		Mockito.doReturn(this.mockedJEmployer).when(this.mockedJEmployer).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJEmployer).textValue();
		Mockito.doReturn(notEmptyEmployerOptional).when(this.employerRepository).findByName(ArgumentMatchers.anyString());
		Mockito.when(this.employerRepository.save(ArgumentMatchers.any(Employer.class)))
				.thenThrow(this.mockedDuplicateKeyException);
		// assert
		Assertions.assertThat(this.employerEntityController.updateEmployer(this.mockedJEmployer))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.employerRepository, Mockito.times(1)).save(ArgumentMatchers.any(Employer.class));
	}

	/**
	 * @test update a {@link Employer}
	 * @context {@link Employer} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Lucas Royackkers
	 */
	@Test
	public void updateEmployer_with_resourceNotFound() {

		// setup
		final Optional<Employer> emptyEmployerOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJEmployer).when(this.mockedJEmployer).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJEmployer).textValue();
		Mockito.doReturn(emptyEmployerOptional).when(this.employerRepository).findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.employerEntityController.updateEmployer(this.mockedJEmployer))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.employerRepository, Mockito.never()).save(ArgumentMatchers.any(Employer.class));
	}

	/**
	 * @test update a {@link Employer}
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * @author Lucas Royackkers
	 */
	@Test
	public void updateEmployer_with_success() {

		// setup
		final Optional<Employer> notEmptyEmployerOptional = Optional.of(new Employer());
		Mockito.doReturn(this.mockedJEmployer).when(this.mockedJEmployer).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJEmployer).textValue();
		Mockito.doReturn(notEmptyEmployerOptional).when(this.employerRepository).findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.employerEntityController.updateEmployer(this.mockedJEmployer)).isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.employerRepository, Mockito.times(1)).save(ArgumentMatchers.any(Employer.class));
	}

}
