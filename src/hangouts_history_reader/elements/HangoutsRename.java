package hangouts_history_reader.elements;

import json.elements.JSONObject;

import java.util.StringTokenizer;

import static hangouts_history_reader.Main.getStringValue;

public class HangoutsRename extends HangoutsEvent {
    public final EventType type() {
        return EventType.RENAME_CONVERSATION;
    }

    private final String OLD_NAME, NEW_NAME;

    public HangoutsRename(JSONObject callData) {
        super(callData);

        OLD_NAME = getStringValue(callData.findElements("conversation_rename.old_name"));
        NEW_NAME = getStringValue(callData.findElements("conversation_rename.new_name"));
    }

    public String toString(HangoutsChat chat) {
        StringTokenizer st = new StringTokenizer("RENAMED CHAT FROM \"" + OLD_NAME + "\" TO \"" + NEW_NAME + "\"");
        return toStringHeaderHelper(chat).toString() + toStringMessageHelper(st).toString();
    }
}
