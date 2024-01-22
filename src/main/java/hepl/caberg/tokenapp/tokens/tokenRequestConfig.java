package hepl.caberg.tokenapp.tokens;

import hepl.caberg.tokenapp.TLS.TokenRequestCreator;
import hepl.caberg.tokenapp.encryption.KeyPairManager;

import java.security.*;

public class tokenRequestConfig {
    // These 2 parameters come from the web form
    private String bankNumber;
    private String keystoreName;

    // This manager gets us the key from the keystore (keystoreName)
    private KeyPairManager keyPairManager;

    // This object manages the tls request. It signs the tokenRequestTemplate and holds the tokenRequestTemplate
    private TokenRequestCreator tokenRequestCreator;


    //      ------------------ CONSTRUCTORS ---------------------   //
    public tokenRequestConfig() {
    }

    public tokenRequestConfig(String bankumber, String key) {
        this.bankNumber = bankumber;
        this.keystoreName = key;
        this.tokenRequestCreator = new TokenRequestCreator(bankumber, this.getKeyPair());
    }

    //      ------------------ GETTERS AND SETTERS ---------------------   //
    private TokenRequestCreator getTokenRequestCreator() {
        // If the object doesn't exist, we create it
        if(this.tokenRequestCreator == null)
            this.tokenRequestCreator = new TokenRequestCreator(this.bankNumber, this.getKeyPair());

        return this.tokenRequestCreator;
    }

    // This is the actual object that will be sent to the ACS
    public tokenRequestTemplate getTokenRequestTemplate() {
        return getTokenRequestCreator().getTokenRequestTemplate();
    }

    // This is the signature of the tokenRequestTemplate
    public byte[] getSignature() {
        return getTokenRequestCreator().getSignature();
    }

    // This is the keypair that will be used to sign the tokenRequestTemplate
    public KeyPair getKeyPair() {
        // If we haven't generated the keys...
        if(this.keyPairManager == null)
            this.keyPairManager = new KeyPairManager(this.keystoreName);

        return keyPairManager.getKeyPair();
    }

    // These are setters and getters for the html form
    public String getBankNumber() {
        return bankNumber;
    }
    public void setBankNumber(String bankumber) {
        this.bankNumber = bankumber;
    }

    public String getKeystoreName() {
        return keystoreName;
    }
    public void setKeystoreName(String keystoreName) {
        this.keystoreName = keystoreName;

        // Since we have a new keystore, we open it with a new KeyPairManager
        this.keyPairManager = new KeyPairManager(keystoreName);

        // We also need to update the tokenRequestCreator
        this.tokenRequestCreator = new TokenRequestCreator(this.bankNumber, this.getKeyPair());
    }
}
