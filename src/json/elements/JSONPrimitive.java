package json.elements;

import scanner.Scanner;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;

public class JSONPrimitive extends JSONValue {
    @Override
    public final ValueType type() {
        return ValueType.PRIMITIVE;
    }

    private final Boolean value;

    public JSONPrimitive(boolean val) {
        value = val;
    }
    public JSONPrimitive(String s) {
        if ("true".equals(s))
            value = true;
        else if ("false".equals(s))
            value = false;
        else
            value = null;
    }

    public Boolean getValue() {
        return value;
    }

    public static JSONPrimitive parsePrimitive(Scanner s) {
        return new JSONPrimitive(s.current());
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
