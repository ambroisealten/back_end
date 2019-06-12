package fr.alten.ambroiseJEE.controller.rest;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.UserBusinessController;

/**
 *
 * @author Andy Chabalier
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class LoginRestControllerTest {

	@InjectMocks
	@Spy
	private final LoginRestController loginRestController = new LoginRestController();

	@Mock
	private UserBusinessController userBusinessController;

	private final ObjectMapper mapper = new ObjectMapper();

	private final Gson gson;

	public LoginRestControllerTest() {
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

//	@Test
//	public void login_sucess() throws Exception {
//		final String subject = "mockedStringUser@mail.com";
//
//		Mockito.when(this.userBusinessController.checkIfCredentialIsValid(ArgumentMatchers.anyString(),
//				ArgumentMatchers.anyString())).thenReturn(Optional.of(subject));
//
//		final Consumer<String> tokenRequirements = tokenJson -> {
//			final Token token = this.gson.fromJson(tokenJson, Token.class);
//			try {
//				final String subjectDecoded = JWTokenUtility.validate(token.getToken());
//				Assert.assertTrue(subjectDecoded.equals(subject));
//			} catch (final InvalidJwtException e) {
//				Assert.assertFalse(true);
//			}
//		};
//
//		// assert that token is valid and have the good subject
//		Assertions
//				.assertThat(this.loginRestController.login(this.mapper
//						.readTree("{" + "\"mail\":\"mockedStringUser@mail.com\"," + "\"pswd\":\"pass\"" + "}")))
//				.isInstanceOfSatisfying(String.class, tokenRequirements);
//
//	}
//
//	@Test(expected = ForbiddenException.class)
//	public void login_with_forbiddenException() throws Exception {
//
//		Mockito.when(this.userBusinessController.checkIfCredentialIsValid(ArgumentMatchers.anyString(),
//				ArgumentMatchers.anyString())).thenReturn(Optional.empty());
//
//		// assert
//		this.loginRestController.login(
//				this.mapper.readTree("{" + "\"mail\":\"notExistingUser@mail.com\"," + "\"pswd\":\"pass\"" + "}"));
//
//	}
//
//	@Test(expected = UnprocessableEntityException.class)
//	public void login_with_nullPointerException_mail() throws Exception {
//		// assert
//		this.loginRestController.login(this.mapper.readTree("{" + "\"pswd\":\"pass\"" + "}"));
//	}
//
//	@Test(expected = UnprocessableEntityException.class)
//	public void login_with_nullPointerException_nothing() throws Exception {
//		// assert
//		this.loginRestController.login(this.mapper.readTree("{}"));
//	}
//
//	@Test(expected = UnprocessableEntityException.class)
//	public void login_with_nullPointerException_pswd() throws Exception {
//		// assert
//		this.loginRestController.login(this.mapper.readTree("{" + "\"mail\":\"notExistingUser@mail.com\"" + "}"));
//	}
}
