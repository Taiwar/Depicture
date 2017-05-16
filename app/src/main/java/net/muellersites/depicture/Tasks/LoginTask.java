package net.muellersites.depicture.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import net.muellersites.depicture.Objects.AsyncTaskResult;
import net.muellersites.depicture.Objects.LoginData;
import net.muellersites.depicture.Objects.User;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginTask extends AsyncTask<String, Void, AsyncTaskResult<User>> {

    private LoginData loginData;
    private Context context;

    public LoginTask(LoginData loginData, Context context) {
        this.loginData = loginData;
        this.context = context;
    }

    @Override
    protected AsyncTaskResult<User> doInBackground(String... params) {

        try {
            byte[] bytes;
            User result_user = new User();
            Log.d("Dev", "Auth: connecting to " + params[0]);

            OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("username", loginData.getName())
                    .addFormDataPart("password", loginData.getPassword())
                    .build();

            Request request = new Request.Builder()
                    .url(params[0])
                    .post(body)
                    .build();


            Log.d("Dev", "Built request");
            try {
                Response response = client.newCall(request).execute();
                bytes =  response.body().bytes();
                response.close();
                if (bytes != null && bytes.length > 0) {
                    JSONObject json = new JSONObject(new String(bytes));
                    Log.d("Dev", "Success");
                    Log.d("Dev", json.toString());
                    result_user.setName(loginData.getName());
                    result_user.setPassword(loginData.getPassword());
                    result_user.setToken(json.getString("token"));
                }
            } catch (Exception e) {
                Log.e("Dev", "User Auth failed");
                Log.e("Dev", e.toString(), e);
                throw e;
            }
            return new AsyncTaskResult<>(result_user);
        } catch (Exception e) {
            return new AsyncTaskResult<>(e);
        }
    }

    @Override
    protected void onPostExecute(final AsyncTaskResult result) {
        Toast toast;
        if (result.getError() != null || isCancelled()) {
            toast = Toast.makeText(context, "Successfully refreshed token", Toast.LENGTH_LONG);
        } else {
            toast = Toast.makeText(context, "Couldn't refresh token", Toast.LENGTH_LONG);
        }
        toast.show();
    }

}