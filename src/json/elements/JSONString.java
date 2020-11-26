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

import scanner.Scanner;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class JSONString extends JSONValue {
    @Override
    public final ValueType type() {
        return ValueType.STRING;
    }

    private final String string;

    public JSONString(String str) {
        string = preprocess(str);
    }

    public static String preprocess(String s) {
        Iterator<Integer> chars = s.chars().iterator();
        StringBuilder builder = new StringBuilder();

        while (chars.hasNext()) {
            char c = (char) (int) chars.next();
            if (c == '\\') {
                switch (c = (char) (int) chars.next()) {
                    case '\"':
                    case '\\':
                    case '/':
                        builder.append(c);
                        break;

                    case 'b':
                        builder.append("\b");
                        break;
                    case 'f':
                        builder.append("\f");
                        break;
                    case 'n':
                        builder.append("\n");
                        break;
                    case 'r':
                        builder.append("\r");
                        break;
                    case 't':
                        builder.append("\t");
                        break;

                    case 'u':
                        builder.append(
                                hexChar(
                                        (char) (int) chars.next(),
                                        (char) (int) chars.next(),
                                        (char) (int) chars.next(),
                                        (char) (int) chars.next()
                                )
                        );
                        break;

                    default:
                        throw new RuntimeException("Invalid character escape encountered while parsing JSON");
                }
            } else
                builder.append(c);
        }
        return builder.toString();
    }

    private static char hexChar(char c1, char c2, char c3, char c4) {
        return (char) (4096 * hex(c1) + 256 * hex(c2) + 16 * hex(c3) + hex(c4));
    }

    private static int hex(char c) {
        if ('0' <= c && c <= '9')
            return c - '0';
        if ('A' <= c && c <= 'Z')
            return c - 'A' + 10;
        if ('a' <= c && c <= 'z')
            return c - 'a' + 10;
        return -1;
    }

    public String getValue() {
        return string;
    }

    public static JSONString parseString(Scanner s) {
        String token = s.current();
        return new JSONString(token.substring(1, token.length() - 1));
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
        outputString(out, "\"");
        outputString(out, string);
        outputString(out, "\"");
    }
}
