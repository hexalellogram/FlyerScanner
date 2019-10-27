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

    private void dispatchTakePictureIntent() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
        //chooser.putExtra(Intent.EXTRA_TITLE, getString(R.string.chooseaction));
        Intent[] intentArray = {cameraIntent};
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        startActivityForResult(chooser, RESULT_LOAD_IMAGE);
    }

    private File getFile() {
        File folder = Environment.getExternalStoragePublicDirectory("/From_camera/imagens");// the file path

        //if it doesn't exist the folder will be created
        if(!folder.exists())
        {folder.mkdir();}

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+ timeStamp + "_";
        File image_file = null;

        try {
            image_file = File.createTempFile(imageFileName,".jpg",folder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCurrentPhotoPath = image_file.getAbsolutePath();
        return image_file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // todo use appropriate resultCode in your case
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == FragmentActivity.RESULT_OK) {
            if (data.getData() != null) {
                // this case will occur in case of picking image from the Gallery,
                // but not when taking picture with a camera
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());

                    runTextRecognition(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {


                // this case will occur when taking a picture with a camera
                Bitmap bitmap = null;
                Cursor cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED,
                                MediaStore.Images.ImageColumns.ORIENTATION}, MediaStore.Images.Media.DATE_ADDED,
                        null, "date_added DESC");
                if (cursor != null && cursor.moveToFirst()) {
                    Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                    String photoPath = uri.toString();
                    cursor.close();
                    if (photoPath != null) {
                        bitmap = BitmapFactory.decodeFile(photoPath);
                    }
                }

                if (bitmap == null) {
                    // for safety reasons you can
                    // use thumbnail if not retrieved full sized image
                    bitmap = (Bitmap) data.getExtras().get("data");
                }
                runTextRecognition(bitmap);
                // do whatever you want with the Bitmap ....
            }

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_PATH,
                            LABEL_PATH,
                            INPUT_SIZE,
                            QUANT);
                    //makeButtonVisible();
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    private void runTextRecognition(Bitmap mSelectedImage) {
        ImageView img = findViewById(R.id.imageView);
        img.setImageBitmap(mSelectedImage);

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mSelectedImage);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        //mTextButton.setEnabled(false);
        recognizer.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                //mTextButton.setEnabled(true);
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                //mTextButton.setEnabled(true);
                                e.printStackTrace();
                            }
                        });
    }

    List<KeyPhrase> processTextRecognitionResult(FirebaseVisionText texts) {
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            showToast("No text found");
            return null;
        }
        String res = "";
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    FirebaseVisionText.Element temp = elements.get(k);
                    res += temp.getText() + " ";
                }
            }
        }
        TextView textViewToChange = (TextView) findViewById(R.id.hello);
        textViewToChange.setText(res);
        Log.v("Flyer-OCR", res);
        return NLP.NLP(res);

    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    public void requestPermissionForReadExtertalStorage()  {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("Flyer","Permission is granted1");
        } else {

            Log.v("Flyer","Permission is revoked1");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        }

        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("Flyer","Permission is granted2");
            dispatchTakePictureIntent();
        } else {

            Log.v("Flyer","Permission is revoked2");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }


}
