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

import fr.alten.ambroiseJEE.controller.business.geographic.GeographicBusinessController;
import fr.alten.ambroiseJEE.model.beans.Agency;
import fr.alten.ambroiseJEE.model.dao.AgencyRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * Test class for AgencyEntityController
 *
 * @author Camille Schnell
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AgencyEntityControllerTest {

	@InjectMocks
	@Spy
	private final AgencyEntityController agencyEntityController = new AgencyEntityController();

	@Mock
	private AgencyRepository agencyRepository;
	
	@Mock
	private GeographicBusinessController geographicBusinessController;
	
	@Mock
	private CreatedException mockedCreatedException;
	@Mock
	private ConflictException mockedConflictException;
	@Mock
	private DuplicateKeyException mockedDuplicateKeyException;
	@Mock
	private JsonNode mockedJAgency;
	@MockBean
	private Agency mockedAgency;

	/**
	 * @test create a {@link Agency}
	 * @context {@link Agency} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void createAgency_with_conflict() {
		// setup
		Mockito.doReturn(this.mockedJAgency).when(this.mockedJAgency).get(ArgumentMatchers.anyString());
		Mockito.doThrow(this.mockedDuplicateKeyException).when(this.agencyRepository)
				.save(ArgumentMatchers.any(Agency.class));
		// assert
		Assertions.assertThat(this.agencyEntityController.createAgency(this.mockedJAgency))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.agencyRepository, Mockito.times(1)).save(ArgumentMatchers.any(Agency.class));
	}

	/**
	 * @test create a {@link Agency}
	 * @context sucess
	 * @expected {@link CreatedException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void createAgency_with_success() {

		// setup
		Mockito.doReturn(this.mockedJAgency).when(this.mockedJAgency).get(ArgumentMatchers.anyString());
		Mockito.doReturn(this.mockedAgency).when(this.agencyRepository).save(ArgumentMatchers.any(Agency.class));
		// assert
		Assertions.assertThat(this.agencyEntityController.createAgency(this.mockedJAgency))
				.isInstanceOf(CreatedException.class);
		// verify
		Mockito.verify(this.agencyRepository, Mockito.times(1)).save(ArgumentMatchers.any(Agency.class));
	}

	/**
	 * @test delete a {@link Agency}
	 * @context {@link Agency} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void deleteAgency_with_Conflict() {

		// setup
		final Optional<Agency> notEmptyAgencyOptional = Optional.of(new Agency());
		Mockito.doReturn(this.mockedJAgency).when(this.mockedJAgency).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJAgency).textValue();
		Mockito.doReturn(notEmptyAgencyOptional).when(this.agencyRepository).findByName(ArgumentMatchers.anyString());
		Mockito.when(this.agencyRepository.save(ArgumentMatchers.any(Agency.class)))
				.thenThrow(this.mockedDuplicateKeyException);
		// assert
		Assertions.assertThat(this.agencyEntityController.deleteAgency(this.mockedJAgency))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.agencyRepository, Mockito.times(1)).save(ArgumentMatchers.any(Agency.class));
	}

	/**
	 * @test delete a {@link Agency}
	 * @context {@link Agency} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Camille Schnell
	 */
	@Test
	public void deleteAgency_with_resourceNotFound() {

		// setup
		final Optional<Agency> emptyAgencyOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJAgency).when(this.mockedJAgency).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJAgency).textValue();
		Mockito.doReturn(emptyAgencyOptional).when(this.agencyRepository).findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.agencyEntityController.deleteAgency(this.mockedJAgency))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.agencyRepository, Mockito.never()).save(ArgumentMatchers.any(Agency.class));
	}

	/**
	 * @test delete a {@link Agency}
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void deleteAgency_with_success() {

		// setup
		final Optional<Agency> notEmptyAgencyOptional = Optional.of(new Agency());
		Mockito.doReturn(this.mockedJAgency).when(this.mockedJAgency).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJAgency).textValue();
		Mockito.doReturn(notEmptyAgencyOptional).when(this.agencyRepository).findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.agencyEntityController.deleteAgency(this.mockedJAgency))
				.isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.agencyRepository, Mockito.times(1)).save(ArgumentMatchers.any(Agency.class));
	}

	/**
	 * @test get all {@link Agency}
	 * @expected return instance of {@link List} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void getAgencies() {

		// assert
		Assertions.assertThat(this.agencyEntityController.getAgencies()).isInstanceOf(List.class);
		// verify
		Mockito.verify(this.agencyRepository, Mockito.times(1)).findAll();
	}

	/**
	 * @test get a {@link Agency} by name
	 * @context {@link Agency} not found in base
	 * @expected {@link ResourceNotFoundException}
	 * @author Camille Schnell
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void getAgency_with_ResourceNotFound() {

		// setup
		final Optional<Agency> emptyAgencyOptional = Optional.ofNullable(null);
		Mockito.when(this.agencyRepository.findByName("name")).thenReturn(emptyAgencyOptional);
		// throw
		this.agencyEntityController.getAgency("name");
	}

	/**
	 * @test get a {@link Agency} by name
	 * @context success
	 * @expected return instance of {@link Agency}
	 * @author Camille Schnell
	 */
	@Test
	public void getAgency_with_success() {

		// setup
		final Optional<Agency> notEmptyAgencyOptional = Optional.of(new Agency());
		Mockito.doReturn(notEmptyAgencyOptional).when(this.agencyRepository).findByName("name");
		// assert
		Assertions.assertThat(this.agencyEntityController.getAgency("name")).isInstanceOf(Agency.class);
		// verify
		Mockito.verify(this.agencyRepository, Mockito.times(1)).findByName("name");
	}

	/**
	 * @test update a {@link Agency}
	 * @context {@link Agency} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void updateAgency_with_Conflict() {

		// setup
		final Optional<Agency> notEmptyAgencyOptional = Optional.of(new Agency());
		Mockito.doReturn(this.mockedJAgency).when(this.mockedJAgency).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJAgency).textValue();
		Mockito.doReturn(notEmptyAgencyOptional).when(this.agencyRepository).findByName(ArgumentMatchers.anyString());
		Mockito.when(this.agencyRepository.save(ArgumentMatchers.any(Agency.class)))
				.thenThrow(this.mockedDuplicateKeyException);
		// assert
		Assertions.assertThat(this.agencyEntityController.updateAgency(this.mockedJAgency))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.agencyRepository, Mockito.times(1)).save(ArgumentMatchers.any(Agency.class));
	}

	/**
	 * @test update a {@link Agency}
	 * @context {@link Agency} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Camille Schnell
	 */
	@Test
	public void updateAgency_with_resourceNotFound() {

		// setup
		final Optional<Agency> emptyAgencyOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJAgency).when(this.mockedJAgency).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJAgency).textValue();
		Mockito.doReturn(emptyAgencyOptional).when(this.agencyRepository).findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.agencyEntityController.updateAgency(this.mockedJAgency))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.agencyRepository, Mockito.never()).save(ArgumentMatchers.any(Agency.class));
	}

	/**
	 * @test update a {@link Agency}
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void updateAgency_with_success() {

		// setup
		final Optional<Agency> notEmptyAgencyOptional = Optional.of(new Agency());
		Mockito.doReturn(this.mockedJAgency).when(this.mockedJAgency).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJAgency).textValue();
		Mockito.doReturn(notEmptyAgencyOptional).when(this.agencyRepository).findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.agencyEntityController.updateAgency(this.mockedJAgency))
				.isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.agencyRepository, Mockito.times(1)).save(ArgumentMatchers.any(Agency.class));
	}
}
