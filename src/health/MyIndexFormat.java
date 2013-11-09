package health;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

	public class MyIndexFormat extends Format {

	    public String[] Labels = null;

	        @Override
	        public StringBuffer format(Object obj, StringBuffer toAppendTo,  FieldPosition pos) {

	            // try turning value to index because it comes from indexes
	            // but if is too far from index, ignore it - it is a tick between indexes
	            float fl = ((Number)obj).floatValue();
	            int index = Math.round(fl);
	            if(Labels == null || Labels.length <= index || Math.abs(fl - index) > 0.1)
	                return new StringBuffer("");    

	            return new StringBuffer(Labels[index]); 
	        }

			@Override
			public Object parseObject(String string, ParsePosition position) {
				// TODO Auto-generated method stub
				return null;
			}
	        }