package net.muellersites.depicture.Tasks;


import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.widget.Toast;

import net.muellersites.depicture.MainActivity;
import net.muellersites.depicture.Objects.AsyncTaskResult;
import net.muellersites.depicture.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RefreshTokenTask extends AsyncTask<String, Void, AsyncTaskResult<String>> {

    private final String token;
    private MainActivity activity;

    public RefreshTokenTask(String token, MainActivity activity) {
        this.token = token;
        this.activity = activity;
    }

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();

    @Override
    protected AsyncTaskResult<String> doInBackground(String... params) {
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
                Log.d("Dev", "Got response");
                Log.d("Dev", json.toString());
                if (json.has("non_field_errors") || json.getString("token").equals("This field is required.")) {
                    Log.e("Dev", "Server returned error " + new JSONArray(json.getString("token")).getString(0));
                    throw new Exception("Server error, either signature expired or no token was sent");
                }
                Log.d("Dev", "Successfully refreshed token");
                return new AsyncTaskResult<>(json.getString("token"));
            }
        } catch (Exception anyError) {
            Log.e("Dev", "Token refreshment failed", anyError);
            return new AsyncTaskResult<>(anyError);
        }
        return null;
    }

    protected void onPostExecute(AsyncTaskResult<String> result){
        if (result.getError() != null || isCancelled()) {
            activity.handleLogout();
        } else {
            Toast toast = Toast.makeText(activity, "Successfully refreshed token", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
