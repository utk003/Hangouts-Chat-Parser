package json.elements;

import scanner.Scanner;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;

public class JSONNumber extends JSONValue {
    @Override
    public final ValueType type() {
        return ValueType.NUMBER;
    }

    private final Number value;

    public JSONNumber(String s) {
        if (s.contains("e") || s.contains("E") || s.contains("."))
            value = Double.parseDouble(s);
        else
            value = Long.parseLong(s);
    }
    public JSONNumber(Number val) {
        value = val;
    }

    public Number getValue() {
        return value;
    }

    public static JSONNumber parseNumber(Scanner s) {
        return new JSONNumber(s.current());
    }

    @Override
    public Collection<JSONValue> findElements(String[] tokenizedPath, int index) {
        if (index == tokenizedPath.length)
            return Collections.singleton(this);
        else
            return Collections.emptySet();
    }

    @Override
    protected void print(PrintStream out, int depth) {
        outputString(out, "" + value);
    }
}
