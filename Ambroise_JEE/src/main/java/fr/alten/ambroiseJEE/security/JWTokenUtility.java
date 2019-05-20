package fr.alten.ambroiseJEE.security;

import javax.annotation.PostConstruct;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Provide utilities methods for JW Token
 *
 * @author Andy Chabalier
 *
 */
@Service
public class JWTokenUtility {

	@Autowired
	private ApplicationContext ctx;

	private static ApplicationContext autowiredCtx;

	@PostConstruct
	private void init() {
		autowiredCtx = this.ctx;
	}

	/**
	 * Build a JW token
	 *
	 * @param subject user data to encrypt in token
	 * @return String generated token
	 * @author Andy Chabalier
	 * @throws MalformedClaimException
	 */
	public static Token buildJWT(final String subject) throws MalformedClaimException {
		final RsaJsonWebKey rsaJsonWebKey = RsaKeyProducer.produce();

		// création de la "charge utile" ou payload - la donnée Ã chiffrer, ici
		// 'subject'
		final JwtClaims claims = new JwtClaims();
		claims.setSubject(subject);
		claims.setExpirationTimeMinutesInTheFuture(
				Float.parseFloat(autowiredCtx.getEnvironment().getProperty("security.token.expirationTime")));

		// création de la signature
		final JsonWebSignature jws = new JsonWebSignature();
		jws.setPayload(claims.toJson());
		jws.setKey(rsaJsonWebKey.getPrivateKey());
		jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

		// encodage du token JWT
		String jwt = null;
		try {
			jwt = jws.getCompactSerialization();
		} catch (final JoseException ex) {
			ex.printStackTrace();
		}
		return new Token(jwt);
	}

	/**
	 * Méthode de validation d'un JWT
	 *
	 * @param jwt le token à tester
	 * @return le contenu du token déchiffré s'il est valide
	 * @throws InvalidJwtException si le token n'est pas valide
	 * @author Andy Chabalier
	 */
	public static String validate(final String jwt) throws InvalidJwtException {
		String subject = null;
		final RsaJsonWebKey rsaJsonWebKey = RsaKeyProducer.produce();

		// construction du décodeur de JWT
		final JwtConsumer jwtConsumer = new JwtConsumerBuilder().setRequireSubject()
				.setVerificationKey(rsaJsonWebKey.getKey()).build();

		// validation du JWT et récupération du contenu
		final JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
		subject = (String) jwtClaims.getClaimValue("sub");

		return subject;
	}
}
