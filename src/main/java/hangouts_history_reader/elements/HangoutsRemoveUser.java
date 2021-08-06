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

import json.elements.JSONObject;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import static hangouts_history_reader.HangoutsUtil.getStringValues;

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
