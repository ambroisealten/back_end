/**
 * 
 */
package fr.alten.ambroiseJEE.model.entityControllers;

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

import fr.alten.ambroiseJEE.model.beans.DocumentSet;
import fr.alten.ambroiseJEE.model.dao.DocumentSetRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DocumentSetEntityControllerTest {

	@InjectMocks
	@Spy
	private final DocumentSetEntityController documentSetEntityController = new DocumentSetEntityController();
	
	@Mock
	private DocumentSetRepository documentSetRepository;
	@Mock
	private JsonNode mockedJDocumentSet;
	@Mock
	private DuplicateKeyException mockedDuplicateKeyException;
	
	@MockBean
	private DocumentSet mockedDocumentSet;
	
	
	@Test
	public void createDocumentSet_with_conflict() {
		//setup
		Mockito.doReturn(this.mockedJDocumentSet).when(this.mockedJDocumentSet).get(ArgumentMatchers.anyString());
		Mockito.doThrow(this.mockedDuplicateKeyException).when(this.documentSetRepository)
				.save(ArgumentMatchers.any(DocumentSet.class));
		// assert
		Assertions.assertThat(this.documentSetEntityController.createDocumentSet(this.mockedJDocumentSet))
				.isInstanceOf(ConflictException.class);
		// verify
		Mockito.verify(this.documentSetRepository, Mockito.times(1)).save(ArgumentMatchers.any(DocumentSet.class));
	}
	
	
	
	
}
