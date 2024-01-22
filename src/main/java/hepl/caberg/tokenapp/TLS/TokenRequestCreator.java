package hepl.caberg.tokenapp.TLS;

import hepl.caberg.tokenapp.tokens.tokenRequestTemplate;
import hepl.caberg.tokenapp.encryption.SHA1RSASignatureMessage;

import java.security.KeyPair;
import java.util.Date;

public class TokenRequestCreator {
    private SHA1RSASignatureMessage signatureMessage;
    private tokenRequestTemplate tokenRequestTemplate;

    public TokenRequestCreator(String bankNumber, KeyPair keyPair) {
        this.tokenRequestTemplate = new tokenRequestTemplate(bankNumber, new Date());
        this.signatureMessage = new SHA1RSASignatureMessage(this.tokenRequestTemplate);

        try {
            signatureMessage.sign(keyPair.getPrivate());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public tokenRequestTemplate getTokenRequestTemplate() {
        return this.tokenRequestTemplate;
    }

    public byte[] getSignature() {
        return this.signatureMessage.getSignature();
    }
}
