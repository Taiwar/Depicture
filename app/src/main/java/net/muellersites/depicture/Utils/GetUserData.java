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
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetUserData {

    public static User main(User user, String server_url) throws IOException, JSONException {
        byte[] bytes;
        Log.d("Dev", "GUD: connecting to " + server_url);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(server_url)
                .header("Authorization", "JWT " + user.getToken())
                .build();


        Log.d("Dev", "Built request");
        try {
            Response response = client.newCall(request).execute();
            bytes =  response.body().bytes();
            response.close();
            if (bytes != null && bytes.length > 0) {
                Log.d("Dev", new String(bytes));
                JSONObject json = new JSONObject(new String(bytes));
                user.setEmail((String) json.get("email"));
                Log.d("Dev", "Got user data: " + user.getEmail());
            }
        } catch (Exception e) {
            Log.e("Dev", "Getting user data failed");
            Log.e("Dev", e.toString(), e);
            throw e;
        }
        return user;
    }
}
