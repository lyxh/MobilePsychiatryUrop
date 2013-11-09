package main;

import java.util.ArrayList;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
/**
 * Store the pair between uid to tag id.
 * @author yixin
 *
 */
public class UserClass extends Activity {
    public static final String name1= "patient_uid";
    public static final String name2= "patient_name";
    public static final String name3= "patient_health";
    public static final String name4= "patient_atten";
	//name1 1 111111111
    public static void addResidentPatientId(Context ctx,String tag_id, String uid){
    	  SharedPreferences sharedPref = ctx.getSharedPreferences(name1, Context.MODE_PRIVATE);
	      sharedPref.edit().putString(tag_id, uid).commit();
    }
    
    /** Check if name1 preference has key tag_id*/
	public static boolean patientExists(Context ctx, String tag_id) {			
		String exist=  "No such resident";
		try {SharedPreferences sharedPref = ctx.getSharedPreferences(name1, Context.MODE_PRIVATE);
			 exist=sharedPref.getString (tag_id, "No such resident");}
		catch(NullPointerException ex){ Log.i("DataManage.patientExists","NullPointerException");}
		if(exist.equals("No such resident")){return false;}
		return true;
	}
    /** Get the uid for tag id.*/
	public static String getPatientUID(Context ctx, String tag_id) {			
    	SharedPreferences sharedPref = ctx.getSharedPreferences(name1, Context.MODE_PRIVATE);
		return sharedPref.getString (tag_id, "No such resident");
    }

	
	
	//name2 22222
    public static void addResidentName(Context ctx,String name, String uid){
    	  SharedPreferences sharedPref = ctx.getSharedPreferences(name2, Context.MODE_PRIVATE);
	      sharedPref.edit().putString(uid,name).commit();
    }
    
    /** Check if name1 preference has key tag_id*/
	public static String getPatientName(Context ctx, String tag_id) {			
    	SharedPreferences sharedPref = ctx.getSharedPreferences(name2, Context.MODE_PRIVATE);
		return sharedPref.getString (tag_id, "No such resident");
    }
	

	//name3 333333
    public static void addResidentHealth(Context ctx,String atten, String uid){
    	  SharedPreferences sharedPref = ctx.getSharedPreferences(name3, Context.MODE_PRIVATE);
	      sharedPref.edit().putString(uid,atten).commit();
    }
    
    /** Get name3 preference has key tag_id*/
	public static String getPatientHealth(Context ctx, String tag_id) {			
    	SharedPreferences sharedPref = ctx.getSharedPreferences(name3, Context.MODE_PRIVATE);
		return sharedPref.getString (tag_id, "No such resident");
	}
	
	//name3 44444
    public static void addResidentAtten(Context ctx,String atten, String uid){
    	  SharedPreferences sharedPref = ctx.getSharedPreferences(name4, Context.MODE_PRIVATE);
	      sharedPref.edit().putString(uid,atten).commit();
    }
    
    /** Check if name4 preference has key tag_id*/
	public static String getPatientAtten(Context ctx, String tag_id) {			
    	SharedPreferences sharedPref = ctx.getSharedPreferences(name4, Context.MODE_PRIVATE);
		return sharedPref.getString (tag_id, "No such resident");
	}
	
	public static void logName(Context context, String name) {
		  SharedPreferences sharedPref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		  Map<String,?> keys = sharedPref.getAll();
		  String message="";
        for(Map.Entry<String,?> entry : keys.entrySet()){
      	  message+=entry.getKey() + ": " + entry.getValue().toString()+", ";	         
		   }
        Log.i("UserClass."+name ,message);   
	}

	
	public static class FetchUIDList implements Runnable {      
		   private WebView myWebView;
		   private TextView feedback;
	       public FetchUIDList(WebView myWebView, TextView feedback){			   
			   this.myWebView=myWebView;this.feedback=feedback;
		   }
		    @Override
			public synchronized void run() {
		     	try {
				     	class MyJavaScriptInterface {     	 
								private TextView contentView;
					            public MyJavaScriptInterface(TextView aContentView) {contentView = aContentView;}
			                    boolean once=true;
					            @JavascriptInterface 
					            public void processContent(String hp) { 
					                final String content =hp;
					                Thread thread = new Thread(new Runnable() {
					                @Override
									public void run() { 		                    
					                    if(once){
				                    		 //parse json file located at the resident link					                    	
					                    	 Log.i("UserClass.fetchList.json file readed has length ",String.valueOf(content.length()));
					                    	 JsonElement jelement = new JsonParser().parse(content);
					                    	 JsonArray jarray = jelement.getAsJsonArray();	                      	  
					                      	 try{
					                      		 for (int i=0;i<jarray.size();i++){
						                      		  JsonObject  jobject = jarray.get(i).getAsJsonObject();	                      		 
						                      	      String uid = jobject.get("uid").toString().trim();
						                      		  uid=uid.replaceAll("^\"|\"$", "");
								                      StartActivity.keys.add(uid);
					                      	       }
					                      	 }
					                      	 catch(Exception e){Log.i("UserClass.FetchUIDList","Exception");}
				                      	     once=false;
			                         	}
					                 }
					                 });
					                 try {
					                	thread.run();thread.join();
									 } catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									 }
					                 Log.i("UserClass.main process",  "Size of key= "+StartActivity.keys.size());
					                 StartActivity.keySet=true;
					                } 
			                     }
				     		     myWebView.getSettings().setJavaScriptEnabled(true);     			 	        
					             myWebView.addJavascriptInterface(new MyJavaScriptInterface(feedback), "INTERFACE"); 				       
					             myWebView.setWebViewClient(new WebViewClient() {  
					        	 boolean t=true;
							     @Override public boolean shouldOverrideUrlLoading(WebView webView, String url) { return true;  }      
					             @Override public void onPageFinished(WebView view, String url)  {  
					            	 if (t){  
					            	        myWebView.loadUrl("javascript: {" +
					                        "document.getElementById('edit-name').value = '"+StartActivity.account_name +"';" +
					                        "document.getElementById('edit-pass').value = '"+StartActivity.account_password+"';" +
					                        "document.getElementById('edit-submit').submit(); };");
				 
					            		    myWebView.loadUrl("http://hectorreyeshouse.org/user/login");
					            		    myWebView.loadUrl("javascript:window.INTERFACE.processContent(document.getElementsByTagName('body')[0].innerText);");
					            	        t=false;
					            	        Log.i("StartActivity", "OnPageFinished. Should be logged in at this point");  	        
					            	}
					             }  
				             });  
					       myWebView.loadUrl(StartActivity.url);
					      
				} catch (Exception e) {Log.i("UserClass.FetchUIDList","Exception");}
		    	}
		}	
	
	
	
	
	public static class FetchEachUID implements Runnable {      
		   private WebView myWebView;
		   private TextView feedback;
		   private String uid;
		   private Context context;
		   private ArrayList<String> keys= new ArrayList<String>();
	       public FetchEachUID(WebView myWebView, TextView feedback,String uid, Context context){			   
			   this.feedback=feedback;this.uid=uid;this.context=context;
			   this.myWebView=myWebView;			  
		   }
		    @Override
			public synchronized void run() {
		    	
		     	  try {
				     	class MyJavaScriptInterface {     	 
					            private TextView contentView;
					           
					            public MyJavaScriptInterface(TextView aContentView) {contentView = aContentView;}
					            @JavascriptInterface 
					            public void processContent(String hp) { 
					                final String content =hp;
					                Thread thread = new Thread(new Runnable() {
					                 	@Override
										public void run() { 
					                 		if(content.length()!=0){
					                    	         Log.i("url is ",myWebView.getUrl ());
							                    	 Log.i("FetchEachUID.each uid has length of ", content.length()+"");							                    	
							                    	 JsonElement jelement = new JsonParser().parse(content);						                    	
							                    	 JsonObject  jobject = jelement.getAsJsonObject();
							                      	 try{
							                      		   String uid = jobject.get("uid").toString().trim();
								                      	   uid=uid.replaceAll("^\"|\"$", "");								                      	   
								                      	   //get the name, tag_id, health_id, and attendance
							                      		   String name = jobject.get("name").toString().trim();
								                      	   name=name.replaceAll("^\"|\"$", "");
								                      	    Log.i("One uid", uid);Log.i("Name", name);								                      	    
								                      	   if (name!=""){
								                      	   		    UserClass.addResidentName(context, name,uid);							                      			   
										                      	    JsonObject atten_jarray=jobject.get("field_monthdata").getAsJsonObject();
										                      	    JsonArray atten_jarray2= atten_jarray.get("und").getAsJsonArray();	
										                      	    String atten = "" ;
										                     	    for (int i=0;i<atten_jarray2.size();i++){
										                      		  JsonObject a =atten_jarray2.get(i).getAsJsonObject();	
										                      		   atten = a.get("value").toString().trim(); 
										                      		   if (atten!="") break;
										                      	   }
										                      	   atten=atten.replaceAll("^\"|\"$", "");		
										                      	   UserClass.addResidentAtten(context,atten,uid);
										                      	   Log.i("Atten", atten);
										                      	 
										                      	   JsonObject health_jarray=jobject.get("field_bio_id").getAsJsonObject();
										                      	   JsonArray health_jarray2= health_jarray.get("und").getAsJsonArray();	
										                      	   String health_id="";
										                     	    for (int i=0;i<health_jarray2.size();i++){
										                      		  JsonObject a =health_jarray2.get(i).getAsJsonObject();	
										                      		  health_id = a.get("value").toString().trim();
										                      		  if (health_id!="") break;
										                      	   }
										                     	    health_id=health_id.replaceAll("^\"|\"$", "");
										                      	   UserClass.addResidentHealth(context, health_id, uid);
										                      	   Log.i("health_id", health_id);
										                      	 
										                      	   JsonObject tag_jarray=jobject.get("field_tag_id").getAsJsonObject();
										                      	   JsonArray tag_jarray2= tag_jarray.get("und").getAsJsonArray();	
										                      	   String  tag_id="";
										                     	   for (int i=0;i< tag_jarray2.size();i++){
										                      		  JsonObject a =tag_jarray2.get(i).getAsJsonObject();	
										                      		  tag_id = a.get("value").toString().trim();  
										                      		  if (tag_id!="")break;
										                      	   }
										                      	   tag_id=tag_id.replaceAll("^\"|\"$", "");
										                      	   UserClass.addResidentPatientId(context,tag_id,uid);
										                      	   Log.i("tag_id", tag_id);
								                      	   }
							                      	 }
							                      	 catch(Exception e){Log.i("FetchEachUID.each uid","Exception");}
					                         	}
					                    	  
					                      }
					                 });//end of  the class
					                 try {
					                	 thread.run();thread.join();
					                	 StartActivity.oneUidSet=true;
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
					            } 
					         }
				     	    
				     		 myWebView.getSettings().setJavaScriptEnabled(true);     
				     		 myWebView.setWebViewClient(new WebViewClient() {  
					        	 boolean t=true;
							     @Override public boolean shouldOverrideUrlLoading(WebView webView, String url) {webView.loadUrl(url);return false;  }      
					             @Override public void onPageFinished(WebView view, String url)  {  
					            	 if (t){  
					            	        myWebView.loadUrl("javascript: {" +
					                        "document.getElementById('edit-name').value = '"+StartActivity.account_name +"';" +
					                        "document.getElementById('edit-pass').value = '"+StartActivity.account_password+"';" +
					                        "document.getElementById('edit-submit').submit(); };");		 
					            		    myWebView.loadUrl("http://hectorreyeshouse.org/user/login");
					            		    myWebView.loadUrl("javascript:window.INTERFACE.processContent(document.getElementsByTagName('body')[0].innerText);");
					            	        t=false;	        
					            	}
					             }  
				             });  
					         myWebView.addJavascriptInterface(new MyJavaScriptInterface(feedback), "INTERFACE"); 					       					      		         
					         String each_url=StartActivity.intermediate_url+uid+".json";
					         Log.i("UserClass.eachUID thread", "url= "+each_url);
					         myWebView.loadUrl(each_url);	
				} catch (Exception e) {Log.i("UserClass.FetchUIDList","Exception");}
		    	}
		    //end of run method
		    public ArrayList<String> returnResult(){
		    	return keys;
		    }
		}	
	
}
