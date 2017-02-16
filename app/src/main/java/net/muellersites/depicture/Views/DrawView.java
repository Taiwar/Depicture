package net.muellersites.depicture.Views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import net.muellersites.depicture.Objects.Point;

public class DrawView extends View implements OnTouchListener {
    List<Point> points = new ArrayList<>();
    Paint paint = new Paint();

    public DrawView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        paint.setColor(Color.BLACK);
    }

    @Override
    public void onDraw(Canvas canvas) {
        for (Point point : points) {
            canvas.drawCircle(point.getX(), point.getY(), 2, paint);
        }
    }

    public boolean onTouch(View view, MotionEvent event) {
        Point point = new Point();
        point.setX(event.getX());
        point.setY(event.getY());
        points.add(point);
        invalidate();
        return true;
    }
}

