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

import java.io.PrintStream;
import java.util.*;

public abstract class JSONValue {
    protected JSONValue() {
    }

    public abstract ValueType type();
    public enum ValueType {
        OBJECT, ARRAY, NUMBER, STRING, PRIMITIVE
    }

    protected static void verify(boolean expr) {
        if (!expr)
            throw new RuntimeException("Unexpected Failure");
    }
    protected static void verify(boolean expr, String position) {
        if (!expr)
            throw new RuntimeException("Failure at " + position);
    }

    public final Collection<JSONValue> findElements(String path) {
        char[] charPath = path.toCharArray();
        ArrayList<String> pathList = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        for (char ch : charPath) {
            if ('.' == ch || '[' == ch || ']' == ch) {
                if (builder.length() != 0)
                    pathList.add(builder.toString());
                builder = new StringBuilder();
            } else
                builder.append(ch);
        }
        if (builder.length() != 0)
            pathList.add(builder.toString());

        return findElements(pathList.toArray(new String[0]), 0);
    }
    protected abstract Collection<JSONValue> findElements(String[] tokenizedPath, int index);

    protected static final String PRINT_INDENT = "  ";

    protected final void outputString(PrintStream out, String toWrite) {
        outputString(out, toWrite, 0);
    }
    protected final void outputString(PrintStream out, String toWrite, int depth) {
        for (int i = 0; i < depth; i++)
            out.print(PRINT_INDENT);
        out.print(toWrite);
    }

    protected final void outputStringWithNewLine(PrintStream out) {
        outputStringWithNewLine(out, "");
    }
    protected final void outputStringWithNewLine(PrintStream out, String toWrite) {
        outputStringWithNewLine(out, toWrite, 0);
    }
    protected final void outputStringWithNewLine(PrintStream out, String toWrite, int depth) {
        outputString(out, toWrite, depth);
        out.println();
    }

    public final void print(PrintStream out) {
        print(out, 0);
    }
    protected abstract void print(PrintStream out, int depth);

    public final void println(PrintStream out) {
        print(out, 0);
        out.println();
    }

    protected final Collection<JSONValue> getElements(Iterator<JSONValue> it, String[] tokenizedPath, int index) {
        verify(0 <= index && index <= tokenizedPath.length);

        if (tokenizedPath.length == index) {
            if ("?".equals(tokenizedPath[index - 1]))
                return Collections.emptySet();
            else
                return Collections.singleton(this);
        }

        String token = tokenizedPath[index++];
        if (token.equals("*")) {
            Collection<JSONValue> elements = new HashSet<>();
            while (it.hasNext())
                elements.addAll(it.next().findElements(tokenizedPath, index));
            return elements;
        }

        if (token.contains("?")) {
            Collection<JSONValue> elements = new HashSet<>();
            while (it.hasNext()) {
                JSONValue jsonValue = it.next();
                elements.addAll(jsonValue.findElements(tokenizedPath, index - 1));
                elements.addAll(jsonValue.findElements(tokenizedPath, index));
            }
            return elements;
        }

        return null;
    }
}