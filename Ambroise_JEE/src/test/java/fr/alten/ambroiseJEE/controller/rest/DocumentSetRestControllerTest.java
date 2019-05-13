/**
 * 
 */
package fr.alten.ambroiseJEE.controller.rest;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.alten.ambroiseJEE.controller.business.DocumentSetBusinessController;
import fr.alten.ambroiseJEE.model.beans.DocumentSet;
import fr.alten.ambroiseJEE.model.beans.mobileDoc.MobileDoc;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 * @author MAQUINGHEN MAXIME
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class DocumentSetRestControllerTest {

	@InjectMocks
	@Spy
	private final DocumentSetRestController documentSetRestController = new DocumentSetRestController();

	@SpyBean
	private final DocumentSet spiedDocumentSet = new DocumentSet();
	@Spy
	private JsonNode spiedJsonNode;
	@Spy
	private HashMap<String, List<MobileDoc>> spiedHashMap = new HashMap<>();

	@Mock
	private DocumentSetBusinessController documentSetBusinessController;
	@Mock
	private JsonNode mockedJsonNode;
	@Mock
	private HttpException mockedHttpException;

	private final ObjectMapper mapper = new ObjectMapper();

	@Test
	public void createDocumentSet_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.documentSetBusinessController)
				.createDocumentSet(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + "\"name\":\"name\"" + ",\"files\": [ {\"name\": \"url\", \"order\":\"order\"} ]"
				+ "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.documentSetRestController.createDocumentSet(this.spiedJsonNode, UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingArg = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingArg);
		// assert missing name
		Assertions.assertThat(this.documentSetRestController.createDocumentSet(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	@Test
	public void createDocumentSet_with_rightParam() {
		// setup
		Mockito.doReturn(true).when(this.documentSetRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());
		Mockito.when(this.documentSetBusinessController.createDocumentSet(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.documentSetRestController.createDocumentSet(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);
	}

	@Test
	public void createDocumentSet_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.documentSetRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.documentSetRestController.createDocumentSet(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	@Test
	public void updateDocumentSet_checkJsonIntegrity() throws IOException {

		// globalSetup
		Mockito.doReturn(this.mockedHttpException).when(this.documentSetBusinessController)
				.updateDocumentSet(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + " \"oldName\":\"oldName\",\r\n" + "\"name\":\"newName\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		Assertions.assertThat(this.documentSetRestController.updateDocumentSet(this.spiedJsonNode, UserRole.CDR))
				.isNotInstanceOf(UnprocessableEntityException.class);

		// setup : missing name
		final String missingName = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		Assertions.assertThat(this.documentSetRestController.updateDocumentSet(this.spiedJsonNode, UserRole.CDR))
				.isInstanceOf(UnprocessableEntityException.class);
	}

	@Test
	public void updateDocumentSet_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.documentSetRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());
		Mockito.when(this.documentSetBusinessController.updateDocumentSet(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.thenReturn(this.mockedHttpException);

		// assert
		Assertions.assertThat(this.documentSetRestController.updateDocumentSet(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedHttpException);

	}

	@Test
	public void updateDocumentSet_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.documentSetRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.documentSetRestController.updateDocumentSet(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	@Test
	public void getDocumentSetAdmin_expectingString() {

		// setup
		Mockito.when(this.documentSetBusinessController.getAllDocumentSet(ArgumentMatchers.any(UserRole.class)))
				.thenReturn(new ArrayList<DocumentSet>());

		// assert
		Assertions.assertThat(this.documentSetRestController.getDocumentSetAdmin(UserRole.CDR_ADMIN))
				.isInstanceOf(String.class);
	}

	@Test
	public void getDocumentSet_validCheckJsonIntegrity() throws IOException {
		// globalSetup
		Mockito.doReturn(spiedHashMap).when(this.documentSetBusinessController)
				.getDocumentSet(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any(UserRole.class));

		// setup : all needed fields present
		final String valid = "{" + " \"name\":\"name\",\r\n" + "\"files\":\"files\"" + "}";

		this.spiedJsonNode = this.mapper.readTree(valid);
		// assert all field present
		// Assertions.assertThat(this.documentSetRestController.getDocumentSet(this.spiedJsonNode,
		// UserRole.CDR))
		// .is;
		assertTrue(String.class
				.isInstance(this.documentSetRestController.getDocumentSet(this.spiedJsonNode, UserRole.CDR)));

	}

	@Test(expected = UnprocessableEntityException.class)
	public void getDocumentSet_unvalidCheckJsonIntegrity() throws IOException {

		// setup : missing name
		final String missingName = "{" + "}";

		this.spiedJsonNode = this.mapper.readTree(missingName);
		// assert missing name
		this.documentSetRestController.getDocumentSet(this.spiedJsonNode, UserRole.CDR);
	}

	@Test
	public void getDocumentSet_with_rightParam() {

		// setup
		Mockito.doReturn(true).when(this.documentSetRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());

		// assert
		Assertions.assertThat(String.class
				.isInstance(this.documentSetRestController.getDocumentSet(this.spiedJsonNode, UserRole.CDR)));

	}

	@Test(expected = UnprocessableEntityException.class)
	public void getDocumentSet_with_wrongParam() {

		// setup
		Mockito.doReturn(false).when(this.documentSetRestController)
				.checkJsonIntegrity(ArgumentMatchers.any(JsonNode.class), ArgumentMatchers.any());

		// assert
		Assertions.assertThat(this.documentSetRestController.getDocumentSet(this.mockedJsonNode, UserRole.CDR_ADMIN))
				.isInstanceOf(UnprocessableEntityException.class);

	}

	@Test
	public void getSpecificDocumentSet_expectingString() {

		// setup
		Mockito.when(this.documentSetBusinessController.getSpecificDocumentSet(ArgumentMatchers.anyString(),
				ArgumentMatchers.any(UserRole.class))).thenReturn(this.spiedDocumentSet);

		// assert
		Assertions.assertThat(this.documentSetRestController.getSpecificDocumentSet("", UserRole.CDR_ADMIN))
				.isInstanceOf(String.class);
	}

}
