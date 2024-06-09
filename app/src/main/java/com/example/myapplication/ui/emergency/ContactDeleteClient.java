package com.example.myapplication.ui.emergency;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ContactDeleteClient {
    private static final String DELETE_CONTACT_URL = "http://100.96.1.3/api_delete_contact.php";

    public static String deleteContact(String contactId) throws Exception {
        URL url = new URL(DELETE_CONTACT_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        String postData = "contact_Id=" + contactId;

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = postData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return processResponse(connection);
    }

    private static String processResponse(HttpURLConnection connection) throws Exception {
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return "Delete operation successful.";
        } else {
            return "Delete operation failed with response code: " + responseCode;
        }
    }
}
