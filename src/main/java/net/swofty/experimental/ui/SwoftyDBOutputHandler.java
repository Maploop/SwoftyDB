package net.swofty.experimental.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SwoftyDBOutputHandler {
    private final ConsoleInterface ci;
    public SwoftyDBOutputHandler(ConsoleInterface consoleInterface) {
        this.ci = consoleInterface;
    }


    private void log(Object o, String prefix) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String strDate = sdf.format(cal.getTime());

        SimpleDateFormat sdf1 = new SimpleDateFormat();
        sdf1.applyPattern("HH:mm:ss");
        Date date = null;
        try {
            date = sdf1.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String string = sdf1.format(date);

        this.ci.outputArea.append("[" + string + "]" + " [" + prefix + "] " + o.toString() + "\n");
    }

    public void println(Object o) {
        log(o, "U");
    }

    public void info(Object o) {
        log(o, "INFO");
    }

    public void severe(Object o) {
        log(o, "ERROR");
    }

    public void warning(Object o) {
        log(o, "WARNING");
    }
}
