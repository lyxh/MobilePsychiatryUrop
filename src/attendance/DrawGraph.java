package attendance;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.View;

/**
 * DrawGraph is used for displaying the graph of sensors data.
 * @author yixin
 *
 */
public class DrawGraph  extends View{

	Paint p;
	private ArrayList<Integer> aList = new ArrayList<Integer>();
	int width;
	int height;

	int c[] = { Color.parseColor("#444444"),Color.parseColor("#777777") };

	public DrawGraph(Context context, ArrayList<Integer> data) {
	    super(context);
	    p = new Paint();
	    aList = data;
	}


	@Override
	public void draw(Canvas canvas) {
	    int x = 100;
	    int y =100;
	    float t = getTotal();
	    p.setColor(Color.parseColor("#78777D"));
	    p.setStyle(Style.STROKE);
	    p.setStrokeWidth(2);
	    //canvas.drawRect(0, 0, x - 1, y - 1, p);
	    int n = aList.size();
	    float curPos = -90;
	    p.setStyle(Style.FILL);
	    RectF rect = new RectF(20, 20, x - 20, y - 20);
	    for (int i = 0; i < n; i++) {
	        p.setColor(c[i]);
	        float thita = (t == 0) ? 0 : 360 * aList.get(i) / t;
	        canvas.drawArc(rect, curPos, thita, true, p);
	        curPos = curPos + thita;
	    }

	}

	/**
	 * Return the sum of all elements in a list
	 */
	  private float getTotal() {
	    int total = 0;
	    for (int i = 0; i < aList.size(); i++) {
	        total = total + aList.get(i);
	    }
	    return total;
	 }
	}
