package net.muellersites.depicture;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import net.muellersites.depicture.Objects.Lobby;
import net.muellersites.depicture.Objects.TempUser;
import net.muellersites.depicture.Views.DrawView;

import me.priyesh.chroma.ChromaDialog;
import me.priyesh.chroma.ColorMode;
import me.priyesh.chroma.ColorSelectListener;

public class DrawActivity extends AppCompatActivity {

    private DrawView drawView;
    private TempUser currUser;
    private Lobby lobby;
    private DrawActivity drawActivity = this;
    private int mColor;
    private int current_brush_width = 10;
    private static final String KEY_COLOR = "extra_color";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        lobby = (Lobby) getIntent().getSerializableExtra("lobby");
        String word = getIntent().getStringExtra("word");


        currUser= lobby.getTempUser();

        drawView = new DrawView(this);
        drawView.setBackgroundColor(Color.WHITE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final RelativeLayout content_draw = (RelativeLayout) findViewById(R.id.content_draw);
        content_draw.addView(drawView);

        mColor = savedInstanceState != null
                ? savedInstanceState.getInt(KEY_COLOR)
                : ContextCompat.getColor(this, R.color.colorPrimary);

        final String confirmation_question = getResources().getString(R.string.confirmation_question);

        FloatingActionButton upload_fab = (FloatingActionButton) findViewById(R.id.fab_upload);
        upload_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openConfirmDialog(confirmation_question);
            }
        });

        FloatingActionButton color_fab = (FloatingActionButton) findViewById(R.id.fab_color_picker);
        color_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorPickerDialog();
            }
        });

        FloatingActionButton width_fab = (FloatingActionButton) findViewById(R.id.fab_width_picker);
        width_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWidthDialog();
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

        TextView word_display = (TextView) findViewById(R.id.word_to_draw);
        word_display.setText(word);
        final TextView timer_digit = (TextView) findViewById(R.id.timer_digit);
        final ConstraintLayout fab_container = (ConstraintLayout) findViewById(R.id.fab_container);
        final ConstraintLayout waittimer_layout = (ConstraintLayout) findViewById(R.id.content_draw_waittimer);

        new CountDownTimer(6000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer_digit.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                timer_digit.setText("0");
                waittimer_layout.setVisibility(View.GONE);
                fab_container.setVisibility(View.VISIBLE);
                content_draw.setVisibility(View.VISIBLE);
            }

        }.start();
    }

    @Override
    public void onBackPressed() {
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

    private void showWidthDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.stroke_width_dialog);
        dialog.setCancelable(false);
        final TextView widthCounter = (TextView) dialog.findViewById(R.id.widthCounter);
        widthCounter.setText(String.valueOf(current_brush_width));
        Button set = (Button) dialog.findViewById(R.id.set_button);
        ImageButton exit = (ImageButton) dialog.findViewById(R.id.closeDialog);

        final SeekBar widthBar = (SeekBar) dialog.findViewById(R.id.width_seek_bar);
        widthBar.setMax(100);
        widthBar.setProgress(current_brush_width);
        widthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                widthCounter.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer width = widthBar.getProgress();
                current_brush_width = width;
                Log.d("Dev", "Width change to: " + width);

                dialog.dismiss();
                drawView.changeWidth(width);
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

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
                showProgress(true);
                try {
                    drawView.saveCanvas(getApplicationContext(), currUser);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.d("Dev", "Couldn't save Canvas");
                    Log.d("Dev", e.toString(), e);
                }
                showProgress(false);
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        drawView.setVisibility(show ? View.GONE : View.VISIBLE);
        drawView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                drawView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        final ProgressBar progressView = (ProgressBar) findViewById(R.id.pic_upload_progress);

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
