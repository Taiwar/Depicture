package net.muellersites.depicture.Tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;

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
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateLobbyTask extends AsyncTask<User, Void, Lobby> {

    private String url;

    public CreateLobbyTask(String url) {
        this.url = url;
    }

    @Override
    protected Lobby doInBackground(User... params) {
        byte[] bytes;
        Lobby new_lobby = new Lobby();
        new_lobby.setOwner(params[0].getName());
        Log.d("Dev", "connecting to " + url);
        Log.d("dev", "with token: " + params[0].getToken());

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "JWT " + params[0].getToken())
                .build();


        Log.d("Dev", "Built request");
        try {
            Response response = client.newCall(request).execute();
            Log.d("Dev", "Successfully sent request");
            bytes =  response.body().bytes();
            response.close();
            if (bytes != null && bytes.length > 0) {
                Log.d("Dev", new String(bytes));
                JSONObject json = new JSONObject(new String(bytes));
                Log.e("Dev", "Successfully created lobby");
                Log.e("Dev", json.toString());
                new_lobby.setId(json.getInt("id"));
                new_lobby.setMessage(json.getString("message"));
                return new_lobby;
            }
        } catch (Exception e) {
            Log.e("Dev", "Pic Upload failed");
            Log.e("Dev", e.toString(), e);
        }
        return null;
    }

}
