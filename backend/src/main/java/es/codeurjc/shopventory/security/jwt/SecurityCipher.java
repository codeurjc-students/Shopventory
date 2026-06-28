package es.codeurjc.shopventory.security.jwt;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityCipher {

	private static final Logger LOG = LoggerFactory.getLogger(SecurityCipher.class);
	private static final String KEYVALUE = "secureCDCKey";
	private static final SecretKeySpec SECRET_KEY;

	static {
		try {
			byte[] key = KEYVALUE.getBytes(StandardCharsets.UTF_8);
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			SECRET_KEY = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Failed to initialize SecurityCipher", e);
		}
	}

	private SecurityCipher() {
		throw new AssertionError("Static!");
	}

	public static String encrypt(String strToEncrypt) {
		if (strToEncrypt == null) return null;
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY);
			return Base64.getEncoder().encodeToString(
					cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			LOG.error("Encryption failed", e);
		}
		return null;
	}

	public static String decrypt(String strToDecrypt) {
		if (strToDecrypt == null) return null;
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			LOG.error("Decryption failed", e);
		}
		return null;
	}
}
