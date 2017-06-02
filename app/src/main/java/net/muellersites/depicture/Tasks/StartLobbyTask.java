package net.muellersites.depicture.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import net.muellersites.depicture.Objects.User;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StartLobbyTask extends AsyncTask<User, Void, Boolean> {

    private String url;

    public StartLobbyTask(String url) {
        this.url = url;
    }

    @Override
    protected Boolean doInBackground(User... params) {
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
                return json.getBoolean("success");
            }
        } catch (Exception e) {
            Log.e("Dev", "Starting action failed");
            Log.e("Dev", e.toString(), e);
        }
        return false;
    }

}
