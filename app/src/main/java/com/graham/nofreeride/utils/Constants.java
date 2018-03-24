package com.graham.nofreeride.utils;

/**
 * Created by grahamherceg on 3/24/18.
 */

public class Constants {
    public interface ACTION {
        String MAIN_ACTION = "com.graham.nofreeride.action.main";
        public static String STARTFOREGROUND_ACTION = "com.graham.nofreeride.action.startforeground";
        String STOPFOREGROUND_ACTION = "com.graham.nofreeride.action.stopforeground";
        String SENDLOCATIONS_ACTION = "com.graham.nofreeride.action.sendlocations";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
        public static String FOREGROUND_CHANNEL_ID = "M_CH_ID";
    }
}
