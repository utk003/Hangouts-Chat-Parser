package hangouts_history_reader.elements;

import json.elements.JSONArray;
import json.elements.JSONObject;
import json.elements.JSONValue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import static hangouts_history_reader.Main.getStringValue;
import static hangouts_history_reader.Main.getValue;

public class HangoutsMessage extends HangoutsEvent {
    public final EventType type() {
        return EventType.REGULAR_CHAT_MESSAGE;
    }
    private final List<String> MESSAGES;

    public HangoutsMessage(JSONObject message) {
        super(message);

        // Get messages
        MESSAGES = new LinkedList<>();

        JSONArray arr = (JSONArray) getValue(message.findElements("chat_message.message_content.segment"), JSONValue.ValueType.ARRAY);
        if (arr != null) {
            Iterator<JSONValue> it = arr.iterator();
            while (it.hasNext()) {
                JSONValue obj = it.next();
                switch (getStringValue(obj.findElements("type"))) {
                    case "TEXT":
                    case "LINK":
                        String text = getStringValue(obj.findElements("text"));
                        if (text != null)
                            MESSAGES.add(text);
                        break;

                    case "LINE_BREAK":
                        MESSAGES.add(null);
                        break;

                    default:
                        break;
                }
            }
        }
        MESSAGES.add(null);
        for (JSONValue obj : message.findElements("chat_message.message_content.attachment[*].embed_item")) {
            String type = getStringValue(obj.findElements("type[*]")), path;
            switch (type) {
                case "PLUS_PHOTO":
                    path = "plus_photo";
                    break;

                default:
                    path = "";
                    break;
            }

            String url = getStringValue(obj.findElements(path + ".original_content_url"));
            if (url == null)
                url = getStringValue(obj.findElements(path + ".url"));

            if (url == null)
                continue;

            MESSAGES.add("UPLOADED " + url);
        }
    }

    public String toString(HangoutsChat chat) {
        StringBuilder builder = toStringHeaderHelper(chat);

        MESSAGES.add(null);
        Iterator<String> it = MESSAGES.iterator();
        while (it.hasNext()) {
            StringBuilder temp = new StringBuilder();

            String next = it.next();
            while (next != null) {
                temp.append(next);
                next = it.next();
            }
            if (temp.length() == 0)
                continue;

            builder.append(toStringMessageHelper(new StringTokenizer(temp.toString())));
        }
        return builder.toString();
    }
}