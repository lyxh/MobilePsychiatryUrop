package health;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyDateFormat extends Format { 

    private static final long serialVersionUID = 1L;    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("DDD:kk");

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        long timestamp = ((Number) obj).longValue();
        Date date = new Date(timestamp);
        return dateFormat.format(date, toAppendTo, pos);
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return null;    
    }
}
