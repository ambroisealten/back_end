package fr.alten.ambroiseJEE.security;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

/**
 * Provide utilities methods for JW Token
 *
 * @author Andy Chabalier
 *
 */
public class JWTokenUtility {

	/**
	 * Build a JW token
	 *
	 * @param subject user data to encrypt in token
	 * @return String generated token
	 * @author Andy Chabalier
	 */
	public static Token buildJWT(String subject) {
		RsaJsonWebKey rsaJsonWebKey = RsaKeyProducer.produce();

		// création de la "charge utile" ou payload - la donnée Ã chiffrer, ici
		// 'subject'
		JwtClaims claims = new JwtClaims();
		claims.setSubject(subject);

		// création de la signature
		JsonWebSignature jws = new JsonWebSignature();
		jws.setPayload(claims.toJson());
		jws.setKey(rsaJsonWebKey.getPrivateKey());
		jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

		// encodage du token JWT
		String jwt = null;
		try {
			jwt = jws.getCompactSerialization();
		} catch (JoseException ex) {
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
	public static String validate(String jwt) throws InvalidJwtException {
		String subject = null;
		RsaJsonWebKey rsaJsonWebKey = RsaKeyProducer.produce();

		// construction du décodeur de JWT
		JwtConsumer jwtConsumer = new JwtConsumerBuilder().setRequireSubject()
				.setVerificationKey(rsaJsonWebKey.getKey()).build();

		// validation du JWT et récupération du contenu
		JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
		subject = (String) jwtClaims.getClaimValue("sub");

		return subject;
	}
}
