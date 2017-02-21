package net.muellersites.depicture.Utils;

import android.util.Log;

import net.muellersites.depicture.Objects.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoginUser {

    public static User main(LoginData loginData, String server) throws IOException {
        User result_user = new User();
        URL url = new URL(server);
        Log.d("Dev", "connecting to: " + url.toString());
        String result = "";
        String data = "username=" + URLEncoder.encode(loginData.getName(), "UTF-8") + "&password=" + URLEncoder.encode(loginData.getPassword(), "UTF-8");
        Log.d("Dev", "With data: " + data);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            Log.d("Dev", "Trying to connect");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept", "application/json");

            Log.d("Dev", "Beginning read");
            DataOutputStream dataOut = new DataOutputStream(
                    connection.getOutputStream());
            dataOut.writeBytes(data);
            dataOut.flush();
            dataOut.close();

            BufferedReader in = null;
            try {
                String line;
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = in.readLine()) != null) {
                    result += line;
                }
            }catch (Exception exc){
                Log.e("Dev", "Exception during read: " + exc);
                throw exc;
            } finally {
                if (in != null) {
                    try {
                        Log.d("Dev", "finished reading, closing stream...");
                        in.close();
                    } catch (IOException ex) {
                        Log.e("Dev", "IOException while closing in: ");
                        ex.printStackTrace();
                    }

                }
            }

            Log.d("Dev", "Received data, decoding...");

            try {
                JSONObject json = new JSONObject(result);
                if (json.get("error") == null) {
                    result_user.setToken((String) json.get("token"));
                    Log.d("Dev", "Logged in without error!");
                } else {
                    Log.d("Dev", "Logged in with error:");
                    throw new IOException((String) json.get("error"));
                }
            } catch (JSONException ex) {
                Log.e("Dev", "Caught JSONException");
                ex.printStackTrace();
            }

            Log.d("Dev", "returning user: " + result_user.getName());
            return result_user;
        }catch (Exception ex){
            Log.e("Dev", "Error while connecting to server! " + ex);
            Log.e("Dev", ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }finally {
            connection.disconnect();
        }
    }
}
