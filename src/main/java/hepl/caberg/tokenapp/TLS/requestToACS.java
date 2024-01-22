package hepl.caberg.tokenapp.TLS;

import org.springframework.stereotype.Component;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;

@Component
public class requestToACS {
    public requestToACS() {
        System.setProperty("javax.net.ssl.keyStore", "tokenApp.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "heplPass");

        System.setProperty("javax.net.ssl.trustStore", "acs.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "heplPass");
    }

    public String requestToACS(Object object, byte[] signature) throws IOException {
        SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket sslSocketForACS = (SSLSocket) sslsocketfactory.createSocket("localhost", 6666);

        var writer = new ObjectOutputStream(sslSocketForACS.getOutputStream());

        var reader = GetBufferedReader(sslSocketForACS);

        writer.writeObject(object);
        writer.flush();

        var answer = reader.readLine();

        if(answer.equals("ACK")) {
            System.out.println("Datas are valid");
            var token = reader.readLine();
            System.out.println("Token is: " + token);

            return token;
        }
        else {
            System.out.println("Datas are invalid");
            return null;
        }
    }

    private BufferedWriter GetBufferedWriter(SSLSocket sslsocket) throws IOException {
        OutputStream outputstream = sslsocket.getOutputStream();
        BufferedWriter bufferedwriter = new BufferedWriter(new OutputStreamWriter(outputstream));
        return bufferedwriter;
    }

    private BufferedReader GetBufferedReader(SSLSocket sslsocket) throws IOException {
        InputStream inputstream = sslsocket.getInputStream();
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
        return bufferedreader;
    }
}
