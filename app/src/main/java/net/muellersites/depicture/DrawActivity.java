package net.muellersites.depicture;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import net.muellersites.depicture.Objects.Lobby;
import net.muellersites.depicture.Objects.User;
import net.muellersites.depicture.Utils.DBHelper;
import net.muellersites.depicture.Views.DrawView;

import me.priyesh.chroma.ChromaDialog;
import me.priyesh.chroma.ColorMode;
import me.priyesh.chroma.ColorSelectListener;

public class DrawActivity extends AppCompatActivity {

    private DrawView drawView;
    private User currUser;
    private Lobby lobby;
    private DrawActivity drawActivity = this;
    private int mColor;
    private static final String KEY_COLOR = "extra_color";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        lobby = (Lobby) getIntent().getSerializableExtra("lobby");

        //DBHelper dbHelper = new DBHelper(getApplicationContext());
        //currUser = dbHelper.getUser();

        drawView = new DrawView(this);
        drawView.setBackgroundColor(Color.WHITE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RelativeLayout content_draw = (RelativeLayout) findViewById(R.id.content_draw);
        content_draw.addView(drawView);

        mColor = savedInstanceState != null
                ? savedInstanceState.getInt(KEY_COLOR)
                : ContextCompat.getColor(this, R.color.colorPrimary);

        FloatingActionButton upload_fab = (FloatingActionButton) findViewById(R.id.fab_upload);
        upload_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openConfirmDialog("Are you sure?");
            }
        });

        FloatingActionButton color_fab = (FloatingActionButton) findViewById(R.id.fab_color_picker);
        color_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorPickerDialog();
            }
        });

        FloatingActionButton undo_fab = (FloatingActionButton) findViewById(R.id.fab_undo);
        undo_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.onClickUndo();
            }
        });

        FloatingActionButton redo_fab = (FloatingActionButton) findViewById(R.id.fab_redo);
        redo_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.onClickRedo();
            }
        });
    }

    private void showColorPickerDialog() {
        new ChromaDialog.Builder()
            .initialColor(mColor)
            .colorMode(ColorMode.RGB)
            .onColorSelected(new ColorSelectListener() {
                @Override public void onColorSelected(int color) {
                    drawView.changeColor(color);
                    mColor = color;
                }
            })
            .create()
            .show(getSupportFragmentManager(), "dialog");
    }

    private void openConfirmDialog(String question) {
        final Dialog dialog = new Dialog(this);
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
                drawView.saveCanvas(lobby.getTempUser());
                drawActivity.finish();
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
