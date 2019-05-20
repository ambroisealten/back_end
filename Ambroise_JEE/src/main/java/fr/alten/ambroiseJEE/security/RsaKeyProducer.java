package fr.alten.ambroiseJEE.security;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Classe permettant la génération d'une clé RSA sur 2048 bits
 *
 * @author Andy Chabalier
 */
@Service
public class RsaKeyProducer {

	// clé RSA
	private static RsaJsonWebKey theOne;

	@Autowired
	private ApplicationContext ctx;

	private static ApplicationContext autowiredCtx;

	@PostConstruct
	private void init() {
		autowiredCtx = this.ctx;
	}

	/**
	 * Méthode produisant une nouvelle clé si elle n'existe pas encore
	 *
	 * @return la clé générée
	 * @author Andy Chabalier
	 */
	public static RsaJsonWebKey produce() {
		if (RsaKeyProducer.theOne == null) {
			try {
				RsaKeyProducer.theOne = RsaJwkGenerator.generateJwk(
						Integer.parseInt(autowiredCtx.getEnvironment().getProperty("security.token.rsaBitCrypted")));
			} catch (final JoseException ex) {
				Logger.getLogger(RsaKeyProducer.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return RsaKeyProducer.theOne;
	}
}