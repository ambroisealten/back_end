package fr.alten.ambroiseJEE.controller.rest;

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
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.alten.ambroiseJEE.controller.business.DiplomaBusinessController;
import fr.alten.ambroiseJEE.model.beans.Diploma;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * 
 * 
 * @author Thomas Decamp
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DiplomaRestControllerTest {

	@InjectMocks
	@Spy
	private final DiplomaRestController diplomaRestController = new DiplomaRestController();
	
	@SpyBean
	private final Diploma spiedDiploma = new Diploma();
	@Spy
	private JsonNode spiedJsonNode;
	
	@Mock
	private DiplomaBusinessController diplomaBusinessController;
	@Mock
	private JsonNode mockedJsonNode;
	@Mock
	private HttpException mockedHttpException;
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * @test testing several Json for integrity when createDiploma
	 * @expected sucess for all json test cases
	 * @author Thomas Decamp
	 * @throws Exception 
	 */
	@Test
	public void createDiploma_checkJsonIntegrity() throws Exception {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.diplomaBusinessController)
				.createDiploma(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"name\":\"diploma\"," + "\"yearOfResult\":\"2019\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.diplomaRestController.createDiploma(this.spiedJsonNode, UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingNom = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingNom);
		// assert missing name
		Assertions.assertThat(this.diplomaRestController.createDiploma(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test create a {@link Diploma}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link DiplomaBusinessController}
	 * @author Thomas Decamp
	 * @throws Exception 
	 */
	@Test
	public void createDiploma_with_rightParam() throws Exception {

		// setup
		Mockito.doReturn(true).when(this.diplomaRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.diplomaBusinessController.createDiploma(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.diplomaRestController.createDiploma(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test create a {@link Diploma}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Thomas Decamp
	 * @throws Exception 
	 */
	@Test
	public void createDiploma_with_wrongParam() throws Exception {

		// setup
		Mockito.doReturn(false).when(this.diplomaRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.diplomaRestController.createDiploma(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test testing several Json for integrity when deleteDiploma
	 * @expected sucess for all json test cases
	 * @author Thomas Decamp
	 * @throws Exception 
	 */
	@Test
	public void deleteDiploma_checkJsonIntegrity() throws Exception {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.diplomaBusinessController)
				.deleteDiploma(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"name\":\"diploma\"," + "\"yearOfResult\":\"2019\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.diplomaRestController.deleteDiploma(this.spiedJsonNode, UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.diplomaRestController.deleteDiploma(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	/**
	 * @test delete a {@link Diploma}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link DiplomaBusinessController}
	 * @author Thomas Decamp
	 * @throws Exception 
	 */
	@Test
	public void deleteDiploma_with_rightParam() throws Exception {

		// setup
		Mockito.doReturn(true).when(this.diplomaRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.diplomaBusinessController.deleteDiploma(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.diplomaRestController.deleteDiploma(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test delete a {@link Diploma}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Thomas Decamp
	 * @throws Exception 
	 */
	@Test
	public void deleteDiploma_with_wrongParam() throws Exception {

		// setup
		Mockito.doReturn(false).when(this.diplomaRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.diplomaRestController.deleteDiploma(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test get the list of all {@link Diploma}
	 * @expected returning a String
	 * @author Thomas Decamp
	 */
	@Test
	public void getCities_expectingString() {

		// setup
		Mockito.when(this.diplomaBusinessController.getDiplomas(ArgumentMatchers.any(UserRole.class)))
				.thenReturn(new ArrayList<Diploma>());

		// assert
		Assertions.assertThat(this.diplomaRestController.getDiplomas(UserRole.CDR_ADMIN)).isInstanceOf(String.class);
	}

	/**
	 * @test testing several Json for integrity when updateDiploma
	 * @expected sucess for all json test cases
	 * @author Thomas Decamp
	 * @throws Exception 
	 */
	@Test
	public void updateDiploma_checkJsonIntegrity() throws Exception {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.diplomaBusinessController)
				.updateDiploma(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{"+ " \"oldName\":\"oldName\",\r\n" + "\"name\":\"newName\"," + "\"yearOfResult\":\"2019\"," + "\"oldYearOfResult\":\"2020\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.diplomaRestController.updateDiploma(this.spiedJsonNode, UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.diplomaRestController.updateDiploma(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);


	}

	/**
	 * @test update a {@link Diploma}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link DiplomaBusinessController}
	 * @author Thomas Decamp
	 * @throws Exception 
	 */
	@Test
	public void updateDiploma_with_rightParam() throws Exception {

		// setup
		Mockito.doReturn(true).when(this.diplomaRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.diplomaBusinessController.updateDiploma(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.diplomaRestController.updateDiploma(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);

	}

	/**
	 * @test update a {@link Diploma}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Thomas Decamp
	 * @throws Exception 
	 */
	@Test
	public void updateDiploma_with_wrongParam() throws Exception {

		// setup
		Mockito.doReturn(false).when(this.diplomaRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.diplomaRestController.updateDiploma(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);

	}
	
}
