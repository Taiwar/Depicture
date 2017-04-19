package net.muellersites.depicture;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.muellersites.depicture.Objects.Lobby;
import net.muellersites.depicture.Tasks.UploadSelectionTask;

import org.json.JSONArray;
import org.json.JSONException;

public class SelectDescriptionActivity extends AppCompatActivity {

    LinearLayout buttonLayout;
    Lobby lobby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_description);
        buttonLayout = (LinearLayout) findViewById(R.id.button_container);

        lobby = (Lobby) getIntent().getSerializableExtra("lobby");
        try {
            JSONArray colors = new JSONArray(getIntent().getStringExtra("colors"));
            for (int i = 0, size = colors.length(); i < size; i++) {
                addDescriptionButton(colors.getString(i));
            }
        } catch (JSONException e) {
            Log.e("Dev", "Couldn't convert colors string to json");
            Log.e("Dev", e.toString(), e);
        }

    }

    private void addDescriptionButton(String color) {
        final Button b = (Button) LayoutInflater.from(this).inflate(R.layout.reference_button, null);
        b.setBackgroundColor(Color.parseColor(color));

        final String confirmation_question = getResources().getString(R.string.confirmation_question);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConfirmDialog(confirmation_question, b);
            }
        });

        buttonLayout.addView(b);
    }

    private void openConfirmDialog(String question, Button button) {
        final Dialog dialog = new Dialog(this);
        Integer color = ((ColorDrawable) button.getBackground()).getColor();
        final String hexColor = String.format("#%06X", (0xFFFFFF & color));
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
                new UploadSelectionTask("https://muellersites.net/api/upload_selection/" + lobby.getTempUser().getId() + "/").execute(hexColor);
                SelectDescriptionActivity.this.finish();
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
