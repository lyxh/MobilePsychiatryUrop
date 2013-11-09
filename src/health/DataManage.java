package health;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import main.MobilePsychiatry;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Three preferences: 
 * name1: store all (tag_id,patient_id) pairs 
 * name2: store all (patient_id+"date"+date, score("EDA,accelerometer")). 
 * Used by fetchEda and fetchAcce methods to get the scores for a particular week. 
 * name3: store all (patient_id,yyyy-mm-dd-hh) latest date
 * pair. Used by query to get download file date range.
 * 

 * @author yixin
 */
public class DataManage extends Activity {
	public static final String name1 = "patient_ids";
	public static final String name2 = "idDateToScore";
	public static final String name3 = "idToLatestDate";
	public static float K = (float) 1.05;
	public static boolean testTxt = true;
	public static boolean ignoreTimeWrongSample =false;
	public static boolean debug=true;
	public static final BigInteger start = new BigInteger("1368090122000");
	// public final static String eol = System.getProperty("line.separator");

	@Override protected void onCreate(Bundle state) {super.onCreate(state);}
	@Override protected void onStop() {super.onStop();}

	/** Helper method: log the preference of a given name */
	public static void logName(Context context, String name) {
		if (debug){
			SharedPreferences sharedPref = context.getSharedPreferences(name,Context.MODE_PRIVATE);
			Map<String, ?> keys = sharedPref.getAll();
			String message = "";
			for (Map.Entry<String, ?> entry : keys.entrySet()) {
				message += entry.getKey() + ": " + entry.getValue().toString()+ ", ";
			}
			Log.i("DataManage.logName" + name, message);
	   }
	}


	// name1 1 111111111 preference
	/** Add (tag_id, health_id) pair to name1 preference */
	public static void addResidentPatientId(Context ctx, String tag_id,String health_id) {
		SharedPreferences sharedPref = ctx.getSharedPreferences(name1,Context.MODE_PRIVATE);
		sharedPref.edit().putString(tag_id, health_id).commit();
	}

	/** Check if name1 preference has key tag_id */
	public static boolean patientExists(Context ctx, String tag_id) {
		String exist = "No such resident";
		try {
			SharedPreferences sharedPref = ctx.getSharedPreferences(name1,Context.MODE_PRIVATE);
			exist = sharedPref.getString(tag_id,"No such resident");
		} catch (NullPointerException ex) {
			if (debug){Log.i("DataManage.patientExists", "NullPointerException");}
		}
		if (exist.equals("No such resident")) {return false;}
		return true;
	}

	/** Delete (tag_id, health_id) pair from name1 preference */
	public static void deleteResidentPatientId(Context ctx, String tag_id) {
		SharedPreferences sharedPref = ctx.getSharedPreferences(name1,Context.MODE_PRIVATE);
		sharedPref.edit().remove(tag_id).commit();
	}

	// name22222222222
	/** Add (health_id+"date"+date, score) pair to name2 preference */
	public static void addResidentDateToScore(Context ctx, String health_id,String date, String score) {
		SharedPreferences sharedPref = ctx.getSharedPreferences(name2,Context.MODE_PRIVATE);
		sharedPref.edit().putString(health_id + "date" + date, score).commit();
	}

	/** Add pair with key health_id+"date"+date from name2 preference */
	public static void delResidentDateToScore(Context ctx, String health_id,String date) {
		SharedPreferences sharedPref = ctx.getSharedPreferences(name2,Context.MODE_PRIVATE);
		String key = health_id + "date" + date;
		if (sharedPref.contains(key)) {
			sharedPref.edit().remove(key).commit();
		}
	}

	// name3333
	/**find out the latest time in times. Each time already formatted as yyyy-mm-dd-hh
	 * Add it to name3 preference
	 * Require the times to have at least one item!
	 * */
	public static void updateLatest(Context ctx, ArrayList<String> times, String health_id) {
		String max_time=""; long value=0;
		for (String time:times){
             long epoch=stringToEpoch(time);
             if (epoch>=value){max_time=time;value=epoch;}
		}
		SharedPreferences sharedPref = ctx.getSharedPreferences(name3,Context.MODE_PRIVATE);
		sharedPref.edit().putString(health_id, max_time).commit();
	}

	/** Find the pair with key health_id from name3 preference */
	public static String getLatestDate(Context ctx, String health_id) {
		SharedPreferences sharedPref = ctx.getSharedPreferences(name3,Context.MODE_PRIVATE);
		return sharedPref.getString(health_id, "");
	}

	/** Check if the pair with key health_id exists in name3. */
	public static boolean patientLatestDateExists(Context ctx, String health_id) {
		String exist = "";
		try {
			SharedPreferences sharedPref = ctx.getSharedPreferences(name3,Context.MODE_PRIVATE);
			exist = sharedPref.getString(health_id, "No such resident");
		} catch (NullPointerException ex) {
			if (debug){Log.i("DataManage.patientExists", "NullPointerException");}
		}
		if (exist.equals("No such resident")) {return false;} 
		else {return true;}
	}

	
	/**
	 * Return String in the format of time(in the format of YYYY-mm-dd-hh),
	 * score x y z,score x y z, score x y z,...
	 * 
	 * @param fileContent
	 * @return
	 */
	public static ArrayList<String[]> parseDataFile(String fileContent) {
		fileContent = fileContent.replaceAll("[*]", "");
		String[] result = fileContent.split("\\r?\\n");
		if (debug){Log.i("DataManage.parseFile","after splitting, get "+result.length+ " items");}
		ArrayList<String[]> returned = new ArrayList<String[]>();
		boolean setTime = false;
		for (int i = 1; i < result.length; i++) {
			if ((result[i].length() >= 54) && result[i].substring(0, 4).equals("0007")) {
				// Log.i("DataManage.parseDataFile",result[i]);
				String time = result[i].substring(6, 6 + 16);
				// Ai (in units of g’s) = (Ai -32768)/256
				String x = String.valueOf((Integer.parseInt(result[i].substring(22, 22 + 4), 16) - 32768) / 256);
				String y = String.valueOf((Integer.parseInt(result[i].substring(26, 26 + 4), 16) - 32768) / 256);
				String z = String.valueOf((Integer.parseInt(result[i].substring(30, 30 + 4), 16) - 32768) / 256);
				// String skinTemp=result[i].substring(34, 34+4);
				String edap = result[i].substring(38, 38 + 4);
				String edabias = result[i].substring(42, 42 + 4);
				// String ambientTemp=result[i].substring(46, 46+4);
				// String ambientHumi=result[i].substring(50, 50+4);
				// String check=result[i].substring(54, 54+2);
				BigInteger timeDec = new BigInteger(time, 16);
				float EDA = getEDA(edap, edabias);
				boolean validSample = (EDA >= 0 & timeDec.compareTo(start)==1 & ignoreTimeWrongSample)|| (EDA >= 0 & !ignoreTimeWrongSample);
				if (validSample) {
					if (!setTime) {
						String[] item = new String[1];
						String date = epochToString(Long.valueOf(String.valueOf(timeDec)));
						item[0] = date;
						returned.add(item);
						setTime = true;
					}
					String[] item = new String[4];
					item[0] = String.valueOf(EDA);
					item[1] = x;
					item[2] = y;
					item[3] = z;
					returned.add(item);
				}
			}
		}
		return returned;
	}

	/** calculate eda from edap and edabias.*/
	public static float getEDA(String edap, String edabias) {
		int edaP = Integer.parseInt(edap, 16);
		int edaB = Integer.parseInt(edabias, 16);
		return K * ((edaB - edaP) / (float) (4096 - edaB));
	}

	
	/**
	 * Given the health_id and file_id, download the original file and parse it.
	 * Calculate the time and final score. Add the pair to name2 preference.
	 * Time in formate yyyy-mm-dd-01, score in format EDA + "acce" + ACC.
     * @return [yyyy-mm-dd-01, EDA, ACC]
	 */
	public synchronized static String[] downloadFile(String health_id,String file_id, Context context) {
		String[] result = new String[3];
		result[0] = "";result[1] = "";result[2] = "";
		// using a new thread since there is network access!!!!
		try {
			String f = "";
			if (testTxt) {f = DataManage.readTxt(file_id,MobilePsychiatry.getAppContext());} 
			else {
				HelloRunnable runnable = new HelloRunnable(health_id, file_id,context);
				Thread a = new Thread(runnable);
				a.start();
				a.join();
				f = runnable.returnResult();
			}
			if (debug){Log.i("downloadFile","Content before parsing has length " + f.length());}
			ArrayList<String[]> afterParsing = parseDataFile(f);
			if (debug){Log.i("downloadFile", "Content after parsing has length "+ afterParsing.size());}

			String date = afterParsing.get(0)[0];
			afterParsing.remove(0);
			// next, calculate the score and accelerometer movement.
			Double[] resultScores = getScore(afterParsing, context);
			String EDA = String.valueOf(resultScores[0]);
			String ACC = String.valueOf(resultScores[1]);
			String score = EDA + "acce" + ACC;
			addResidentDateToScore(context, health_id, date, score);
			result[0] = date;result[1] = EDA;result[2] = ACC;
			Log.i("downloadFile:date=item[0] returned by downloadFile, time converted to yyyy-mm-dd-hh", date);
			Log.i("downloadFile:EDA=item[1] returned by downloadFile", EDA);
			Log.i("downloadFile:ACC=item[2] returned by downloadFile", ACC);
			
		} catch (Exception e) { if (debug) Log.i("DataManage.downloadFile", "Exception");}
		return result;
	}

	/**
	 * Read a txt file stored in assets folder, file has the name file_id+".txt"
	 */
	public static String readTxt(String file_id, Context context) {
		Log.i("DataManage.readFile", "start readText");
		StringBuilder text = new StringBuilder();
		AssetManager am = context.getAssets();
		try {
			InputStream is = am.open(file_id + ".txt");
			InputStreamReader inputreader = new InputStreamReader(is);
			BufferedReader buffreader = new BufferedReader(inputreader);
			String line;
			try {
				while ((line = buffreader.readLine()) != null) {
					text.append(line);text.append('\n');
				}
			} catch (IOException e) {if (debug)Log.i("DataManage.readFile", "IoException");}
		} catch (IOException e) {if (debug)Log.i("DataManage.readFile", "IoException");}
		return text.toString();
	}



	/**
	 * Given epoch time(milisec), convert it to String time in the format of
	 * yyyy-mm-dd-hh, start of the day time in epoch,
	 */
	public static String epochToString(Long time) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh");
		return formatter.format(new Date(time));
	}
	
	/**
	 * Given yyyy-mm-dd-hh, convert it to epoch
	 */
	public static long stringToEpoch(String time) {
		long epoch=0;
		if (time.length()==13){
		    SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd-hh");
		    Date date = new Date();
			try {
				date = df.parse(time);
			} catch (ParseException e) {
				Log.i("DataManage.stringToEpoch","ParseException");
			}
		    epoch = date.getTime();
		}
		return epoch; 		
	}
	

	/**
	 * Return the score calculated from the files. Score = MeanEDA * standard
	 * deviation. Accelerometer score=... Given an arrayList of String[], each
	 * items is: EDA score.,x,y,z., calculate the EDA score. Return score is
	 * [0,0] if the result is empty.
	 * 
	 * @return double[]:[EDA score, accelerometer score]
	 */
	public static Double[] getScore(ArrayList<String[]> fileContent,
			Context context) {
		// next calculate the average.
		if (fileContent.size() < 1) {
			Double[] result = new Double[2];
			result[0] = 0.0;
			result[1] = 0.0;
			return result;
		}
		double sum = 0, sum2 = 0;
		// loop through every item
		for (String[] one_item : fileContent) {
			String EDA = one_item[0];
			BigDecimal num = new BigDecimal(EDA);
			String numWithNoExponents = num.toPlainString();
			sum += Float.parseFloat(numWithNoExponents);
			int x = Integer.parseInt(one_item[1]);
			int y = Integer.parseInt(one_item[2]);
			int z = Integer.parseInt(one_item[3]);
			double dis = Math.sqrt((x * x + y * y + z * z));
			sum2 += dis;
		}
		double mean = sum / (fileContent.size());
		double mean2 = sum2 / (fileContent.size());

		// calculate the std
		double std = 0, std2 = 0;
		for (String[] one_item : fileContent) {
			String EDA = one_item[0];
			BigDecimal num = new BigDecimal(EDA);
			String numWithNoExponents = num.toPlainString();
			std = std + Math.pow(Float.parseFloat(numWithNoExponents)- mean, 2);
			int x = Integer.parseInt(one_item[1]);
			int y = Integer.parseInt(one_item[2]);
			int z = Integer.parseInt(one_item[3]);
			double dis = Math.sqrt((x * x + y * y + z * z));
			std2 = std2 + Math.pow(dis - mean, 2);
		}
		std = std / (fileContent.size());
		std = Math.sqrt(std);
		if (debug){
			Log.i("sum is ", "" + sum);
			Log.i("mean is ", "" + mean);
			Log.i("std is ", "" + std);
		}
		std2 = std2 / (fileContent.size());
		std2 = Math.sqrt(std2);
		if (debug){
			Log.i("sum2 is ", "" + sum2);		
			Log.i("mean2 is ", "" + mean2);
			Log.i("std2 is ", "" + std2);
		}
		Double[] finalScore = new Double[2];
		finalScore[0] = mean * std;
		finalScore[1] = mean2 * std2;
		return finalScore;
	}

	/**
	 * Fetch the score for seven days and a particular health_id From the name2
	 * preference. If a score does not exist, add "0" to the list.
	 * Note that the scores returned are a string of formate eda+"acce"+acce
	 * @return
	 */
	public static ArrayList<String> fetchEdaScoreForWeekYear(Context context,ArrayList<String> sevenDays, String health_id) {
		ArrayList<String> result = new ArrayList<String>();
		for (String date : sevenDays) {
			for (int hour = 00; hour < 24; hour++) {
				String formatedHour = String.format("%02d", hour);
				String vals = "0";
				try {
					SharedPreferences sharedPref = context.getSharedPreferences(name2, Context.MODE_PRIVATE);
					vals = sharedPref.getString(health_id + "date" + date + "-"+ formatedHour, "0");
					if (!vals.equals("0")){
						int start_index_of_date=vals.indexOf("a");
						vals=vals.substring(0,start_index_of_date);
					}
				} 
				catch (NullPointerException exc) {Log.i("DataManage.getResidetCompleteDate","NullPointerException");}
				result.add(vals);
			}
		}
		if (debug){
			String message="";
			for (String val:result) {message += val+ ", ";}
			Log.i("DataManage.fetchEdaScoreForWeekYear returned a list of ", message);
		}
		return result;
	}

	
	/**
	 * Fetch the score for seven days and a particular health_id From the name2
	 * preference. If a score does not exist, add "0" to the list.
	 * Note that the scores returned are a string of formate eda+"acce"+acce
	 * TODO: Return acce score to the proper range?
	 * @return
	 */
	public static ArrayList<String> fetchAcceScoreForWeekYear(Context context,ArrayList<String> sevenDays, String health_id) {
		ArrayList<String> result = new ArrayList<String>();
		for (String date : sevenDays) {
			for (int hour = 00; hour < 24; hour++) {
				String formatedHour = String.format("%02d", hour);
				String vals = "0";
				try {
					SharedPreferences sharedPref = context.getSharedPreferences(name2, Context.MODE_PRIVATE);
					vals = sharedPref.getString(health_id + "date" + date + "-"+ formatedHour, "0");					
					if (!vals.equals("0")){
						int end_index_of_date=vals.indexOf("e");
						vals=vals.substring(end_index_of_date+1,vals.length());
					}					
				} 
				catch (NullPointerException exc) {Log.i("DataManage.getResidetCompleteDate","NullPointerException");}
				result.add(vals);
			}
		}
		if (debug){
			String message="";
			for (String val:result) {message += val+ ", ";}
			Log.i("DataManage.fetchAcceScoreForWeekYear returned a list of ", message);
		}
		return result;
	}
	
	
	// threads.
	/**Download the file of id file_id with health_id. Call returnResult() method to get the file content.*/
	public static class HelloRunnable implements Runnable {
		public String file_id = "", health_id = "", result = "";public Context context;
		public HelloRunnable(String health_id, String file_id, Context context) {
			this.health_id = health_id;this.file_id = file_id;this.context = context;
		}
		@Override
		public synchronized void run() {
			try {result = Client.downloadFileOfId(file_id);} 
			catch (Exception e) {Log.i("HelloRunnable", "Exception");}
		}
        public String returnResult() {return result;}
	}

	/**
	 * Helper method
	 */
	public static ArrayList<String> getCreatedDataFromId(int i,String start_time, String now) {
		FetchFileIdList runnable = new FetchFileIdList(i, start_time, now);
		Thread a = new Thread(runnable);
		a.start();
		try {a.join();} 
		catch (InterruptedException e) {
			Log.i("DataManage.getCreatedDataFromId", "InterruptedException");
		}
		return runnable.returnResult();
	}

	
	/**Get the file Id list from a start_time to end_time*/
	public static class FetchFileIdList implements Runnable {
		public int i = 13;public String start_time = "", now = "";
		public ArrayList<String> result = new ArrayList<String>();
		public FetchFileIdList(int i, String start_time, String now) {
			this.i = i;this.start_time = start_time;this.now = now;
		}
		@Override
		public synchronized void run() {
			try {result = Client.getCreatedDataFromId(i, start_time, now);} 
			catch (Exception e) {Log.i("FetchFileIdList", "Exception");}
		}
		public ArrayList<String> returnResult() {
			Log.i("FetchFileIdList.returnResult", "Size is" + result.size());
			return result;
		}
	}
}
