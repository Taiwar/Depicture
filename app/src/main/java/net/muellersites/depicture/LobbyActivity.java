package net.muellersites.depicture;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.muellersites.depicture.Objects.Lobby;


public class LobbyActivity extends AppCompatActivity {


    private Lobby lobby = new Lobby();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lobby);

        TextView mContentView = (TextView) findViewById(R.id.message);
        TextView idView = (TextView) findViewById(R.id.ID);

        lobby = (Lobby) getIntent().getSerializableExtra("lobby");

        mContentView.setText(lobby.getMessage());
        idView.setText(String.valueOf(lobby.getId()));

        Button drawButton = (Button) findViewById(R.id.draw_button);
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LobbyActivity.this, DrawActivity.class);
                intent.putExtra("lobby", lobby);
                startActivity(intent);
            }
        });
    }

}
