package com.example.androidpicture;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.androidpicture.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    private File mPictureFile;
    private List<String> mFilePaths;

    private static final int REQUEST_CODE_PERMISSION = 0;
    private static final int REQUEST_CODE_TAKE_PICTURE = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String AUTHORITIES = "com.example.androidpicture.fileprovider";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mFilePaths = new ArrayList<>();

        initRecyclerView();

        mBinding.btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasPermission()) {
                    createFileAndTakePicture();

                } else {
                    requestPermission();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_TAKE_PICTURE & resultCode == Activity.RESULT_OK) {
            Uri androidPictureUri = FileProvider.getUriForFile(
                    MainActivity.this, AUTHORITIES, mPictureFile);
            revokeUriPermission(androidPictureUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            mFilePaths.add(mPictureFile.getPath());
            setupAdapter(mFilePaths);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    createFileAndTakePicture();
                }
        }
    }

    private boolean hasPermission() {
        boolean hasWriteExternalStoragePermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        boolean hasCameraPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        return hasWriteExternalStoragePermission & hasCameraPermission;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                REQUEST_CODE_PERMISSION);
    }

    private void createFileAndTakePicture() {
        File file = new File(
                Environment.getExternalStorageDirectory(),
                "AndroidPicture");
        if (!file.exists()) {
            file.mkdirs();
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                mPictureFile = File.createTempFile(
                        "img_" + new Date().getTime(),
                        ".jpg",
                        file);

                if (mPictureFile != null) {
                    Uri androidPictureUri = FileProvider.getUriForFile(
                            MainActivity.this, AUTHORITIES, mPictureFile);

                    List<ResolveInfo> activities = getPackageManager()
                            .queryIntentActivities(
                                    intent,
                                    PackageManager.MATCH_DEFAULT_ONLY);

                    for (ResolveInfo activity : activities) {
                        grantUriPermission(
                                activity.activityInfo.packageName,
                                androidPictureUri,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, androidPictureUri);
                    startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);

                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    private void initRecyclerView() {
        mBinding.recyclerViewPictures.setLayoutManager(new GridLayoutManager(this, 3));
    }

    private void setupAdapter(List<String> filePaths) {
        PictureAdapter adapter = new PictureAdapter(this, filePaths);
        mBinding.recyclerViewPictures.setAdapter(adapter);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }
}