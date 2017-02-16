package net.muellersites.kritzel;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import net.muellersites.kritzel.Views.DrawView;

public class Scribbler extends Activity {
    DrawView drawView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawView = new DrawView(this);
        drawView.setBackgroundColor(Color.WHITE);
        setContentView(drawView);
        drawView.requestFocus();

    }
}