package timer;
import health.DataManage;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyAlarmService extends Service {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.i("MyAlarmService","onCreate() called");
		//Toast.makeText(new StartActivity(), "MyAlarmService.onCreate()", Toast.LENGTH_LONG).show();
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("MyAlarmService","onBind() called");
		//Toast.makeText(this, "MyAlarmService.onBind()", Toast.LENGTH_LONG).show();
		return null;
	}
		
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("MyAlarmService","onDestroy() called");
		super.onDestroy();
		//Toast.makeText(this, "MyAlarmService.onDestroy()", Toast.LENGTH_LONG).show();
	}
	
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		Log.i("MyAlarmService","onStart() called");
		DataManage.addResidentPatientId(getApplicationContext(), "test_download", "12");
		//TODO：now onStart() called every 5 seconds! :)
		super.onStart(intent, startId);
		//Toast.makeText(this, "MyAlarmService.onStart()", Toast.LENGTH_LONG).show();
	}
	
	
	
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("MyAlarmService","onUnbind() called");
		//Toast.makeText(this, "MyAlarmService.onUnbind()", Toast.LENGTH_LONG).show();
		return super.onUnbind(intent);
	}



}
