package com.sdhacks.flyerscanner;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attach;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Calendar;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Scanner;

import com.google.gson.Gson;

public class EventStorage
{

    private PriorityQueue<ComparableCalendar> queue;
    private String fp;

    public EventStorage(String folderPath) throws IOException, ParserException
    {
        this.fp = folderPath;

        // queue.json is the JSON file
        this.queue = new PriorityQueue<>();
        File folder = new File(folderPath);
        File json = folder;
        if(json == null)
        {
            json = new File(this.fp);
            json.createNewFile();
        }

        Gson JSONConverter = new Gson();

        try
        {
            Scanner readFile = new Scanner(json);
            while (readFile.hasNextLine()) //  loop through file lines
            {
                // Convert the String into a ComparableCalendar object.
                ComparableCalendar toAdd = JSONConverter.fromJson(readFile.nextLine(),
                        ComparableCalendar.class);

                // add the ComparableCalendar object to the PQ.
                this.queue.add(toAdd);
            }
        }
        catch(Exception e)
        {
            // Print any error message that results to the console.
            e.printStackTrace();
        }

    }

    public void exportJSON()
    {
        // Initialize a new Gson object to convert Issues into JSON strings for addition into the export file.
        Gson JSONConverter = new Gson();
        FileWriter writer;

        try
        {
            // wipe the file
            PrintWriter write = new PrintWriter(new File(fp));
            write.print("");
            write.close();

            // Initialize FileWriter to write Strings into JSON file.
            writer = new FileWriter(fp + "/queue.json");

            Iterator<ComparableCalendar> iter = this.queue.iterator();
            // Loop through the PQ.
            while(iter.hasNext())
            {
                ComparableCalendar nextObject = iter.next();
                // Convert an issue object into a JSON-formatted representation of it, as a String.
                String representationJSON = JSONConverter.toJson(nextObject);

                // Write that String to the file.
                writer.write(representationJSON + "\n");
            }
            // Close the FileWriter to conserve system resources.
            writer.close();

        }
        catch (Exception e)
        {
            // Print any error messages that results to the console.
            e.printStackTrace();
        }

    }

    /*
    Return codes:
    0 = added new event
    1 = modified existing event
    2 = error
     */
    public int addEvent(String summary, java.util.Date startDate, java.util.Date endDate,
                        String locationString, String comment, byte[] attachmentImage)
    {
        int returnCode = 0;
        if (endDate == null) {
            Calendar cal = Calendar.getInstance(); // creates calendar
            cal.setTime(startDate); // sets calendar time/date
            cal.add(Calendar.HOUR_OF_DAY, 2); // add two hours
            endDate = cal.getTime(); // returns new date object, two hours in the future
        }
        PropertyList<Property> propertyList = new PropertyList<>();

        DtStart start = new DtStart();
        DtEnd end = new DtEnd();

        start.setDate(new DateTime(startDate));
        end.setDate(new DateTime(endDate));

        propertyList.add(start);
        propertyList.add(end);

        propertyList.add(new Summary(summary));
        propertyList.add(new Description(comment));
        propertyList.add(new Location(locationString));

        long timestamp = Instant.now().toEpochMilli();
        propertyList.add(new DtStamp(new DateTime(timestamp)));
        String uidString = timestamp + "@" + android.os.Build.MODEL;
        propertyList.add(new Uid(uidString)); // right now hostname is hardcoded

        propertyList.add(new Attach(attachmentImage));

        VEvent newEv = new VEvent(propertyList);
        VEvent findExisting = eventExists(newEv);

        if (findExisting != null) {
            boolean result = deleteEvent(findExisting);
            if (!result)
            {
                returnCode = 2;
                return returnCode;
            }
            returnCode = 1;
        }

        ComponentList<CalendarComponent> compList = new ComponentList<>();
        compList.add(newEv);
        PropertyList<Property> calPropList = new PropertyList<>();
        calPropList.add(new ProdId("com.sdhacks.flyerscanner"));
        calPropList.add(Version.VERSION_2_0);
        ComparableCalendar cal = new ComparableCalendar(calPropList, compList);
        queue.add(cal);
        return returnCode;

    }

    /*
    Returns a VEvent if a VEvent if the same name is found, else return false
     */
    private VEvent eventExists(VEvent testMe)
    {
        for (ComparableCalendar current : queue)
        {
            VEvent currEv = (VEvent) current.getComponent("VEVENT");
            if (testMe.getSummary().equals(currEv.getSummary()))
                return testMe;
        }
        return null;
    }

    public boolean deleteEvent(VEvent deleteMe)
    {
        return queue.remove(deleteMe);
    }

    public PriorityQueue<ComparableCalendar> getQueue()
    {
        return this.queue;
    }

    public File icsExport(String path, ComparableCalendar cal) throws IOException
    {
        File ics =  new File(path);
        if (!ics.exists())
            ics.createNewFile();

        FileOutputStream fos = new FileOutputStream(path);
        CalendarOutputter outputter = new CalendarOutputter();
        // need to test if this actually outputs the data to the file or just a buffer
        // assuming it outputs for the file for now
        outputter.output(cal, fos);

        return ics;
    }

}
