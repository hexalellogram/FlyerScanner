package com.sdhacks.flyerscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.camerakit.CameraKitView;

public class MainActivity extends AppCompatActivity {

    private CameraKitView cameraKitView;
    private Button photoButton;
    private Button listButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        constraintLayout.setBackgroundColor(Color.BLACK);

        cameraKitView = findViewById(R.id.camera);
        photoButton = findViewById(R.id.photoButton);
        listButton = findViewById(R.id.listButton);

        photoButton.setOnClickListener(photoOnClickListener);
    }

    private View.OnClickListener photoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                @Override
                public void onImage(CameraKitView view, final byte[] photo) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                Intent confirmIntent = new Intent(getApplicationContext(), ConfirmActivity.class);
                                confirmIntent.putExtra(getResources().getString(R.string.image_bytes), photo);
                                startActivity(confirmIntent);
                                // TODO handle the photo being taken

                                /*final Jpeg jpeg = new Jpeg(photo);
                                imageView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setJpeg(jpeg);
                                    }
                                });*/
                                //Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
                                //imageView.setImageBitmap(bitmap);
                                /*String path = getFilesDir().getAbsolutePath() + "//photo.jpg";
                                File file = new File(path);
                                if(file.exists()) {
                                    file.delete();
                                }
                                FileOutputStream stream = new FileOutputStream(path);
                                stream.write(photo);
                                Bitmap bitmap = BitmapFactory.decodeFile(path);
                                imageView.setImageBitmap(bitmap);*/
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onListButtonClicked(View view) {
        Intent listIntent = new Intent(this, ListActivity.class);
        startActivity(listIntent);
    }
}
