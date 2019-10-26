package com.sdhacks.flyerscanner;

import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attach;
import net.fortuna.ical4j.model.property.Comment;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.PriorityQueue;

public class EventStorage
{

    private PriorityQueue<ComparableCalendar> queue;

    public EventStorage()
    {
        this.queue = new PriorityQueue<>();
    }

    /*
    Return codes:
    0 = added new event
    1 = modified existing event (handled by modify method)
    2 = failed to add event
    3 = failed to modify event (handled by modify method)
     */
    public int addEvent(String summary, java.util.Date startDate, java.util.Date endDate,
                        String locationString, String comment, File attachmentImage)
            throws IOException
    {
        PropertyList<Property> propertyList = new PropertyList();

        propertyList.add(new DtStart(new net.fortuna.ical4j.model.Date(startDate)));
        propertyList.add(new DtEnd(new net.fortuna.ical4j.model.Date(endDate)));
        propertyList.add(new Summary(summary));
        propertyList.add(new Comment(comment));
        propertyList.add(new Location(locationString));

        FileInputStream fis = new FileInputStream(attachmentImage);
        // unsafe as image could be larger than 2.5MB
        // TODO read the attachment image safely
        byte[] bArray = new byte[(int)attachmentImage.length()];
        fis.read(bArray);
        fis.close();

        propertyList.add(new Attach(bArray));

        VEvent newEv = new VEvent();

        if (eventExists(newEv))
        {
            return modifyEvent(newEv);
        }
        else
        {
            ComponentList<CalendarComponent> compList = new ComponentList<>();
            compList.add(newEv);
            ComparableCalendar cal = new ComparableCalendar(compList);
            queue.add(cal);
            return 0;
        }
    }

    /*
    Return codes:
    0 = added new event (will never return 0 since that is done by the adding method)
    1 = modified existing event
    2 = failed to add event (will never return 2 since that is done by the adding method)
    3 = failed to modify event
     */
    //TODO implement modifyEvent
    private int modifyEvent(VEvent modified)
    {

    }

    private boolean eventExists(VEvent testMe)
    {
        for (ComparableCalendar current : queue)
        {
            VEvent currEv = (VEvent) current.getComponent("VEVENT");
            if (testMe.equals(currEv))
                return true;
        }
        return false;
    }

    public boolean deleteEvent(VEvent deleteMe)
    {
        return queue.remove(deleteMe);
    }
}
