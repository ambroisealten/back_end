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

import fr.alten.ambroiseJEE.model.beans.PostalCode;
import fr.alten.ambroiseJEE.model.dao.PostalCodeRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * Test class for PostalCodeEntityController
 *
 * @author Camille Schnell
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PostalCodeEntityControllerTest {

	@InjectMocks
	@Spy
	private final PostalCodeEntityController postalCodeEntityController = new PostalCodeEntityController();

	@Mock
	private PostalCodeRepository postalCodeRepository;
	@Mock
	private CreatedException mockedCreatedException;
	@Mock
	private ConflictException mockedConflictException;
	@Mock
	private DuplicateKeyException mockedDuplicateKeyException;
	@Mock
	private JsonNode mockedJPostalCode;
	@MockBean
	private PostalCode mockedPostalCode;

	/**
	 * @test create a {@link PostalCode}
	 * @context {@link PostalCode} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void createPostalCode_with_conflict() {
		// setup
		Mockito.doReturn(this.mockedJPostalCode).when(this.mockedJPostalCode).get(ArgumentMatchers.anyString());
		Mockito.doThrow(this.mockedDuplicateKeyException).when(this.postalCodeRepository)
				.save(ArgumentMatchers.any(PostalCode.class));
		// assert
		Assertions.assertThat(this.postalCodeEntityController.createPostalCode(this.mockedJPostalCode))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.postalCodeRepository, Mockito.times(1)).save(ArgumentMatchers.any(PostalCode.class));
	}

	/**
	 * @test create a {@link PostalCode}
	 * @context sucess
	 * @expected {@link CreatedException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void createPostalCode_with_success() {

		// setup
		Mockito.doReturn(this.mockedJPostalCode).when(this.mockedJPostalCode).get(ArgumentMatchers.anyString());
		Mockito.doReturn(this.mockedPostalCode).when(this.postalCodeRepository)
				.save(ArgumentMatchers.any(PostalCode.class));
		// assert
		Assertions.assertThat(this.postalCodeEntityController.createPostalCode(this.mockedJPostalCode))
				.isInstanceOf(CreatedException.class);
		// verify
		Mockito.verify(this.postalCodeRepository, Mockito.times(1)).save(ArgumentMatchers.any(PostalCode.class));
	}

	/**
	 * @test delete a {@link PostalCode}
	 * @context {@link PostalCode} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void deletePostalCode_with_Conflict() {

		// setup
		final Optional<PostalCode> notEmptyPostalCodeOptional = Optional.of(new PostalCode());
		Mockito.doReturn(this.mockedJPostalCode).when(this.mockedJPostalCode).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJPostalCode).textValue();
		Mockito.doReturn(notEmptyPostalCodeOptional).when(this.postalCodeRepository)
				.findByName(ArgumentMatchers.anyString());
		Mockito.when(this.postalCodeRepository.save(ArgumentMatchers.any(PostalCode.class)))
				.thenThrow(this.mockedDuplicateKeyException);
		// assert
		Assertions.assertThat(this.postalCodeEntityController.deletePostalCode(this.mockedJPostalCode))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.postalCodeRepository, Mockito.times(1)).save(ArgumentMatchers.any(PostalCode.class));
	}

	/**
	 * @test delete a {@link PostalCode}
	 * @context {@link PostalCode} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Camille Schnell
	 */
	@Test
	public void deletePostalCode_with_resourceNotFound() {

		// setup
		final Optional<PostalCode> emptyPostalCodeOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJPostalCode).when(this.mockedJPostalCode).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJPostalCode).textValue();
		Mockito.doReturn(emptyPostalCodeOptional).when(this.postalCodeRepository)
				.findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.postalCodeEntityController.deletePostalCode(this.mockedJPostalCode))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.postalCodeRepository, Mockito.never()).save(ArgumentMatchers.any(PostalCode.class));
	}

	/**
	 * @test delete a {@link PostalCode}
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void deletePostalCode_with_success() {

		// setup
		final Optional<PostalCode> notEmptyPostalCodeOptional = Optional.of(new PostalCode());
		Mockito.doReturn(this.mockedJPostalCode).when(this.mockedJPostalCode).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJPostalCode).textValue();
		Mockito.doReturn(notEmptyPostalCodeOptional).when(this.postalCodeRepository)
				.findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.postalCodeEntityController.deletePostalCode(this.mockedJPostalCode))
				.isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.postalCodeRepository, Mockito.times(1)).save(ArgumentMatchers.any(PostalCode.class));
	}

	/**
	 * @test get all {@link PostalCode}
	 * @expected return instance of {@link List} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void getPostalCodes() {

		// assert
		Assertions.assertThat(this.postalCodeEntityController.getPostalCodes()).isInstanceOf(List.class);
		// verify
		Mockito.verify(this.postalCodeRepository, Mockito.times(1)).findAll();
	}

	/**
	 * @test get a {@link PostalCode} by name
	 * @context {@link PostalCode} not found in base
	 * @expected {@link ResourceNotFoundException}
	 * @author Camille Schnell
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void getPostalCode_with_ResourceNotFound() {

		// setup
		final Optional<PostalCode> emptyPostalCodeOptional = Optional.ofNullable(null);
		Mockito.when(this.postalCodeRepository.findByName("name")).thenReturn(emptyPostalCodeOptional);
		// throw
		this.postalCodeEntityController.getPostalCode("name");
	}

	/**
	 * @test get a {@link PostalCode} by name
	 * @context success
	 * @expected return instance of {@link PostalCode}
	 * @author Camille Schnell
	 */
	@Test
	public void getPostalCode_with_success() {

		// setup
		final Optional<PostalCode> notEmptyPostalCodeOptional = Optional.of(new PostalCode());
		// Mockito.when(this.postalCodeRepository.findByName("name")).thenReturn(notEmptyPostalCodeOptional);
		Mockito.doReturn(notEmptyPostalCodeOptional).when(this.postalCodeRepository).findByName("name");
		// assert
		Assertions.assertThat(this.postalCodeEntityController.getPostalCode("name")).isInstanceOf(PostalCode.class);
		// verify
		Mockito.verify(this.postalCodeRepository, Mockito.times(1)).findByName("name");
	}

	/**
	 * @test update a {@link PostalCode}
	 * @context {@link PostalCode} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void updatePostalCode_with_Conflict() {

		// setup
		final Optional<PostalCode> notEmptyPostalCodeOptional = Optional.of(new PostalCode());
		Mockito.doReturn(this.mockedJPostalCode).when(this.mockedJPostalCode).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJPostalCode).textValue();
		Mockito.doReturn(notEmptyPostalCodeOptional).when(this.postalCodeRepository)
				.findByName(ArgumentMatchers.anyString());
		Mockito.when(this.postalCodeRepository.save(ArgumentMatchers.any(PostalCode.class)))
				.thenThrow(this.mockedDuplicateKeyException);
		// assert
		Assertions.assertThat(this.postalCodeEntityController.updatePostalCode(this.mockedJPostalCode))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.postalCodeRepository, Mockito.times(1)).save(ArgumentMatchers.any(PostalCode.class));
	}

	/**
	 * @test update a {@link PostalCode}
	 * @context {@link PostalCode} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Camille Schnell
	 */
	@Test
	public void updatePostalCode_with_resourceNotFound() {

		// setup
		final Optional<PostalCode> emptyPostalCodeOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJPostalCode).when(this.mockedJPostalCode).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJPostalCode).textValue();
		Mockito.doReturn(emptyPostalCodeOptional).when(this.postalCodeRepository)
				.findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.postalCodeEntityController.updatePostalCode(this.mockedJPostalCode))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.postalCodeRepository, Mockito.never()).save(ArgumentMatchers.any(PostalCode.class));
	}

	/**
	 * @test update a {@link PostalCode}
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * @author Camille Schnell
	 */
	@Test
	public void updatePostalCode_with_success() {

		// setup
		final Optional<PostalCode> notEmptyPostalCodeOptional = Optional.of(new PostalCode());
		Mockito.doReturn(this.mockedJPostalCode).when(this.mockedJPostalCode).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJPostalCode).textValue();
		Mockito.doReturn(notEmptyPostalCodeOptional).when(this.postalCodeRepository)
				.findByName(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.postalCodeEntityController.updatePostalCode(this.mockedJPostalCode))
				.isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.postalCodeRepository, Mockito.times(1)).save(ArgumentMatchers.any(PostalCode.class));
	}
}
