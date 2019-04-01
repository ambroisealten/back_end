package fr.alten.ambroiseJEE.security;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
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
		System.out.println("RSA hash code... " + rsaJsonWebKey.hashCode());

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
}
