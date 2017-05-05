package net.muellersites.depicture.Views;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import net.muellersites.depicture.Objects.TempUser;
import net.muellersites.depicture.Tasks.UploadPicTask;
import net.muellersites.depicture.Tasks.UploadUrlTask;


public class DrawView extends View implements OnTouchListener {


    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private LinkedList<Path> paths = new LinkedList<>();
    private LinkedList<Path> undone_paths = new LinkedList<>();
    private Map<Path, Integer> colorsMap = new HashMap<>();
    private Map<Path, Float> widthMap = new HashMap<>();
    private Bitmap bitmap;
    private int selectedColor = Color.BLACK;
    private float selectedWidth = 10f;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);

        this.setOnTouchListener(this);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(selectedColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(selectedWidth);
        mCanvas = new Canvas(bitmap);
        mPath = new Path();
        paths.add(mPath);
        colorsMap.put(mPath, selectedColor);
        widthMap.put(mPath, selectedWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPaint != null) {
            for (Path p : paths) {
                if (colorsMap.get(p) != null) {
                    mPaint.setColor(colorsMap.get(p));
                    mPaint.setStrokeWidth(widthMap.get(p));
                    canvas.drawPath(p, mPaint);
                }
            }
            mPaint.setColor(selectedColor);
            mPaint.setStrokeWidth(selectedWidth);
            canvas.drawPath(mPath, mPaint);
        }
    }

    public void changeColor(int color) {
        this.selectedColor = color;
    }

    public void changeWidth(float width) {
        this.selectedWidth = width;
    }

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        widthMap.put(mPath, selectedWidth);
        colorsMap.put(mPath, selectedColor);
        // kill this so we don't double draw
        mPath = new Path();
        paths.add(mPath);
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    public void onClickUndo () {
        if (paths.size() > 1) {
            Log.d("Dev", "Got " + paths.size() + " paths, removing " + (paths.size() - 2));
            undone_paths.add(paths.remove(paths.size() - 2));
            invalidate();
        }
    }

    public void onClickRedo (){
        if (undone_paths.size() > 0) {
            Log.d("Dev", "Got " + undone_paths.size() + " undone paths, adding " + (undone_paths.size() - 1) + " to " + paths.size() + " paths");
            paths.add(undone_paths.remove(undone_paths.size() - 1));
            invalidate();
        }
    }

    public File saveCanvas(Context context, TempUser tempUser) throws PackageManager.NameNotFoundException {
        Bitmap  bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);

        String app_dir = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.dataDir;

        File file = new File(app_dir, "drawing.jpg");

        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, new FileOutputStream(file));
            String file_url = new UploadPicTask(file).execute().get();
            new UploadUrlTask(file_url).execute("https://muellersites.net/api/upload_file/" + tempUser.getId() + "/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

}