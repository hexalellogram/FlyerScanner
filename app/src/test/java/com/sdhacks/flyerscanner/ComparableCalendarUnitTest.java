package com.sdhacks.flyerscanner;

import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryImpl;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.Dates;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ComparableCalendarUnitTest
{
    @Test
    public void descriptionTest()
    {
        java.util.Date javaStart = new Date(1572138078000L); // Sunday, October 27, 2019 1:01:18 AM
        java.util.Date javaEnd = new Date(1572224371000L); // Monday, October 28, 2019 12:59:31 AM

        PropertyList<Property> propList = new PropertyList<>();

        propList.add(new DtStart(new net.fortuna.ical4j.model.Date(javaStart)));
        propList.add(new DtEnd(new net.fortuna.ical4j.model.Date(javaEnd)));

        propList.add(new Summary("Testing summary"));
        propList.add(new Description("Testing description"));
        propList.add(new Location("Testing location"));

        VEvent testEvent = new VEvent(propList);

        ComponentList<CalendarComponent> compList = new ComponentList<>();
        compList.add(testEvent);
        PropertyList<Property> calPropList = new PropertyList<>();
        calPropList.add(new ProdId("com.sdhacks.flyerscanner"));
        calPropList.add(Version.VERSION_2_0);
        ComparableCalendar cal = new ComparableCalendar(calPropList, compList);

        assertThat(cal.getDescription()).isEqualTo("Testing description");
    }

    @Test
    public void summaryTest()
    {
        java.util.Date javaStart = new Date(1572138078000L); // Sunday, October 27, 2019 1:01:18 AM
        java.util.Date javaEnd = new Date(1572224371000L); // Monday, October 28, 2019 12:59:31 AM

        PropertyList<Property> propList = new PropertyList<>();

        propList.add(new DtStart(new net.fortuna.ical4j.model.Date(javaStart)));
        propList.add(new DtEnd(new net.fortuna.ical4j.model.Date(javaEnd)));

        propList.add(new Summary("Testing summary"));
        propList.add(new Description("Testing description"));
        propList.add(new Location("Testing location"));

        VEvent testEvent = new VEvent(propList);

        ComponentList<CalendarComponent> compList = new ComponentList<>();
        compList.add(testEvent);
        PropertyList<Property> calPropList = new PropertyList<>();
        calPropList.add(new ProdId("com.sdhacks.flyerscanner"));
        calPropList.add(Version.VERSION_2_0);
        ComparableCalendar cal = new ComparableCalendar(calPropList, compList);

        assertThat(cal.getSummary()).isEqualTo("Testing summary");
    }

    @Test
    public void locationTest()
    {
        java.util.Date javaStart = new Date(1572138078000L); // Sunday, October 27, 2019 1:01:18 AM
        java.util.Date javaEnd = new Date(1572224371000L); // Monday, October 28, 2019 12:59:31 AM

        PropertyList<Property> propList = new PropertyList<>();

        propList.add(new DtStart(new net.fortuna.ical4j.model.Date(javaStart)));
        propList.add(new DtEnd(new net.fortuna.ical4j.model.Date(javaEnd)));

        propList.add(new Summary("Testing summary"));
        propList.add(new Description("Testing description"));
        propList.add(new Location("Testing location"));

        VEvent testEvent = new VEvent(propList);

        ComponentList<CalendarComponent> compList = new ComponentList<>();
        compList.add(testEvent);
        PropertyList<Property> calPropList = new PropertyList<>();
        calPropList.add(new ProdId("com.sdhacks.flyerscanner"));
        calPropList.add(Version.VERSION_2_0);
        ComparableCalendar cal = new ComparableCalendar(calPropList, compList);

        assertThat(cal.getLocation()).isEqualTo("Testing location");
    }

    @Test
    public void startDateTest()
    {
        long startTime = 1572393600000L;
        long endTime = 1572393600000L;

        PropertyList<Property> propList = new PropertyList<>();

        DtStart start = new DtStart();
        DtEnd end = new DtEnd();

        net.fortuna.ical4j.model.Date icalDateStart = new net.fortuna.ical4j.model.DateTime();
        net.fortuna.ical4j.model.Date icalDateEnd = new net.fortuna.ical4j.model.DateTime();

        icalDateStart.setTime(startTime);
        icalDateEnd.setTime(endTime);

        start.setDate(icalDateStart);
        end.setDate(icalDateEnd);

        propList.add(start);
        propList.add(end);

        propList.add(new Summary("Testing summary"));
        propList.add(new Description("Testing description"));
        propList.add(new Location("Testing location"));

        VEvent testEvent = new VEvent(propList);

        ComponentList<CalendarComponent> compList = new ComponentList<>();
        compList.add(testEvent);
        PropertyList<Property> calPropList = new PropertyList<>();
        calPropList.add(new ProdId("com.sdhacks.flyerscanner"));
        calPropList.add(Version.VERSION_2_0);
        ComparableCalendar cal = new ComparableCalendar(calPropList, compList);

        Calendar startCal = new GregorianCalendar();
        startCal.setTime(new Date(startTime));

        assertThat(cal.getStartDate()).isEqualTo(startCal);
    }

    @Test
    public void endDateTest()
    {
        long startTime = 1572138078000L;
        long endTime = 1572440400000L;

        java.util.Date endJava = new Date(endTime);

        System.out.println("Java End Date");
        System.out.println("Month: " + endJava.getMonth());
        System.out.println("Day of month: " + endJava.getDate());
        System.out.println("Day of week: " + endJava.getDay());
        System.out.println("Hours: " + endJava.getHours());
        System.out.println("Minutes: " + endJava.getMinutes());
        System.out.println("Seconds: " + endJava.getSeconds());
        System.out.println();


        PropertyList<Property> propList = new PropertyList<>();

        DtStart start = new DtStart();
        DtEnd end = new DtEnd();

        net.fortuna.ical4j.model.Date icalDateStart = new net.fortuna.ical4j.model.DateTime();
        net.fortuna.ical4j.model.Date icalDateEnd = new net.fortuna.ical4j.model.DateTime();

        icalDateStart.setTime(startTime);
        icalDateEnd.setTime(endTime);

        System.out.println("ical4j End Date");
        System.out.println("Month: " + icalDateEnd.getMonth());
        System.out.println("Day of month: " + icalDateEnd.getDate());
        System.out.println("Day of week: " + icalDateEnd.getDay());
        System.out.println("Hours: " + icalDateEnd.getHours());
        System.out.println("Minutes: " + icalDateEnd.getMinutes());
        System.out.println("Seconds: " + icalDateEnd.getSeconds());
        System.out.println();

        start.setDate(icalDateStart);
        end.setDate(icalDateEnd);

        propList.add(start);
        propList.add(end);

        propList.add(new Summary("Testing summary"));
        propList.add(new Description("Testing description"));
        propList.add(new Location("Testing location"));

        VEvent testEvent = new VEvent(propList);

        ComponentList<CalendarComponent> compList = new ComponentList<>();
        compList.add(testEvent);
        PropertyList<Property> calPropList = new PropertyList<>();
        calPropList.add(new ProdId("com.sdhacks.flyerscanner"));
        calPropList.add(Version.VERSION_2_0);
        ComparableCalendar cal = new ComparableCalendar(calPropList, compList);

        Calendar endCal = new GregorianCalendar();
        endCal.setTime(new java.util.Date(endTime));

        assertThat(cal.getEndDate()).isEqualTo(endCal);

        // expected value is 1572246000000
        // Monday, October 28, 2019 7:00:00 AM (GMT)
        // Monday, October 28, 2019 12:00:00 AM (local)

        // actual value is 1572220800000
        // Monday, October 28, 2019 12:00:00 AM (GMT)
        // Sunday, October 27, 2019 5:00:00 PM (local)
    }
}
