package json.elements;

import java.util.Iterator;

public interface JSONStorageElement {
    int numElements();
    boolean isEmpty();

    void addElement(String key, JSONValue obj);

    Iterator<JSONValue> iterator();
}
