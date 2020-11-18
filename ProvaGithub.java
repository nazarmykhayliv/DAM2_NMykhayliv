package Proves;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ProvaGithub {

	public static void main(String[] args) throws UnsupportedEncodingException {
		
		
		
		String textClar = "Avui fa bon dia"; // creem el text clar.
		
		;
		System.out.println("Text clar sense els canvis [" + textClar+"]");
		
		// GENEREM LA CLAU AES
		
		SecretKey sKey = passwordKeyGeneration("BonDia",128); // per xifrar la clau utiltizo el metode de generar la clau amb text i numero de bytes.
		String clau = Base64.getEncoder().encodeToString(sKey.getEncoded()); // passem de SecretKey a String utilitzan Base64
		System.out.println("La clau genereda amb AES (text + longitud de la clau) -> " + clau);
		
		// XIFREM TEXT AMB AES

		
			byte[] textClarBytes = textClar.getBytes("UTF-8");		// obtenim array amb bytes passades des de la paraula que tenim creada.
			byte[] textXifrat = encryptData(sKey,textClarBytes);	// la encriptem cridant la funcio i guardem amb aquet nom de variable.
			
			System.out.println("El text xifrat AES en format String es -> " +new String(Base64.getEncoder().encodeToString(textXifrat)));
		
		
		// OBTENIM ELS KEYS PUBLIC I PRIVAT
		
		KeyPair claus = randomGenerate(2048);	//
		System.out.println("Clau publica -> " +Base64.getEncoder().encodeToString(claus.getPublic().getEncoded()));
		System.out.println("Clau privada -> "+ Base64.getEncoder().encodeToString(claus.getPrivate().getEncoded())); // codificació bytes amb lletres numeros, majuscules,minuscules, barres, etc..
		
		// XIFREM LA CLAU AES AMB RSA
		
		byte[] ClauAESXifrada = encryptData(sKey.getEncoded(),claus.getPublic());
		System.out.println("La clau AES Xifrada amb RSA ->" + Base64.getEncoder().encodeToString(ClauAESXifrada));
		
		
		// DESXIFREM LA CLAU AES AMB RSA
		System.out.println("");
		System.out.println("PASSEM A DESXIFRAR");
		System.out.println("");
		
		
		byte[] ClauAESDesxifrada = decryptData(ClauAESXifrada,claus.getPrivate());
		System.out.println("La clau desxifrada amb RSA -> " + new String(ClauAESDesxifrada));
		
		// DESXIFREM EL TEXT AMB CLAU DESXIFRADA RSA i TEXT XIFRAT AMB AES
		 SecretKey ClauAESDesxifrada2 = new SecretKeySpec(ClauAESDesxifrada, "AES");
		byte[] textDesxifrat = decryptData(ClauAESDesxifrada2, textXifrat);	// crearem el array cridant la funció amb sKey i amb textXifrat.
		System.out.println("El text desxifrat en format String es: " + new String(textDesxifrat));
		
	}
	
	// XIFREM LA CLAU AMB AES
	public static SecretKey passwordKeyGeneration(String text, int keySize) {
		SecretKey sKey = null;
		if ((keySize == 128) || (keySize == 192) || (keySize == 256)) {
			try {
				
				
				byte[] data = text.getBytes("UTF-8");							//array de byte , a cada posicio guarde una lletra (convertim a bytes)
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				byte[] hash = md.digest(data);									// passem un array a l'altre, genere la clau amb byes, com que solament funciona amb bytes. 
				byte[] key = Arrays.copyOf(hash, keySize / 8);					// ens parteix per bytes.
				sKey = new SecretKeySpec(key, "AES");
				
				
			} catch (Exception ex) {
				System.err.println("Error generant la clau:" + ex);
			}
		}
		return sKey;
	}
	// XIFRAR EL TEXT AMB AES
	public static byte[] encryptData(SecretKey sKey, byte[] data) {		// necessita clau secreta, text xifrat amb bytes // encriptació
		byte[] encryptedData = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, sKey);
			encryptedData = cipher.doFinal(data);
		} catch (Exception ex) {
			System.err.println("Error xifrant les dades: " + ex);
		}
		return encryptedData;
	}
	
	// generar claus publiques i privades
	public static KeyPair randomGenerate(int len) {
		KeyPair keys = null;
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");	// genera claus per RSA (1024/2048/4096)
			keyGen.initialize(len);
			keys = keyGen.genKeyPair();
		} catch (Exception ex) {
			System.err.println("Generador no disponible.");
		}
		return keys;
	}
		// xifrar la clau AES AMB RSA
	public static byte[] encryptData(byte[] data, PublicKey pub) {
		byte[] encryptedData = null;
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "SunJCE");
			cipher.init(Cipher.ENCRYPT_MODE, pub);
			encryptedData = cipher.doFinal(data);
		} catch (Exception ex) {
			System.err.println("Error xifrant: " + ex);
		}
		return encryptedData;
	}

	// DESXIFREM
	
	// metode per desxifrar la clau AES amb RSA
	
	public static byte[] decryptData(byte[] data, PrivateKey priv) {
		byte[] decryptedData = null;
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "SunJCE");
			cipher.init(Cipher.DECRYPT_MODE, priv);
			decryptedData = cipher.doFinal(data);
			
		} catch (Exception ex) {
			System.err.println("Error xifrant: " + ex);
		}
		return decryptedData;
	}
	// METODE PER DESENCRIPTAR AMB AES
	public static byte[] decryptData(SecretKey sKey, byte[] data) {		// necessita clau secreta, text xifrat amb bytes // desencriptació
		byte[] decryptedData = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, sKey);
			decryptedData = cipher.doFinal(data);
		} catch (Exception ex) {
			System.err.println("Error desxifrant les dades: " + ex);
		}
		return decryptedData;
	}
}