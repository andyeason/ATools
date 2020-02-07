package com.andyeason.atool;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.andyeason.atools.oss.MyOSSUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {

    String[] mPermissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    public static final int REQUEST_PICK_IMAGE = 8001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(MainActivity.this, mPermissionList, 8002);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 8002:
                boolean writeExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (grantResults.length > 0 && writeExternalStorage && readExternalStorage) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
                                REQUEST_PICK_IMAGE);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(intent, REQUEST_PICK_IMAGE);
                    }
                } else {
                    Toast.makeText(this, "请设置必要权限", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        switch (requestCode) {

            case REQUEST_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    final Uri uri = data.getData();
                    if (uri == null) {
                        return;
                    }
                    String localFilePath = Tools.getRealPathFromUri(MainActivity.this, uri);
                    MyOSSUtils.getInstance().uploadFile(this, new File(localFilePath).getName(), localFilePath, new MyOSSUtils.OssUpCallback() {
                        @Override
                        public void UploadSuccess(String ossUrl) {
                            Log.e("Andy", ossUrl);
                        }

                        @Override
                        public void UploadFail() {
                            Log.e("Andy", "fail");
                        }

                        @Override
                        public void inProgress(long curProgress, long totalSize) {
                            Log.e("Andy", "curProgress" + curProgress + ",totalSize" + totalSize);
                        }
                    });


                }
                break;
            default:
                break;
        }

    }
}
