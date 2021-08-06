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

import static hangouts_history_reader.HangoutsUtil.getStringValue;

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
