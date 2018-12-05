package paypal;

import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * Created by kumbhak on 12/4/2018.
 */


@Path("/myapi")
public class myapi {

    public myapi(){}

    String CLIENT =
            "ASTjM_seQ0yYPK9tmh-0ZI7jTiYFdoOa6UhlbGnQJSaNtb_Mq4d8Rpapz_tXmtRKGKfDCAkzyGx0e9xE";
    String SECRET =
            "ELjeO4XvZ8h4vpNvfosqplpp2jkG4fOeK4lDYEOSIE3n92GyLNN1TZnSCLswky1pJaqeGkXf9lJ9K5aE";

    String PAYPAL_SERVER = "https://api.sandbox.paypal.com";
    String ACCESS_TOKEN_URL = PAYPAL_SERVER+"/v1/oauth2/token";
    String PAYMENT_URL =  PAYPAL_SERVER+"/v1/payments/payment";
    String BN_CODE="PP-DemoPortal-EC-JSV4-java-REST";

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }

    @POST
    @Path("/createpayment")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.TEXT_PLAIN)
    public String createpayment() throws IOException, URISyntaxException {

        java.net.URL url = new java.net.URL(PAYMENT_URL);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        String line;
        String access_token=getAccessToken();
        String requestBody = readFile();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + access_token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setUseCaches(false);
        conn.setDoOutput(true);

        java.io.DataOutputStream output = new java.io.DataOutputStream(conn.getOutputStream());
        output.write(requestBody.getBytes());
        output.close();

        // Read the response:
        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
        JSONObject jsonObj = null;

        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            jsonObj = new JSONObject(line);

        }
        reader.close();
        return jsonObj.toString();
    }

    public String getAccessToken() throws IOException {

        java.net.URL url = new java.net.URL(ACCESS_TOKEN_URL);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        String line;
        String access_token=null;
        String up = CLIENT + ":" + SECRET;
        String tokenInput = "grant_type=client_credentials";

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Basic " + new String(new org.apache.commons.codec.binary.Base64().encode(up.getBytes())));
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Accept-Language", "en_US");
        conn.setUseCaches(false);
        conn.setDoOutput(true);
        java.io.DataOutputStream output = new java.io.DataOutputStream(conn.getOutputStream());
        output.write(tokenInput.getBytes());
        output.close();

        // Read the response:
        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));

        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            JSONObject jsonObj = new JSONObject(line);

            access_token = (String) jsonObj.get("access_token");
        }
        reader.close();

        System.out.println("Response code:" + conn.getResponseCode());
        System.out.println("Response message:" + conn.getResponseMessage());

        return access_token;
    }


    private String readFile() throws IOException, URISyntaxException {

        StringBuilder result = new StringBuilder("");

        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("request").getFile());

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();

    }



}
