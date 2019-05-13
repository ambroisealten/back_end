/**
 * 
 */
package fr.alten.ambroiseJEE.controller.business;

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
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.DocumentSet;
import fr.alten.ambroiseJEE.model.beans.mobileDoc.MobileDoc;
import fr.alten.ambroiseJEE.model.entityControllers.DocumentSetEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DocumentSetBusinessControllerTest {

	@InjectMocks
	@Spy
	private final DocumentSetBusinessController documentSetBusinessController = new DocumentSetBusinessController();

	@Mock
	private DocumentSetEntityController documentSetEntityController;
	@Mock
	private HttpException mockedHttpException;
	@Mock
	private ForbiddenException mockedForbiddenException;
	@Mock
	private JsonNode mockedJDocumentSet;
	@Mock
	private List<DocumentSet> mockedDocumentSetList;
	@Mock
	private HashMap<String, List<MobileDoc>> mockedHashdocumentset;
	@Mock
	private DocumentSet mockedDocumentSetDocumentSet;

	@MockBean
	private DocumentSet mockedDocumentSet;

	@Test
	public void createDocumentSet_as_adminUser() {
		Mockito.doReturn(true).when(this.documentSetBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.documentSetEntityController.createDocumentSet(this.mockedJDocumentSet))
				.thenReturn(this.mockedHttpException);

		Assertions.assertThat(
				this.documentSetBusinessController.createDocumentSet(this.mockedJDocumentSet, UserRole.DEACTIVATED))
				.isEqualTo(this.mockedHttpException);
	}

	@Test
	public void createDocumentSet_as_nonAdminUser() {
		Mockito.doReturn(false).when(this.documentSetBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Assertions.assertThat(
				this.documentSetBusinessController.createDocumentSet(this.mockedJDocumentSet, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);
	}

	@Test
	public void updateDocumentSet_as_Admin() {
		Mockito.doReturn(true).when(this.documentSetBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.documentSetEntityController.updateDocumentSet(this.mockedJDocumentSet))
				.thenReturn(this.mockedHttpException);
		// assert
		Assertions.assertThat(
				this.documentSetBusinessController.updateDocumentSet(this.mockedJDocumentSet, UserRole.DEACTIVATED))
				.isEqualTo(this.mockedHttpException);
	}

	@Test
	public void updateDocumentSet_as_nonAdmin() {
		Mockito.doReturn(false).when(this.documentSetBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// assert
		Assertions.assertThat(
				this.documentSetBusinessController.updateDocumentSet(this.mockedJDocumentSet, UserRole.MANAGER_ADMIN))
				.isInstanceOf(ForbiddenException.class);
	}

	@Test
	public void getDocumentSet_as_ConnectedUser() {

		// setup
		Mockito.doReturn(true).when(this.documentSetBusinessController)
				.isConnected(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.documentSetEntityController.getDocumentSetChanges(this.mockedJDocumentSet))
				.thenReturn(this.mockedHashdocumentset);
		// assert
		Assertions.assertThat(
				this.documentSetBusinessController.getDocumentSet(this.mockedJDocumentSet, UserRole.DEACTIVATED))
				.isEqualTo(this.mockedHashdocumentset);
	}

	@Test
	public void getAllDocumentSet_as_AdminUser() {
		Mockito.doReturn(true).when(this.documentSetBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.documentSetEntityController.getAllDocumentSet()).thenReturn(this.mockedDocumentSetList);
		// assert
		Assertions.assertThat(this.documentSetBusinessController.getAllDocumentSet(UserRole.DEACTIVATED))
				.isEqualTo(this.mockedDocumentSetList);
	}

	@Test(expected = ForbiddenException.class)
	public void getAllDocumentSet_as_ConnectedUser() {
		Mockito.doReturn(false).when(this.documentSetBusinessController).isAdmin(ArgumentMatchers.any(UserRole.class));
		// assert
		this.documentSetBusinessController.getAllDocumentSet(UserRole.MANAGER_ADMIN);
	}
	
	@Test
	public void getSpecificDocumentSet_as_ConnectedUser() {
		Mockito.doReturn(true).when(this.documentSetBusinessController).isConnected(ArgumentMatchers.any(UserRole.class));
		Mockito.when(this.documentSetEntityController.getSpecificDocumentSet(ArgumentMatchers.anyString())).thenReturn(this.mockedDocumentSetDocumentSet);
		// assert
		Assertions.assertThat(this.documentSetBusinessController.getSpecificDocumentSet("", UserRole.CDR_ADMIN))
				.isEqualTo(this.mockedDocumentSetDocumentSet);
	}
	
	@Test(expected = ForbiddenException.class)
	public void getSpecificDocumentSet_as_nonConnectedUser() {
		Mockito.doReturn(false).when(this.documentSetBusinessController).isConnected(ArgumentMatchers.any(UserRole.class));
		// assert
		this.documentSetBusinessController.getSpecificDocumentSet("", UserRole.CDR_ADMIN);
	}
}
