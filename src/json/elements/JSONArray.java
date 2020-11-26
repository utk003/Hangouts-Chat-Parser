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

package json.elements;

import json.JSONParser;
import scanner.Scanner;

import java.io.PrintStream;
import java.util.*;

public class JSONArray extends JSONValue implements JSONStorageElement {
    @Override
    public final ValueType type() {
        return ValueType.ARRAY;
    }

    private final List<JSONValue> list;

    public JSONArray() {
        list = new LinkedList<>();
    }

    public void addElement(String key, JSONValue val) {
        if (key == null)
            list.add(val);
        else
            list.add(Integer.parseInt(key), val);
    }
    public boolean isEmpty() {
        return list.isEmpty();
    }
    public int numElements() {
        return list.size();
    }
    public Iterator<JSONValue> iterator() {
        return list.iterator();
    }

    public JSONValue getValue(int index) {
        return (0 <= index && index < numElements()) ? list.get(index) : null;
    }

    public static JSONArray parseArray(Scanner s) {
        JSONArray obj = new JSONArray();
        do {
            if (s.advance().equals("]"))
                break;

            obj.list.add(JSONParser.parseRecursive(s));
        } while (s.advance().equals(","));
        return obj;
    }

    private static boolean isInteger(String s) {
        for (char c : s.toCharArray())
            if (!('0' <= c && c <= '9'))
                return false;
        return true;
    }

    @Override
    public Collection<JSONValue> findElements(String[] tokenizedPath, int index) {
        Collection<JSONValue> collection = getElements(iterator(), tokenizedPath, index);
        if (collection == null) {
            String token = tokenizedPath[index];

            if (isInteger(token)) {
                int ind = Integer.parseInt(token);
                if (ind < list.size())
                    collection = list.get(ind).findElements(tokenizedPath, index + 1);
            }

            if (collection == null)
                collection = Collections.emptySet();
        }
        return collection;
    }

    @Override
    protected void print(PrintStream out, int depth) {
        depth++;
        outputStringWithNewLine(out, "[");

        int count = 0, total = list.size();
        for (JSONValue jsonValue : list) {
            outputString(out, "", depth);
            jsonValue.print(out, depth);

            if (++count != total)
                outputStringWithNewLine(out, ",");
            else
                outputStringWithNewLine(out);
        }

        depth--;
        outputString(out, "]", depth);
    }
}
