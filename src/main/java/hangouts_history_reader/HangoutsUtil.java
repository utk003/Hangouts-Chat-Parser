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

package hangouts_history_reader;

import json.elements.JSONString;
import json.elements.JSONValue;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

public class HangoutsUtil {
    public static JSONValue getValue(Collection<JSONValue> elements, JSONValue.ValueType type) {
        JSONValue value = null;
        for (JSONValue jsonValue : elements)
            if (jsonValue.type() == type)
                value = jsonValue;
        return value;
    }

    public static Collection<JSONValue> getValues(Collection<JSONValue> elements, JSONValue.ValueType type) {
        Collection<JSONValue> value = new LinkedList<>();
        for (JSONValue jsonValue : elements)
            if (jsonValue.type() == type)
                value.add(jsonValue);
        return value;
    }

    public static String getStringValue(Collection<JSONValue> elements) {
        String value = null;
        for (JSONValue jsonValue : elements)
            if (jsonValue.type() == JSONValue.ValueType.STRING)
                value = ((JSONString) jsonValue).getValue();
        return value;
    }

    public static Collection<String> getStringValues(Collection<JSONValue> elements) {
        Collection<String> set = new HashSet<>();
        for (JSONValue jsonValue : elements)
            if (jsonValue.type() == JSONValue.ValueType.STRING)
                set.add(((JSONString) jsonValue).getValue());
        return set;
    }
}
