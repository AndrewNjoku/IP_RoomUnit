package com.tunstall.utility;

import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SimpleXmlParser {

	private static final String ns = null;

	private final String TAG_RESPONSE = "response";
	private final String TAG_ACk = "ack";
	private final String TAG_BUTTON_STATE = "buttonState";
	private final String TAG_ERROR = "error";
	private final String TAG_MESSAGE_ID = "messageId";
	private final String TAG_VIDEO_LOCATION = "doorVideoLocation";

	private final String ATTRIBUTE_PROTOCOL = "protocol";
	private final String ATTRIBUTE_LED = "led";
    private final String ATTRIBUTE_DURATION = "duration";
    private final String ATTRIBUTE_ENABLED = "enabled";
    private final String ATTRIBUTE_VISIBLE = "visible";

	public Entry parse(InputStream in) throws XmlPullParserException,
			IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);

			parser.nextTag();
			return readResponse(parser);
		} finally {
			in.close();
		}
	}

	private Entry readResponse(XmlPullParser parser)
			throws XmlPullParserException, IOException {

		Entry entrie = null;
		String name = parser.getName();

		if (name.equals(TAG_RESPONSE)) {
			entrie = readEntry(parser);
		} else {
			skip(parser);
		}

		return entrie;
	}

	// This class represents a single entry

	public static class Entry {
		public final String ack;

		public final String errorMessage;
		public final String protocol;
		public List<AttributeTag> buttonStateList = null;
		public final long messageId;
        public ArrayList<String> doorVideoURLs;

		private Entry(String ack, String errorMessage, String protocol,
				List<AttributeTag> buttonStateList, long messageId,
				ArrayList<String> doorVideoURLs) {
			this.ack = ack;

			this.errorMessage = errorMessage;
			this.protocol = protocol;
			this.buttonStateList = buttonStateList;
			this.messageId = messageId;
            this.doorVideoURLs = doorVideoURLs;
		}

		@Override
		public String toString() {
		
			StringBuilder sb = new StringBuilder();
			sb.append("messageId:" + " " + messageId);
			sb.append("ack:" + " " + ack);
			sb.append("\n");

			for (AttributeTag tag : buttonStateList) {
				sb.append("ledState:" + " " + tag.tag);
				sb.append("\n");
                sb.append("duration:" + " " + tag.duration);
                sb.append("\n");
                sb.append("enabled:" + " " + tag.enabled);
                sb.append("\n");
                sb.append("visible:" + " " + tag.visible);
                sb.append("\n");
				sb.append("button:" + " " + tag.attribute);
				sb.append("\n");
			}
			sb.append("errorMessage:" + " " + errorMessage);
			sb.append("\n");
			sb.append("protocol:" + " " + protocol);
			sb.append("\n");

			return sb.toString();
			// return super.toStr();
		}
	}

	// Parses the contents of an entry. If it encounters a title, summary, or
	// link tag, hands them
	// off
	// to their respective &quot;read&quot; methods for processing. Otherwise,
	// skips the tag.
	private Entry readEntry(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, TAG_RESPONSE);
		String protocol = parser.getAttributeValue(null, ATTRIBUTE_PROTOCOL);
		String ack = "";
		long messageId = -1;

		AttributeTag buttonState = new AttributeTag();
		String errorMessage = "";

		List<AttributeTag> buttonStateList = new ArrayList<AttributeTag>();

        ArrayList<String> doorVideoURLs = new ArrayList<String>();

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(TAG_ACk)) {
				ack = readACK(parser);
			} else if (name.equals(TAG_ERROR)) {
				errorMessage = readErrorCode(parser);

			} else if (name.equals(TAG_BUTTON_STATE)) {
				buttonState = readButtonState(parser);
				buttonStateList.add(buttonState);
			} else if (name.equals(TAG_MESSAGE_ID)) {
				messageId = Long.parseLong(readMessageId(parser));

			} else if (name.equals(TAG_VIDEO_LOCATION)) {
				String doorVideoLocation = readDoorVideoLocation(parser);
				if (!TextUtils.isEmpty(doorVideoLocation)) {
					Log.d("SimpleXmlParser", "readEntry() doorVideoLocation " + doorVideoLocation);
                	doorVideoURLs.add(doorVideoLocation);
				}
			} else {
				skip(parser);
			}
		}
		return new Entry(ack, errorMessage, protocol, buttonStateList,
				messageId, doorVideoURLs);
	}

	// Processes ACK
	private String readACK(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAG_ACk);
		String ack = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, TAG_ACk);
		return ack;
	}

	// Processes messageID
	private String readMessageId(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAG_MESSAGE_ID);
		String messageId = readText(parser);
        Log.d("SimpleXmlParser", "messageId " + messageId);
		parser.require(XmlPullParser.END_TAG, ns, TAG_MESSAGE_ID);
		return messageId;
	}

	// Processes doorVideoLocation
	private String readDoorVideoLocation(XmlPullParser parser)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, TAG_VIDEO_LOCATION);
		String doorVideoLocation = readText(parser);
        Log.d("SimpleXmlParser", "readDoorVideoLocation() doorVideoLocation " + doorVideoLocation);
        parser.require(XmlPullParser.END_TAG, ns, TAG_VIDEO_LOCATION);
		return doorVideoLocation;
	}

	
	private String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	// Skips tags the parser isn't interested in. Uses depth to handle nested
	// tags. i.e.,
	// if the next tag after a START_TAG isn't a matching END_TAG, it keeps
	// going until it
	// finds the matching END_TAG (as indicated by the value of "depth" being
	// 0).
	private void skip(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

	private AttributeTag readButtonState(XmlPullParser parser)
			throws IOException, XmlPullParserException {

		AttributeTag buttonstate = new AttributeTag();
		parser.require(XmlPullParser.START_TAG, ns, TAG_BUTTON_STATE);

		String ledState = parser.getAttributeValue(null, ATTRIBUTE_LED);
        String duration = parser.getAttributeValue(null, ATTRIBUTE_DURATION);
        String enabled = parser.getAttributeValue(null, ATTRIBUTE_ENABLED);
        String visible = parser.getAttributeValue(null, ATTRIBUTE_VISIBLE);
        if (duration == null) // Check for a NULL formatted namespace duration field
        {
            duration = "0";  //If null is found force the duration field to zero
        }
        if (enabled == null) // Check for a NULL formatted namespace enabled field
        {
            enabled = "true";  //If null is found force the enabled field to true
        }
        if (visible == null) // Check for a NULL formatted namespace visible field
        {
            visible = "true";  //If null is found force the enabled field to true
        }
        String button = readText(parser);

		buttonstate.attribute = button;
		buttonstate.tag = ledState;
        buttonstate.duration = duration;
        buttonstate.enabled = enabled;
        buttonstate.visible = visible;

		parser.require(XmlPullParser.END_TAG, ns, TAG_BUTTON_STATE);
		return buttonstate;
	}

	private String readErrorCode(XmlPullParser parser) throws IOException,
			XmlPullParserException {

		parser.require(XmlPullParser.START_TAG, ns, TAG_ERROR);
		String message = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, TAG_ERROR);
		return message;

	}

	public class AttributeTag {
		public String attribute;
		public String tag;
        public String duration;
        public String enabled;
        public String visible;

	}

}
