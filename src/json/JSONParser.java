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

package json;

import json.elements.*;
import scanner.Scanner;

import java.io.InputStream;
import java.util.Stack;

public class JSONParser {
    public static JSONValue parse(InputStream source) {
        Scanner scanner = new Scanner(source);
        return parseRecursive(scanner);
    }

    public static JSONValue parseRecursive(Scanner s) {
        char c = s.current().charAt(0);
        switch (c) {
            case '{':
                return JSONObject.parseObject(s);

            case '[':
                return JSONArray.parseArray(s);

            case '"':
                return JSONString.parseString(s);

            default:
                if (c == '-' || '0' <= c && c <= '9')
                    return JSONNumber.parseNumber(s);
                else
                    return JSONPrimitive.parsePrimitive(s);
        }
    }

    public static JSONValue parseNonRecursive(Scanner s) {
        Stack<JSONValue> stack = new Stack<>();
        Stack<String> strStack = new Stack<>();

        stack.push(getElement(s.current()));
        strStack.push(null);

        JSONValue.ValueType type = stack.peek().type();
        boolean justAddedStorageElement = type == JSONValue.ValueType.OBJECT || type == JSONValue.ValueType.ARRAY;
        while (s.hasMore()) {
            String str = s.advance(), key;
            char c = str.charAt(0);

            if (c == '}' || c == ']' || c == ',') {
                if (justAddedStorageElement)
                    justAddedStorageElement = false;
                else {
                    JSONValue element = stack.pop();
                    ((JSONStorageElement) stack.peek()).addElement(strStack.pop(), element);
                }
                continue;
            }

            if (stack.peek().type() == JSONValue.ValueType.OBJECT) {
                key = str.substring(1, str.length() - 1); // str contains key for JSONValue
                s.advance(); // skip colon
                str = s.advance(); // s.advance() gets first token of nested element
            } else {
                key = null;
                // str contains first token of nested element
            }

            strStack.push(key);
            stack.push(getElement(str));
            type = stack.peek().type();
            justAddedStorageElement = type == JSONValue.ValueType.OBJECT || type == JSONValue.ValueType.ARRAY;
        }
        return stack.peek();
    }

    private static JSONValue getElement(String token) {
        char c = token.charAt(0);
        switch (c) {
            case '{':
                return new JSONObject();

            case '[':
                return new JSONArray();

            case '"':
                return new JSONString(token.substring(1, token.length() - 1));

            default:
                if (c == '-' || '0' <= c && c <= '9')
                    return new JSONNumber(token);
                else
                    return new JSONPrimitive(token);
        }
    }
}
