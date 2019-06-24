package com.tunstall.grandstream.Storage;

public class constants {


    // I have created a constants file to seperate hard-set values and provide better scoping and seperation of concerns




    // APP SETTINGS RELATED

    static class AppSettingConstants {

        private static final String LOG_TAG= "AppSettings";

        public static final String PREF_IP_ADDRESS = "ip_address";
        public static final String PREF_PORT = "port";
        public static final String PREF_RESIDENT_ID = "resident_id";

        public static final String PREF_DOOR_OPEN = "pref_door_open";
        public static final String PREF_DOOR_VIDEO = "pref_door_video";
        public static final String PREF_DOOR_PRIVACY = "pref_door_privacy";
        public static final String PREF_HOME_AWAY = "pref_home_away";
        public static final String PREF_IM_OK = "pref_im_ok";
        public static final String PREF_ALARM = "pref_alarm";
        public static final String PREF_WIFI_VISIBILITY = "pref_wifi";

        public static final String PREF_WIFI_SSID = "pref_wifi_ssid";
        public static final String PREF_WIFI_PWD = "pref_wifi_pwd";
        public static final String PREF_MAC_ADDRESS = "pref_mac_address";

        public static final String PREF_DOOR_VIDEO_TIMEOUT = "pref_door_video_timeout";
        public static final String PREF_CONFIG_URL = "pref_config_url";
        public static final String PREF_CONFIG_CHECK_INTERVAL = "pref_config_check_interval";
        public static final String PREF_SETTINGS_PIN = "pref_settings_pin";

        public static String PREF_LAST_CONFIG_MESSAGE = "pref_last_config_message";
        public static String LAST_CONFIG_MESSAGE_DEFAULT = "No Config downloaded";

        public static final String CHANGEABLE_APPEND = "changeable";

    }


    //SOCKET SERVICE RELATED

    public static class SocketServiceConstants {

        public static final String LOG_TAG = "SocketService";

        public static final String DOOR_OPEN = "doorOpen";
        public static final String DOOR_PRIVACY = "doorPrivacy";
        public static final String DOOR_VIDEO = "doorVideo";
        public static final String HOME_AWAY = "homeAway";
        public static final String IM_OK = "imOk";
        public static final String ALARM = "alarm";
        public static final String GET_STATES = "getStates";

        public static final String ERROR_CODE = "errorCode";
        public static final String ERROR_GENERAL_ERROR = "generalError";
        public static final String ERORR_UNKNOWN_RESIDENT = "unknownResident";
        public static final String ERORR_NO_ACTIVE_DOOR_CALL = "noActiveDoorCallForResident";

        public static final String LED_ON = "on";
        public static final String LED_OFF = "off";
        public static final String LED_FLASH = "flash";


        public static final String BUTTON_KEY = "button";
        public static final String LED_STATE_KEY = "ledState";
        public static final String LED_DURATION_KEY = "ledDuration";
        public static final String BUTTON_ENABLED_KEY = "buttonEnabled";
        public static final String BUTTON_VISIBLE_KEY = "buttonVisible";

        public static final String ACTION_SOCKET_DATA_MISSING = "com.tunstall.grandStream.DATA_MISSING";
        public static final String ACTION_SOCKET_BUTTON_STATE_CHANGED = "com.tunstall.grandStream.BUTTON_STATE_CHANGED";
        public static final String ACTION_SOCKET_IP_ISSUE = "com.tunstall.grandStream.IP_ISSUE";
        public static final String ACTION_SOCKET_RESPONSE_ERROR = "com.tunstall.grandStream.RESPONSE_ERROR";

        public static int getMsgResponseNotReceived() {
            return MSG_RESPONSE_NOT_RECEIVED;
        }

        public static int getMsgResponseButtonChanged() {
            return MSG_RESPONSE_BUTTON_CHANGED;
        }

        public static int getMsgIpIssue() {
            return MSG_IP_ISSUE;
        }

        public static int getMsgResponseError() {
            return MSG_RESPONSE_ERROR;
        }

        public static int getMsgStartVideoStream() {
            return MSG_START_VIDEO_STREAM;
        }


    }


}
