package hangouts_history_reader.elements;

import json.elements.JSONObject;

import java.util.Date;
import java.util.StringTokenizer;

import static hangouts_history_reader.Main.getStringValue;

public abstract class HangoutsEvent implements Comparable<HangoutsEvent> {
    public final String EVENT_ID;

    protected final Date TIME_STAMP;
    public final String SENDER_ID;

    public static HangoutsEvent getEvent(JSONObject event) {
        EventType type = EventType.fromString(getStringValue(event.findElements("event_type")));
        switch (type) {
            case REGULAR_CHAT_MESSAGE:
                return new HangoutsMessage(event);

            case HANGOUT_CALL:
                return new HangoutsCall(event);

            case RENAME_CONVERSATION:
                return new HangoutsRename(event);

            case ADD_USER:
                return new HangoutsAddUser(event);

            case REMOVE_USER:
                return new HangoutsRemoveUser(event);

            case UNKNOWN:
            default:
                return null;
        }
    }

    protected HangoutsEvent(JSONObject event) {
        EVENT_ID = getStringValue(event.findElements("event_id"));
        if (EVENT_ID == null)
            throw new IllegalStateException("Unable to recreate Hangouts event - Cannot locate event id");

        SENDER_ID = getStringValue(event.findElements("sender_id.gaia_id"));
        if (SENDER_ID == null)
            throw new IllegalStateException("Unable to recreate Hangouts event - Cannot identify event sender");

        try {
            long timeStamp = Long.parseLong(getStringValue(event.findElements("timestamp")));
            TIME_STAMP = new Date(timeStamp / 1000);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Unable to recreate Hangouts event - Cannot determine event time");
        }
    }

    public abstract String toString(HangoutsChat chat);

    protected final StringBuilder toStringHeaderHelper(HangoutsChat chat) {
        StringBuilder builder = new StringBuilder();

        builder.append("[[").append(TIME_STAMP).append("]]");
        for (int i = builder.length(); i < 35; i++)
            builder.append(" ");

        builder.append(chat.resolveUser(SENDER_ID)).append(":");
        for (int i = builder.length(); i < 72; i++)
            builder.append(" ");

        return builder;
    }

    protected final StringBuilder toStringMessageHelper(StringTokenizer tokenizer) {
        return toStringMessageHelper(tokenizer, 120);
    }
    protected final StringBuilder toStringMessageHelper(StringTokenizer tokenizer, final int LINE_WRAPPING_LIMIT) {
        final int INDENT_WIDTH = 72;

        StringBuilder builder = new StringBuilder();

        StringBuilder line = new StringBuilder();
        for (int i = 0; i < INDENT_WIDTH; i++)
            line.append(" ");
        final String MESSAGE_INDENT = line.toString();

        line = new StringBuilder();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (line.length() + token.length() + INDENT_WIDTH > LINE_WRAPPING_LIMIT)
                if (line.length() * 3 > LINE_WRAPPING_LIMIT - INDENT_WIDTH) {
                    builder.append(line).append("\n").append(MESSAGE_INDENT);
                    line = new StringBuilder();
                }
            line.append(token).append(" ");
        }
        builder.append(line).append("\n").append(MESSAGE_INDENT);

        return builder;
    }

    @Override
    public final int compareTo(HangoutsEvent message) {
        return TIME_STAMP.compareTo(message.TIME_STAMP);
    }

    public abstract EventType type();
    public enum EventType {
        RENAME_CONVERSATION, REGULAR_CHAT_MESSAGE, HANGOUT_CALL, ADD_USER, REMOVE_USER, UNKNOWN;

        public static EventType fromString(String s) {
            switch (s) {
                case "RENAME_CONVERSATION":
                    return RENAME_CONVERSATION;

                case "REGULAR_CHAT_MESSAGE":
                    return REGULAR_CHAT_MESSAGE;

                case "HANGOUT_EVENT":
                    return HANGOUT_CALL;

                case "ADD_USER":
                    return ADD_USER;

                case "REMOVE_USER":
                    return REMOVE_USER;

                default:
                    return UNKNOWN;
            }
        }
    }

    @Override
    public final int hashCode() {
        return EVENT_ID.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HangoutsEvent && ((HangoutsEvent) obj).EVENT_ID.equals(EVENT_ID);
    }
}
