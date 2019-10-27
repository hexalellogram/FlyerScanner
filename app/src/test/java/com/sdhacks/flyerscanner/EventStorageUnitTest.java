package com.sdhacks.flyerscanner;

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attach;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;

public class EventStorageUnitTest
{
    private EventStorage testingObject;

    public EventStorageUnitTest()
    {
        try {
            testingObject = new EventStorage("/Users/bryce/Downloads/icsTest");
        }
        catch (IOException | ParserException ex)
        {
            ex.printStackTrace();
        }
    }

    @Test
    public void addTest() throws Exception
    {
        ComparableCalendar cal = addPlaceholderItem(true);
        ComparableCalendar cal2 = addDifferentPlaceholder(true);

        assertThat(testingObject.getQueue()).containsExactly(cal, cal2);
    }

    private ComparableCalendar addPlaceholderItem(boolean addToQueue) throws Exception
    {
        java.util.Date startDate = new Date(1572393600000L);
        java.util.Date endDate = new Date(1572440400000L);

        File image = new File("/Users/bryce/Downloads/testImage.png");

        if (addToQueue)
            testingObject.addEvent("test summary", startDate, endDate,
                "test location", "test comment", image);

        PropertyList<Property> propertyList = new PropertyList<>();

        DtStart start = new DtStart();
        DtEnd end = new DtEnd();

        start.setDate(new net.fortuna.ical4j.model.DateTime(startDate));
        end.setDate(new net.fortuna.ical4j.model.DateTime(endDate));

        propertyList.add(start);
        propertyList.add(end);

        propertyList.add(new Summary("test summary"));
        propertyList.add(new Description("test comment"));
        propertyList.add(new Location("test location"));


        long timestamp = Instant.now().toEpochMilli();
        propertyList.add(new DtStamp(new DateTime(timestamp)));
        String uidString = timestamp +  "@hostname";
        propertyList.add(new Uid(uidString)); // right now hostname is hardcoded


        FileInputStream fis = new FileInputStream(image);
        byte[] bArray = new byte[(int) image.length()];
        fis.read(bArray);
        fis.close();

        propertyList.add(new Attach(bArray));

        VEvent newEv = new VEvent(propertyList);

        ComponentList<CalendarComponent> compList = new ComponentList<>();
        compList.add(newEv);
        PropertyList<Property> calPropList = new PropertyList<>();
        calPropList.add(new ProdId("com.sdhacks.flyerscanner"));
        calPropList.add(Version.VERSION_2_0);
        ComparableCalendar cal = new ComparableCalendar(calPropList, compList);
        return cal;
    }

    private ComparableCalendar addDifferentPlaceholder(boolean addToQueue) throws Exception
    {
        java.util.Date startDate = new Date(1572393600000L);
        java.util.Date endDate = new Date(1572440400000L);

        File image = new File("/Users/bryce/Downloads/testImage.png");

        if (addToQueue)
            testingObject.addEvent("different summary", startDate, endDate,
                    "different location", "different comment", image);

        PropertyList<Property> propertyList = new PropertyList<>();

        DtStart start = new DtStart();
        DtEnd end = new DtEnd();

        start.setDate(new net.fortuna.ical4j.model.DateTime(startDate));
        end.setDate(new net.fortuna.ical4j.model.DateTime(endDate));

        propertyList.add(start);
        propertyList.add(end);

        propertyList.add(new Summary("different summary"));
        propertyList.add(new Description("different comment"));
        propertyList.add(new Location("test location"));


        long timestamp = Instant.now().toEpochMilli();
        propertyList.add(new DtStamp(new DateTime(timestamp)));
        String uidString = timestamp +  "@hostname";
        propertyList.add(new Uid(uidString)); // right now hostname is hardcoded


        FileInputStream fis = new FileInputStream(image);
        byte[] bArray = new byte[(int) image.length()];
        fis.read(bArray);
        fis.close();

        propertyList.add(new Attach(bArray));

        VEvent newEv = new VEvent(propertyList);

        ComponentList<CalendarComponent> compList = new ComponentList<>();
        compList.add(newEv);
        PropertyList<Property> calPropList = new PropertyList<>();
        calPropList.add(new ProdId("com.sdhacks.flyerscanner"));
        calPropList.add(Version.VERSION_2_0);
        ComparableCalendar cal = new ComparableCalendar(calPropList, compList);
        return cal;
    }

    @Test
    public void testExisting() throws Exception
    {
        ComparableCalendar cal1 = addPlaceholderItem(true);

        assertThat(testingObject.getQueue()).contains(cal1);
    }

    @Test
    public void testICSExport() throws Exception
    {
        ComparableCalendar cal1 = addPlaceholderItem(true);

        testingObject.icsExport("/Users/bryce/Downloads/testFile.ics", cal1);
        assertThat("a").contains("a");
    }
}
