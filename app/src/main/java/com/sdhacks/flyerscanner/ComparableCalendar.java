package com.sdhacks.flyerscanner;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;

import java.util.GregorianCalendar;

public class ComparableCalendar extends Calendar implements Comparable<ComparableCalendar>
{

    public ComparableCalendar(PropertyList<Property> pl, ComponentList<CalendarComponent> list)
    {
        super(pl, list);
    }

    public int compareTo(ComparableCalendar other)
    {
        VEvent myEv = (VEvent) this.getComponent("VEVENT");
        VEvent otherEv = (VEvent) other.getComponent("VEVENT");

        Date myStartDate = myEv.getStartDate().getDate();
        Date otherStartDate = otherEv.getStartDate().getDate();

        return myStartDate.compareTo(otherStartDate);
    }

    public String getSummary()
    {
        return ((VEvent) this.getComponent("VEVENT")).getSummary().getValue();
    }

    public String getDescription()
    {
        return ((VEvent) this.getComponent("VEVENT")).getDescription().getValue();
    }

    public String getLocation()
    {
        return ((VEvent) this.getComponent("VEVENT")).getLocation().getValue();
    }

    /*
    Note: Calendars returned have times im GMT all the time, no matter what the user's time zone
     */
    public java.util.Calendar getStartDate()
    {
        java.util.Date date = ((VEvent)this.getComponent("VEVENT")).getStartDate().getDate();
        java.util.Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal;

    }

    /*
    Note: Calendars returned have times im GMT all the time, no matter what the user's time zone
     */
    public java.util.Calendar getEndDate()
    {
        java.util.Date date = ((VEvent)this.getComponent("VEVENT")).getEndDate().getDate();
        java.util.Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal;
    }

    public VEvent getVEvent() {
        return (VEvent) this.getComponent("VEVENT");
    }
}
