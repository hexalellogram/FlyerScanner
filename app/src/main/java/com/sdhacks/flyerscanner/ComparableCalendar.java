package com.sdhacks.flyerscanner;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;

public class ComparableCalendar extends Calendar implements Comparable<ComparableCalendar>
{
    public int compareTo(ComparableCalendar other)
    {
        VEvent myEv = (VEvent) this.getComponent("VEVENT");
        VEvent otherEv = (VEvent) other.getComponent("VEVENT");

        Date myStartDate = myEv.getStartDate().getDate();
        Date otherStartDate = otherEv.getStartDate().getDate();

        return myStartDate.compareTo(otherStartDate);
    }
}
