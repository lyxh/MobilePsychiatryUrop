package main;

import health.HealthData;
import main.StartActivity;
import java.util.Timer;
import java.util.TimerTask;
import com.example.mobilepsychiatry.R;
import timer.MyTimerClass;
import calendar.CalendarData;

import android.app.Application;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import attendance.AttendanceData;
/**
 * MainActivity is the activity launched after log in. 
 * It shows the welcome message, log out button and three tab views.
 * @author yixin
 *
 */

public class MainActivity extends TabActivity {
	private TabHost mTabHst;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //show the usernaem
        TextView text=(TextView)findViewById(R.id.welcome);
        String line=StartActivity.username;
    	text.setText("Welcome, ");
    	if (line.equals("No such resident")){
    	    text.append("user");
    	}
    	else{
    		String[] splitted=line.split("\\s+");
    		if (splitted.length<=1){
    		     text.append(Character.toUpperCase(line.charAt(0)) + line.substring(1)+ "  ");
    		}
    		else{
    			  text.append(Character.toUpperCase(splitted[0].charAt(0)) + splitted[0].substring(1)+ "  ");
    		}
    	}
    	
    	 Resources res = getResources();
    	 mTabHst = (TabHost) findViewById(android.R.id.tabhost);
 		 mTabHst.setup();
 		mTabHst.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
 		Intent i = new Intent(this, CalendarData.class);
        Intent healthData = new Intent(this, HealthData.class);
        Intent attendanceData = new Intent(this, AttendanceData.class);
        
        final View view = LayoutInflater.from(mTabHst.getContext()).inflate(R.layout.tabs_bg, null);
 		TextView tv = (TextView) view.findViewById(R.id.tabsText);
 		tv.setText("My Schedule");
 		ImageView image=(ImageView) view.findViewById(R.id.image);
 		image.setBackgroundResource(R.drawable.one);
 		TabSpec setContent =mTabHst.newTabSpec("tab_test1").setIndicator(view).setContent(i);
		mTabHst.addTab(setContent);
         
		final View view2 = LayoutInflater.from(mTabHst.getContext()).inflate(R.layout.tabs_bg2, null);
 		TextView tv2 = (TextView) view2.findViewById(R.id.tabsText);
 		tv2.setText("My Sensor Data");
 		ImageView image2=(ImageView) view2.findViewById(R.id.image);
 		image2.setBackgroundResource(R.drawable.two);
 		TabSpec setContent2 =mTabHst.newTabSpec("tab_test2").setIndicator(view2).setContent(healthData);
		mTabHst.addTab(setContent2);
		
		
		final View view3 = LayoutInflater.from(mTabHst.getContext()).inflate(R.layout.tabs_bg3, null);
 		TextView tv3 = (TextView) view3.findViewById(R.id.tabsText);
 		tv3.setText("My Attendance");
 		ImageView image3=(ImageView) view3.findViewById(R.id.image);
 		image3.setBackgroundResource(R.drawable.three);
 		TabSpec setContent3 =mTabHst.newTabSpec("tab_test3").setIndicator(view3).setContent(attendanceData);
		mTabHst.addTab(setContent3);
		
         //clicking on the log out button logs the user out by switching to start activity.
         Button button1 = (Button) findViewById(R.id.logout);       
         button1.setOnClickListener(new View.OnClickListener() {
             @Override
		     public void onClick(View v) {
    	    	   Intent i = new Intent(getApplicationContext(), StartActivity.class);  
    	    	   startActivity(i);}
         });
         
    } 
    
    
    private void setupTab(final View view, final String tag, final String imageName) {
		View tabview = createTabView(mTabHst.getContext(), tag,imageName);
		Intent i = new Intent(this, CalendarData.class);
        TabSpec setContent = mTabHst.newTabSpec(tag).setIndicator(tabview).setContent(i); 
		mTabHst.addTab(setContent);

	}

	private static View createTabView(final Context context, final String text, final String imageName) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		ImageView image=(ImageView) view.findViewById(R.id.image);
	//	image.setBackgroundResource(res.getDrawable(R.drawable.imageName));
		return view;
	}
    
    
	
	
    //when different keyboards are pressed, switch to that view
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
         TabHost mTabHst = getTabHost(); 
         Button button1 = (Button) findViewById(R.id.logout);
        switch (keyCode) {
            case KeyEvent.KEYCODE_Q:            	
                 mTabHst.setCurrentTab(0);
                 return true;
            case KeyEvent.KEYCODE_W:
            	 mTabHst.setCurrentTab(1);
                 return true;
            case KeyEvent.KEYCODE_E:
           	     mTabHst.setCurrentTab(2);
                return true;
            case KeyEvent.KEYCODE_L:
          	     button1.performClick();
               return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }
    

}

