////////////////////////////////////////////////////////////////////////////////////
// MIT License                                                                    //
//                                                                                //
// Copyright (c) 2020 Utkarsh Priyam                                              //
//                                                                                //
// Permission is hereby granted, free of charge, to any person obtaining a copy   //
// of this software and associated documentation files (the "Software"), to deal  //
// in the Software without restriction, including without limitation the rights   //
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell      //
// copies of the Software, and to permit persons to whom the Software is          //
// furnished to do so, subject to the following conditions:                       //
//                                                                                //
// The above copyright notice and this permission notice shall be included in all //
// copies or substantial portions of the Software.                                //
//                                                                                //
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR     //
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,       //
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE    //
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER         //
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  //
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  //
// SOFTWARE.                                                                      //
////////////////////////////////////////////////////////////////////////////////////

package hangouts_history_reader.elements;

import json.elements.JSONArray;
import json.elements.JSONObject;
import json.elements.JSONValue;

import java.io.PrintStream;
import java.util.*;

import static hangouts_history_reader.HangoutsUtil.getStringValue;

public class HangoutsChat {
    private static final boolean USE_SHARED_USER_MAP = true;
    private static final Map<String, String> SHARED_USERS = new HashMap<>();
    private Map<String, String> getUserMap() {
        return USE_SHARED_USER_MAP ? SHARED_USERS : USERS;
    }

    public final String CHAT_ID, CHAT_NAME;
    public final ChatType CHAT_TYPE;

    private final Map<String, String> USERS;
    private final Set<HangoutsEvent> EVENTS;

    public HangoutsChat(JSONObject conversation, JSONArray events) {
        // Chat Data
        CHAT_ID = getStringValue(conversation.findElements("conversation.id.id"));
        if (CHAT_ID == null)
            throw new IllegalStateException("Unable to recreate Hangouts chat - Cannot locate conversation id");

        CHAT_TYPE = ChatType.fromString(getStringValue(conversation.findElements("conversation.type")));

        USERS = new HashMap<>();
        for (JSONValue user : conversation.findElements("conversation.participant_data[*]")) {
            String userID = getStringValue(user.findElements("id.gaia_id"));
            String userName = getStringValue(user.findElements("fallback_name"));

            USERS.put(userID, userName);
            if (USE_SHARED_USER_MAP) {
                String name = SHARED_USERS.get(userID);
                int length = name == null ? Integer.MAX_VALUE : name.length();
                int newLen = userName == null ? Integer.MAX_VALUE : userName.length();
                if (length > newLen)
                    SHARED_USERS.put(userID, userName);
            }
        }

        String chatName = getStringValue(conversation.findElements("conversation.name"));
        if (chatName == null)
            chatName = "Chat with " + USERS.size() + " people";
        CHAT_NAME = chatName.trim();

        // Messages
        EVENTS = new TreeSet<>();
        for (JSONValue jsonValue : events.findElements("[*]"))
            if (jsonValue.type() == JSONValue.ValueType.OBJECT) {
                HangoutsEvent event = HangoutsEvent.getEvent((JSONObject) jsonValue);
                if (event != null)
                    EVENTS.add(event);
                else {
                    jsonValue.print(System.out);
                    System.out.println();
                    System.out.println();
                }
            }
    }

    public String resolveUser(String userID) {
        return getUserMap().get(userID);
    }

    public enum ChatType {
        DM, GROUP, UNKNOWN;

        public static ChatType fromString(String s) {
            switch (s) {
                case "GROUP":
                    return GROUP;

                case "STICKY_ONE_TO_ONE":
                    return DM;

                default:
                    return UNKNOWN;
            }
        }
    }

    public void print(PrintStream out) {
        out.println("Chat Name: " + CHAT_NAME);

        out.print("Participants: ");
        int count = 0, total = USERS.size();
        for (String userID : USERS.keySet()) {
            out.print(resolveUser(userID));

            if (++count != total)
                out.print(", ");
            else
                out.println();
        }

        out.println();
        out.println("Chat Events:");

        for (HangoutsEvent event : EVENTS)
            out.println(event.toString(this));
    }

    @Override
    public int hashCode() {
        return CHAT_ID.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HangoutsChat && ((HangoutsChat) obj).CHAT_ID.equals(CHAT_ID);
    }
}
