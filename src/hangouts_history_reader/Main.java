package hangouts_history_reader;

import json.elements.JSONString;
import json.elements.JSONValue;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

public class Main {
    public static void main() {

    }

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
