package calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Instances;
/**
 * The CalendarClass takes care of the processing dates and calendar events. and its methods are used by other Classes
 * @author liyixin
 *
 */
@SuppressLint("InlinedApi") public class CalendarClass {
		
		public static final String[] EVENT_PROJECTION = new String[] { 
	        Calendars._ID, // 0 
	        Calendars.ACCOUNT_NAME, // 1 
	        Calendars.CALENDAR_DISPLAY_NAME // 2
	        }; 
		public static final String[] INSTANCE_PROJECTION = new String[] {
			Instances.TITLE,          // 2
		    Instances.BEGIN,         // 1
		    Instances.END,
		    Instances.DESCRIPTION,
		  };
	
	
	    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;  
	    private static final int PROJECTION_ID_INDEX = 0;
	    

	    /**
	     * Given the week and the year, return a string that shows the range of the week
	     * @param enterWeek the week
	     * @param enterYear the year
	     * @return an arraylist of string that shows the start and end of the week, in the format "2012-04-27 00:00:00",
	     */
	    public static ArrayList<String> getStartEndOfWeek2(int enterWeek, int enterYear){
	            ArrayList<String> result=new ArrayList<String> ();
		        Calendar calendar = Calendar.getInstance();
		        calendar.clear();
		        calendar.set(Calendar.WEEK_OF_YEAR, enterWeek);
		        calendar.set(Calendar.YEAR, enterYear);
		        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-dd-MMM HH:mm:ss"); 
		        calendar.add(Calendar.DATE,0);
		        Date startDate = calendar.getTime();
		        String startDateInStr = formatter.format(startDate);
		        result.add(startDateInStr);
                
		        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-dd-MMM HH:mm:ss"); 
		        calendar.add(Calendar.DATE, 6);
		        Date endDate = calendar.getTime();
		        String endDateString = formatter2.format(endDate);
		        result.add(endDateString);
	            return result;
		}
	    
	    
	    
	    /**
	     * Given the week and the year, return a string that shows the range of the week
	     * @param enterWeek the week
	     * @param enterYear the year
	     * @return a string that shows the range of the week
	     */
	    public static String getStartEndOFWeek(int enterWeek, int enterYear){
            String result="";
	        Calendar calendar = Calendar.getInstance();
	        calendar.clear();
	        calendar.set(Calendar.WEEK_OF_YEAR, enterWeek);
	        calendar.set(Calendar.YEAR, enterYear);
	        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd"); 
	        calendar.add(Calendar.DATE, 0);
	        Date startDate = calendar.getTime();
	        String startDateInStr = formatter.format(startDate);
	        result+=startDateInStr;
            
	        SimpleDateFormat formatter2 = new SimpleDateFormat("MMM dd yyyy"); 
	        calendar.add(Calendar.DATE, 6);
	        Date endDate = calendar.getTime();
	        String endDaString = formatter2.format(endDate);
	        result+=" - " +endDaString;
	       
            return result;
        }
    
    
	   /** 
	    * Given year and week number, return the seven dates of the week shown in dd MMM format
	    * @param enterWeek: the week number
	    * @param enterYear: the year
	    * @return an ArrayList of string, each of form dd MM that represents a date
	    */
	    public static ArrayList<String> getWeek(int enterWeek, int enterYear){
            	ArrayList<String> result=new ArrayList<String> ();
		        Calendar calendar = Calendar.getInstance();
		        calendar.clear();
		        calendar.set(Calendar.WEEK_OF_YEAR, enterWeek);
		        calendar.set(Calendar.YEAR, enterYear);
		        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");

		        
		        calendar.add(Calendar.DATE, 0);
		        Date day0= calendar.getTime();
		        String Day0 = formatter.format(day0);
		        result.add(Day0);
		        
		        calendar.add(Calendar.DATE, 1);
		        Date day1= calendar.getTime();
		        String Day1 = formatter.format(day1);
		        result.add(Day1);
    
		        calendar.add(Calendar.DATE, 1);
		        Date day2= calendar.getTime();
		        String Day2 = formatter.format(day2);
		        result.add(Day2);
		        
		        calendar.add(Calendar.DATE, 1);
		        Date day3= calendar.getTime();
		        String Day3 = formatter.format(day3);
		        result.add(Day3);
		        
		        calendar.add(Calendar.DATE, 1);
		        Date day4= calendar.getTime();
		        String Day4 = formatter.format(day4);
		        result.add(Day4);
		        
		        calendar.add(Calendar.DATE, 1);
		        Date day5= calendar.getTime();
		        String Day5 = formatter.format(day5);
		        result.add(Day5);
		        
		        calendar.add(Calendar.DATE, 1);
		        Date day6= calendar.getTime();
		        String Day6 = formatter.format(day6);
		        result.add(Day6);
		        
		    
	            return result;
		}
    
    
    
		/**
		 * Given the year and week number, return the two long numbers milliseconds. 
		 * The first/second one is the start/end date of the week in milliseconds
		 * @param enterWeek
		 * @param enterYear
		 * @return an ArrayList of long numbers
		 */
        public static ArrayList<Long> getStartEndOFWeek1(int enterWeek, int enterYear){
		        //enterWeek is week number
		        //enterYear is year
	            ArrayList<Long> result= new  ArrayList<Long>();
		        Calendar calendar = Calendar.getInstance();
		        calendar.clear();
		        calendar.set(Calendar.WEEK_OF_YEAR, enterWeek);
		        calendar.set(Calendar.YEAR, enterYear);

		        SimpleDateFormat formatter = new SimpleDateFormat("ddMMM yyyy"); // PST`
		        calendar.add(Calendar.DATE, 0);
		        Date startDate = calendar.getTime();
		        long startMili= startDate.getTime();
		        result.add(startMili);

		        calendar.add(Calendar.DATE, 7);
		        Date endDate = calendar.getTime();
		        long endMili=endDate.getTime();
		        result.add(endMili);
		       
		        return result;
		}
    
    public static boolean hasCalendar(String calendarName, String googleAccount, ContentResolver cr, int week, Uri uri, Uri.Builder builder ){
    	  Cursor cur = null; 
	      String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND (" 
	              + Calendars.ACCOUNT_TYPE + " = ?) AND ("
	              + Calendars.CALENDAR_DISPLAY_NAME+ "=?))"; 
	      String[] selectionArgs = new String[] { googleAccount, 
	              "com.google", calendarName }; 
	      
	      cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);   
	      String displayName=null;
	      Long calID =null;
	      while (cur.moveToNext()) {      
	             displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX); 
	             calID = cur.getLong(PROJECTION_ID_INDEX);
	       }
	       cur.close(); 
          if (calID==null){return false;}
          else{return true;}	
    }
    
    /**
     * Given the calendar name, google account and the week number,
     * return the events of that week of the corresponding calendar of the google account
     * @param calendarName
     * @param googleAccount
     * @param cr
     * @param week
     * @param uri
     * @param builder
     * @return
     */
      public static  ArrayList<Event> getEvents (String calendarName, String googleAccount, ContentResolver cr, int week, Uri uri, Uri.Builder builder ){
			  ArrayList<Event> result= new ArrayList<Event>();
			  Cursor cur = null; 
		      String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND (" 
		              + Calendars.ACCOUNT_TYPE + " = ?) AND ("
		              + Calendars.CALENDAR_DISPLAY_NAME+ "=?))"; 
		      String[] selectionArgs = new String[] { googleAccount, 
		              "com.google", calendarName }; 
		      
		      cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);       
		      String displayName = null; 
		      Long calID = null;
	    
	    	  while (cur.moveToNext()) {      
		             displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX); 
		             calID = cur.getLong(PROJECTION_ID_INDEX);
	           }
	          cur.close();
	          if (calID!=null) {
				      String selection2 =  Instances.CALENDAR_ID + " = ?";
				      String[] selectionArgs2 =new String[] {calID.toString()};
			          ArrayList<Long> twoMilis=getStartEndOFWeek1(week, 2013);
			
			          ContentUris.appendId(builder, twoMilis.get(0));
			          ContentUris.appendId(builder, twoMilis.get(1) );
			
			          Cursor eventCursor =  cr.query(builder.build(), INSTANCE_PROJECTION, selection2, selectionArgs2, null);
			
			          if (eventCursor!=null){
			                 while (eventCursor.moveToNext()) {      
					               final String title = eventCursor.getString(0);
					               final Date begin = new Date(eventCursor.getLong(1));
					               final Date end = new Date(eventCursor.getLong(2));
					               final String description= eventCursor.getString(3);
				                   Event a=new Event(title, begin,end,description);
				                   result.add(a);
			                  }
			                  eventCursor.close();
			          }            
	          }
	          return result;
    }



    /**
     * Given the events as a ArrayList, return a list of strings, with each string showing an event
     * @param events an ArrayList of events that happen in the same week
     * @return an array of String of size 7. Each String shows the events in that date of the week.
     */
    public static String[] sortEvents(ArrayList<Event> events){
    	 String[] result= new String[]{"","","","","","",""};
    	 for(int i=0;i<events.size();i++){
    		 Event individualEvent=events.get(i);
    		 //get the date, and decide which to add
			 int dayOfWeek=individualEvent.begin.getDay();
    		 String event= individualEvent.title + "\n"+individualEvent.beginString.substring(13) + " - "+individualEvent.endString.substring(13)+"\n"+"\n";
    		 result[dayOfWeek]+=event;
    	 } 
	     return result;
     }



	public static ArrayList<String> getSevenDatesForScore(int week, int year) {
    	ArrayList<String> result=new ArrayList<String> ();
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, week);
        calendar.set(Calendar.YEAR,year);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        
        calendar.add(Calendar.DATE, 1);
        Date day1= calendar.getTime();
        String Day1 = formatter.format(day1);
        result.add(Day1);

        calendar.add(Calendar.DATE, 1);
        Date day2= calendar.getTime();
        String Day2 = formatter.format(day2);
        result.add(Day2);
        
        calendar.add(Calendar.DATE, 1);
        Date day3= calendar.getTime();
        String Day3 = formatter.format(day3);
        result.add(Day3);
        
        calendar.add(Calendar.DATE, 1);
        Date day4= calendar.getTime();
        String Day4 = formatter.format(day4);
        result.add(Day4);
        
        calendar.add(Calendar.DATE, 1);
        Date day5= calendar.getTime();
        String Day5 = formatter.format(day5);
        result.add(Day5);
        
        calendar.add(Calendar.DATE, 1);
        Date day6= calendar.getTime();
        String Day6 = formatter.format(day6);
        result.add(Day6);
        
        calendar.add(Calendar.DATE, 1);
        Date day7= calendar.getTime();
        String Day7 = formatter.format(day7);
        result.add(Day7);
    
        return result;
	}

}
  


