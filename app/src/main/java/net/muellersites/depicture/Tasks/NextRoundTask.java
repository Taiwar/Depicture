package net.muellersites.depicture.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import net.muellersites.depicture.Objects.Lobby;
import net.muellersites.depicture.Objects.User;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NextRoundTask extends AsyncTask<String, Void, Boolean> {

    private String url;

    public NextRoundTask(String url) {
        this.url = url;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        byte[] bytes;
        Log.d("Dev", "connecting to " + url);
        Log.d("dev", "with token: " + params[0]);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "JWT " + params[0])
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
                Log.e("Dev", "Successfully started next round");
                Log.e("Dev", json.toString());
                return json.getBoolean("success");
            }
        } catch (Exception e) {
            Log.e("Dev", "starting next round failed");
            Log.e("Dev", e.toString(), e);
        }
        return null;
    }

}
