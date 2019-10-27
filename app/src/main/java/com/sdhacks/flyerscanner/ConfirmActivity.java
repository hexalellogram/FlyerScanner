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

import com.amazonaws.services.comprehend.model.Entity;
import com.amazonaws.services.comprehend.model.KeyPhrase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.jpegkit.Jpeg;
import com.jpegkit.JpegImageView;

import net.fortuna.ical4j.data.ParserException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ConfirmActivity extends AppCompatActivity {

    private byte[] picture;
    private Button continueButton;
    private List<Entity> keyEntities = null;

    private String mOcrText;

    private String mDate;
    private String mOrg;
    private String mLoc;

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
                    Log.v("FlyerScanner-parse", "WE have parsed it all");
                }
            });
        }
    }

    public void onRetakeClick(View view) {
        //Intent retakeIntent = new Intent(this, MainActivity.class);
        //startActivity(retakeIntent);
        finish();
    }

    public void onContinueClick(View view) {

        // Find which str is the date/time/event name/etc
        // Fill in relevant information to user form
        try {
            //EventStorage e = new EventStorage(new File(getFilesDir().getAbsolutePath() + "//ics//").getAbsolutePath());
            String sourceDate = "2019-10-25";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date myDate = format.parse(sourceDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(myDate);
            cal.add(Calendar.DATE, 1);
            myDate = cal.getTime();

            long millisToAdd = 0;
            if (mDate.toUpperCase().contains("PM")){
                millisToAdd += 12 * 60 * 60 * 1000;
            }

            String d = mDate.substring(0, mDate.indexOf('M')-1).trim();
            millisToAdd += ((long)Integer.parseInt(d)) * 60 * 60 * 60 * 1000;

            System.out.println("DATE: " + mDate);
            System.out.println("Time is " + millisToAdd);

            Date date = new Date(millisToAdd); // Convert mDate to date TODO
            if (ListActivity.eventStorage == null){
                File folder = new File(getFilesDir().getAbsolutePath() + "//ics//");
                if(!folder.exists()) {
                    folder.mkdir();
                }
                File f = new File(folder.getAbsolutePath() + "/queue.json");
                if (!f.exists()){
                    f.createNewFile();
                }
                ListActivity.eventStorage = new EventStorage(folder.getAbsolutePath() + "/queue.json");
            }
            ListActivity.eventStorage.addEvent(mOrg, date, null, mLoc, mOcrText, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // Have user confirm and send to backend
        // Add event to storage (probably not our problem)
        // Update our view to show the new event
        // Do something with strings and call next activity

        if(keyEntities == null) {
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
                                keyEntities = processTextRecognitionResult(texts);
                                if (keyEntities != null) {
                                    float dateConf = 0.0f;
                                    float orgConf = 0.0f;
                                    float locConf = 0.0f;

                                    for (Entity key : keyEntities) {
                                        Log.v("FlyerScanner-OCR", key.getText());
                                        if (key.getType().contains("DATE")){
                                            if (key.getScore() > dateConf){
                                                mDate = key.getText();
                                                dateConf = key.getScore();
                                            }
                                        } else if (key.getType().contains("ORGANIZATION")){
                                            if (key.getScore() > orgConf){
                                                mOrg = key.getText();
                                                orgConf = key.getScore();
                                            }
                                        } else if (key.getType().contains("LOCATION")){
                                            if (key.getScore() > locConf){
                                                mLoc = key.getText();
                                                locConf = key.getScore();
                                            }
                                        }
                                    }
                                    continueButton.setText(getResources().getString(R.string.continue_button_label));
                                    continueButton.setClickable(true);
                                }
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

    private List<Entity> processTextRecognitionResult(FirebaseVisionText texts) {
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
        mOcrText = res;
        return NLP.NLP(res, this);
    }
}
