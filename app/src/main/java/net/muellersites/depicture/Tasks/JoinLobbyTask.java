package net.muellersites.depicture.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import net.muellersites.depicture.Objects.Lobby;
import net.muellersites.depicture.Objects.TempUser;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JoinLobbyTask extends AsyncTask<Lobby, Void, Lobby> {

    private String url;

    public JoinLobbyTask(String url) {
        this.url = url;
    }

    @Override
    protected Lobby doInBackground(Lobby... params) {
        byte[] bytes;
        Lobby new_lobby = params[0];
        new_lobby.setIsOwner(false);
        TempUser tempUser = new_lobby.getTempUser();
        Log.d("Dev", "connecting to " + url);
        Log.d("Dev", "with username: " + new_lobby.getTempUser().getName() + " and instance_id: " + new_lobby.getTempUser().getInstanceID());

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", tempUser.getName())
                .addFormDataPart("instance_id", tempUser.getInstanceID())
                .build();

        Request request = new Request.Builder()
                .url(url)
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
                Log.e("Dev", "Successfully joined lobby");
                Log.e("Dev", json.toString());
                new_lobby.setId(json.getString("lobby_id"));
                new_lobby.setOwner(json.getString("owner"));
                new_lobby.getTempUser().setId(json.getString("player_id"));
                new_lobby.setMessage(json.getString("message"));
                return new_lobby;
            }
        } catch (Exception e) {
            Log.e("Dev", "JoinLobby failed", e);
        }
        return null;
    }
}
