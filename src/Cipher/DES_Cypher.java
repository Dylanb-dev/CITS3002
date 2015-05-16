package Cipher;

import java.util.Scanner;

import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

@SuppressWarnings("unused")
public class DES_Cypher {

	public static void main(String args[]) throws Exception {
	    SecretKey key = KeyGenerator.getInstance("DES").generateKey();

	    byte[] initVector = new byte[] { 0x10, 0x10, 0x01, 0x04, 0x01, 0x01, 0x01, 0x02 };

	    AlgorithmParameterSpec algParamSpec = new IvParameterSpec(initVector);
	    Cipher m_encrypter = Cipher.getInstance("DES/CBC/PKCS5Padding");
	    Cipher m_decrypter = Cipher.getInstance("DES/CBC/PKCS5Padding");

	    m_encrypter.init(Cipher.ENCRYPT_MODE, key, algParamSpec);
	    m_decrypter.init(Cipher.DECRYPT_MODE, key, algParamSpec);

	    byte[] originalText = "test".getBytes();
	    String echo_originalText = new String(originalText);

	    byte[] encryptedText = m_encrypter.doFinal(originalText);
	    String echo_encryptedText = new String(encryptedText);

	    byte[] decryptedText = m_decrypter.doFinal(encryptedText);
	    String echo_decryptedText = new String(decryptedText);
	    

	    System.out.println(new String(echo_originalText));
	    System.out.println(new String(echo_encryptedText));
	    System.out.println(new String(echo_decryptedText));

	  }
}
