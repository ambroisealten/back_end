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

import fr.alten.ambroiseJEE.model.beans.Departement;
import fr.alten.ambroiseJEE.model.dao.DepartementRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * Test class for DepartementEntityController
 *
 * @author Camille Schnell
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DepartementEntityControllerTest {

	@InjectMocks
	@Spy
	private final DepartementEntityController departementEntityController = new DepartementEntityController();

	@Mock
	private DepartementRepository departementRepository;
	@Mock
	private CreatedException mockedCreatedException;
	@Mock
	private ConflictException mockedConflictException;
	@Mock
	private DuplicateKeyException mockedDuplicateKeyException;
	@Mock
	private JsonNode mockedJDepartement;
	@MockBean
	private Departement mockedDepartement;

	/**
	 * @test create a {@link Departement}
	 * @context {@link Departement} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void createDepartement_with_conflict() {
		// setup
		Mockito.doReturn(this.mockedJDepartement).when(this.mockedJDepartement).get(ArgumentMatchers.anyString());
		Mockito.doThrow(this.mockedDuplicateKeyException).when(this.departementRepository)
				.save(ArgumentMatchers.any(Departement.class));
		// assert
		Assertions.assertThat(this.departementEntityController.createDepartement(this.mockedJDepartement))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.departementRepository, Mockito.times(1)).save(ArgumentMatchers.any(Departement.class));
	}

	/**
	 * @test create a {@link Departement}
	 * @context sucess
	 * @expected {@link CreatedException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void createDepartement_with_success() {

		// setup
		Mockito.doReturn(this.mockedJDepartement).when(this.mockedJDepartement).get(ArgumentMatchers.anyString());
		Mockito.doReturn(this.mockedDepartement).when(this.departementRepository).save(ArgumentMatchers.any(Departement.class));
		// assert
		Assertions.assertThat(this.departementEntityController.createDepartement(this.mockedJDepartement))
				.isInstanceOf(CreatedException.class);
		// verify
		Mockito.verify(this.departementRepository, Mockito.times(1)).save(ArgumentMatchers.any(Departement.class));
	}

	/**
	 * @test delete a {@link Departement}
	 * @context {@link Departement} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void deleteDepartement_with_Conflict() {

		// setup
		final Optional<Departement> notEmptyDepartementOptional = Optional.of(new Departement());
		Mockito.doReturn(this.mockedJDepartement).when(this.mockedJDepartement).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJDepartement).textValue();
		Mockito.doReturn(notEmptyDepartementOptional).when(this.departementRepository).findByCode(ArgumentMatchers.anyString());
		Mockito.when(this.departementRepository.save(ArgumentMatchers.any(Departement.class)))
				.thenThrow(this.mockedDuplicateKeyException);
		// assert
		Assertions.assertThat(this.departementEntityController.deleteDepartement(this.mockedJDepartement))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.departementRepository, Mockito.times(1)).save(ArgumentMatchers.any(Departement.class));
	}

	/**
	 * @test delete a {@link Departement}
	 * @context {@link Departement} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Camille Schnell
	 */
	@Test
	public void deleteDepartement_with_resourceNotFound() {

		// setup
		final Optional<Departement> emptyDepartementOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJDepartement).when(this.mockedJDepartement).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJDepartement).textValue();
		Mockito.doReturn(emptyDepartementOptional).when(this.departementRepository).findByCode(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.departementEntityController.deleteDepartement(this.mockedJDepartement))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.departementRepository, Mockito.never()).save(ArgumentMatchers.any(Departement.class));
	}

	/**
	 * @test delete a {@link Departement}
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void deleteDepartement_with_success() {

		// setup
		final Optional<Departement> notEmptyDepartementOptional = Optional.of(new Departement());
		Mockito.doReturn(this.mockedJDepartement).when(this.mockedJDepartement).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJDepartement).textValue();
		Mockito.doReturn(notEmptyDepartementOptional).when(this.departementRepository).findByCode(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.departementEntityController.deleteDepartement(this.mockedJDepartement))
				.isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.departementRepository, Mockito.times(1)).save(ArgumentMatchers.any(Departement.class));
	}

	/**
	 * @test get all {@link Departement}
	 * @expected return instance of {@link List} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void getDepartements() {

		// assert
		Assertions.assertThat(this.departementEntityController.getDepartements()).isInstanceOf(List.class);
		// verify
		Mockito.verify(this.departementRepository, Mockito.times(1)).findAll();
	}

	/**
	 * @test get a {@link Departement} by name
	 * @context {@link Departement} not found in base
	 * @expected {@link ResourceNotFoundException}
	 * @author Camille Schnell
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void getDepartement_with_ResourceNotFound() {

		// setup
		final Optional<Departement> emptyDepartementOptional = Optional.ofNullable(null);
		Mockito.when(this.departementRepository.findByNom("name")).thenReturn(emptyDepartementOptional);
		// throw
		this.departementEntityController.getDepartement("name");
	}

	/**
	 * @test get a {@link Departement} by name
	 * @context success
	 * @expected return instance of {@link Departement}
	 * @author Camille Schnell
	 */
	@Test
	public void getDepartement_with_success() {

		// setup
		final Optional<Departement> notEmptyDepartementOptional = Optional.of(new Departement());
		// Mockito.when(this.departementRepository.findByName("name")).thenReturn(notEmptyDepartementOptional);
		Mockito.doReturn(notEmptyDepartementOptional).when(this.departementRepository).findByNom("name");
		// assert
		Assertions.assertThat(this.departementEntityController.getDepartement("name")).isInstanceOf(Departement.class);
		// verify
		Mockito.verify(this.departementRepository, Mockito.times(1)).findByNom("name");
	}

	/**
	 * @test update a {@link Departement}
	 * @context {@link Departement} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void updateDepartement_with_Conflict() {

		// setup
		final Optional<Departement> notEmptyDepartementOptional = Optional.of(new Departement());
		Mockito.doReturn(this.mockedJDepartement).when(this.mockedJDepartement).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJDepartement).textValue();
		Mockito.doReturn(notEmptyDepartementOptional).when(this.departementRepository).findByCode(ArgumentMatchers.anyString());
		Mockito.when(this.departementRepository.save(ArgumentMatchers.any(Departement.class)))
				.thenThrow(this.mockedDuplicateKeyException);
		// assert
		Assertions.assertThat(this.departementEntityController.updateDepartement(this.mockedJDepartement))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.departementRepository, Mockito.times(1)).save(ArgumentMatchers.any(Departement.class));
	}

	/**
	 * @test update a {@link Departement}
	 * @context {@link Departement} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Camille Schnell
	 */
	@Test
	public void updateDepartement_with_resourceNotFound() {

		// setup
		final Optional<Departement> emptyDepartementOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJDepartement).when(this.mockedJDepartement).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJDepartement).textValue();
		Mockito.doReturn(emptyDepartementOptional).when(this.departementRepository).findByCode(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.departementEntityController.updateDepartement(this.mockedJDepartement))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.departementRepository, Mockito.never()).save(ArgumentMatchers.any(Departement.class));
	}

	/**
	 * @test update a {@link Departement}
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void updateDepartement_with_success() {

		// setup
		final Optional<Departement> notEmptyDepartementOptional = Optional.of(new Departement());
		Mockito.doReturn(this.mockedJDepartement).when(this.mockedJDepartement).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJDepartement).textValue();
		Mockito.doReturn(notEmptyDepartementOptional).when(this.departementRepository).findByCode(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.departementEntityController.updateDepartement(this.mockedJDepartement))
				.isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.departementRepository, Mockito.times(1)).save(ArgumentMatchers.any(Departement.class));
	}
}
