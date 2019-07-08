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

import fr.alten.ambroiseJEE.controller.business.JobBusinessController;
import fr.alten.ambroiseJEE.model.beans.Job;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * Test class for JobRestController
 *
 * @author Camille Schnell
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class JobRestControllerTest {

	@InjectMocks
	@Spy
	private final JobRestController jobRestController = new JobRestController();

	@Spy
	private JsonNode spiedJsonNode;

	@Mock
	private JobBusinessController jobBusinessController;
	@Mock
	private JsonNode mockedJsonNode;
	@Mock
	private HttpException mockedHttpException;

	private final ObjectMapper mapper = new ObjectMapper();

	/**
	 * @test testing several Json for integrity when createJob
	 * @expected sucess for all json test cases
	 * @author Camille Schnell
	 */
	@Test
	public void createJob_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.jobBusinessController)
				.createJob(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"title\":\"job\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.jobRestController.createJob(this.spiedJsonNode, UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing title
		final String missingTitle = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingTitle);
		// assert missing title
		Assertions.assertThat(this.jobRestController.createJob(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test create a {@link Job}
	 * @context {@link JsonNode} params containing field "title"
	 * @expected same {@link HttpException} returned by the
	 *           {@link JobBusinessController}
	 * @author Camille Schnell
	 */
	@Test
	public void createJob_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.jobRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.jobBusinessController.createJob(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.jobRestController.createJob(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test create a {@link Job}
	 * @context {@link JsonNode} params not containing field "title"
	 * @expected {@link UnprocessableEntityException}
	 * @author Camille Schnell
	 */
	@Test
	public void createJob_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.jobRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.jobRestController.createJob(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test testing several Json for integrity when deleteJob
	 * @expected sucess for all json test cases
	 * @author Camille Schnell
	 */
	@Test
	public void deleteJob_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.jobBusinessController)
				.deleteJob(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"title\":\"job\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.jobRestController.deleteJob(this.spiedJsonNode, UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing title
		final String missingTitle = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingTitle);
		// assert missing title
		Assertions.assertThat(this.jobRestController.deleteJob(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	/**
	 * @test delete a {@link Job}
	 * @context {@link JsonNode} params containing field "title"
	 * @expected same {@link HttpException} returned by the
	 *           {@link JobBusinessController}
	 * @author Camille Schnell
	 */
	@Test
	public void deleteJob_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.jobRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.jobBusinessController.deleteJob(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.jobRestController.deleteJob(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test delete a {@link Job}
	 * @context {@link JsonNode} params not containing field "title"
	 * @expected {@link UnprocessableEntityException}
	 * @author Camille Schnell
	 */
	@Test
	public void deleteJob_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.jobRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.jobRestController.deleteJob(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test get the list of all {@link Job}
	 * @expected returning a String
	 * @author Camille Schnell
	 */
	@Test
	public void getJobs_expectingString() {

		// setup
		Mockito.when(this.jobBusinessController.getJobs(ArgumentMatchers.any(UserRole.class)))
				.thenReturn(new ArrayList<Job>());

		// assert
		Assertions.assertThat(this.jobRestController.getJobs(UserRole.CDR_ADMIN)).isInstanceOf(String.class);
	}

	/**
	 * @test testing several Json for integrity when updateJob
	 * @expected sucess for all json test cases
	 * @author Camille Schnell
	 */
	@Test
	public void updateJob_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.jobBusinessController)
				.updateJob(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{"+ " \"oldTitle\":\"oldTitle\",\r\n" + "\"title\":\"newTitle\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.jobRestController.updateJob(this.spiedJsonNode, UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing title
		final String missingTitle = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingTitle);
		// assert missing title
		Assertions.assertThat(this.jobRestController.updateJob(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);


	}

	/**
	 * @test update a {@link Job}
	 * @context {@link JsonNode} params containing field "title"
	 * @expected same {@link HttpException} returned by the
	 *           {@link JobBusinessController}
	 * @author Camille Schnell
	 */
	@Test
	public void updateJob_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.jobRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.jobBusinessController.updateJob(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.jobRestController.updateJob(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);

	}

	/**
	 * @test update a {@link Job}
	 * @context {@link JsonNode} params not containing field "title"
	 * @expected {@link UnprocessableEntityException}
	 * @author Camille Schnell
	 */
	@Test
	public void updateJob_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.jobRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.jobRestController.updateJob(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);

	}
}
