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
