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

package scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Scanner {
    private final BufferedReader bf;
    private final boolean parseWhiteSpace;

    private char[] nextLine = {};
    private int index = 0, lineNum = 0;

    private long numTokens = 0L;
    public long tokensPassed() {
        return numTokens;
    }

    private char currentChar = '\0';
    private String currentToken = "";

    private boolean eof = false;
    public boolean hasMore() {
        return !eof;
    }

    public Scanner(InputStream source) {
        this(source, true, false);
    }

    public Scanner(InputStream source, boolean advanceFirst, boolean parseWhiteSpace) {
        bf = new BufferedReader(new InputStreamReader(source));
        this.parseWhiteSpace = parseWhiteSpace;

        nextChar();
        if (advanceFirst)
            advance();
    }

    private char nextChar() {
        try {
            while (index >= nextLine.length) {
                String temp = bf.readLine();
                if (eof = temp == null)
                    return currentChar = '\0';
                nextLine = temp.toCharArray();
                lineNum++;
                index = 0;
            }
            return currentChar = nextLine[index++];
        }
        catch (IOException e) {
            throw new IllegalStateException("Unexpected error while parsing JSON");
        }
    }

    public String current() {
        return currentToken;
    }
    public String advance() {
        numTokens++;
        StringBuilder builder = new StringBuilder();
        if (isWhiteSpace(currentChar)) {
            if (parseWhiteSpace) {
                builder.append(currentChar);
                while (isWhiteSpace(nextChar()))
                    builder.append(currentChar);
                return currentToken = builder.toString();
            } else
                while (isWhiteSpace(nextChar())) /* do nothing */ ;
        }

        if (currentChar == '"') {
            builder.append(currentChar);
            while (nextChar() != '"') {
                builder.append(currentChar);
                if (currentChar == '\\')
                    builder.append(nextChar());
            }
            builder.append(currentChar);
            nextChar();
            return currentToken = builder.toString();
        }

        if (isNumberOrValueChar(currentChar)) {
            builder.append(currentChar);
            while (isNumberOrValueChar(nextChar()))
                builder.append(currentChar);
            return currentToken = builder.toString();
        }

        builder.append(currentChar);
        nextChar();
        return currentToken = builder.toString();
    }

    private boolean isNumberOrValueChar(char c) {
        return '0' <= c && c <= '9' ||
                'a' <= c && c <= 'z' ||
                'A' <= c && c <= 'Z' ||
                '+' == c || '-' == c || '.' == c;
    }

    private boolean isWhiteSpace(char c) {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t';
    }

    public String toString() {
        return lineNum + " " + index;
    }
}
