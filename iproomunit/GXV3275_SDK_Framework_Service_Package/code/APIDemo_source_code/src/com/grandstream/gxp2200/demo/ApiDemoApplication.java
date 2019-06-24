/****************************************************************************
 *
 * FILENAME:        com.grandstream.gxp2200.demo.ApiDemoApplication.java
 *
 * LAST REVISION:   $Revision: 1.0
 * LAST MODIFIED:   $Date: 2013/01/22 02:14:08 2013-5-10
 *
 * DESCRIPTION:     The class encapsulates the music ring tone operations.
 *
 * vi: set ts=4:
 *
 * Copyright (c) 2009-2013 by Grandstream Networks, Inc.
 * All rights reserved.
 *
 * This material is proprietary to Grandstream Networks, Inc. and,
 * in addition to the above mentioned Copyright, may be
 * subject to protection under other intellectual property
 * regimes, including patents, trade secrets, designs and/or
 * trademarks.
 *
 * Any use of this material for any purpose, except with an
 * express license from Grandstream Networks, Inc. is strictly
 * prohibited.
 *
 ***************************************************************************/
package com.grandstream.gxp2200.demo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class ApiDemoApplication extends Application {
	
	public ApiDemoApplication() {
		
		super();
	}

    public static void initKeyInputPreference(Context context){
        SharedPreferences mShared = context.getSharedPreferences(
                KeyInputDemo.class.getSimpleName(), 0);
        if(!mShared.contains("0")){
            SharedPreferences.Editor mEditor = mShared.edit();
            
            mEditor.putString("0","UNKNOWN");
            mEditor.putString("1","SOFT_LEFT");
            mEditor.putString("2","SOFT_RIGHT");
            mEditor.putString("3","HOME");
            mEditor.putString("4","BACK");
            mEditor.putString("5","CALL");
            mEditor.putString("6","ENDCALL");
            mEditor.putString("7","0");
            mEditor.putString("8","1");
            mEditor.putString("9","2");
            mEditor.putString("10","3");
            mEditor.putString("11","4");
            mEditor.putString("12","5");
            mEditor.putString("13","6");
            mEditor.putString("14","7");
            mEditor.putString("15","8");
            mEditor.putString("16","9");
            mEditor.putString("17","STAR");
            mEditor.putString("18","POUND");
            mEditor.putString("19","DPAD_UP");
            mEditor.putString("20","DPAD_DOWN");
            mEditor.putString("21","DPAD_LEFT");
            mEditor.putString("22","DPAD_RIGHT");
            mEditor.putString("23","DPAD_CENTER");
            mEditor.putString("24","VOLUME_UP");
            mEditor.putString("25","VOLUME_DOWN");
            mEditor.putString("26","POWER");
            mEditor.putString("27","CAMERA");
            mEditor.putString("28","CLEAR");
            mEditor.putString("29","A");
            mEditor.putString("30","B");
            mEditor.putString("31","C");
            mEditor.putString("32","D");
            mEditor.putString("33","E");
            mEditor.putString("34","F");
            mEditor.putString("35","G");
            mEditor.putString("36","H");
            mEditor.putString("37","I");
            mEditor.putString("38","J");
            mEditor.putString("39","K");
            mEditor.putString("40","L");
            mEditor.putString("41","M");
            mEditor.putString("42","N");
            mEditor.putString("43","O");
            mEditor.putString("44","P");
            mEditor.putString("45","Q");
            mEditor.putString("46","R");
            mEditor.putString("47","S");
            mEditor.putString("48","T");
            mEditor.putString("49","U");
            mEditor.putString("50","V");
            mEditor.putString("51","W");
            mEditor.putString("52","X");
            mEditor.putString("53","Y");
            mEditor.putString("54","Z");
            mEditor.putString("55","COMMA");
            mEditor.putString("56","PERIOD");
            mEditor.putString("57","ALT_LEFT");
            mEditor.putString("58","ALT_RIGHT");
            mEditor.putString("59","SHIFT_LEFT");
            mEditor.putString("60","SHIFT_RIGHT");
            mEditor.putString("61","TAB");
            mEditor.putString("62","SPACE");
            mEditor.putString("63","SYM");
            mEditor.putString("64","EXPLORER");
            mEditor.putString("65","ENVELOPE");
            mEditor.putString("66","ENTER");
            mEditor.putString("67","DEL");
            mEditor.putString("68","GRAVE");
            mEditor.putString("69","MINUS");
            mEditor.putString("70","EQUALS");
            mEditor.putString("71","LEFT_BRACKET");
            mEditor.putString("72","RIGHT_BRACKET");
            mEditor.putString("73","BACKSLASH");
            mEditor.putString("74","SEMICOLON");
            mEditor.putString("75","APOSTROPHE");
            mEditor.putString("76","SLASH");
            mEditor.putString("77","AT");
            mEditor.putString("78","NUM");
            mEditor.putString("79","HEADSETHOOK");
            mEditor.putString("80","FOCUS");
            mEditor.putString("81","PLUS");
            mEditor.putString("82","MENU");
            mEditor.putString("83","NOTIFICATION");
            mEditor.putString("84","SEARCH");
            mEditor.putString("85","MEDIA_PLAY_PAUSE");
            mEditor.putString("86","MEDIA_STOP");
            mEditor.putString("87","MEDIA_NEXT");
            mEditor.putString("88","MEDIA_PREVIOUS");
            mEditor.putString("89","MEDIA_REWIND");
            mEditor.putString("90","MEDIA_FAST_FORWARD");
            mEditor.putString("91","MUTE");
            mEditor.putString("92","PAGE_UP");
            mEditor.putString("93","PAGE_DOWN");
            mEditor.putString("94","PICTSYMBOLS");
            mEditor.putString("95","SWITCH_CHARSET");
            mEditor.putString("96","BUTTON_A");
            mEditor.putString("97","BUTTON_B");
            mEditor.putString("98","BUTTON_C");
            mEditor.putString("99","BUTTON_X");
            mEditor.putString("100","BUTTON_Y");
            mEditor.putString("101","BUTTON_Z");
            mEditor.putString("102","BUTTON_L1");
            mEditor.putString("103","BUTTON_R1");
            mEditor.putString("104","BUTTON_L2");
            mEditor.putString("105","BUTTON_R2");
            mEditor.putString("106","BUTTON_THUMBL");
            mEditor.putString("107","BUTTON_THUMBR");
            mEditor.putString("108","BUTTON_START");
            mEditor.putString("109","BUTTON_SELECT");
            mEditor.putString("110","BUTTON_MODE");
    
           mEditor.putString("200","PHONEBOOK ");
           mEditor.putString("201","HOLD ");
           mEditor.putString("202","HEADSET ");
           mEditor.putString("203","MSG ");
           mEditor.putString("204","TRNF ");
           mEditor.putString("205","CONF ");
           mEditor.putString("206","SEND ");
           mEditor.putString("207","SPEAKER ");
           
           mEditor.commit();
        }
    }
    
}

