package com.graham.nofreeride.utils;

/**
 * Created by grahamherceg on 3/24/18.
 */

public class Constants {
    public interface ACTION {
        String MAIN_ACTION = "com.graham.nofreeride.action.main";
        String EXTERNALSTOP_ACTION = "com.graham.nofreeride.action.externalstop";
        String STARTFOREGROUND_ACTION = "com.graham.nofreeride.action.startforeground";
        String STOPFOREGROUND_ACTION = "com.graham.nofreeride.action.stopforeground";

        String SENDLOCATIONS_ACTION = "com.graham.nofreeride.action.sendlocations";
        String STOPMESSAGE_ACTION = "com.graham.nofreeride.action.stopmessage";

    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
        String FOREGROUND_CHANNEL_ID = "M_CH_ID";
    }

    public interface CONSTANTS {
        int MAX_PASSENGERS = 5;
        int MIN_PASSENGERS = 0;
        double PCT_INSURANCE = 0.0002;
    }
}
