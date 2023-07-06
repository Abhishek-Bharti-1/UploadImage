package com.example.uploadimage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 1;
    private static final int REQUEST_IMAGE_PICK = 5;

    private static Uri uritext ;

    private ImageView imageView;
    private EditText etName, etFatherName, etMotherName, etLocation;
    private Button btnUpload;
    private Button btnUploadImg;

    private UploadManager uploadManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.name);
        etFatherName = findViewById(R.id.fname);
        etMotherName = findViewById(R.id.mname);
        etLocation = findViewById(R.id.loca);
        btnUpload = findViewById(R.id.button);
        btnUploadImg =findViewById(R.id.uploadImg) ;

        imageView = findViewById(R.id.imageView);
        uploadManager = new UploadManager(this);


        Glide.with(this).load("https://displaypic.s3.ap-south-1.amazonaws.com/a51dede2-9a7d-4991-9d0c-3128088985fa").into(imageView);

        btnUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseNuploadImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
                    System.out.println("Your Android version is 13.");
                    checkPermission(Manifest.permission.READ_MEDIA_IMAGES, REQUEST_PERMISSION);
                } else {
                    System.out.println("Your Android version is not 13.");
                    checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION);
                }
            }
        });
    }

    private void chooseNuploadImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }


    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
            uploadFormData();
        }
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            if (data != null) {
                Uri imageUri = data.getData();
                uritext=imageUri;
            }
        }
    }


    private void uploadFormData() {
        String name = etName.getText().toString().trim();
        String fatherName = etFatherName.getText().toString().trim();
        String motherName = etMotherName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        // Replace with your server URL
        //String url = "http://testingimg-env.eba-2gxmkfcj.ap-south-1.elasticbeanstalk.com/user";
String url = "http://testingimg-env.eba-2gxmkfcj.ap-south-1.elasticbeanstalk.com/api/users";
        // Replace with the actual image file path on the device
//        File imageFile = new File("/path/to/image.jpg");


        String imagePath = getImageFilePath(uritext); // Pass the selected image URI here

        Log.w("IMAGE Path",imagePath);

        if (imagePath != null) {
            File imageFile = new File(imagePath);
            uploadManager.uploadFormData(url, name, fatherName, motherName, location, imageFile);
            System.out.println("Uploaded");
        } else {
            Toast.makeText(this, "Failed to get the image file path", Toast.LENGTH_SHORT).show();
        }

    }

    private String getImageFilePath(Uri uri) {
        String imagePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
                imagePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return imagePath;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Storage permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
