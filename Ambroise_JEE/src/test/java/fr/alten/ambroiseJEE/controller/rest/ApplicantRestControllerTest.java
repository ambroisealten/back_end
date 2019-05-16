package fr.alten.ambroiseJEE.controller.rest;

import java.io.IOException;
import java.text.ParseException;
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

import fr.alten.ambroiseJEE.controller.business.ApplicantBusinessController;
import fr.alten.ambroiseJEE.controller.business.EmployerBusinessController;
import fr.alten.ambroiseJEE.model.beans.Person;
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
public class ApplicantRestControllerTest {
	@InjectMocks
	@Spy
	private final ApplicantRestController applicantRestController = new ApplicantRestController();

	@SpyBean
	private final Person spiedPerson = new Person();
	@Spy
	private JsonNode spiedJsonNode;

	@Mock
	private ApplicantBusinessController applicantBusinessController;
	@Mock
	private JsonNode mockedJsonNode;
	@Mock
	private HttpException mockedHttpException;

	private final ObjectMapper mapper = new ObjectMapper();

	/**
	 * @test testing several Json for integrity when createApplicant
	 * @expected success when the JsonNode is fully filled, otherwise
	 *           {@link UnprocessableEntityException}
	 * @throws IOException
	 * @throws ParseException
	 * @author Lucas Royackkers
	 */
	@Test
	public void createApplicant_checkJsonIntegrity() throws IOException, ParseException {
		// global Setup
		Mockito.doReturn(this.mockedHttpException).when(this.applicantBusinessController).createApplicant(
				ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class),
				ArgumentMatchers.anyString());

		// setup : all needed fields present
		final String valid = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"name\" : \"PAS ça Zinédine\","
				+ "\"monthlyWage\" : \"252525\"," + "\"mail\" : \"oskour@null.com\","
				+ "\"personInChargeMail\" : \"pasimal@cheh.net\"," + "\"highestDiploma\" : \"BAC PRO CAMPING\","
				+ "\"highestDiplomaYear\" : \"1\"," + "\"job\" : \"Récurreur de chiottes\","
				+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"opinion\" : \"++\","
				+ "\"experienceTime\" : \"2\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);

		// assert all fields are present
		Assertions.assertThat(this.applicantRestController.createApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing mail of the Person (Applicant)
		final String missingMail = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"name\" : \"PAS ça Zinédine\","
				+ "\"monthlyWage\" : \"252525\"," + "\"personInChargeMail\" : \"pasimal@cheh.net\","
				+ "\"highestDiploma\" : \"BAC PRO CAMPING\"," + "\"highestDiplomaYear\" : \"1\","
				+ "\"job\" : \"Récurreur de chiottes\","
				+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"opinion\" : \"++\","
				+ "\"experienceTime\" : \"2\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingMail);
		// assert missing mail
		Assertions.assertThat(this.applicantRestController.createApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing surname of the Person (Applicant)
		final String missingSurname = "{" + "\"name\" : \"PAS ça Zinédine\"," + "\"monthlyWage\" : \"252525\","
				+ "\"mail\" : \"oskour@null.com\"," + "\"personInChargeMail\" : \"pasimal@cheh.net\","
				+ "\"highestDiploma\" : \"BAC PRO CAMPING\"," + "\"highestDiplomaYear\" : \"1\","
				+ "\"job\" : \"Récurreur de chiottes\","
				+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"opinion\" : \"++\","
				+ "\"experienceTime\" : \"2\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingSurname);

		// assert missing surname
		Assertions.assertThat(this.applicantRestController.createApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing name of the Person (Applicant)
		final String missingName = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"monthlyWage\" : \"252525\","
				+ "\"mail\" : \"oskour@null.com\"," + "\"personInChargeMail\" : \"pasimal@cheh.net\","
				+ "\"highestDiploma\" : \"BAC PRO CAMPING\"," + "\"highestDiplomaYear\" : \"1\","
				+ "\"job\" : \"Récurreur de chiottes\","
				+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"opinion\" : \"++\","
				+ "\"experienceTime\" : \"2\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);

		// assert missing name
		Assertions.assertThat(this.applicantRestController.createApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing monthlyWage of the Person (Applicant)
		final String missingMonthlyWage = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"name\" : \"PAS ça Zinédine\","
				+ "\"mail\" : \"oskour@null.com\"," + "\"personInChargeMail\" : \"pasimal@cheh.net\","
				+ "\"highestDiploma\" : \"BAC PRO CAMPING\"," + "\"highestDiplomaYear\" : \"1\","
				+ "\"job\" : \"Récurreur de chiottes\","
				+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"opinion\" : \"++\","
				+ "\"experienceTime\" : \"2\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingMonthlyWage);

		// assert missing monthlyWage
		Assertions.assertThat(this.applicantRestController.createApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing experienceTime of the Person (Applicant)
		final String missingExperienceTime = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"name\" : \"PAS ça Zinédine\","
				+ "\"monthlyWage\" : \"252525\"," + "\"mail\" : \"oskour@null.com\","
				+ "\"personInChargeMail\" : \"pasimal@cheh.net\"," + "\"highestDiploma\" : \"BAC PRO CAMPING\","
				+ "\"highestDiplomaYear\" : \"1\"," + "\"job\" : \"Récurreur de chiottes\","
				+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"opinion\" : \"++\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingExperienceTime);

		// assert missing experienceTime
		Assertions.assertThat(this.applicantRestController.createApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing highestDiploma of the Person (Applicant)
		final String missingHighestDiploma = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"name\" : \"PAS ça Zinédine\","
				+ "\"monthlyWage\" : \"252525\"," + "\"mail\" : \"oskour@null.com\","
				+ "\"personInChargeMail\" : \"pasimal@cheh.net\"," + "\"highestDiplomaYear\" : \"1\","
				+ "\"job\" : \"Récurreur de chiottes\","
				+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"opinion\" : \"++\","
				+ "\"experienceTime\" : \"2\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingHighestDiploma);

		// assert missing highestDiploma
		Assertions.assertThat(this.applicantRestController.createApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing highestDiplomaYear of the Person (Applicant)
		final String missingHighestDiplomaYear = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"name\" : \"PAS ça Zinédine\","
				+ "\"monthlyWage\" : \"252525\"," + "\"mail\" : \"oskour@null.com\","
				+ "\"personInChargeMail\" : \"pasimal@cheh.net\"," + "\"highestDiploma\" : \"BAC PRO CAMPING\","
				+ "\"job\" : \"Récurreur de chiottes\","
				+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"opinion\" : \"++\","
				+ "\"experienceTime\" : \"2\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingHighestDiplomaYear);

		// assert missing highestDiplomaYear
		Assertions.assertThat(this.applicantRestController.createApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing job of the Person (Applicant)
		final String missingJob = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"name\" : \"PAS ça Zinédine\","
				+ "\"monthlyWage\" : \"252525\"," + "\"mail\" : \"oskour@null.com\","
				+ "\"personInChargeMail\" : \"pasimal@cheh.net\"," + "\"highestDiploma\" : \"BAC PRO CAMPING\","
				+ "\"highestDiplomaYear\" : \"1\","
				+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"opinion\" : \"++\","
				+ "\"experienceTime\" : \"2\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingJob);

		// assert missing job
		Assertions.assertThat(this.applicantRestController.createApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing employer of the Person (Applicant)
		final String missingEmployer = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"name\" : \"PAS ça Zinédine\","
				+ "\"monthlyWage\" : \"252525\"," + "\"mail\" : \"oskour@null.com\","
				+ "\"personInChargeMail\" : \"pasimal@cheh.net\"," + "\"highestDiploma\" : \"BAC PRO CAMPING\","
				+ "\"highestDiplomaYear\" : \"1\"," + "\"job\" : \"Récurreur de chiottes\"," + "\"opinion\" : \"++\","
				+ "\"experienceTime\" : \"2\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingEmployer);

		// assert missing employer
		Assertions.assertThat(this.applicantRestController.createApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing opinion of the Person (Applicant)
		final String missingOpinion = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"name\" : \"PAS ça Zinédine\","
				+ "\"monthlyWage\" : \"252525\"," + "\"mail\" : \"oskour@null.com\","
				+ "\"personInChargeMail\" : \"pasimal@cheh.net\"," + "\"highestDiploma\" : \"BAC PRO CAMPING\","
				+ "\"highestDiplomaYear\" : \"1\"," + "\"job\" : \"Récurreur de chiottes\","
				+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"experienceTime\" : \"2\""
				+ "}";

		this.spiedJsonNode = this.mapper.readTree(missingOpinion);

		// assert missing opinion
		Assertions.assertThat(this.applicantRestController.createApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test create a {@link Person} (of type Applicant)
	 * @context {@link JsonNode} params containing field "mail"
	 * @expected same {@link HttpException} returned by the
	 *           {@link ApplicantBusinessController}
	 * @throws ParseException
	 * @author Lucas Royackkers
	 */
	@Test
	public void createApplicant_with_rightParam() throws ParseException {
		// setup
		Mockito.doReturn(true).when(this.applicantRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());

		Mockito.when(this.applicantBusinessController.createApplicant(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any(UserRole.class), ArgumentMatchers.anyString()))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions
				.assertThat(
						this.applicantRestController.createApplicant(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test create a {@link Person} (of type Applicant)
	 * @context {@link JsonNode} params not containing field "mail"
	 * @expected {@link UnprocessableEntityException}
	 * @throws ParseException
	 * @author Lucas Royackkers
	 */
	@Test
	public void createApplicant_with_wrongParam() throws ParseException {
		// setup
		Mockito.doReturn(false).when(this.applicantRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());

		// assert
		Assertions
				.assertThat(
						this.applicantRestController.createApplicant(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test delete a {@link Person} (of type Applicant)
	 * @expected success when the Json is fully given, otherwise
	 *           {@link UnprocessableEntityException}
	 * @throws ParseException
	 * @throws IOException
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteApplicant_checkJsonIntegrity() throws ParseException, IOException {
		// global Setup
		Mockito.doReturn(this.mockedHttpException).when(this.applicantBusinessController)
				.deleteApplicant(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"mail\" : \"test@gmail.com\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);

		// assert all fields are present
		Assertions.assertThat(this.applicantRestController.deleteApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing mail of the Person (Applicant)
		final String missingMail = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingMail);
		// assert missing mail
		Assertions.assertThat(this.applicantRestController.deleteApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test delete a {@link Person} (of type Applicant)
	 * @context {@link JsonNode} params containing field "mail"
	 * @expected same {@link HttpException} returned by the
	 *           {@link ApplicantBusinessController}
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteApplicant_with_rightParam() {
		// setup
		Mockito.doReturn(true).when(this.applicantRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());

		Mockito.when(this.applicantBusinessController.deleteApplicant(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any(UserRole.class))).thenReturn(this.mockedHttpException);

		// assert
		Assertions
				.assertThat(
						this.applicantRestController.deleteApplicant(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test delete a {@link Person} (of type Applicant)
	 * @context {@link JsonNode} params not containing field "mail"
	 * @expected {@link UnprocessableEntityException}
	 * 
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteApplicant_with_wrongParam() {
		// setup
		Mockito.doReturn(false).when(this.applicantRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());

		// assert
		Assertions
				.assertThat(
						this.applicantRestController.deleteApplicant(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test get the list of all {@link Person} (of type Applicant)
	 * @expected returning a String
	 * @author Lucas Royackkers
	 */
	@Test
	public void getApplicants_expectingString() {
		// setup
		Mockito.when(this.applicantBusinessController.getApplicants(ArgumentMatchers.any(UserRole.class)))
				.thenReturn(new ArrayList<Person>());

		// assert
		Assertions.assertThat(this.applicantRestController.getApplicants("", UserRole.CDR_ADMIN))
				.isInstanceOf(String.class);
	}

	/**
	 * @test get a specific {@link Person} (of type Applicant) given its mail
	 * @expected returning a String
	 * @author Lucas Royackkers
	 */
	@Test
	public void getApplicant_expectingString() {
		// setup
		Mockito.when(this.applicantBusinessController.getApplicant(ArgumentMatchers.anyString(),
				ArgumentMatchers.any(UserRole.class))).thenReturn(new Person());

		// assert
		Assertions.assertThat(this.applicantRestController.getApplicant("", "", UserRole.CDR_ADMIN))
				.isInstanceOf(String.class);
	}

	/**
	 * @test testing several Json for integrity when updateApplicant
	 * @expected success when the Json is fully given, otherwise
	 *           {@link UnprocessableEntityException}
	 * @throws IOException
	 * @throws ParseException
	 * @author Lucas Royackkers
	 */
	@Test
	public void updateApplicant_checkJsonIntegrity() throws IOException, ParseException {
		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.applicantBusinessController).updateApplicant(
				ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class),
				ArgumentMatchers.anyString());

		// setup : all needed fields present
		final String valid = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"name\" : \"PAS ça Zinédine\","
				+ "\"monthlyWage\" : \"252525\"," + "\"mail\" : \"oskour@null.com\","
				+ "\"personInChargeMail\" : \"pasimal@cheh.net\"," + "\"highestDiploma\" : \"BAC PRO CAMPING\","
				+ "\"highestDiplomaYear\" : \"1\"," + "\"job\" : \"Récurreur de chiottes\","
				+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"opinion\" : \"++\","
				+ "\"experienceTime\" : \"2\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);

		// assert all fields are present
		Assertions.assertThat(this.applicantRestController.updateApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing mail of the Person (Applicant)
		final String missingMail = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"name\" : \"PAS ça Zinédine\","
				+ "\"monthlyWage\" : \"252525\"," + "\"personInChargeMail\" : \"pasimal@cheh.net\","
				+ "\"highestDiploma\" : \"BAC PRO CAMPING\"," + "\"highestDiplomaYear\" : \"1\","
				+ "\"job\" : \"Récurreur de chiottes\","
				+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"opinion\" : \"++\","
				+ "\"experienceTime\" : \"2\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingMail);
		// assert missing mail
		Assertions.assertThat(this.applicantRestController.updateApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing surname of the Person (Applicant)
		final String missingSurname = "{" + "\"name\" : \"PAS ça Zinédine\"," + "\"monthlyWage\" : \"252525\","
				+ "\"mail\" : \"oskour@null.com\"," + "\"personInChargeMail\" : \"pasimal@cheh.net\","
				+ "\"highestDiploma\" : \"BAC PRO CAMPING\"," + "\"highestDiplomaYear\" : \"1\","
				+ "\"job\" : \"Récurreur de chiottes\","
				+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"opinion\" : \"++\","
				+ "\"experienceTime\" : \"2\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingSurname);

		// assert missing surname
		Assertions.assertThat(this.applicantRestController.updateApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing name of the Person (Applicant)
		final String missingName = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"monthlyWage\" : \"252525\","
				+ "\"mail\" : \"oskour@null.com\"," + "\"personInChargeMail\" : \"pasimal@cheh.net\","
				+ "\"highestDiploma\" : \"BAC PRO CAMPING\"," + "\"highestDiplomaYear\" : \"1\","
				+ "\"job\" : \"Récurreur de chiottes\","
				+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"opinion\" : \"++\","
				+ "\"experienceTime\" : \"2\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);

		// assert missing name
		Assertions.assertThat(this.applicantRestController.updateApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing monthlyWage of the Person (Applicant)
		final String missingMonthlyWage = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"name\" : \"PAS ça Zinédine\","
				+ "\"mail\" : \"oskour@null.com\"," + "\"personInChargeMail\" : \"pasimal@cheh.net\","
				+ "\"highestDiploma\" : \"BAC PRO CAMPING\"," + "\"highestDiplomaYear\" : \"1\","
				+ "\"job\" : \"Récurreur de chiottes\","
				+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"opinion\" : \"++\","
				+ "\"experienceTime\" : \"2\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingMonthlyWage);

		// assert missing monthlyWage
		Assertions.assertThat(this.applicantRestController.updateApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing experienceTime of the Person (Applicant)
		final String missingExperienceTime = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"name\" : \"PAS ça Zinédine\","
				+ "\"monthlyWage\" : \"252525\"," + "\"mail\" : \"oskour@null.com\","
				+ "\"personInChargeMail\" : \"pasimal@cheh.net\"," + "\"highestDiploma\" : \"BAC PRO CAMPING\","
				+ "\"highestDiplomaYear\" : \"1\"," + "\"job\" : \"Récurreur de chiottes\","
				+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"opinion\" : \"++\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingExperienceTime);

		// assert missing experienceTime
		Assertions.assertThat(this.applicantRestController.updateApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing highestDiploma of the Person (Applicant)
		final String missingHighestDiploma = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"name\" : \"PAS ça Zinédine\","
				+ "\"monthlyWage\" : \"252525\"," + "\"mail\" : \"oskour@null.com\","
				+ "\"personInChargeMail\" : \"pasimal@cheh.net\"," + "\"highestDiplomaYear\" : \"1\","
				+ "\"job\" : \"Récurreur de chiottes\","
				+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"opinion\" : \"++\","
				+ "\"experienceTime\" : \"2\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingHighestDiploma);

		// assert missing highestDiploma
		Assertions.assertThat(this.applicantRestController.updateApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing highestDiplomaYear of the Person (Applicant)
		final String missingHighestDiplomaYear = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"name\" : \"PAS ça Zinédine\","
				+ "\"monthlyWage\" : \"252525\"," + "\"mail\" : \"oskour@null.com\","
				+ "\"personInChargeMail\" : \"pasimal@cheh.net\"," + "\"highestDiploma\" : \"BAC PRO CAMPING\","
				+ "\"job\" : \"Récurreur de chiottes\","
				+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"opinion\" : \"++\","
				+ "\"experienceTime\" : \"2\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingHighestDiplomaYear);

		// assert missing highestDiplomaYear
		Assertions.assertThat(this.applicantRestController.updateApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing job of the Person (Applicant)
		final String missingJob = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"name\" : \"PAS ça Zinédine\","
				+ "\"monthlyWage\" : \"252525\"," + "\"mail\" : \"oskour@null.com\","
				+ "\"personInChargeMail\" : \"pasimal@cheh.net\"," + "\"highestDiploma\" : \"BAC PRO CAMPING\","
				+ "\"highestDiplomaYear\" : \"1\","
				+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"opinion\" : \"++\","
				+ "\"experienceTime\" : \"2\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingJob);

		// assert missing job
		Assertions.assertThat(this.applicantRestController.updateApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing employer of the Person (Applicant)
		final String missingEmployer = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"name\" : \"PAS ça Zinédine\","
				+ "\"monthlyWage\" : \"252525\"," + "\"mail\" : \"oskour@null.com\","
				+ "\"personInChargeMail\" : \"pasimal@cheh.net\"," + "\"highestDiploma\" : \"BAC PRO CAMPING\","
				+ "\"highestDiplomaYear\" : \"1\"," + "\"job\" : \"Récurreur de chiottes\"," + "\"opinion\" : \"++\","
				+ "\"experienceTime\" : \"2\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingEmployer);

		// assert missing employer
		Assertions.assertThat(this.applicantRestController.updateApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

		// setup : missing opinion of the Person (Applicant)
		final String missingOpinion = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"name\" : \"PAS ça Zinédine\","
				+ "\"monthlyWage\" : \"252525\"," + "\"mail\" : \"oskour@null.com\","
				+ "\"personInChargeMail\" : \"pasimal@cheh.net\"," + "\"highestDiploma\" : \"BAC PRO CAMPING\","
				+ "\"highestDiplomaYear\" : \"1\"," + "\"job\" : \"Récurreur de chiottes\","
				+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"experienceTime\" : \"2\""
				+ "}";

		this.spiedJsonNode = this.mapper.readTree(missingOpinion);

		// assert missing opinion
		Assertions.assertThat(this.applicantRestController.updateApplicant(this.spiedJsonNode, "mail", UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test update a {@link Person} (of type Applicant)
	 * @context {@link JsonNode} params containing field "mail"
	 * @expected same {@link HttpException} returned by the
	 *           {@link EmployerBusinessController}
	 * @throws ParseException
	 * @author Lucas Royackkers
	 */
	@Test
	public void updateApplicant_with_rightParam() throws ParseException {
		// setup
		Mockito.doReturn(true).when(this.applicantRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());
		Mockito.when(this.applicantBusinessController.updateApplicant(this.mockedJsonNode, UserRole.CDR_ADMIN, "mail"))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions
				.assertThat(
						this.applicantRestController.updateApplicant(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test update a {@link Person} (of type Applicant)
	 * @context {@link JsonNode} params not containing field "mail"
	 * @expected {@link UnprocessableEntityException}
	 * @throws ParseException
	 * @author Lucas Royackkers
	 */
	@Test
	public void updateApplicant_with_wrongParam() throws ParseException {
		// setup
		Mockito.doReturn(false).when(this.applicantRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());

		// assert
		Assertions
				.assertThat(
						this.applicantRestController.updateApplicant(this.mockedJsonNode, "mail", UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

}
