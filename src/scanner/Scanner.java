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
