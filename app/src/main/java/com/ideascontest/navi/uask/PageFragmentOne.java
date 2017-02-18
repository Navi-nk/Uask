package com.ideascontest.navi.uask;


import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by Navi on 17-02-2017.
 */

public class PageFragmentOne extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    //ScaleGestureDetector SGD;
    //Matrix matrix;
    //private int mPage;
     MyImageView imageView;
    //float sf=1f;

    public static PageFragmentOne newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragmentOne fragment = new PageFragmentOne();
        fragment.setArguments(args);
        return fragment;
    }

    public PageFragmentOne() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.kentridge_layout, container, false);
     /*   imageView = (MyImageView) view.findViewById(R.id.mapOne);
        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inJustDecodeBounds = true;
        //options.inScaled = false;
        //options.inSampleSize = 10;
        Bitmap bm = BitmapFactory.decodeResource(view.getContext().getResources(),R.drawable.kentridge_map_50_1 , options);
        int nh = (int) ( bm.getHeight() * (512.0 / bm.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(bm, 512, nh, true);
        imageView.setImageBitmap(bm);
     //   BitmapFactory.Options opts = new BitmapFactory.Options();*/
       // opts.inSampleSize = ;
       // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.id.mapOne, opts);
  //      imageView.setImageBitmap (decodeSampledBitmapFromResource(view.getContext().getResources(),R.id.mapOne,100,100));
       /*     imageView = (ImageView) view.findViewById(R.id.mapOne);
            matrix = new Matrix();
            SGD = new ScaleGestureDetector(view.getContext(),new ScaleListener());
        Log.d("oncreate","map1");

          view.setOnTouchListener(new View.OnTouchListener() {
              @Override
              public boolean onTouch(View view, MotionEvent motionEvent) {
                  SGD.onTouchEvent(motionEvent);
            //      Log.d("ontouch","map1");
                  return true;
              }
          });
*/

       /* WindowManager wm = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        Bitmap bm = drawableToBitmap(ContextCompat.getDrawable(view.getContext(), R.drawable.kentridge_map));
        Bitmap bitmapsimplesize = Bitmap.createScaledBitmap(bm, width, height, true);
        bm.recycle();
        ImageView imageView = (ImageView) view.findViewById(R.id.mapOne);
        imageView.setImageBitmap(bitmapsimplesize);
        //ImageView imageView = (ImageView) view.findViewById(R.id.mapOne);
        //((BitmapDrawable)imageView.getDrawable()).getBitmap().recycle();
        // imageView.setImageResource(R.drawable.kentridge_map);*/

        return view;
    }
/*
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d("h:",Integer.toString(height));
        Log.d("w:",Integer.toString(width));

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }*/
    /*public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }*/

 /*   private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.d("scale listener","mapone");
            sf= sf*detector.getScaleFactor();
            sf=Math.max(.5f,Math.min(sf,2.0f));
            matrix.setScale(sf,sf);
            imageView.setImageMatrix(matrix);
            return true;
        }
    }*/
}