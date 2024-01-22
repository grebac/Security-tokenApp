package hepl.caberg.tokenapp.Web;

import hepl.caberg.tokenapp.encryption.SHA1RSASignatureMessage;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

public class tokenRequestConfig {
    private String bankNumber;
    private String publicKey;
    private KeyPair keyPair;
    private SHA1RSASignatureMessage signatureMessage;
    private tokenRequestTemplate tokenRequestTemplate;

    public tokenRequestConfig() {
    }

    public tokenRequestConfig(String bankumber, String publicKey) {
        this.bankNumber = bankumber;
        this.publicKey = publicKey;
    }

    private void getRSAKeyPair() {
        try {
            // Construct a relative path
            String keystoreRelativePath = "src/main/resources/keys/" + getPublicKey();

            // Build the absolute path
            String rootPath = System.getProperty("user.dir");
            Path absolutePath = Paths.get(rootPath, keystoreRelativePath);

            // Load the keystore
            char[] keystorePassword = "heplPass".toCharArray();
            FileInputStream keystoreFile = new FileInputStream(absolutePath.toString());

            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(keystoreFile, keystorePassword);

            // Retrieve key pair from keystore
            String alias = "clientKeys";  // Replace with the alias of your key pair
            char[] keyPassword = "heplPass".toCharArray();  // Replace with the password of your key pair

            KeyPair keyPair = getKeyPairFromKeyStore(keystore, alias, keyPassword);
            this.setKeyPair(keyPair);

            // Print information about the key pair
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            // System.out.println("Public Key: " + publicKey);
            // System.out.println("Private Key: " + privateKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public tokenRequestTemplate getTokenRequestTemplate() {
        return this.tokenRequestTemplate;
    }

    public byte[] getSignature() {
        // We have already generated the signature
        if(this.signatureMessage != null)
            return this.signatureMessage.getSignature();

        // Let's generate the signature
        this.tokenRequestTemplate = new tokenRequestTemplate(this.getBankNumber(), new Date());
        this.signatureMessage = new SHA1RSASignatureMessage(this.tokenRequestTemplate);

        try {
            signatureMessage.sign(this.getKeyPair().getPrivate());
            return signatureMessage.getSignature();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static KeyPair getKeyPairFromKeyStore(KeyStore keystore, String alias, char[] keyPassword) throws Exception {
        // Retrieve the private key and certificate chain from the keystore
        PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, keyPassword);
        Certificate[] certChain = keystore.getCertificateChain(alias);

        // Assume that the first certificate in the chain is the end-entity certificate
        X509Certificate x509Cert = (X509Certificate) certChain[0];
        PublicKey publicKey = x509Cert.getPublicKey();

        // Create a KeyPair from the retrieved keys
        return new KeyPair(publicKey, privateKey);
    }

    public String getBankNumber() {
        return bankNumber;
    }
    public void setBankNumber(String bankumber) {
        this.bankNumber = bankumber;
    }

    public String getPublicKey() {
        return publicKey;
    }
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        getRSAKeyPair();
        this.signatureMessage = null;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }
    public void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }
}
