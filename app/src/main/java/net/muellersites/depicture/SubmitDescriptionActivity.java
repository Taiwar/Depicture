package net.muellersites.depicture;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.muellersites.depicture.Objects.Lobby;
import net.muellersites.depicture.Objects.TempUser;
import net.muellersites.depicture.Tasks.UploadDescriptionTask;

public class SubmitDescriptionActivity extends AppCompatActivity {

    private Lobby lobby;
    private EditText descriptionField;
    private TextView infoText;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_description);

        lobby = (Lobby) getIntent().getSerializableExtra("lobby");

        infoText = (TextView) findViewById(R.id.description_info_text);
        descriptionField = (EditText) findViewById(R.id.description_field);
        submitButton = (Button) findViewById(R.id.submit_button);

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
                showProgress(true);
                dialog.dismiss();
                new UploadDescriptionTask("https://muellersites.net/api/upload_description/" + lobby.getTempUser().getId() + "/").execute(description);
                showProgress(false);
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        final ViewGroup viewGroup = null;
        viewGroup.addView(descriptionField);
        viewGroup.addView(infoText);
        viewGroup.addView(submitButton);

        viewGroup.setVisibility(show ? View.GONE : View.VISIBLE);
        viewGroup.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                viewGroup.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        final ProgressBar progressView = (ProgressBar) findViewById(R.id.description_upload_progress);

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
