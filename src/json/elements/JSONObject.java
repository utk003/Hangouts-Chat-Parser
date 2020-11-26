package json.elements;

import json.JSONParser;
import scanner.Scanner;

import java.io.PrintStream;
import java.util.*;

public class JSONObject extends JSONValue implements JSONStorageElement {
    @Override
    public final ValueType type() {
        return ValueType.OBJECT;
    }

    private final Map<String, JSONValue> map;

    public JSONObject() {
        map = new HashMap<>(20);
    }

    public void addElement(String key, JSONValue val) {
        map.put(key, val);
    }
    public boolean isEmpty() {
        return map.isEmpty();
    }
    public int numElements() {
        return map.size();
    }
    public Iterator<JSONValue> iterator() {
        Set<JSONValue> valueSet = new HashSet<>(map.size());
        for (String str: map.keySet())
            valueSet.add(map.get(str));
        return valueSet.iterator();
    }

    public JSONValue getValue(String key) {
        return map.get(key);
    }

    public static JSONObject parseObject(Scanner s) {
        JSONObject obj = new JSONObject();

        String token;
        do {
            token = s.advance();
            if (token.equals("}"))
                break;

            s.advance(); // skip colon (:)

            s.advance(); // load first token of value
            obj.map.put(token.substring(1, token.length() - 1), JSONParser.parseRecursive(s));
        } while (s.advance().equals(","));
        return obj;
    }

    @Override
    public Collection<JSONValue> findElements(String[] tokenizedPath, int index) {
        Collection<JSONValue> collection = getElements(iterator(), tokenizedPath, index);
        if (collection == null) {
            JSONValue element = map.get(tokenizedPath[index]);
            if (element != null)
                collection = element.findElements(tokenizedPath, index + 1);
            else
                collection = Collections.emptySet();
        }
        return collection;
    }

    @Override
    protected void print(PrintStream out, int depth) {
        depth++;
        outputStringWithNewLine(out, "{");

        int count = 0, total = map.size();
        for (String key: map.keySet()) {
            outputString(out, "", depth);
            outputString(out, "\"");
            outputString(out, key);
            outputString(out, "\"");
            outputString(out, ": ");

            map.get(key).print(out, depth);

            if (++count != total)
                outputStringWithNewLine(out, ",");
            else
                outputStringWithNewLine(out);
        }

        depth--;
        outputString(out, "}", depth);
    }
}
