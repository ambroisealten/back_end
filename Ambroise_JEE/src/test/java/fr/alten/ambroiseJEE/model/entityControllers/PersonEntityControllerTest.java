package fr.alten.ambroiseJEE.model.entityControllers;

import java.text.ParseException;
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

import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.dao.PersonRepository;
import fr.alten.ambroiseJEE.utils.PersonRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

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
	private PersonRepository personRepository;
	@Mock
	private CreatedException mockedCreatedException;
	@Mock
	private ConflictException mockedConflictException;
	@Mock
	private DuplicateKeyException mockedDuplicateKeyException;
	@Mock
	private JsonNode mockedJPerson;
	@MockBean
	private Person mockedPerson;

	/**
	 * @test create a {@link Person} of Applicant type
	 * @context {@link Person} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Lucas Royackkers
	 * @throws ParseException 
	 */
	@Test
	public void createApplicant_with_conflict() throws ParseException {

		// setup
		Mockito.doReturn(this.mockedJPerson).when(this.mockedJPerson).get(ArgumentMatchers.anyString());
		Mockito.doThrow(this.mockedDuplicateKeyException).when(this.personRepository)
				.save(ArgumentMatchers.any(Person.class));
		// assert
		Assertions.assertThat(this.personEntityController.createPerson(this.mockedJPerson,PersonRole.APPLICANT))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).save(ArgumentMatchers.any(Person.class));
	}
	
	/**
	 * @test create a {@link Person} of Consultant type
	 * @context {@link Person} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Lucas Royackkers
	 * @throws ParseException 
	 */
	@Test
	public void createConsultant_with_conflict() throws ParseException {

		// setup
		Mockito.doReturn(this.mockedJPerson).when(this.mockedJPerson).get(ArgumentMatchers.anyString());
		Mockito.doThrow(this.mockedDuplicateKeyException).when(this.personRepository)
				.save(ArgumentMatchers.any(Person.class));
		// assert
		Assertions.assertThat(this.personEntityController.createPerson(this.mockedJPerson,PersonRole.CONSULTANT))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test create a {@link Person} of Applicant type
	 * @context sucess
	 * @expected {@link CreatedException} save() call only once
	 * @author Lucas Royackkers
	 * @throws ParseException 
	 */
	@Test
	public void createApplicant_with_success() throws ParseException {

		// setup
		Mockito.doReturn(this.mockedJPerson).when(this.mockedJPerson).get(ArgumentMatchers.anyString());
		Mockito.doReturn(this.mockedPerson).when(this.personRepository).save(ArgumentMatchers.any(Person.class));
		// assert
		Assertions.assertThat(this.personEntityController.createPerson(this.mockedJPerson,PersonRole.APPLICANT))
				.isInstanceOf(CreatedException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).save(ArgumentMatchers.any(Person.class));
	}
	
	/**
	 * @test create a {@link Person} of Consultant type
	 * @context sucess
	 * @expected {@link CreatedException} save() call only once
	 * @author Lucas Royackkers
	 * @throws ParseException 
	 */
	@Test
	public void createConsultant_with_success() throws ParseException {

		// setup
		Mockito.doReturn(this.mockedJPerson).when(this.mockedJPerson).get(ArgumentMatchers.anyString());
		Mockito.doReturn(this.mockedPerson).when(this.personRepository).save(ArgumentMatchers.any(Person.class));
		// assert
		Assertions.assertThat(this.personEntityController.createPerson(this.mockedJPerson,PersonRole.CONSULTANT))
				.isInstanceOf(CreatedException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test delete a {@link Person} of Applicant type
	 * @context {@link Person} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteApplicant_with_Conflict() {

		// setup
		final Optional<Person> notEmptyPersonOptional = Optional.of(new Person());
		Mockito.doReturn(this.mockedJPerson).when(this.mockedJPerson).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJPerson).textValue();
		Mockito.doReturn(notEmptyPersonOptional).when(this.personRepository).findByMail(ArgumentMatchers.anyString());
		Mockito.when(this.personRepository.save(ArgumentMatchers.any(Person.class)))
				.thenThrow(this.mockedDuplicateKeyException);
		// assert
		Assertions.assertThat(this.personEntityController.deletePerson(this.mockedJPerson,PersonRole.APPLICANT))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).save(ArgumentMatchers.any(Person.class));
	}
	
	/**
	 * @test delete a {@link Person} of Consultant type
	 * @context {@link Person} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteConsultant_with_Conflict() {

		// setup
		final Optional<Person> notEmptyPersonOptional = Optional.of(new Person());
		Mockito.doReturn(this.mockedJPerson).when(this.mockedJPerson).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJPerson).textValue();
		Mockito.doReturn(notEmptyPersonOptional).when(this.personRepository).findByMail(ArgumentMatchers.anyString());
		Mockito.when(this.personRepository.save(ArgumentMatchers.any(Person.class)))
				.thenThrow(this.mockedDuplicateKeyException);
		// assert
		Assertions.assertThat(this.personEntityController.deletePerson(this.mockedJPerson,PersonRole.CONSULTANT))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test delete a {@link Person} of type Applicant
	 * @context {@link Person} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteApplicant_with_resourceNotFound() {

		// setup
		final Optional<Person> emptyPersonOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJPerson).when(this.mockedJPerson).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJPerson).textValue();
		Mockito.doReturn(emptyPersonOptional).when(this.personRepository).findByMail(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.personEntityController.deletePerson(this.mockedJPerson,PersonRole.APPLICANT))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.never()).save(ArgumentMatchers.any(Person.class));
	}
	
	/**
	 * @test delete a {@link Person} of Consultant type
	 * @context {@link Person} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteConsultant_with_resourceNotFound() {

		// setup
		final Optional<Person> emptyPersonOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJPerson).when(this.mockedJPerson).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJPerson).textValue();
		Mockito.doReturn(emptyPersonOptional).when(this.personRepository).findByMail(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.personEntityController.deletePerson(this.mockedJPerson,PersonRole.CONSULTANT))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.never()).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test delete a {@link Person} of Applicant type
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteApplicant_with_success() {

		// setup
		final Optional<Person> notEmptyPersonOptional = Optional.of(new Person());
		Mockito.doReturn(this.mockedJPerson).when(this.mockedJPerson).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJPerson).textValue();
		Mockito.doReturn(notEmptyPersonOptional).when(this.personRepository).findByMail(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.personEntityController.deletePerson(this.mockedJPerson,PersonRole.APPLICANT)).isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).save(ArgumentMatchers.any(Person.class));
	}
	
	/**
	 * @test delete a {@link Person} of Consultant type
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * @author Lucas Royackkers
	 */
	@Test
	public void deleteConsultant_with_success() {

		// setup
		final Optional<Person> notEmptyPersonOptional = Optional.of(new Person());
		Mockito.doReturn(this.mockedJPerson).when(this.mockedJPerson).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJPerson).textValue();
		Mockito.doReturn(notEmptyPersonOptional).when(this.personRepository).findByMail(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.personEntityController.deletePerson(this.mockedJPerson,PersonRole.CONSULTANT)).isInstanceOf(OkException.class);
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
		Assertions.assertThat(this.personEntityController.getPersonsByRole(PersonRole.APPLICANT)).isInstanceOf(List.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).findAll();
	}
	
	/**
	 * @test get all {@link Person} of Consultant type
	 * @expected return instance of {@link List} save() call only once
	 * @author Lucas Royackkers
	 */
	@Test
	public void getConsultants() {

		// assert
		Assertions.assertThat(this.personEntityController.getPersonsByRole(PersonRole.CONSULTANT)).isInstanceOf(List.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).findAll();
	}

	/**
	 * @test get a {@link Person} by name
	 * @context {@link Person} not found in base
	 * @expected {@link ResourceNotFoundException}
	 * @author Lucas Royackkers
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void getPerson_with_ResourceNotFound() {

		// setup
		final Optional<Person> emptyPersonOptional = Optional.ofNullable(null);
		Mockito.when(this.personRepository.findByMail("name")).thenReturn(emptyPersonOptional);
		// throw
		this.personEntityController.getPerson("name");
	}

	/**
	 * @test get a {@link Person} by name
	 * @context success
	 * @expected return instance of {@link Person}
	 * @author Lucas Royackkers
	 */
	@Test
	public void getPerson_with_success() {

		// setup
		final Optional<Person> notEmptyPersonOptional = Optional.of(new Person());
		Mockito.when(this.personRepository.findByNom("name")).thenReturn(notEmptyPersonOptional);
		// assert
		Assertions.assertThat(this.personEntityController.getPerson("name")).isInstanceOf(Person.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).findByNom("name");
	}

	/**
	 * @test update a {@link Person}
	 * @context {@link Person} already existing in base
	 * @expected {@link ConflictException} save() call only once
	 * @author Lucas Royackkers
	 */
	@Test
	public void updatePerson_with_Conflict() {

		// setup
		final Optional<Person> notEmptyPersonOptional = Optional.of(new Person());
		Mockito.doReturn(this.mockedJPerson).when(this.mockedJPerson).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJPerson).textValue();
		Mockito.doReturn(notEmptyPersonOptional).when(this.personRepository).findByCode(ArgumentMatchers.anyString());
		Mockito.when(this.personRepository.save(ArgumentMatchers.any(Person.class)))
				.thenThrow(this.mockedDuplicateKeyException);
		// assert
		Assertions.assertThat(this.personEntityController.updatePerson(this.mockedJPerson))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test update a {@link Person}
	 * @context {@link Person} not found in base
	 * @expected {@link ResourceNotFoundException} save() never called
	 * @author Lucas Royackkers
	 */
	@Test
	public void updatePerson_with_resourceNotFound() {

		// setup
		final Optional<Person> emptyPersonOptional = Optional.ofNullable(null);
		Mockito.doReturn(this.mockedJPerson).when(this.mockedJPerson).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJPerson).textValue();
		Mockito.doReturn(emptyPersonOptional).when(this.personRepository).findByCode(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.personEntityController.updatePerson(this.mockedJPerson))
				.isInstanceOf(ResourceNotFoundException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.never()).save(ArgumentMatchers.any(Person.class));
	}

	/**
	 * @test update a {@link Person}
	 * @context success
	 * @expected {@link OkException} save() call only once
	 * @author Lucas Royackkers
	 */
	@Test
	public void updatePerson_with_success() {

		// setup
		final Optional<Person> notEmptyPersonOptional = Optional.of(new Person());
		Mockito.doReturn(this.mockedJPerson).when(this.mockedJPerson).get(ArgumentMatchers.anyString());
		Mockito.doReturn("anyString").when(this.mockedJPerson).textValue();
		Mockito.doReturn(notEmptyPersonOptional).when(this.personRepository).findByCode(ArgumentMatchers.anyString());
		// assert
		Assertions.assertThat(this.personEntityController.updatePerson(this.mockedJPerson)).isInstanceOf(OkException.class);
		// verify
		Mockito.verify(this.personRepository, Mockito.times(1)).save(ArgumentMatchers.any(Person.class));
	}
}
