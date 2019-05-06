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
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.alten.ambroiseJEE.controller.business.EmployerBusinessController;
import fr.alten.ambroiseJEE.model.beans.Employer;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * 
 * 
 * @author Lucas Royackkers
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class EmployerRestControllerTest {

	@InjectMocks
	@Spy
	private final EmployerRestController employerRestController = new EmployerRestController();
	
	@SpyBean
	private final Employer spiedEmployer = new Employer();
	@Spy
	private JsonNode spiedJsonNode;
	
	@Mock
	private EmployerBusinessController employerBusinessController;
	@Mock
	private JsonNode mockedJsonNode;
	@Mock
	private HttpException mockedHttpException;
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * @test testing several Json for integrity when createEmployer
	 * @expected sucess for all json test cases
	 * @author Lucas Royackkers
	 */
	@Test
	public void createEmployer_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.employerBusinessController)
				.createEmployer(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"name\":\"employer\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.employerRestController.createEmployer(this.spiedJsonNode, UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingNom = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingNom);
		// assert missing name
		Assertions.assertThat(this.employerRestController.createEmployer(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test create a {@link Employer}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link EmployerBusinessController}
	 * @author Lucas Royackkers
	 */
	@Test
	public void createEmployer_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.employerRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.employerBusinessController.createEmployer(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.employerRestController.createEmployer(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test create a {@link Employer}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Lucas Royackkers
	 */
	@Test
	public void createEmployer_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.employerRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.employerRestController.createEmployer(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test testing several Json for integrity when deleteEmployer
	 * @expected sucess for all json test cases
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteEmployer_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.employerBusinessController)
				.deleteEmployer(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"name\":\"employer\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.employerRestController.deleteEmployer(this.spiedJsonNode, UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.employerRestController.deleteEmployer(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	/**
	 * @test delete a {@link Employer}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link EmployerBusinessController}
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteEmployer_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.employerRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.employerBusinessController.deleteEmployer(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.employerRestController.deleteEmployer(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test delete a {@link Employer}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteEmployer_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.employerRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.employerRestController.deleteEmployer(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test get the list of all {@link Employer}
	 * @expected returning a String
	 * @author Lucas Royackkers
	 */
	@Test
	public void getCities_expectingString() {

		// setup
		Mockito.when(this.employerBusinessController.getEmployers(ArgumentMatchers.any(UserRole.class)))
				.thenReturn(new ArrayList<Employer>());

		// assert
		Assertions.assertThat(this.employerRestController.getEmployers(UserRole.CDR_ADMIN)).isInstanceOf(String.class);
	}

	/**
	 * @test testing several Json for integrity when updateEmployer
	 * @expected sucess for all json test cases
	 * @author Lucas Royackkers
	 */
	@Test
	public void updateEmployer_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.employerBusinessController)
				.updateEmployer(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{"+ " \"oldName\":\"oldName\",\r\n" + "\"name\":\"newName\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.employerRestController.updateEmployer(this.spiedJsonNode, UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.employerRestController.updateEmployer(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);


	}

	/**
	 * @test update a {@link Employer}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link EmployerBusinessController}
	 * @author Lucas Royackkers
	 */
	@Test
	public void updateEmployer_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.employerRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.employerBusinessController.updateEmployer(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.employerRestController.updateEmployer(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);

	}

	/**
	 * @test update a {@link Employer}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Lucas Royackkers
	 */
	@Test
	public void updateEmployer_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.employerRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.employerRestController.updateEmployer(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);

	}
	
}
