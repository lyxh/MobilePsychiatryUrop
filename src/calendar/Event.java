package calendar;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Event represents a single event fetched from the Google calendar.
 * @author yixin
 *
 */
public class Event {

		public String id, title, description, calendar_id, beginString,endString;
	    public int week;
	   public Date begin, end;
	   public Event(String title2, Date begin, Date end2, String description2) {
			this.title=title2;
			this.begin=begin;
			this.end=end2;
			this.description=description2;
			
			SimpleDateFormat format = new SimpleDateFormat("MMM dd,yyyy  hh:mm a");
			this.beginString = format.format(begin).toString();

			SimpleDateFormat format2 = new SimpleDateFormat("MMM dd,yyyy  hh:mm a");
		    this.endString= format2.format(end).toString();
		}
	   
}
