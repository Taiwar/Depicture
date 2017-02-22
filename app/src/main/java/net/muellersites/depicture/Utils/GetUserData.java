package net.muellersites.depicture.Utils;

import android.util.Log;

import net.muellersites.depicture.Objects.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetUserData {

    public static User main(User user, String server) throws IOException {
        URL url = new URL(server);
        Log.d("Dev", "connecting to: " + url.toString());
        String result = "";
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            Log.d("Dev", "Trying to connect with token: " + user.getToken());
            connection.setDoInput(true);
            //connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty ("Authorization", "JWT " + user.getToken());
            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //connection.setRequestProperty("Accept", "application/json");

            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    result += line;
                }
            }catch (Exception exc){
                Log.e("Dev", "Exception during read: " + exc);
                throw exc;
            } finally {
                try {
                    Log.d("Dev", "finished reading, closing stream...");
                    in.close();
                } catch (IOException ex) {
                    Log.e("Dev", "IOException while closing in: " + ex);
                    ex.printStackTrace();
                }
            }

            Log.d("Dev", "Received data, decoding...");

            try {
                JSONObject json = new JSONObject(result);
                user.setEmail((String) json.get("email"));
            } catch (JSONException ex) {
                Log.e("Dev", "Caught JSONException" + ex);
                ex.printStackTrace();
            }

            return user;
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
