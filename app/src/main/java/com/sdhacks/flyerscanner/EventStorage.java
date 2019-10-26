package com.sdhacks.flyerscanner;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attach;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.PriorityQueue;

public class EventStorage
{

    private PriorityQueue<ComparableCalendar> queue;

    public EventStorage(String folderPath) throws IOException, ParserException
    {
        this.queue = new PriorityQueue<>();
        File folder = new File(folderPath);
        File[] icsFolder = folder.listFiles();
        for (File f : icsFolder)
        {
            if (f != null)
            {
                if (f.getAbsolutePath().endsWith(".ics"))
                {
                    FileInputStream fis = null;
                    fis = new FileInputStream(f.getAbsolutePath());
                    CalendarBuilder builder = new CalendarBuilder();
                    ComparableCalendar calendar = (ComparableCalendar) builder.build(fis);
                    queue.add(calendar);
                }
            }
        }

    }

    /*
    Return codes:
    0 = added new event
    1 = modified existing event
     */
    public int addEvent(String summary, java.util.Date startDate, java.util.Date endDate,
                        String locationString, String comment, File attachmentImage)
            throws IOException, Exception
    {
        PropertyList<Property> propertyList = new PropertyList<>();

        propertyList.add(new DtStart(new net.fortuna.ical4j.model.Date(startDate)));
        propertyList.add(new DtEnd(new net.fortuna.ical4j.model.Date(endDate)));
        propertyList.add(new Summary(summary));
        propertyList.add(new Description(comment));
        propertyList.add(new Location(locationString));

        FileInputStream fis = new FileInputStream(attachmentImage);
        byte[] bArray = new byte[(int) attachmentImage.length()];
        fis.read(bArray);
        fis.close();

        propertyList.add(new Attach(bArray));

        VEvent newEv = new VEvent(propertyList);
        VEvent findExisting = eventExists(newEv);

        int returnCode = 0;

        if (findExisting != null) {
            boolean result = deleteEvent(findExisting);
                if (!result)
                    throw new Exception();
            returnCode = 1;
        }

        ComponentList<CalendarComponent> compList = new ComponentList<>();
        compList.add(newEv);
        ComparableCalendar cal = new ComparableCalendar(compList);
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
