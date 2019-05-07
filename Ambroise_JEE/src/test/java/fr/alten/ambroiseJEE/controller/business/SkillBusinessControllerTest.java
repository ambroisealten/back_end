package fr.alten.ambroiseJEE.controller.business;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.restassured.specification.Argument;

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

import fr.alten.ambroiseJEE.model.beans.Skill;
import fr.alten.ambroiseJEE.model.entityControllers.SkillEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * 
 * @author Thomas Decamp
 */

@RunWith(MockitoJUnitRunner.class)
public class SkillBusinessControllerTest {

    @InjectMocks
    @Spy
    private final SkillBusinessController skillBusinessController = new SkillBusinessController();

    @Mock
    private SkillEntityController skillEntityController;
    @Mock
    private HttpException mockedHttpException;
    @Mock
    private JsonNode mockedJSkill;
    @Mock
    private List<Skill> mockedSkillList;

    @MockBean
    private Skill mockedSkill;

    /**
     * @test create a {@link Skill}
     * @content as admin user
     * @expected same {@link HttpException} return by the
     *           {@link SkillEntityController}
     * 
     * @author Thomas Decamp
     */
    @Test
    public void createSkill_as_adminUser() {

        // setup
        Mockito.doReturn(true).when(this.skillBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
        Mockito.when(this.skillEntityController.createSkill(this.mockedJSkill)).thenReturn(this.mockedHttpException);
        // assert
        Assertions.assertThat(this.skillBusinessController.createSkill(this.mockedJSkill, UserRole.DEACTIVATED))
                .isEqualTo(this.mockedHttpException);
    }

    /**
	 * @test create a {@link Skill}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link SkillEntityController}
     * 
	 * @author Thomas Decamp
	 */
    @Test
    public void createSkill_as_nonAdminUser() {

        // assert
        Mockito.doReturn(false).when(this.skillBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
        Assertions.assertThat(this.skillBusinessController.createSkill(this.mockedJSkill, UserRole.MANAGER_ADMIN))
        .isInstanceOf(ForbiddenException.class);
    }

   	/**
	 * @test delete a {@link Skill}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link SkillEntityController}
     * 
	 * @author Thomas Decamp
	 */
    @Test
    public void deleteSkill_as_adminUser() {

        // setup
		Mockito.doReturn(true).when(this.skillBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.skillEntityController.deleteSkill(this.mockedJSkill)).thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(this.skillBusinessController.deleteSkill(this.mockedJSkill, UserRole.MANAGER))
				.isEqualTo(this.mockedHttpException);
    }

    /**
	 * @test delete a {@link Skill}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link SkillEntityController}
     * 
	 * @author Thomas Decamp
	 */
    @Test
    public void deleteSkill_as_nonAdminUser() {

        // setup
		Mockito.doReturn(false).when(this.skillBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions.assertThat(this.skillBusinessController.deleteSkill(this.mockedJSkill, UserRole.MANAGER_ADMIN))
				.isEqualTo(this.mockedHttpException);
    }

    /**
	 * @test get all {@link Skill}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link SkillEntityController}
     * 
	 * @author Thomas Decamp
	 */
    @Test
    public void getSkills_as_adminUser() {

        // setup
        Mockito.doReturn(true).when(this.skillBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
        Mockito.when(this.skillEntityController.getSkills()).thenReturn(this.mockedSkillList);
        // assert
        Assertions.assertThat(this.skillBusinessController.getSkills(UserRole.DEACTIVATED))
                .isEqualTo(this.mockedSkillList);
    }

	/**
	 * @test get all {@link Skill}
	 * @context as non admin user
	 * @expected throw a {@link ForbiddenException}
     * 
	 * @author Thomas Decamp
	 */
	@Test(expected = ForbiddenException.class)
	public void getSkills_as_nonAdminUser() {

        // setup
        Mockito.doReturn(false).when(this.skillBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
        // throw
        this.skillBusinessController.getSkills(UserRole.MANAGER_ADMIN);
    }

    /**
	 * @test create a {@link Skill}
	 * @expected same {@link Optional} returned by the {@link SkillEntityController}
     * 
	 * @author Thomas Decamp
	 */
    @Test
    public void getSkill() {
        // setup
        Mockito.when(this.skillEntityController.getSkill("name")).thenReturn(this.mockedSkill);
        // assert
        Assertions.assertThat(this.skillBusinessController.getSkill(this.mockedJSkill, UserRole.MANAGER_ADMIN))).isEqualTo(this.mockedSkill);
    }

    /**
	 * @test update a {@link Skill}
	 * @context as admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link SkillEntityController}
     * 
	 * @author Thomas Decamp
	 */
    @Test
    public void updateSkill_as_adminUser() {

        // setup
        Mockito.doReturn(true).when(this.skillBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
        Mockito.when(this.skillEntityController.updateSkill(this.mockedJSkill)).thenReturn(this.mockedHttpException);
        // assert
        Assertions.assertThat(this.skillBusinessController.updateSkill(this.mockedJSkill, UserRole.DEACTIVATED))
                .isEqualTo(this.mockedHttpException);
    }

    /**
	 * @test update a {@link Skill}
	 * @context as non admin user
	 * @expected same {@link HttpException} returned by the
	 *           {@link SkillEntityController}
	 * @author Kylian Gehier
	 */
	@Test
	public void updateSkill_as_nonAdminUser() {

		// setup
		Mockito.doReturn(false).when(this.skillBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions.assertThat(this.skillBusinessController.updateSkill(this.mockedJSkill, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);

	}
}
