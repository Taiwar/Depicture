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

public class CreateLobbyTask extends AsyncTask<User, Void, Lobby> {

    private String url;
    private Integer word_list_id;

    public CreateLobbyTask(String url, Integer word_list_id) {
        this.url = url;
        this.word_list_id = word_list_id;
    }

    @Override
    protected Lobby doInBackground(User... params) {
        byte[] bytes;
        Lobby new_lobby = new Lobby();
        User user = params[0];
        new_lobby.setTempUser(user);
        new_lobby.setIsOwner(true);
        new_lobby.setOwner(params[0].getName());
        Log.d("Dev", "connecting to " + url + "; choosing word list: " + word_list_id);
        Log.d("dev", "with token: " + params[0].getToken() + " and instance_id: " + user.getInstanceID());

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("instance_id", user.getInstanceID())
                .addFormDataPart("word_list_id", String.valueOf(word_list_id))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "JWT " + params[0].getToken())
                .post(body)
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
                new_lobby.setId(json.getString("id"));
                new_lobby.setMessage(json.getString("message"));
                new_lobby.getTempUser().setId(json.getString("player_id"));
                Log.d("Dev", "Task returning Lobby with isOwner: " + new_lobby.getIsOwner());
                return new_lobby;
            }
        } catch (Exception e) {
            Log.e("Dev", "Lobby creation failed");
            Log.e("Dev", e.toString(), e);
        }
        return null;
    }

}
