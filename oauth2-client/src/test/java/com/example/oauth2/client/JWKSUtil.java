package com.example.oauth2.client;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.lang3.time.DateUtils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

public class JWKSUtil {

    private static RSAKey rsaKey;

    private static JWKSet jwkSet;

    private static SignedJWT signedJWT;

    static {
        KeyPair keyPair = generateRsaKey();

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        rsaKey = new RSAKey.Builder(publicKey).privateKey(privateKey).keyID("1").build();
        jwkSet = new JWKSet(rsaKey);

        System.out.println(jwkSet.toString());
        System.out.println(rsaKey);
        System.out.println(jwkSet);

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("1").build();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("user")
                .issueTime(new Date())
                .expirationTime(DateUtils.addHours(new Date(), 2))
                .issuer("http://localhost:8080")
                .build();

        signedJWT = new SignedJWT(header, claimsSet);

        try {
            signedJWT.sign(new RSASSASigner(privateKey));
        } catch (JOSEException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }


    /**
     * @return the JSON string of http://localhost:8080/oauth2/jwks
     */
    public static String getJWKSet() {
        return jwkSet.toString();
    }

    public static String getSignedJWT(){
        return signedJWT.serialize();
    }


    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return keyPair;
    }
}
