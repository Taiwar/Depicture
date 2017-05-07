package net.muellersites.depicture;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.muellersites.depicture.Objects.Lobby;
import net.muellersites.depicture.Objects.TempUser;
import net.muellersites.depicture.Tasks.UploadDescriptionTask;

public class SubmitDescriptionActivity extends AppCompatActivity {

    private Lobby lobby;
    private EditText descriptionField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_description);

        lobby = (Lobby) getIntent().getSerializableExtra("lobby");

        descriptionField = (EditText) findViewById(R.id.description_field);
        Button submitButton = (Button) findViewById(R.id.submit_button);

        final String confirmation_question = getResources().getString(R.string.confirmation_question);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConfirmDialog(confirmation_question);
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    private void openConfirmDialog(String question) {
        final Dialog dialog = new Dialog(this);
        final String description = descriptionField.getText().toString();
        dialog.setContentView(R.layout.confirm_dialog);
        dialog.setCancelable(false);
        TextView questionView = (TextView) dialog.findViewById(R.id.confirm_question);
        questionView.setText(question);
        Button yes = (Button) dialog.findViewById(R.id.yes_button);
        Button no = (Button) dialog.findViewById(R.id.no_button);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Dev", "Save");
                dialog.dismiss();
                new UploadDescriptionTask("https://muellersites.net/api/upload_description/" + lobby.getTempUser().getId() + "/").execute(description);
                SubmitDescriptionActivity.this.finish();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
