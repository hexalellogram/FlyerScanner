package com.sdhacks.flyerscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.services.comprehend.model.KeyPhrase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.jpegkit.Jpeg;
import com.jpegkit.JpegImageView;
import java.util.List;

public class ConfirmActivity extends AppCompatActivity {

    private byte[] picture;
    private Button continueButton;
    private List<KeyPhrase> keyPhrases = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        picture = getIntent().getExtras().getByteArray(getResources().getString(R.string.image_bytes));
        continueButton = findViewById(R.id.continueButton);

        if(picture != null){
            final Jpeg jpeg = new Jpeg(picture);
            final JpegImageView imageView = findViewById(R.id.imageView);
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    imageView.setJpeg(jpeg);
                    runTextRecognition();
                }
            });
        }
    }

    public void onRetakeClick(View view) {
        Intent retakeIntent = new Intent(this, MainActivity.class);
        startActivity(retakeIntent);
    }

    public void onContinueClick(View view) {
        // Do something with strings and call next activity

        if(keyPhrases == null) {
            Toast.makeText(getApplicationContext(), "No text found", Toast.LENGTH_LONG).show();
        } else {
            finish();
        }
    }

    private void runTextRecognition() {
        Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        recognizer.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                keyPhrases = processTextRecognitionResult(texts);
                                continueButton.setText(getResources().getString(R.string.continue_button_label));
                                continueButton.setClickable(true);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                e.printStackTrace();
                            }
                        });
    }

    private List<KeyPhrase> processTextRecognitionResult(FirebaseVisionText texts) {
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            Log.d("Block size", "block size is 0");
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
        return NLP.NLP(res, this);
    }
}
