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
	   
	    /*String echo;
	    for(int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
            
        }*/

	    byte[] initVector = new byte[] { 0x10, 0x10, 0x01, 0x04, 0x01, 0x01, 0x01, 0x02 };

	    AlgorithmParameterSpec algParamSpec = new IvParameterSpec(initVector);
	    Cipher m_encrypter = Cipher.getInstance("DES/CBC/PKCS5Padding");
	    Cipher m_decrypter = Cipher.getInstance("DES/CBC/PKCS5Padding");

	    m_encrypter.init(Cipher.ENCRYPT_MODE, key, algParamSpec);
	    m_decrypter.init(Cipher.DECRYPT_MODE, key, algParamSpec);

	    //working on echo get
	    byte[] originalText = echo.getBytes();

	    byte[] encryptedText = m_encrypter.doFinal(originalText);

	    byte[] decryptedText = m_decrypter.doFinal(encryptedText);

	    System.out.println(new String(originalText));
	    System.out.println(new String(encryptedText));
	    System.out.println(new String(decryptedText));

	  }
}
