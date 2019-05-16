package fr.alten.ambroiseJEE.controller.business;

import java.text.ParseException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
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

import fr.alten.ambroiseJEE.model.beans.Diploma;
import fr.alten.ambroiseJEE.model.entityControllers.DiplomaEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * 
 * @author Thomas Decamp
 */

@RunWith(MockitoJUnitRunner.class)
public class DiplomaBusinessControllerTest {

    @InjectMocks
    @Spy
    private final DiplomaBusinessController diplomaBusinessController = new DiplomaBusinessController();

    @Mock
    private DiplomaEntityController diplomaEntityController;
    @Mock
    private HttpException mockedHttpException;
    @Mock
    private JsonNode mockedJDiploma;
    @Mock
    private List<Diploma> mockedDiplomaList;

    @MockBean
    private Diploma mockedDiploma;

    /**
     * @test create a {@link Diploma}
     * @content as admin user
     * @expected same {@link HttpException} return by the
     *           {@link DiplomaEntityController}
     * 
     * @author Thomas Decamp
     * @throws ParseException 
     */
    @Test
    public void createDiploma_as_adminUser() throws ParseException {

        // setup
        Mockito.doReturn(true).when(this.diplomaBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
        Mockito.when(this.diplomaEntityController.createDiploma(this.mockedJDiploma)).thenReturn(this.mockedHttpException);
        // assert
        Assertions.assertThat(this.diplomaBusinessController.createDiploma(this.mockedJDiploma, UserRole.DEACTIVATED))
                .isEqualTo(this.mockedHttpException);
    }

    /**
	 * @test create a {@link Diploma}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link DiplomaEntityController}
     * 
	 * @author Thomas Decamp
     * @throws ParseException 
	 */
    @Test
    public void createDiploma_as_nonAdminUser() throws ParseException {

        // assert
        Mockito.doReturn(false).when(this.diplomaBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
        Assertions.assertThat(this.diplomaBusinessController.createDiploma(this.mockedJDiploma, UserRole.MANAGER_ADMIN))
        		.isInstanceOf(ForbiddenException.class);
    }

   	/**
	 * @test delete a {@link Diploma}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link DiplomaEntityController}
     * 
	 * @author Thomas Decamp
	 */
    @Test
    public void deleteDiploma_as_adminUser() {

        // setup
		Mockito.doReturn(true).when(this.diplomaBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.diplomaEntityController.deleteDiploma(this.mockedJDiploma)).thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(this.diplomaBusinessController.deleteDiploma(this.mockedJDiploma, UserRole.MANAGER_ADMIN))
				.isEqualTo(this.mockedHttpException);
    }

    /**
	 * @test delete a {@link Diploma}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link DiplomaEntityController}
     * 
	 * @author Thomas Decamp
	 */
    @Test
    public void deleteDiploma_as_nonAdminUser() {

        // setup
		Mockito.doReturn(false).when(this.diplomaBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions.assertThat(this.diplomaBusinessController.deleteDiploma(this.mockedJDiploma, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);
    }

    /**
	 * @test get all {@link Diploma}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link DiplomaEntityController}
     * 
	 * @author Thomas Decamp
	 */
    @Test
    public void getDiplomas_as_adminUserOrCDROrManager() {

        // setup
        Mockito.doReturn(true).when(this.diplomaBusinessController).isManagerOrCdrOrAdmin(ArgumentMatchers.any(UserRole.class));
        Mockito.when(this.diplomaEntityController.getDiplomas()).thenReturn(this.mockedDiplomaList);
        // assert
        Assertions.assertThat(this.diplomaBusinessController.getDiplomas(UserRole.DEACTIVATED))
                .isEqualTo(this.mockedDiplomaList);
    }

	/**
	 * @test get all {@link Diploma}
	 * @context as non admin user
	 * @expected throw a {@link ForbiddenException}
     * 
	 * @author Thomas Decamp
	 */
	@Test(expected = ForbiddenException.class)
	public void getDiplomas_as_nonAdminUser() {

        // setup
        Mockito.doReturn(false).when(this.diplomaBusinessController).isManagerOrCdrOrAdmin(ArgumentMatchers.any(UserRole.class));
        // throw
        this.diplomaBusinessController.getDiplomas(UserRole.MANAGER_ADMIN);
    }

    /**
	 * @test update a {@link Diploma}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link DiplomaEntityController}
     * 
	 * @author Thomas Decamp
     * @throws ParseException 
	 */
    @Test
    public void updateDiploma_as_adminUser() throws ParseException {

        // setup
        Mockito.doReturn(true).when(this.diplomaBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
        Mockito.when(this.diplomaEntityController.updateDiploma(this.mockedJDiploma)).thenReturn(this.mockedHttpException);
        // assert
        Assertions.assertThat(this.diplomaBusinessController.updateDiploma(this.mockedJDiploma, UserRole.DEACTIVATED))
                .isEqualTo(this.mockedHttpException);
    }

    /**
	 * @test update a {@link Diploma}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link DiplomaEntityController}
	 * @author Kylian Gehier
     * @throws ParseException 
	 */
	@Test
	public void updateDiploma_as_nonAdminUser() throws ParseException {

		// setup
		Mockito.doReturn(false).when(this.diplomaBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions.assertThat(this.diplomaBusinessController.updateDiploma(this.mockedJDiploma, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}
}
