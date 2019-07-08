package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import fr.alten.ambroiseJEE.model.beans.File;
import fr.alten.ambroiseJEE.model.dao.FileRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 *
 * @author Andy Chabalier
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class FileEntityControllerTest {

	@InjectMocks
	@Spy
	private final FileEntityController fileEntityController = new FileEntityController();

	@Mock
	private FileRepository fileRepository;

	@Test
	public void deleteFile_sucess() {
		Mockito.doReturn(Optional.ofNullable(new File())).when(this.fileRepository)
				.findBy_id(ArgumentMatchers.any(ObjectId.class));
		Mockito.doNothing().when(this.fileRepository).delete(ArgumentMatchers.any(File.class));
		Mockito.doNothing().when(this.fileEntityController).deleteMobileDocOnCascade(ArgumentMatchers.anyString());

		// assert
		Assertions.assertThat(this.fileEntityController.deleteFile(new ObjectId().toHexString()))
				.isInstanceOf(OkException.class);
	}

	@Test
	public void deleteFile_with_resourceNotFound() {
		// assert
		Assertions.assertThat(this.fileEntityController.deleteFile("")).isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	public void getCollectionFiles() {
		// assert
		Assertions.assertThat(this.fileEntityController.getCollectionFiles("")).isInstanceOf(List.class);
	}

	@Test
	public void getFile_sucess() {
		Mockito.doReturn(Optional.of(new File())).when(this.fileRepository)
				.findBy_id(ArgumentMatchers.any(ObjectId.class));

		// assert
		Assertions.assertThat(this.fileEntityController.getFile(new ObjectId())).isInstanceOf(File.class);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void getFile_with_resourceNotFoundException() {
		Mockito.doReturn(Optional.empty()).when(this.fileRepository).findBy_id(ArgumentMatchers.any(ObjectId.class));

		// assert
		this.fileEntityController.getFile(new ObjectId());
	}

	@Test
	public void getFiles() {
		// assert
		Assertions.assertThat(this.fileEntityController.getFiles()).isInstanceOf(List.class);
	}

	@Test
	public void getFilesForum() {
		// assert
		Assertions.assertThat(this.fileEntityController.getFilesForum()).isInstanceOf(List.class);
	}

	@Test
	public void pushDocument_sucess() {
		Mockito.doReturn(new File()).when(this.fileRepository).save(ArgumentMatchers.any(File.class));

		// assert
		Assertions.assertThat(this.fileEntityController.pushDocument("", "")).isInstanceOf(File.class);
	}

	@Test(expected = ConflictException.class)
	public void pushDocument_with_conflictException() {
		// throw any exception
		Mockito.doThrow(RuntimeException.class).when(this.fileRepository).save(ArgumentMatchers.any(File.class));

		// assert
		Assertions.assertThat(this.fileEntityController.pushDocument("", "")).isInstanceOf(File.class);
	}

	@Test
	public void updateFile_sucess() {
		Mockito.doReturn(new File()).when(this.fileRepository).save(ArgumentMatchers.any(File.class));
		Mockito.doReturn(Optional.of(new File())).when(this.fileRepository)
				.findBy_id(ArgumentMatchers.any(ObjectId.class));

		// assert
		Assertions.assertThat(this.fileEntityController.updateFile(new ObjectId().toHexString(), "", ""))
				.isInstanceOf(OkException.class);
	}

	@Test
	public void updateFile_with_resourceNotFoundException() {
		Mockito.doReturn(Optional.empty()).when(this.fileRepository).findBy_id(ArgumentMatchers.any(ObjectId.class));

		// assert
		Assertions.assertThat(this.fileEntityController.updateFile(new ObjectId().toHexString(), "", ""))
				.isInstanceOf(ResourceNotFoundException.class);
	}

}
