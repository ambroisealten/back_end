package fr.alten.ambroiseJEE.controller.rest;

import java.io.IOException;
import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.alten.ambroiseJEE.controller.business.geographic.PostalCodeBusinessController;
import fr.alten.ambroiseJEE.model.beans.PostalCode;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Test class for PostalCodeRestController
 *
 * @author Camille Schnell
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PostalCodeRestControllerTest {

	@InjectMocks
	@Spy
	private final PostalCodeRestController postalCodeRestController = new PostalCodeRestController();

	@Spy
	private JsonNode spiedJsonNode;

	@Mock
	private PostalCodeBusinessController postalCodeBusinessController;
	@Mock
	private JsonNode mockedJsonNode;
	@Mock
	private HttpException mockedHttpException;

	private final ObjectMapper mapper = new ObjectMapper();

	/**
	 * @test testing several Json for integrity when createPostalCode
	 * @expected sucess for all json test cases
	 * @author Camille Schnell
	 */
	@Test
	public void createPostalCode_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.postalCodeBusinessController)
				.createPostalCode(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"name\":\"67000\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.postalCodeRestController.createPostalCode(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.postalCodeRestController.createPostalCode(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test create a {@link PostalCode}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link PostalCodeBusinessController}
	 * @author Camille Schnell
	 */
	@Test
	public void createPostalCode_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.postalCodeRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());
		Mockito.when(this.postalCodeBusinessController.createPostalCode(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions
				.assertThat(
						this.postalCodeRestController.createPostalCode(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test create a {@link PostalCode}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Camille Schnell
	 */
	@Test
	public void createPostalCode_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.postalCodeRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());

		// assert
		Assertions
				.assertThat(
						this.postalCodeRestController.createPostalCode(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test testing several Json for integrity when deletePostalCode
	 * @expected sucess for all json test cases
	 * @author Camille Schnell
	 */
	@Test
	public void deletePostalCode_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.postalCodeBusinessController)
				.deletePostalCode(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"name\":\"67000\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.postalCodeRestController.deletePostalCode(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing code
		final String missingCode = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingCode);
		// assert missing code
		Assertions.assertThat(this.postalCodeRestController.deletePostalCode(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	/**
	 * @test delete a {@link PostalCode}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link PostalCodeBusinessController}
	 * @author Camille Schnell
	 */
	@Test
	public void deletePostalCode_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.postalCodeRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());
		Mockito.when(this.postalCodeBusinessController.deletePostalCode(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions
				.assertThat(
						this.postalCodeRestController.deletePostalCode(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test delete a {@link PostalCode}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Camille Schnell
	 */
	@Test
	public void deletePostalCode_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.postalCodeRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());

		// assert
		Assertions
				.assertThat(
						this.postalCodeRestController.deletePostalCode(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test get the list of all {@link PostalCode}
	 * @expected returning a String
	 * @author Camille Schnell
	 */
	@Test
	public void getPostalCodes_expectingString() {

		// setup
		Mockito.when(this.postalCodeBusinessController.getPostalCodes(ArgumentMatchers.any(UserRole.class)))
				.thenReturn(new ArrayList<PostalCode>());

		// assert
		Assertions.assertThat(this.postalCodeRestController.getPostalCodes("mail", UserRole.CDR_ADMIN))
				.isInstanceOf(String.class);
	}

	/**
	 * @test testing several Json for integrity when updatePostalCode
	 * @expected sucess for all json test cases
	 * @author Camille Schnell
	 */
	@Test
	public void updatePostalCode_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.postalCodeBusinessController)
				.updatePostalCode(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{\r\n" + "  \"oldName\":\"old\",\r\n" + "  \"name\":\"newName\"\r\n" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.postalCodeRestController.updatePostalCode(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{" + "  \"oldName\":\"old\"\r\n" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.postalCodeRestController.updatePostalCode(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingOldName = "{" + "  \"name\":\"newName\"\r\n" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingOldName);
		// assert missing name
		Assertions.assertThat(this.postalCodeRestController.updatePostalCode(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test update a {@link PostalCode}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link PostalCodeBusinessController}
	 * @author Camille Schnell
	 */
	@Test
	public void updatePostalCode_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.postalCodeRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());
		Mockito.when(this.postalCodeBusinessController.updatePostalCode(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions
				.assertThat(
						this.postalCodeRestController.updatePostalCode(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);

	}

	/**
	 * @test update a {@link PostalCode}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Camille Schnell
	 */
	@Test
	public void updatePostalCode_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.postalCodeRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());

		// assert
		Assertions
				.assertThat(
						this.postalCodeRestController.updatePostalCode(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);

	}
}
