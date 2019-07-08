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

import fr.alten.ambroiseJEE.model.beans.Job;
import fr.alten.ambroiseJEE.model.dao.JobRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * Test class for JobEntityController
 *
 * @author Camille Schnell
 *
 */
 @RunWith(MockitoJUnitRunner.class)
public class JobEntityControllerTest {

    @InjectMocks
  	@Spy
  	private final JobEntityController jobEntityController = new JobEntityController();

  	@Mock
  	private JobRepository jobRepository;
  	@Mock
  	private CreatedException mockedCreatedException;
  	@Mock
  	private ConflictException mockedConflictException;
  	@Mock
  	private DuplicateKeyException mockedDuplicateKeyException;
  	@Mock
  	private JsonNode mockedJJob;
  	@MockBean
  	private Job mockedJob;

  	/**
  	 * @test create a {@link Job}
  	 * @context {@link Job} already existing in base
  	 * @expected {@link ConflictException} save() call only once
  	 * @author Camille Schnell
  	 */
  	@Test
  	public void createJob_with_conflict() {
  		//setup
  		Mockito.doReturn(this.mockedJJob).when(this.mockedJJob).get(ArgumentMatchers.anyString());
  		Mockito.doThrow(this.mockedDuplicateKeyException).when(this.jobRepository)
  				.save(ArgumentMatchers.any(Job.class));
  		// assert
  		Assertions.assertThat(this.jobEntityController.createJob(this.mockedJJob))
  				.isInstanceOf(ConflictException.class);
  		// verify
  		Mockito.verify(this.jobRepository, Mockito.times(1)).save(ArgumentMatchers.any(Job.class));
  	}

  	/**
  	 * @test create a {@link Job}
  	 * @context sucess
  	 * @expected {@link CreatedException} save() call only once
  	 * @author Camille Schnell
  	 */
  	@Test
  	public void createJob_with_success() {

  		// setup
  		Mockito.doReturn(this.mockedJJob).when(this.mockedJJob).get(ArgumentMatchers.anyString());
  		Mockito.doReturn(this.mockedJob).when(this.jobRepository).save(ArgumentMatchers.any(Job.class));
  		// assert
  		Assertions.assertThat(this.jobEntityController.createJob(this.mockedJJob))
  				.isInstanceOf(CreatedException.class);
  		// verify
  		Mockito.verify(this.jobRepository, Mockito.times(1)).save(ArgumentMatchers.any(Job.class));
  	}

  	/**
  	 * @test delete a {@link Job}
  	 * @context {@link Job} already existing in base
  	 * @expected {@link ConflictException} save() call only once
  	 * @author Camille Schnell
  	 */
  	@Test
  	public void deleteJob_with_Conflict() {

  		// setup
  		final Optional<Job> notEmptyJobOptional = Optional.of(new Job());
  		Mockito.doReturn(this.mockedJJob).when(this.mockedJJob).get(ArgumentMatchers.anyString());
  		Mockito.doReturn("anyString").when(this.mockedJJob).textValue();
  		Mockito.doReturn(notEmptyJobOptional).when(this.jobRepository).findByTitle(ArgumentMatchers.anyString());
  		Mockito.when(this.jobRepository.save(ArgumentMatchers.any(Job.class)))
  				.thenThrow(this.mockedDuplicateKeyException);
  		// assert
  		Assertions.assertThat(this.jobEntityController.deleteJob(this.mockedJJob))
  				.isInstanceOf(ConflictException.class);
  		// verify
  		Mockito.verify(this.jobRepository, Mockito.times(1)).save(ArgumentMatchers.any(Job.class));
  	}

  	/**
  	 * @test delete a {@link Job}
  	 * @context {@link Job} not found in base
  	 * @expected {@link ResourceNotFoundException} save() never called
  	 * @author Camille Schnell
  	 */
  	@Test
  	public void deleteJob_with_resourceNotFound() {

  		// setup
  		final Optional<Job> emptyJobOptional = Optional.ofNullable(null);
  		Mockito.doReturn(this.mockedJJob).when(this.mockedJJob).get(ArgumentMatchers.anyString());
  		Mockito.doReturn("anyString").when(this.mockedJJob).textValue();
  		Mockito.doReturn(emptyJobOptional).when(this.jobRepository).findByTitle(ArgumentMatchers.anyString());
  		// assert
  		Assertions.assertThat(this.jobEntityController.deleteJob(this.mockedJJob))
  				.isInstanceOf(ResourceNotFoundException.class);
  		// verify
  		Mockito.verify(this.jobRepository, Mockito.never()).save(ArgumentMatchers.any(Job.class));
  	}

  	/**
  	 * @test delete a {@link Job}
  	 * @context success
  	 * @expected {@link OkException} save() call only once
  	 * @author Camille Schnell
  	 */
  	@Test
  	public void deleteJob_with_success() {

  		// setup
  		final Optional<Job> notEmptyJobOptional = Optional.of(new Job());
  		Mockito.doReturn(this.mockedJJob).when(this.mockedJJob).get(ArgumentMatchers.anyString());
  		Mockito.doReturn("anyString").when(this.mockedJJob).textValue();
  		Mockito.doReturn(notEmptyJobOptional).when(this.jobRepository).findByTitle(ArgumentMatchers.anyString());
  		// assert
  		Assertions.assertThat(this.jobEntityController.deleteJob(this.mockedJJob)).isInstanceOf(OkException.class);
  		// verify
  		Mockito.verify(this.jobRepository, Mockito.times(1)).save(ArgumentMatchers.any(Job.class));
  	}

  	/**
  	 * @test get all {@link Job}
  	 * @expected return instance of {@link List} save() call only once
  	 * @author Camille Schnell
  	 */
  	@Test
  	public void getJobs() {

  		// assert
  		Assertions.assertThat(this.jobEntityController.getJobs()).isInstanceOf(List.class);
  		// verify
  		Mockito.verify(this.jobRepository, Mockito.times(1)).findAll();
  	}

  	/**
  	 * @test get a {@link Job} by title
  	 * @context {@link Job} not found in base
  	 * @expected {@link ResourceNotFoundException}
  	 * @author Camille Schnell
  	 */
  	@Test(expected = ResourceNotFoundException.class)
  	public void getJob_with_ResourceNotFound() {

  		// setup
  		final Optional<Job> emptyJobOptional = Optional.ofNullable(null);
  		Mockito.when(this.jobRepository.findByTitle("title")).thenReturn(emptyJobOptional);
  		// throw
  		this.jobEntityController.getJob("title");
  	}

  	/**
  	 * @test get a {@link Job} by title
  	 * @context success
  	 * @expected return instance of {@link Job}
  	 * @author Camille Schnell
  	 */
  	@Test
  	public void getJob_with_success() {

  		// setup
  		final Optional<Job> notEmptyJobOptional = Optional.of(new Job());
  		//Mockito.when(this.jobRepository.findByTitle("title")).thenReturn(notEmptyJobOptional);
  		Mockito.doReturn(notEmptyJobOptional).when(this.jobRepository).findByTitle("title");
  		// assert
  		Assertions.assertThat(this.jobEntityController.getJob("title")).isInstanceOf(Job.class);
  		// verify
  		Mockito.verify(this.jobRepository, Mockito.times(1)).findByTitle("title");
  	}

  	/**
  	 * @test update a {@link Job}
  	 * @context {@link Job} already existing in base
  	 * @expected {@link ConflictException} save() call only once
  	 * @author Camille Schnell
  	 */
  	@Test
  	public void updateJob_with_Conflict() {

  		// setup
  		final Optional<Job> notEmptyJobOptional = Optional.of(new Job());
  		Mockito.doReturn(this.mockedJJob).when(this.mockedJJob).get(ArgumentMatchers.anyString());
  		Mockito.doReturn("anyString").when(this.mockedJJob).textValue();
  		Mockito.doReturn(notEmptyJobOptional).when(this.jobRepository).findByTitle(ArgumentMatchers.anyString());
  		Mockito.when(this.jobRepository.save(ArgumentMatchers.any(Job.class)))
  				.thenThrow(this.mockedDuplicateKeyException);
  		// assert
  		Assertions.assertThat(this.jobEntityController.updateJob(this.mockedJJob))
  				.isInstanceOf(ConflictException.class);
  		// verify
  		Mockito.verify(this.jobRepository, Mockito.times(1)).save(ArgumentMatchers.any(Job.class));
  	}

  	/**
  	 * @test update a {@link Job}
  	 * @context {@link Job} not found in base
  	 * @expected {@link ResourceNotFoundException} save() never called
  	 * @author Camille Schnell
  	 */
  	@Test
  	public void updateJob_with_resourceNotFound() {

  		// setup
  		final Optional<Job> emptyJobOptional = Optional.ofNullable(null);
  		Mockito.doReturn(this.mockedJJob).when(this.mockedJJob).get(ArgumentMatchers.anyString());
  		Mockito.doReturn("anyString").when(this.mockedJJob).textValue();
  		Mockito.doReturn(emptyJobOptional).when(this.jobRepository).findByTitle(ArgumentMatchers.anyString());
  		// assert
  		Assertions.assertThat(this.jobEntityController.updateJob(this.mockedJJob))
  				.isInstanceOf(ResourceNotFoundException.class);
  		// verify
  		Mockito.verify(this.jobRepository, Mockito.never()).save(ArgumentMatchers.any(Job.class));
  	}

  	/**
  	 * @test update a {@link Job}
  	 * @context success
  	 * @expected {@link OkException} save() call only once
  	 * @author Camille Schnell
  	 */
  	@Test
  	public void updateJob_with_success() {

  		// setup
  		final Optional<Job> notEmptyJobOptional = Optional.of(new Job());
  		Mockito.doReturn(this.mockedJJob).when(this.mockedJJob).get(ArgumentMatchers.anyString());
  		Mockito.doReturn("anyString").when(this.mockedJJob).textValue();
  		Mockito.doReturn(notEmptyJobOptional).when(this.jobRepository).findByTitle(ArgumentMatchers.anyString());
  		// assert
  		Assertions.assertThat(this.jobEntityController.updateJob(this.mockedJJob)).isInstanceOf(OkException.class);
  		// verify
  		Mockito.verify(this.jobRepository, Mockito.times(1)).save(ArgumentMatchers.any(Job.class));
  	}
}
