package net.muellersites.depicture.Tasks;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RefreshTokenTask extends AsyncTask<String, Void, String> {

    private final String token;

    public RefreshTokenTask(String token) {
        this.token = token;
    }

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(String... params) {
        byte[] bytes;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject().put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assert jsonObject != null;

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request.Builder builder = new Request.Builder();
        builder
                .url(params[0])
                .post(body)
                .build();

        Request request = builder.build();
        Log.d("Dev", "Built request");
        try {
            Response response = client.newCall(request).execute();
            bytes =  response.body().bytes();
            if (bytes != null && bytes.length > 0) {
                JSONObject json = new JSONObject(new String(bytes));
                Log.e("Dev", "Successfully refreshed token");
                Log.e("Dev", json.toString());
                return json.getString("token");
            }
        } catch (Exception e) {
            Log.e("Dev", "Token refreshment failed");
            Log.e("Dev", e.getMessage());
        }
        return null;
    }

}
