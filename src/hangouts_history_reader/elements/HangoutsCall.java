package hangouts_history_reader.elements;

import json.elements.JSONObject;

import static hangouts_history_reader.Main.getStringValue;

public class HangoutsCall extends HangoutsEvent {
    public final EventType type() {
        return EventType.HANGOUT_CALL;
    }

    private final CallStatus CALL_ACTION;

    public HangoutsCall(JSONObject callData) {
        super(callData);

        CALL_ACTION = CallStatus.fromString(getStringValue(callData.findElements("hangout_event.event_type")));
    }

    public String toString(HangoutsChat chat) {
        StringBuilder builder = toStringHeaderHelper(chat);
        switch (CALL_ACTION) {
            case START:
                builder.append("STARTED VOICE CALL");
                break;

            case END:
                builder.append("ENDED VOICE CALL");
                break;

            case UNKNOWN:
                builder.append("MODIFIED VOICE CALL");
        }
        return builder.append("\n").toString();
    }

    public enum CallStatus {
        START, END, UNKNOWN;

        public static CallStatus fromString(String s) {
            switch (s) {
                case "START_HANGOUT":
                    return START;

                case "END_HANGOUT":
                    return END;

                default:
                    return UNKNOWN;
            }
        }
    }
}
