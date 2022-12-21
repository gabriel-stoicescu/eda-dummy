package org.cosmin.util;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Constants {
    public final static DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
}
