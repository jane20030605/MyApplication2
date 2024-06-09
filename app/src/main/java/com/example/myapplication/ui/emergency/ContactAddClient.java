package com.example.myapplication.ui.emergency;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ContactAddClient {
    private static final String ADD_CONTACT_URL = "http://100.96.1.3/api_add_contact.php";

    public static String addContact(String contactName, String contactTel, String relation, String accountId) throws Exception {
        URL url = new URL(ADD_CONTACT_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        String postData = "contact_name=" + contactName +
                "&contact_tel=" + contactTel +
                "&relation=" + relation +
                "&account=" + accountId;

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = postData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return processResponse(connection);
    }

    private static String processResponse(HttpURLConnection connection) throws Exception {
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return "Add operation successful.";
        } else {
            return "Add operation failed with response code: " + responseCode;
        }
    }
}

