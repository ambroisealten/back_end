package fr.alten.ambroiseJEE.controller.business;

import java.util.List;

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

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Job;
import fr.alten.ambroiseJEE.model.entityControllers.JobEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * Test class for JobBusinessController
 * 
 * @author Camille Schnell
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class JobBusinessControllerTest {

    @InjectMocks
  	@Spy
  	private final JobBusinessController jobBusinessController = new JobBusinessController();

  	@Mock
  	private JobEntityController jobEntityController;
  	@Mock
  	private HttpException mockedHttpException;
  	@Mock
  	private JsonNode mockedJJob;
  	@Mock
  	private List<Job> mockedJobList;

  	@MockBean
  	private Job mockedJob;

  	/**
  	 * @test create a {@link Job}
  	 * @context as admin user
  	 * @expected same {@link HttpException} returned by the
  	 *           {@link JobEntityController}
  	 * @author Camille Schnell
  	 */
  	@Test
  	public void createJob_as_adminUser() {

  		// setup
  		Mockito.doReturn(true).when(this.jobBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
  		Mockito.when(this.jobEntityController.createJob(this.mockedJJob)).thenReturn(this.mockedHttpException);
  		// assert
  		Assertions.assertThat(this.jobBusinessController.createJob(this.mockedJJob, UserRole.DEACTIVATED))
  				.isEqualTo(this.mockedHttpException);
  	}

  	/**
  	 * @test create a {@link Job}
  	 * @context as non admin user
  	 * @expected same {@link HttpException} returned by the
  	 *           {@link JobEntityController}
  	 * @author Camille Schnell
  	 */
  	@Test
  	public void createJob_as_nonAdminUser() {

  		// assert
  		Mockito.doReturn(false).when(this.jobBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
  		Assertions.assertThat(this.jobBusinessController.createJob(this.mockedJJob, UserRole.MANAGER_ADMIN))
  				.isInstanceOf(ForbiddenException.class);

  	}

  	/**
  	 * @test delete a {@link Job}
  	 * @context as admin user
  	 * @expected same {@link HttpException} returned by the
  	 *           {@link JobEntityController}
  	 * @author Camille Schnell
  	 */
  	@Test
  	public void deleteJob_as_adminUser() {

  		// setup
  		Mockito.doReturn(true).when(this.jobBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
  		Mockito.when(this.jobEntityController.deleteJob(this.mockedJJob)).thenReturn(this.mockedHttpException);
  		// assert
  		Assertions.assertThat(this.jobBusinessController.deleteJob(this.mockedJJob, UserRole.MANAGER_ADMIN))
  				.isEqualTo(this.mockedHttpException);
  	}

  	/**
  	 * @test delete a {@link Job}
  	 * @context as non admin user
  	 * @expected same {@link HttpException} returned by the
  	 *           {@link JobEntityController}
  	 * @author Camille Schnell
  	 */
  	@Test
  	public void deleteJob_as_nonAdminUser() {

  		// setup
  		Mockito.doReturn(false).when(this.jobBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
  		// assert
  		Assertions.assertThat(this.jobBusinessController.deleteJob(this.mockedJJob, UserRole.MANAGER_ADMIN))
  				.isInstanceOf(ForbiddenException.class);

  	}

  	/**
  	 * @test get all {@link Job}
  	 * @context as a non-Consultant or Deactivated
  	 * @expected same {@link HttpException} returned by the
  	 *           {@link JobEntityController}
  	 * @author Camille Schnell
  	 */
  	@Test
  	public void getJobs_as_notConsultantOrDeactivated() {

  		// setup
  		Mockito.doReturn(true).when(this.jobBusinessController).isNotConsultantOrDeactivated(ArgumentMatchers.any(UserRole.class));
  		Mockito.when(this.jobEntityController.getJobs()).thenReturn(this.mockedJobList);
  		// assert
  		Assertions.assertThat(this.jobBusinessController.getJobs(UserRole.DEACTIVATED))
  				.isEqualTo(this.mockedJobList);
  	}

  	/**
  	 * @test get all {@link Job}
  	 * @context as a non-Consultant or Deactivated
  	 * @expected throw a {@link ForbiddenException}
  	 * @author Camille Schnell
  	 */
  	@Test(expected = ForbiddenException.class)
  	public void getJobs_as_nonAdminUser() {

  		// setup
  		Mockito.doReturn(false).when(this.jobBusinessController).isNotConsultantOrDeactivated(ArgumentMatchers.any(UserRole.class));
  		// throw
  		this.jobBusinessController.getJobs(UserRole.MANAGER_ADMIN);

  	}


  	/**
  	 * @test update a {@link Job}
  	 * @context as admin user
  	 * @expected same {@link HttpException} returned by the
  	 *           {@link JobEntityController}
  	 * @author Camille Schnell
  	 */
  	@Test
  	public void updateJob_as_adminUser() {

  		// setup
  		Mockito.doReturn(true).when(this.jobBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
  		Mockito.when(this.jobEntityController.updateJob(this.mockedJJob)).thenReturn(this.mockedHttpException);
  		// assert
  		Assertions.assertThat(this.jobBusinessController.updateJob(this.mockedJJob, UserRole.DEACTIVATED))
  				.isEqualTo(this.mockedHttpException);
  	}

  	/**
  	 * @test update a {@link Job}
  	 * @context as non admin user
  	 * @expected same {@link HttpException} returned by the
  	 *           {@link JobEntityController}
  	 * @author Camille Schnell
  	 */
  	@Test
  	public void updateJob_as_nonAdminUser() {

  		// setup
  		Mockito.doReturn(false).when(this.jobBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
  		// assert
  		Assertions.assertThat(this.jobBusinessController.updateJob(this.mockedJJob, UserRole.MANAGER_ADMIN))
  				.isInstanceOf(ForbiddenException.class);

  	}
}
