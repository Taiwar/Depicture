package net.muellersites.depicture.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import net.muellersites.depicture.Objects.Lobby;
import net.muellersites.depicture.Objects.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CreateLobbyTask extends AsyncTask<User, Void, Lobby> {
    private Exception exception;

    private String server_url;

    public CreateLobbyTask(String server_url){
        this.server_url = server_url;
    }

    protected Lobby doInBackground(User... users) {
        URL url;
        Lobby new_lobby = new Lobby();
        new_lobby.setOwner(users[0].getName());
        try {
            url = new URL(server_url);
        } catch (MalformedURLException e) {
            Log.d("Dev", server_url + " is not a valid url!");
            exception = e;
            return null;
        }
        HttpURLConnection connection = null;
        String result = "";
        try {
            Log.d("Dev", "Trying to connect with token: " + users[0].getToken());
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty ("Authorization", "JWT " + users[0].getToken());

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
                    exception = ex;
                }
            }

            Log.d("Dev", "Received data, decoding...");

            try {
                JSONObject json = new JSONObject(result);
                new_lobby.setId((Integer) json.get("id"));
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
