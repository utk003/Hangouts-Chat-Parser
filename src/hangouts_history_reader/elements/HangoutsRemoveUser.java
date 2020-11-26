package hangouts_history_reader.elements;

import json.elements.JSONObject;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import static hangouts_history_reader.Main.getStringValues;

public class HangoutsRemoveUser extends HangoutsEvent {
    public final EventType type() {
        return EventType.REMOVE_USER;
    }

    private final List<String> USERS;
    private final boolean REMOVED_SELF;

    public HangoutsRemoveUser(JSONObject callData) {
        super(callData);

        Collection<String> removed = getStringValues(callData.findElements("membership_change.participant_id[*].gaia_id"));
        REMOVED_SELF = removed.remove(SENDER_ID);
        USERS = new LinkedList<>(removed);
    }

    public String toString(HangoutsChat chat) {
        StringBuilder toString = toStringHeaderHelper(chat);
        if (!USERS.isEmpty()) {
            StringBuilder builder = new StringBuilder("REMOVED ");
            int count = 0, total = USERS.size();
            for (String userID : USERS) {
                builder.append(chat.resolveUser(userID));

                if (++count != total) {
                    builder.append(", ");
                    if (count + 1 == total)
                        builder.append("and ");
                }
            }
            toString.append(toStringMessageHelper(new StringTokenizer(builder.append(" FROM THIS CHAT").toString())));
        }
        if (REMOVED_SELF)
            toString.append(toStringMessageHelper(new StringTokenizer("LEFT THIS CHAT")));
        return toString.toString();
    }
}
