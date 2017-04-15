package net.muellersites.depicture.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import net.muellersites.depicture.Objects.Lobby;
import net.muellersites.depicture.Objects.User;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class StartLobbyTask extends AsyncTask<User, Void, Void> {

    private String url;

    public StartLobbyTask(String url) {
        this.url = url;
    }

    @Override
    protected Void doInBackground(User... params) {
        byte[] bytes;
        User user = params[0];
        Log.d("Dev", "connecting to " + url);
        Log.d("dev", "with token: " + user.getToken());

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "JWT " + user.getToken())
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
                Log.e("Dev", "Successfully started lobby");
                Log.e("Dev", json.toString());
                if (!json.getBoolean("success")) {
                    throw new Exception("Failure during upload");
                }
            }
        } catch (Exception e) {
            Log.e("Dev", "Starting action failed");
            Log.e("Dev", e.toString(), e);
        }
        return null;
    }

}
