package crypto;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.bouncycastle.util.encoders.Hex;

public class Crypto {

	private static Crypto instance;
	
	public Crypto() {
		java.security.Security.addProvider(new BouncyCastleFipsProvider());
	}
	
	public static Crypto getInstance() {
		if(instance == null) {
			instance = new Crypto();
		}
		return instance;
	}
	
	   /*Usado para gerar o salt  */
    public String generateSal() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return Hex.toHexString(salt);
    }
	
    
     /* Gerar chave derivada da senha     */
    public String generateDerivedKey(String password, String sal) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), sal.getBytes(), 10000, 128);
        SecretKeyFactory pbkdf2;
        String derivedPass = null;
        try {
            pbkdf2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            SecretKey sk = pbkdf2.generateSecret(spec);
            derivedPass = Hex.toHexString(sk.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return derivedPass;
    }
    
    public String hMAC(String text, String sal) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
    	//DERIVA UMA CHAVE A PARTIR DO SENHA E O SAL
    	String key = generateDerivedKey(text,sal);
    	Mac hmac = Mac.getInstance("HMacSHA256", "BCFIPS");
    	Key signingKey = new SecretKeySpec(key.getBytes(), "HMacSHA256");
    	
    	//REALIZA O HMAC
    	hmac.init(signingKey);
    	hmac.update(text.getBytes());
    	byte[] hmacText = hmac.doFinal();
    	
    	return Hex.toHexString(hmacText);
    }
    
    public String toSha256(String text) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        return Hex.toHexString(hash);
    }
}
