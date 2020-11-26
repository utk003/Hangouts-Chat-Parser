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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import static hangouts_history_reader.HangoutsUtil.getStringValue;
import static hangouts_history_reader.HangoutsUtil.getValue;

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