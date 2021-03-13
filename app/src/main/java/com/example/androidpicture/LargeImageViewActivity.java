package com.example.androidpicture;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.androidpicture.databinding.ActivityLargeImageViewBinding;

public class LargeImageViewActivity extends AppCompatActivity {
    private ActivityLargeImageViewBinding mBinding;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor;

    private static final String EXTRA_FILE_PATH = "com.example.androidpicture.filePath";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_large_image_view);
        /*mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        mScaleFactor = 1.0f;
*/
        String filePath = getIntent().getStringExtra(EXTRA_FILE_PATH);
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        mBinding.imgPicture.setImageBitmap(bitmap);
    }

   /* @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= mScaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            mBinding.imgPicture.setScaleX(mScaleFactor);
            mBinding.imgPicture.setScaleY(mScaleFactor);
            return true;
        }
    }*/

    public static Intent newIntent(Context context, String filePath) {
        Intent intent = new Intent(context, LargeImageViewActivity.class);
        intent.putExtra(EXTRA_FILE_PATH, filePath);
        return intent;
    }
}