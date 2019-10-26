package com.sdhacks.flyerscanner;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;

public class ComparableCalendar extends Calendar implements Comparable<ComparableCalendar>
{

    public ComparableCalendar(ComponentList<CalendarComponent> list)
    {
        super(list);
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
        return ((VEvent) this.getComponent("VEVENT")).getSummary().toString();
    }

    public String getDescription()
    {
        return ((VEvent) this.getComponent("VEVENT")).getDescription().toString();
    }

    public String getLocation()
    {
        return ((VEvent) this.getComponent("VEVENT")).getLocation().toString();
    }

    public String getStartDate()
    {
        return ((VEvent) this.getComponent("VEVENT")).getStartDate().toString();
    }

    public String getEndDate()
    {
        return ((VEvent) this.getComponent("VEVENT")).getEndDate().toString();
    }

    public VEvent getVEvent() {
        return (VEvent) this.getComponent("VEVENT");
    }
}
