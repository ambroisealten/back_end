package fr.alten.ambroiseJEE.model.entityControllers;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DuplicateKeyException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.alten.ambroiseJEE.model.beans.Diploma;
import fr.alten.ambroiseJEE.model.beans.Employer;
import fr.alten.ambroiseJEE.model.beans.Job;
import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.PersonRepository;
import fr.alten.ambroiseJEE.utils.PersonRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.InternalServerErrorException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * 
 * @author Lucas Royackkers
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PersonEntityControllerTest {

	@InjectMocks
	@Spy
	private final PersonEntityController personEntityController = new PersonEntityController();

	@Mock
	private final UserEntityController userEntityController = new UserEntityController();
	@Mock
	private final DiplomaEntityController diplomaEntityController = new DiplomaEntityController();
	@Mock
	private final EmployerEntityController employerEntityController = new EmployerEntityController();
	@Mock
	private final JobEntityController jobEntityController = new JobEntityController();

	@Mock
	private ResourceNotFoundException mockedRNFE;

	@Mock
	private PersonRepository personRepository;
	@Mock
	private CreatedException mockedCreatedException;
	@Mock
	private ConflictException mockedConflictException;
	@Mock
	private DuplicateKeyException mockedDuplicateKeyException;
	@Mock
	private JsonNode mockedJPerson;
	@SpyBean
	private static Person spiedPerson = new Person();
	@SpyBean
	private static User spiedUser = new User();
	@SpyBean
	private static Diploma spiedDiploma = new Diploma();
	@SpyBean
	private static Job spiedJob = new Job();
	@SpyBean
	private static Employer spiedEmployer = new Employer();

	private static String jsonStringTest = "{" + "\"surname\" : \"OSKOUUUR\"," + "\"name\" : \"PAS ça Zinédine\","
			+ "\"monthlyWage\" : \"252525\"," + "\"mail\" : \"oskour@null.com\","
			+ "\"personInChargeMail\" : \"pasimal@cheh.net\"," + "\"highestDiploma\" : \"BAC PRO CAMPING\","
			+ "\"highestDiplomaYear\" : \"1\"," + "\"job\" : \"Récurreur de chiottes\","
			+ "\"employer\" : \"La Coopérative Mangin de Boulazac-en-Dordogne\"," + "\"opinion\" : \"++\"" + "}";

	private ObjectMapper mapper = new ObjectMapper();

	@BeforeClass
	public static void init() {
		initUser();
		initDiploma();
		initJob();
		initEmployer();
	}

	public static void initUser() {
		spiedUser.setMail("unmail@orange.fr");
	}

	public static void initDiploma() {
		spiedDiploma.setName("au pif");
		spiedDiploma.setYearOfResult("un truc au pif");
	}

	public static void initJob() {
		spiedJob.setTitle("FERME LA LE TEST");
	}

	public static void initEmployer() {
		spiedEmployer.setName("PAS COMME CA ZINEDINE :/");
	}

	/**
	 * @test crate a {@link Person} of Applicant type
	 * @context email of the {@link Person} of wrong type
	 * @expected {@link UnprocessableEntityException} save() never called
	 * @throws IOException
	 * @author Lucas Royackkers
	 */
	@Test
	public void createApplicant_with_mail_invalid() throws IOException {
		JsonNode jPerson = mapper.readTree(jsonStringTest);
		// setup
		Mockito.doReturn(false).when(this.personEntityController).validateMail(ArgumentMatchers.anyString());

		// assert
		Assertions.assertThat(this.personEntityController.createPerson(jPerson, PersonRole.APPLICANT, ""))
				.isInstanceOf(UnprocessableEntityException.class);

		// verify
		Mockito.verify(this.personRepository, Mockito.never()).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test crate a {@link Person} of Consultant type
	 * @context email of the {@link Person} of wrong type
	 * @expected {@link UnprocessableEntityException} save() never called
	 * @throws IOException
	 * @author Lucas Royackkers
	 */
	@Test
	public void createConsultant_with_mail_invalid() throws IOException {
		JsonNode jPerson = mapper.readTree(jsonStringTest);
		// setup
		Mockito.doReturn(false).when(this.personEntityController).validateMail(ArgumentMatchers.anyString());

		// assert
		Assertions.assertThat(this.personEntityController.createPerson(jPerson, PersonRole.CONSULTANT, ""))
				.isInstanceOf(UnprocessableEntityException.class);

		// verify
		Mockito.verify(this.personRepository, Mockito.never()).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test create a {@link Person} of Applicant type
	 * @context {@link Person} already existing in base
	 * @expected {@link ConflictException} save() call twice
	 * @author Lucas Royackkers
	 * @throws ParseException
	 * @throws IOException
	 */
	@Test
	public void createApplicant_with_conflict() throws ParseException, IOException {
		JsonNode jPerson = mapper.readTree(jsonStringTest);
		// setup validated
		Mockito.doReturn(true).when(this.personEntityController).validateMail(ArgumentMatchers.anyString());

		// setup other controllers & types
		Mockito.doReturn(PersonEntityControllerTest.spiedUser).when(this.userEntityController)
				.getUserByMail(ArgumentMatchers.anyString());

		Mockito.when(this.diplomaEntityController.getDiplomaByNameAndYearOfResult(ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(spiedDiploma).thenThrow(new ResourceNotFoundException());
		Mockito.when(
				this.diplomaEntityController.createDiploma(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(spiedDiploma);

		Mockito.when(this.jobEntityController.getJob(ArgumentMatchers.anyString())).thenReturn(spiedJob)
				.thenThrow(new ResourceNotFoundException());
		Mockito.when(this.jobEntityController.createJob(ArgumentMatchers.anyString())).thenReturn(spiedJob);

		Mockito.when(this.employerEntityController.getEmployer(ArgumentMatchers.anyString())).thenReturn(spiedEmployer)
				.thenThrow(new ResourceNotFoundException());
		Mockito.when(this.employerEntityController.createEmployer(ArgumentMatchers.anyString()))
				.thenReturn(spiedEmployer);

		Mockito.doThrow(this.mockedDuplicateKeyException).when(this.personRepository)
				.save(ArgumentMatchers.any(Person.class));
		// assert
		Assertions.assertThat(this.personEntityController.createPerson(jPerson, PersonRole.APPLICANT, ""))
				.isInstanceOf(ConflictException.class);
		Assertions.assertThat(this.personEntityController.createPerson(jPerson, PersonRole.APPLICANT, ""))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(2)).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test create a {@link Person} of Consultant type
	 * @context {@link Person} already existing in base, test is repeated two times
	 *          to trigger a ResourceNotFoundException when we search Diploma,
	 *          Employer and Job objects
	 * @expected {@link ConflictException} save() call twice
	 * @author Lucas Royackkers
	 * @throws ParseException, IOException
	 */
	@Test
	public void createConsultant_with_conflict() throws ParseException, IOException {
		JsonNode jPerson = mapper.readTree(jsonStringTest);
		// setup validated
		Mockito.doReturn(true).when(this.personEntityController).validateMail(ArgumentMatchers.anyString());

		// setup other controllers & types
		Mockito.doReturn(PersonEntityControllerTest.spiedUser).when(this.userEntityController)
				.getUserByMail(ArgumentMatchers.anyString());

		Mockito.when(this.diplomaEntityController.getDiplomaByNameAndYearOfResult(ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(spiedDiploma).thenThrow(new ResourceNotFoundException());
		Mockito.when(
				this.diplomaEntityController.createDiploma(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(spiedDiploma);

		Mockito.when(this.jobEntityController.getJob(ArgumentMatchers.anyString())).thenReturn(spiedJob)
				.thenThrow(new ResourceNotFoundException());
		Mockito.when(this.jobEntityController.createJob(ArgumentMatchers.anyString())).thenReturn(spiedJob);

		Mockito.when(this.employerEntityController.getEmployer(ArgumentMatchers.anyString())).thenReturn(spiedEmployer)
				.thenThrow(new ResourceNotFoundException());
		Mockito.when(this.employerEntityController.createEmployer(ArgumentMatchers.anyString()))
				.thenReturn(spiedEmployer);

		Mockito.doThrow(this.mockedDuplicateKeyException).when(this.personRepository)
				.save(ArgumentMatchers.any(Person.class));
		// assert
		Assertions.assertThat(this.personEntityController.createPerson(jPerson, PersonRole.CONSULTANT, ""))
				.isInstanceOf(ConflictException.class);
		Assertions.assertThat(this.personEntityController.createPerson(jPerson, PersonRole.CONSULTANT, ""))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(2)).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test create a {@link Person} of Applicant type
	 * @context Wrong parameters (null or wrong type) given by the user
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Lucas Royackkers
	 * @throws IOException
	 */
	@Test
	public void createApplicant_with_rnfe() throws IOException {
		JsonNode jPerson = mapper.readTree(jsonStringTest);
		// setup validated
		Mockito.doReturn(true).when(this.personEntityController).validateMail(ArgumentMatchers.anyString());

		// setup other controllers & types
		Mockito.doThrow(new ResourceNotFoundException()).when(this.userEntityController)
				.getUserByMail(ArgumentMatchers.anyString());

		// assert
		Assertions.assertThat(this.personEntityController.createPerson(jPerson, PersonRole.APPLICANT, ""))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.never()).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test create a {@link Person} of Consultant type
	 * @context Wrong parameters (null or wrong type) given by the user
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Lucas Royackkers
	 * @throws IOException
	 */
	@Test
	public void createConsultant_with_rnfe() throws IOException {
		JsonNode jPerson = mapper.readTree(jsonStringTest);
		// setup validated
		Mockito.doReturn(true).when(this.personEntityController).validateMail(ArgumentMatchers.anyString());

		// setup other controllers & types
		Mockito.doThrow(new ResourceNotFoundException()).when(this.userEntityController)
				.getUserByMail(ArgumentMatchers.anyString());

		// assert
		Assertions.assertThat(this.personEntityController.createPerson(jPerson, PersonRole.CONSULTANT, ""))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.never()).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test create a {@link Person} of Applicant type
	 * @context All parameters are OK
	 * @expected {@link InternalServerErrorException} save() called twice
	 * @throws IOException
	 * @author Lucas Royackkers
	 */
	@Test
	public void createApplicant_with_success() throws IOException {
		JsonNode jPerson = mapper.readTree(jsonStringTest);
		// setup validated
		Mockito.doReturn(true).when(this.personEntityController).validateMail(ArgumentMatchers.anyString());

		// setup other controllers & types
		Mockito.doReturn(PersonEntityControllerTest.spiedUser).when(this.userEntityController)
				.getUserByMail(ArgumentMatchers.anyString());

		Mockito.when(this.diplomaEntityController.getDiplomaByNameAndYearOfResult(ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(spiedDiploma).thenThrow(new ResourceNotFoundException());
		Mockito.when(
				this.diplomaEntityController.createDiploma(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(spiedDiploma);

		Mockito.when(this.jobEntityController.getJob(ArgumentMatchers.anyString())).thenReturn(spiedJob)
				.thenThrow(new ResourceNotFoundException());
		Mockito.when(this.jobEntityController.createJob(ArgumentMatchers.anyString())).thenReturn(spiedJob);

		Mockito.when(this.employerEntityController.getEmployer(ArgumentMatchers.anyString())).thenReturn(spiedEmployer)
				.thenThrow(new ResourceNotFoundException());
		Mockito.when(this.employerEntityController.createEmployer(ArgumentMatchers.anyString()))
				.thenReturn(spiedEmployer);

		// assert
		Assertions.assertThat(this.personEntityController.createPerson(jPerson, PersonRole.APPLICANT, ""))
				.isInstanceOf(CreatedException.class);
		Assertions.assertThat(this.personEntityController.createPerson(jPerson, PersonRole.APPLICANT, ""))
				.isInstanceOf(CreatedException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(2)).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test create a {@link Person} of Consultant type
	 * @context All parameters are OK
	 * @expected {@link InternalServerErrorException} save() called twice
	 * @throws IOException
	 * @author Lucas Royackkers
	 */
	@Test
	public void createConsultant_with_success() throws IOException {
		JsonNode jPerson = mapper.readTree(jsonStringTest);
		// setup validated
		Mockito.doReturn(true).when(this.personEntityController).validateMail(ArgumentMatchers.anyString());

		// setup other controllers & types
		Mockito.doReturn(PersonEntityControllerTest.spiedUser).when(this.userEntityController)
				.getUserByMail(ArgumentMatchers.anyString());

		Mockito.when(this.diplomaEntityController.getDiplomaByNameAndYearOfResult(ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(spiedDiploma).thenThrow(new ResourceNotFoundException());
		Mockito.when(
				this.diplomaEntityController.createDiploma(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(spiedDiploma);

		Mockito.when(this.jobEntityController.getJob(ArgumentMatchers.anyString())).thenReturn(spiedJob)
				.thenThrow(new ResourceNotFoundException());
		Mockito.when(this.jobEntityController.createJob(ArgumentMatchers.anyString())).thenReturn(spiedJob);

		Mockito.when(this.employerEntityController.getEmployer(ArgumentMatchers.anyString())).thenReturn(spiedEmployer)
				.thenThrow(new ResourceNotFoundException());
		Mockito.when(this.employerEntityController.createEmployer(ArgumentMatchers.anyString()))
				.thenReturn(spiedEmployer);

		// assert
		Assertions.assertThat(this.personEntityController.createPerson(jPerson, PersonRole.CONSULTANT, ""))
				.isInstanceOf(CreatedException.class);
		Assertions.assertThat(this.personEntityController.createPerson(jPerson, PersonRole.CONSULTANT, ""))
				.isInstanceOf(CreatedException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(2)).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test delete a {@link Person} of Applicant type
	 * @context {@link Person} already existing in base
	 * @expected {@link ConflictException} save() called once
	 * @author Lucas Royackkers
	 * @throws IOException
	 */
	@Test
	public void deleteApplicant_with_conflict() throws IOException {
		JsonNode jPerson = mapper.readTree(jsonStringTest);
		// setup validated
		Mockito.doReturn(PersonEntityControllerTest.spiedPerson).when(this.personEntityController)
				.getPersonByMailAndType(ArgumentMatchers.anyString(), ArgumentMatchers.any(PersonRole.class));

		Mockito.doThrow(this.mockedDuplicateKeyException).when(this.personRepository)
				.save(ArgumentMatchers.any(Person.class));
		// assert
		Assertions.assertThat(this.personEntityController.deletePerson(jPerson, PersonRole.APPLICANT))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test delete a {@link Person} of Consultant type
	 * @context {@link Person} already existing in base
	 * @expected {@link ConflictException} save() called once
	 * @author Lucas Royackkers
	 * @throws IOException
	 */
	@Test
	public void deleteConsultant_with_conflict() throws IOException {
		JsonNode jPerson = mapper.readTree(jsonStringTest);
		// setup validated
		Mockito.doReturn(PersonEntityControllerTest.spiedPerson).when(this.personEntityController)
				.getPersonByMailAndType(ArgumentMatchers.anyString(), ArgumentMatchers.any(PersonRole.class));

		Mockito.doThrow(this.mockedDuplicateKeyException).when(this.personRepository)
				.save(ArgumentMatchers.any(Person.class));
		// assert
		Assertions.assertThat(this.personEntityController.deletePerson(jPerson, PersonRole.CONSULTANT))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test delete a {@link Person} of type Applicant
	 * @context {@link Person} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Lucas Royackkers
	 * @throws IOException
	 */
	@Test
	public void deleteApplicant_with_resourceNotFound() throws IOException {
		JsonNode jPerson = mapper.readTree(jsonStringTest);
		// setup validated
		Mockito.doThrow(new ResourceNotFoundException()).when(this.personEntityController)
				.getPersonByMailAndType(ArgumentMatchers.anyString(), ArgumentMatchers.any(PersonRole.class));

		Assertions.assertThat(this.personEntityController.deletePerson(jPerson, PersonRole.APPLICANT))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.never()).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test delete a {@link Person} of Consultant type
	 * @context {@link Person} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Lucas Royackkers
	 * @throws IOException
	 */
	@Test
	public void deleteConsultant_with_resourceNotFound() throws IOException {
		JsonNode jPerson = mapper.readTree(jsonStringTest);
		// setup validated
		Mockito.doThrow(new ResourceNotFoundException()).when(this.personEntityController)
				.getPersonByMailAndType(ArgumentMatchers.anyString(), ArgumentMatchers.any(PersonRole.class));

		Assertions.assertThat(this.personEntityController.deletePerson(jPerson, PersonRole.CONSULTANT))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.never()).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test delete a {@link Person} of Applicant type
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * @author Lucas Royackkers
	 * @throws IOException
	 */
	@Test
	public void deleteApplicant_with_success() throws IOException {
		JsonNode jPerson = mapper.readTree(jsonStringTest);
		// setup validated
		Mockito.doReturn(PersonEntityControllerTest.spiedPerson).when(this.personEntityController)
				.getPersonByMailAndType(ArgumentMatchers.anyString(), ArgumentMatchers.any(PersonRole.class));

		// assert
		Assertions.assertThat(this.personEntityController.deletePerson(jPerson, PersonRole.CONSULTANT))
				.isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test delete a {@link Person} of Consultant type
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * @author Lucas Royackkers
	 * @throws IOException
	 */
	@Test
	public void deleteConsultant_with_success() throws IOException {
		JsonNode jPerson = mapper.readTree(jsonStringTest);
		// setup validated
		Mockito.doReturn(PersonEntityControllerTest.spiedPerson).when(this.personEntityController)
				.getPersonByMailAndType(ArgumentMatchers.anyString(), ArgumentMatchers.any(PersonRole.class));

		// assert
		Assertions.assertThat(this.personEntityController.deletePerson(jPerson, PersonRole.CONSULTANT))
				.isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test get all {@link Person} of Applicant type
	 * @expected return instance of {@link List} save() call only once
	 * @author Lucas Royackkers
	 */
	@Test
	public void getApplicants() {

		// assert
		Assertions.assertThat(this.personEntityController.getPersonsByRole(PersonRole.APPLICANT))
				.isInstanceOf(List.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).findAllByRole(PersonRole.APPLICANT);
	}

	/**
	 * @test get all {@link Person} of Consultant type
	 * @expected return instance of {@link List} save() call only once
	 * @author Lucas Royackkers
	 */
	@Test
	public void getConsultants() {

		// assert
		Assertions.assertThat(this.personEntityController.getPersonsByRole(PersonRole.CONSULTANT))
				.isInstanceOf(List.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).findAllByRole(PersonRole.CONSULTANT);
	}

	/**
	 * @test get a {@link Person} by name of Applicant type
	 * @context {@link Person} not found in base
	 * @expected {@link ResourceNotFoundException}
	 * @author Lucas Royackkers
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void getApplicant_with_ResourceNotFound() {
		// setup
		final Optional<Person> emptyPersonOptional = Optional.ofNullable(null);
		Mockito.when(this.personRepository.findByMailAndRole("name", PersonRole.APPLICANT))
				.thenReturn(emptyPersonOptional);
		// throw
		this.personEntityController.getPersonByMailAndType("name", PersonRole.APPLICANT);
	}

	/**
	 * @test get a {@link Person} by name of Consultant type
	 * @context {@link Person} not found in base
	 * @expected {@link ResourceNotFoundException}
	 * @author Lucas Royackkers
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void getConsultant_with_ResourceNotFound() {

		// setup
		final Optional<Person> emptyPersonOptional = Optional.ofNullable(null);
		Mockito.when(this.personRepository.findByMailAndRole("name", PersonRole.CONSULTANT))
				.thenReturn(emptyPersonOptional);
		// throw
		this.personEntityController.getPersonByMailAndType("name", PersonRole.CONSULTANT);
	}

	/**
	 * @test get a {@link Person} by name of Applicant type
	 * @context success
	 * @expected return instance of {@link Person}
	 * @author Lucas Royackkers
	 */
	@Test
	public void getApplicant_with_success() {

		// setup
		final Optional<Person> notEmptyPersonOptional = Optional.of(new Person());
		Mockito.when(this.personRepository.findByMailAndRole("name", PersonRole.APPLICANT))
				.thenReturn(notEmptyPersonOptional);
		// assert
		Assertions.assertThat(this.personEntityController.getPersonByMailAndType("name", PersonRole.APPLICANT))
				.isInstanceOf(Person.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).findByMailAndRole("name", PersonRole.APPLICANT);
	}
	
	/**
	 * @test get a {@link Person} (of any type) by its mail
	 * @context {@link Person} not found in the database
	 * @expected {@link ResourceNotFoundException}
	 * @author Lucas Royackkers
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void getPersonByMail_with_rnfe() {
		// setup
		final Optional<Person> emptyPersonOptional = Optional.ofNullable(null);
		Mockito.when(this.personRepository.findByMail("name"))
				.thenReturn(emptyPersonOptional);
		this.personEntityController.getPersonByMail("name");
	}
	
	/**
	 * @test get a {@link Person} (of any type) by its mail
	 * @context success
	 * @expected {@link ResourceNotFoundException}
	 * @author Lucas Royackkers
	 */
	@Test
	public void getPersonByMail_with_success() {
		// setup
		final Optional<Person> notEmptyPersonOptional = Optional.of(new Person());
		Mockito.when(this.personRepository.findByMail("name"))
				.thenReturn(notEmptyPersonOptional);
		// assert
		Assertions.assertThat(this.personEntityController.getPersonByMail("name"))
				.isInstanceOf(Person.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).findByMail("name");
	}
	
	/**
	 * @test get a List of {@link Person} (of any type) given their highest diploma
	 * @context the result can be empty or not, given if there are objects that match the given filter
	 * @expected return an instance of a List of Person objects
	 * @author Lucas Royackkers
	 */
	@Test
	public void getPersonsByHighestDiploma() {
		// assert
		Assertions.assertThat(this.personEntityController.getPersonsByHighestDiploma(ArgumentMatchers.anyString())).isInstanceOf(List.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).findByHighestDiploma("");
	}
	
	/**
	 * @test get a List of {@link Person} (of any type) given their job
	 * @context the result can be empty or not, given if there are objects that match the given filter
	 * @expected return an instance of a List of Person objects
	 * @author Lucas Royackkers
	 */
	@Test
	public void getPersonsByJob() {
		// assert
		Assertions.assertThat(this.personEntityController.getPersonsByJob(ArgumentMatchers.anyString())).isInstanceOf(List.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).findByJob("");
	}
	
	/**
	 * @test get a List of {@link Person} (of any type) given their name
	 * @context the result can be empty or not, given if there are objects that match the given filter
	 * @expected return an instance of a List of Person objects
	 * @author Lucas Royackkers
	 */
	@Test
	public void getPersonsByName() {
		// assert
		Assertions.assertThat(this.personEntityController.getPersonsByName(ArgumentMatchers.anyString())).isInstanceOf(List.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).findByName("");
	}
	
	/**
	 * @test get a List of all applicants-{@link Person}
	 * @context the result can be empty or not, if there are any Applicants in the database
	 * @expected return an instance of a List of Person (Applicant) objects
	 * @author Lucas Royackkers
	 */
	@Test
	public void getAllApplicants() {
		// assert
		Assertions.assertThat(this.personEntityController.getPersonsByRole(PersonRole.APPLICANT)).isInstanceOf(List.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).findAllByRole(PersonRole.APPLICANT);
	}
	
	/**
	 * @test get a List of all consultants-{@link Person} 
	 * @context the result can be empty or not, if there are any Consultants in the database
	 * @expected return an instance of a List of Person (Consultant) objects
	 * @author Lucas Royackkers
	 */
	@Test
	public void getAllConsultants() {
		// assert
		Assertions.assertThat(this.personEntityController.getPersonsByRole(PersonRole.CONSULTANT)).isInstanceOf(List.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).findAllByRole(PersonRole.CONSULTANT);
	}
	
	/**
	 * @test get a List of all {@link Person} given their surname
	 * @context the result can be empty or not, if there are any Person object that match the given filter
	 * @expected return an instance of a List of Person objects
	 * @author Lucas Royackkers
	 */
	@Test
	public void getPersonsBySurname() {
		// assert
		Assertions.assertThat(this.personEntityController.getPersonsBySurname(ArgumentMatchers.anyString())).isInstanceOf(List.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).findBySurname("");
	}
	
	@Test
	public void getAllPersons() {
		// assert
		Assertions.assertThat(this.personEntityController.getAllPersons()).isInstanceOf(List.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).findAll();
	}

	/**
	 * @test get a {@link Person} by name of Consultant type
	 * @context success
	 * @expected return instance of {@link Person}
	 * @author Lucas Royackkers
	 */
	@Test
	public void getConsultant_with_success() {

		// setup
		final Optional<Person> notEmptyPersonOptional = Optional.of(new Person());
		Mockito.when(this.personRepository.findByMailAndRole("name", PersonRole.CONSULTANT))
				.thenReturn(notEmptyPersonOptional);
		// assert
		Assertions.assertThat(this.personEntityController.getPersonByMailAndType("name", PersonRole.CONSULTANT))
				.isInstanceOf(Person.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).findByMailAndRole("name", PersonRole.CONSULTANT);
	}

	/**
	 * @test update a {@link Person} of Applicant type
	 * @context {@link Person} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Lucas Royackkers
	 * @throws ParseException
	 * @throws IOException
	 */
	@Test
	public void updateApplicant_with_conflict() throws ParseException, IOException {
		JsonNode jPerson = mapper.readTree(jsonStringTest);
		// setup other controllers & types
		Mockito.doReturn(PersonEntityControllerTest.spiedUser).when(this.userEntityController)
				.getUserByMail(ArgumentMatchers.anyString());

		Mockito.doReturn(spiedPerson).when(this.personEntityController).getPersonByMailAndType(ArgumentMatchers.anyString(),
				ArgumentMatchers.any(PersonRole.class));

		Mockito.when(this.diplomaEntityController.getDiplomaByNameAndYearOfResult(ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(spiedDiploma).thenThrow(new ResourceNotFoundException());
		Mockito.when(
				this.diplomaEntityController.createDiploma(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(spiedDiploma);

		Mockito.when(this.jobEntityController.getJob(ArgumentMatchers.anyString())).thenReturn(spiedJob)
				.thenThrow(new ResourceNotFoundException());
		Mockito.when(this.jobEntityController.createJob(ArgumentMatchers.anyString())).thenReturn(spiedJob);

		Mockito.when(this.employerEntityController.getEmployer(ArgumentMatchers.anyString())).thenReturn(spiedEmployer)
				.thenThrow(new ResourceNotFoundException());
		Mockito.when(this.employerEntityController.createEmployer(ArgumentMatchers.anyString()))
				.thenReturn(spiedEmployer);

		Mockito.doThrow(this.mockedDuplicateKeyException).when(this.personRepository)
				.save(ArgumentMatchers.any(Person.class));
		// assert
		Assertions.assertThat(this.personEntityController.updatePerson(jPerson, PersonRole.APPLICANT, ""))
				.isInstanceOf(ConflictException.class);
		Assertions.assertThat(this.personEntityController.updatePerson(jPerson, PersonRole.APPLICANT, ""))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(2)).save(ArgumentMatchers.any(Person.class));
	}
	
	/**
	 * @test update a {@link Person} of Consultant type
	 * @context {@link Person} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Lucas Royackkers
	 * @throws ParseException 
	 * @throws IOException 
	 */
	@Test
	public void updateConsultant_with_conflict() throws ParseException, IOException {
		JsonNode jPerson = mapper.readTree(jsonStringTest);
		// setup other controllers & types
		Mockito.doReturn(PersonEntityControllerTest.spiedUser).when(this.userEntityController)
				.getUserByMail(ArgumentMatchers.anyString());

		Mockito.doReturn(spiedPerson).when(this.personEntityController).getPersonByMailAndType(ArgumentMatchers.anyString(),
				ArgumentMatchers.any(PersonRole.class));

		Mockito.when(this.diplomaEntityController.getDiplomaByNameAndYearOfResult(ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(spiedDiploma).thenThrow(new ResourceNotFoundException());
		Mockito.when(
				this.diplomaEntityController.createDiploma(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(spiedDiploma);

		Mockito.when(this.jobEntityController.getJob(ArgumentMatchers.anyString())).thenReturn(spiedJob)
				.thenThrow(new ResourceNotFoundException());
		Mockito.when(this.jobEntityController.createJob(ArgumentMatchers.anyString())).thenReturn(spiedJob);

		Mockito.when(this.employerEntityController.getEmployer(ArgumentMatchers.anyString())).thenReturn(spiedEmployer)
				.thenThrow(new ResourceNotFoundException());
		Mockito.when(this.employerEntityController.createEmployer(ArgumentMatchers.anyString()))
				.thenReturn(spiedEmployer);

		Mockito.doThrow(this.mockedDuplicateKeyException).when(this.personRepository)
				.save(ArgumentMatchers.any(Person.class));
		// assert
		Assertions.assertThat(this.personEntityController.updatePerson(jPerson, PersonRole.CONSULTANT, ""))
				.isInstanceOf(ConflictException.class);
		Assertions.assertThat(this.personEntityController.updatePerson(jPerson, PersonRole.CONSULTANT, ""))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(2)).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test update a {@link Person} of Applicant type
	 * @context {@link Person} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Lucas Royackkers
	 * @throws ParseException 
	 */
	@Test
	public void updateApplicant_with_resourceNotFound() throws ParseException {

		// setup
		final Optional<Person> emptyPersonOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJPerson).when(this.mockedJPerson).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJPerson).textValue();
		Mockito.doReturn(emptyPersonOptional).when(this.personRepository).findByMailAndRole(ArgumentMatchers.anyString(),ArgumentMatchers.any(PersonRole.class));
		// assert
		Assertions.assertThat(this.personEntityController.updatePerson(this.mockedJPerson,PersonRole.APPLICANT, ""))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.never()).save(ArgumentMatchers.any(Person.class));
	}
	
	/**
	 * @test update a {@link Person} of Consultant type
	 * @context {@link Person} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Lucas Royackkers
	 * @throws ParseException 
	 */
	@Test
	public void updateConsultant_with_resourceNotFound() throws ParseException {

		// setup
		final Optional<Person> emptyPersonOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJPerson).when(this.mockedJPerson).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJPerson).textValue();
		Mockito.doReturn(emptyPersonOptional).when(this.personRepository).findByMailAndRole(ArgumentMatchers.anyString(),ArgumentMatchers.any(PersonRole.class));
		// assert
		Assertions.assertThat(this.personEntityController.updatePerson(this.mockedJPerson,PersonRole.APPLICANT, ""))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.never()).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test update a {@link Person} of Applicant type
	 * @context success
	 * @expected {@link OkException} save() called twice
	 * @author Lucas Royackkers
	 * @throws ParseException
	 * @throws IOException
	 */
	@Test
	public void updateApplicant_with_success() throws ParseException, IOException {
		JsonNode jPerson = mapper.readTree(jsonStringTest);
		// setup
		final Optional<Person> notEmptyPersonOptional = Optional.of(new Person());

		Mockito.doReturn(PersonEntityControllerTest.spiedUser).when(this.userEntityController)
				.getUserByMail(ArgumentMatchers.anyString());

		Mockito.when(this.diplomaEntityController.getDiplomaByNameAndYearOfResult(ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(spiedDiploma).thenThrow(new ResourceNotFoundException());
		Mockito.when(
				this.diplomaEntityController.createDiploma(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(spiedDiploma);

		Mockito.when(this.jobEntityController.getJob(ArgumentMatchers.anyString())).thenReturn(spiedJob)
				.thenThrow(new ResourceNotFoundException());
		Mockito.when(this.jobEntityController.createJob(ArgumentMatchers.anyString())).thenReturn(spiedJob);

		Mockito.when(this.employerEntityController.getEmployer(ArgumentMatchers.anyString())).thenReturn(spiedEmployer)
				.thenThrow(new ResourceNotFoundException());
		Mockito.when(this.employerEntityController.createEmployer(ArgumentMatchers.anyString()))
				.thenReturn(spiedEmployer);

		Mockito.doReturn(notEmptyPersonOptional).when(this.personRepository)
				.findByMailAndRole(ArgumentMatchers.anyString(), ArgumentMatchers.any(PersonRole.class));
		// assert
		Assertions.assertThat(this.personEntityController.updatePerson(jPerson, PersonRole.APPLICANT, ""))
				.isInstanceOf(OkException.class);
		Assertions.assertThat(this.personEntityController.updatePerson(jPerson, PersonRole.APPLICANT, ""))
				.isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(2)).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test update a {@link Person} of Consultant type
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * @author Lucas Royackkers
	 * @throws ParseException 
	 * @throws IOException 
	 */
	@Test
	public void updateConsultant_with_success() throws ParseException, IOException {
		JsonNode jPerson = mapper.readTree(jsonStringTest);
		// setup
		final Optional<Person> notEmptyPersonOptional = Optional.of(new Person());

		Mockito.doReturn(PersonEntityControllerTest.spiedUser).when(this.userEntityController)
				.getUserByMail(ArgumentMatchers.anyString());

		Mockito.when(this.diplomaEntityController.getDiplomaByNameAndYearOfResult(ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString())).thenReturn(spiedDiploma).thenThrow(new ResourceNotFoundException());
		Mockito.when(
				this.diplomaEntityController.createDiploma(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(spiedDiploma);

		Mockito.when(this.jobEntityController.getJob(ArgumentMatchers.anyString())).thenReturn(spiedJob)
				.thenThrow(new ResourceNotFoundException());
		Mockito.when(this.jobEntityController.createJob(ArgumentMatchers.anyString())).thenReturn(spiedJob);

		Mockito.when(this.employerEntityController.getEmployer(ArgumentMatchers.anyString())).thenReturn(spiedEmployer)
				.thenThrow(new ResourceNotFoundException());
		Mockito.when(this.employerEntityController.createEmployer(ArgumentMatchers.anyString()))
				.thenReturn(spiedEmployer);

		Mockito.doReturn(notEmptyPersonOptional).when(this.personRepository)
				.findByMailAndRole(ArgumentMatchers.anyString(), ArgumentMatchers.any(PersonRole.class));
		// assert
		Assertions.assertThat(this.personEntityController.updatePerson(jPerson, PersonRole.CONSULTANT, ""))
				.isInstanceOf(OkException.class);
		Assertions.assertThat(this.personEntityController.updatePerson(jPerson, PersonRole.CONSULTANT, ""))
				.isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(2)).save(ArgumentMatchers.any(Person.class));
	}
}
