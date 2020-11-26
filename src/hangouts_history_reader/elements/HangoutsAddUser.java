package hangouts_history_reader.elements;

import json.elements.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import static hangouts_history_reader.Main.getStringValues;

public class HangoutsAddUser extends HangoutsEvent {
    public final EventType type() {
        return EventType.ADD_USER;
    }

    private final List<String> USERS;

    public HangoutsAddUser(JSONObject callData) {
        super(callData);

        USERS = new LinkedList<>(getStringValues(callData.findElements("membership_change.participant_id[*].gaia_id")));
    }

    public String toString(HangoutsChat chat) {
        StringBuilder builder = new StringBuilder("ADDED ");
        int count = 0, total = USERS.size();
        for (String userID : USERS) {
            builder.append(chat.resolveUser(userID));

            if (++count != total) {
                builder.append(", ");
                if (count + 1 == total)
                    builder.append("and ");
            }
        }
        StringTokenizer st = new StringTokenizer(builder.append(" TO THIS CHAT").toString());
        return toStringHeaderHelper(chat).toString() + toStringMessageHelper(st).toString();
    }
}
