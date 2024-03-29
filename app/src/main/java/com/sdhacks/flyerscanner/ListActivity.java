package com.sdhacks.flyerscanner;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import java.io.File;
import java.util.HashMap;
import java.util.PriorityQueue;

public class ListActivity extends AppCompatActivity {

    static EventStorage eventStorage;
    private LinearLayout linearLayout;
    private HashMap<CheckBox, ComparableCalendar> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        linearLayout = findViewById(R.id.internalLinearLayout);

        try {
            File folder = new File(getFilesDir().getAbsolutePath() + "//ics//queue.json");
            if(!folder.exists()) {
                folder.mkdir();
            }
            eventStorage = new EventStorage(folder.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        map = new HashMap<>();
        addCheckBoxes(eventStorage.getQueue());

    }

    public void onScanAnotherClick(View view) {
        finish();
    }

    public void onDeleteClick(View view) {
        for(CheckBox checkBox : map.keySet()) {
            if(checkBox.isChecked()) {
                map.remove(checkBox);
                linearLayout.removeView(checkBox);
                ComparableCalendar currentCalendar = map.get(checkBox);
                if(currentCalendar != null){
                    eventStorage.deleteEvent(currentCalendar.getVEvent());
                }
            }
        }
    }

    public void onExportClick(View view) {
        for(CheckBox checkBox : map.keySet()) {
            if(checkBox.isChecked()) {
                ComparableCalendar currentCalendar = map.get(checkBox);
                if(currentCalendar != null){
                    //eventStorage.icsExport(, currentCalendar);
                }
            }
        }
    }

    private void addCheckBoxes(PriorityQueue<ComparableCalendar> queue) {
        for(ComparableCalendar currentCalendar : queue) {
            CheckBox checkBox = new CheckBox(getApplicationContext());
            map.put(checkBox, currentCalendar);
            // Add start and end date
            String text = currentCalendar.getDescription() + " @ " + currentCalendar.getLocation();
            checkBox.setText(text);
            linearLayout.addView(checkBox);
        }
    }
}
