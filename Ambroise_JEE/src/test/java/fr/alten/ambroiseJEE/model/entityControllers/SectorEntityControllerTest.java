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

import fr.alten.ambroiseJEE.model.beans.Sector;
import fr.alten.ambroiseJEE.model.dao.SectorRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 *
 * @author Andy Chabalier
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SectorEntityControllerTest {

	@InjectMocks
	@Spy
	private final SectorEntityController sectorEntityController = new SectorEntityController();

	@Mock
	private SectorRepository sectorRepository;
	@Mock
	private CreatedException mockedCreatedException;
	@Mock
	private ConflictException mockedConflictException;
	@Mock
	private DuplicateKeyException mockedDuplicateKeyException;
	@Mock
	private JsonNode mockedJSector;
	@MockBean
	private Sector mockedSector;

	/**
	 * @test create a {@link Sector}
	 * @context {@link Sector} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Andy Chabalier
	 */
	@Test
	public void createSector_with_conflict() {

		// setup
		Mockito.doReturn(this.mockedJSector).when(this.mockedJSector).get(ArgumentMatchers.anyString());
		Mockito.doThrow(this.mockedDuplicateKeyException).when(this.sectorRepository)
				.save(ArgumentMatchers.any(Sector.class));
		// assert
		Assertions.assertThat(this.sectorEntityController.createSector(this.mockedJSector))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.sectorRepository, Mockito.times(1)).save(ArgumentMatchers.any(Sector.class));
	}

	/**
	 * @test create a {@link Sector}
	 * @context sucess
	 * @expected {@link CreatedException} save() call only once
	 * @author Andy Chabalier
	 */
	@Test
	public void createSector_with_success() {

		// setup
		Mockito.doReturn(this.mockedJSector).when(this.mockedJSector).get(ArgumentMatchers.anyString());
		Mockito.doReturn(this.mockedSector).when(this.sectorRepository).save(ArgumentMatchers.any(Sector.class));
		// assert
		Assertions.assertThat(this.sectorEntityController.createSector(this.mockedJSector))
				.isInstanceOf(CreatedException.class);
		// verify
		Mockito.verify(this.sectorRepository, Mockito.times(1)).save(ArgumentMatchers.any(Sector.class));
	}

	/**
	 * @test delete a {@link Sector}
	 * @context {@link Sector} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Andy Chabalier
	 */
	@Test
	public void deleteSector_with_resourceNotFound() {

		// setup
		final Optional<Sector> emptySectorOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJSector).when(this.mockedJSector).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJSector).textValue();
		Mockito.doReturn(emptySectorOptional).when(this.sectorRepository).findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.sectorEntityController.deleteSector(this.mockedJSector))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.sectorRepository, Mockito.never()).save(ArgumentMatchers.any(Sector.class));
	}

	/**
	 * @test delete a {@link Sector}
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * @author Andy Chabalier
	 */
	@Test
	public void deleteSector_with_success() {

		// setup
		final Optional<Sector> notEmptySectorOptional = Optional.of(new Sector());
		Mockito.doReturn(this.mockedJSector).when(this.mockedJSector).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJSector).textValue();
		Mockito.doReturn(notEmptySectorOptional).when(this.sectorRepository).findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.sectorEntityController.deleteSector(this.mockedJSector))
				.isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.sectorRepository, Mockito.times(1)).delete(ArgumentMatchers.any(Sector.class));
	}

	/**
	 * @test get all {@link Sector}
	 * @expected return instance of {@link List} save() call only once
	 * @author Andy Chabalier
	 */
	@Test
	public void getCities() {

		// assert
		Assertions.assertThat(this.sectorEntityController.getSectors()).isInstanceOf(List.class);
		// verify
		Mockito.verify(this.sectorRepository, Mockito.times(1)).findAll();
	}

	/**
	 * @test get a {@link Sector} by name
	 * @context {@link Sector} not found in base
	 * @expected {@link ResourceNotFoundException}
	 * @author Andy Chabalier
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void getSector_with_ResourceNotFound() {

		// setup
		final Optional<Sector> emptySectorOptional = Optional.ofNullable(null);
		Mockito.when(this.sectorRepository.findByName("name")).thenReturn(emptySectorOptional);
		// throw
		this.sectorEntityController.getSector("name");
	}

	/**
	 * @test get a {@link Sector} by name
	 * @context success
	 * @expected return instance of {@link Sector}
	 * @author Andy Chabalier
	 */
	@Test
	public void getSector_with_success() {

		// setup
		final Optional<Sector> notEmptySectorOptional = Optional.of(new Sector());
		Mockito.when(this.sectorRepository.findByName("name")).thenReturn(notEmptySectorOptional);
		// assert
		Assertions.assertThat(this.sectorEntityController.getSector("name")).isInstanceOf(Sector.class);
		// verify
		Mockito.verify(this.sectorRepository, Mockito.times(1)).findByName("name");
	}

	/**
	 * @test update a {@link Sector}
	 * @context {@link Sector} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Andy Chabalier
	 */
	@Test
	public void updateSector_with_Conflict() {

		// setup
		final Optional<Sector> notEmptySectorOptional = Optional.of(new Sector());
		Mockito.doReturn(this.mockedJSector).when(this.mockedJSector).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJSector).textValue();
		Mockito.doReturn(notEmptySectorOptional).when(this.sectorRepository).findByName(ArgumentMatchers.anyString());
		Mockito.when(this.sectorRepository.save(ArgumentMatchers.any(Sector.class)))
				.thenThrow(this.mockedDuplicateKeyException);
		// assert
		Assertions.assertThat(this.sectorEntityController.updateSector(this.mockedJSector))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.sectorRepository, Mockito.times(1)).save(ArgumentMatchers.any(Sector.class));
	}

	/**
	 * @test update a {@link Sector}
	 * @context {@link Sector} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Andy Chabalier
	 */
	@Test
	public void updateSector_with_resourceNotFound() {

		// setup
		final Optional<Sector> emptySectorOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJSector).when(this.mockedJSector).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJSector).textValue();
		Mockito.doReturn(emptySectorOptional).when(this.sectorRepository).findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.sectorEntityController.updateSector(this.mockedJSector))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.sectorRepository, Mockito.never()).save(ArgumentMatchers.any(Sector.class));
	}

	/**
	 * @test update a {@link Sector}
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * @author Andy Chabalier
	 */
	@Test
	public void updateSector_with_success() {

		// setup
		final Optional<Sector> notEmptySectorOptional = Optional.of(new Sector());
		Mockito.doReturn(this.mockedJSector).when(this.mockedJSector).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJSector).textValue();
		Mockito.doReturn(notEmptySectorOptional).when(this.sectorRepository).findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.sectorEntityController.updateSector(this.mockedJSector))
				.isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.sectorRepository, Mockito.times(1)).save(ArgumentMatchers.any(Sector.class));
	}

}
