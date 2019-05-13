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

import fr.alten.ambroiseJEE.controller.business.SkillBusinessController;
import fr.alten.ambroiseJEE.model.beans.Skill;
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
public class SkillRestControllerTest {

	@InjectMocks
	@Spy
	private final SkillRestController skillRestController = new SkillRestController();
	
	@SpyBean
	private final Skill spiedSkill = new Skill();
	@Spy
	private JsonNode spiedJsonNode;
	
	@Mock
	private SkillBusinessController skillBusinessController;
	@Mock
	private JsonNode mockedJsonNode;
	@Mock
	private HttpException mockedHttpException;
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * @test testing several Json for integrity when createSkill
	 * @expected sucess for all json test cases
	 * @author Thomas Decamp
	 * @throws Exception 
	 */
	@Test
	public void createSkill_checkJsonIntegrity() throws Exception {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.skillBusinessController)
				.createSkill(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"mail\":\"\"," + "\"name\":\"skill\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.skillRestController.createSkill(this.spiedJsonNode, UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingNom = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingNom);
		// assert missing name
		Assertions.assertThat(this.skillRestController.createSkill(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test create a {@link Skill}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link SkillBusinessController}
	 * @author Thomas Decamp
	 * @throws Exception 
	 */
	@Test
	public void createSkill_with_rightParam() throws Exception {

		// setup
		Mockito.doReturn(true).when(this.skillRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.skillBusinessController.createSkill(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.skillRestController.createSkill(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test create a {@link Skill}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Thomas Decamp
	 * @throws Exception 
	 */
	@Test
	public void createSkill_with_wrongParam() throws Exception {

		// setup
		Mockito.doReturn(false).when(this.skillRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.skillRestController.createSkill(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test testing several Json for integrity when deleteSkill
	 * @expected sucess for all json test cases
	 * @author Thomas Decamp
	 * @throws Exception 
	 */
	@Test
	public void deleteSkill_checkJsonIntegrity() throws Exception {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.skillBusinessController)
				.deleteSkill(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"name\":\"skill\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.skillRestController.deleteSkill(this.spiedJsonNode, UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.skillRestController.deleteSkill(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	/**
	 * @test delete a {@link Skill}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link SkillBusinessController}
	 * @author Thomas Decamp
	 * @throws Exception 
	 */
	@Test
	public void deleteSkill_with_rightParam() throws Exception {

		// setup
		Mockito.doReturn(true).when(this.skillRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.skillBusinessController.deleteSkill(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.skillRestController.deleteSkill(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	/**
	 * @test delete a {@link Skill}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Thomas Decamp
	 * @throws Exception 
	 */
	@Test
	public void deleteSkill_with_wrongParam() throws Exception {

		// setup
		Mockito.doReturn(false).when(this.skillRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.skillRestController.deleteSkill(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	/**
	 * @test get the list of all {@link Skill}
	 * @expected returning a String
	 * @author Thomas Decamp
	 */
	@Test
	public void getCities_expectingString() {

		// setup
		Mockito.when(this.skillBusinessController.getSkills(ArgumentMatchers.any(UserRole.class)))
				.thenReturn(new ArrayList<Skill>());

		// assert
		Assertions.assertThat(this.skillRestController.getSkills(UserRole.CDR_ADMIN)).isInstanceOf(String.class);
	}

	/**
	 * @test testing several Json for integrity when updateSkill
	 * @expected sucess for all json test cases
	 * @author Thomas Decamp
	 * @throws Exception 
	 */
	@Test
	public void updateSkill_checkJsonIntegrity() throws Exception {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.skillBusinessController)
				.updateSkill(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{"+ " \"oldName\":\"oldName\",\r\n" + "\"name\":\"newName\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.skillRestController.updateSkill(this.spiedJsonNode, UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.skillRestController.updateSkill(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);


	}

	/**
	 * @test update a {@link Skill}
	 * @context {@link JsonNode} params containing field "name"
	 * @expected same {@link HttpException} returned by the
	 *           {@link SkillBusinessController}
	 * @author Thomas Decamp
	 * @throws Exception 
	 */
	@Test
	public void updateSkill_with_rightParam() throws Exception {

		// setup
		Mockito.doReturn(true).when(this.skillRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());
		Mockito.when(this.skillBusinessController.updateSkill(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.skillRestController.updateSkill(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);

	}

	/**
	 * @test update a {@link Skill}
	 * @context {@link JsonNode} params not containing field "name"
	 * @expected {@link UnprocessableEntityException}
	 * @author Thomas Decamp
	 * @throws Exception 
	 */
	@Test
	public void updateSkill_with_wrongParam() throws Exception {

		// setup
		Mockito.doReturn(false).when(this.skillRestController).checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class),
				ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.skillRestController.updateSkill(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);

	}
	
}
