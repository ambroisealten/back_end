package fr.alten.ambroiseJEE.security;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.lang.JoseException;

/**
 * Classe permettant la génération d'une clé RSA sur 2048 bits
 *
 * @author Andy Chabalier
 */
public class RsaKeyProducer {

	// clé RSA
	private static RsaJsonWebKey theOne;

	/**
	 * Méthode produisant une nouvelle clé si elle n'existe pas encore
	 *
	 * @return la clé générée
	 * @author Andy Chabalier
	 */
	public static RsaJsonWebKey produce() {
		if (RsaKeyProducer.theOne == null) {
			try {
				RsaKeyProducer.theOne = RsaJwkGenerator.generateJwk(2048);
			} catch (final JoseException ex) {
				Logger.getLogger(RsaKeyProducer.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return RsaKeyProducer.theOne;
	}
}