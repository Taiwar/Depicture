package net.muellersites.depicture.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import net.muellersites.depicture.Objects.Lobby;
import net.muellersites.depicture.Objects.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class JoinLobbyTask extends AsyncTask<String, Void, Lobby> {
    private Exception exception;

    private String server_url;

    public JoinLobbyTask(String server_url){
        this.server_url = server_url;
    }

    protected Lobby doInBackground(String... strings) {
        URL url;
        Lobby new_lobby = new Lobby();
        try {
            url = new URL(server_url);
        } catch (MalformedURLException e) {
            Log.d("Dev", server_url + " is not a valid url!");
            exception = e;
            return null;
        }
        HttpURLConnection connection = null;
        String result = "";
        String data = null;
        try {
            data = "username=" + URLEncoder.encode(strings[0], "UTF-8");
            Log.d("Dev", "Sending data: " + data);
        } catch (UnsupportedEncodingException e) {
            exception = e;
        }
        try {
            Log.d("Dev", "Trying to connect");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept", "application/json");

            Log.d("Dev", "Beginning write");
            DataOutputStream dataOut = new DataOutputStream(
                    connection.getOutputStream());
            dataOut.writeBytes(data);
            dataOut.flush();
            dataOut.close();

            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            Log.d("Dev", "Beginning read");

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
                    exception = ex;
                }
            }

            Log.d("Dev", "Received data, decoding...");

            try {
                JSONObject json = new JSONObject(result);
                new_lobby.setId((Integer) json.get("id"));
                new_lobby.setOwner((String) json.get("owner"));
                new_lobby.setMessage((String) json.get("message"));
            } catch (JSONException ex) {
                Log.e("Dev", "Caught JSONException" + ex);
                exception = ex;
            }
        }catch (Exception ex){
            Log.e("Dev", "Error while connecting to server! " + ex);
            Log.e("Dev", ex.getMessage());
            exception = ex;
        }finally {
            assert connection != null;
            connection.disconnect();
        }
        return new_lobby;
    }

    @Override
    protected void onPostExecute(final Lobby new_lobby) {
        if(this.exception == null){
            Log.d("Dev", "Upload finished without any errors");
        }else {
            Log.d("Dev", "Upload failed with following exception: " + exception);
            exception.printStackTrace();
        }
    }
}
