import hangouts_history_reader.elements.HangoutsChat;
import json.JSONParser;
import json.elements.JSONArray;
import json.elements.JSONObject;
import json.elements.JSONValue;
import scanner.Scanner;

import java.io.*;
import java.util.*;

public class Main {
    private static long time;
    private static void markStartTime() {
        time = System.nanoTime();
    }
    private static long readAndPrintTime(String message) {
        long delta = System.nanoTime() - time;
        if (message != null) {
            System.out.println(message + ": " + delta / 1_000_000.0 + " ms");
            System.out.println(message + ": " + delta / 1_000_000_000.0 + " s");
        }
        return delta;
    }

    public static void main(String[] args) throws IOException {
        dumpChats();
    }

    private static void dumpChats() throws IOException {
        markStartTime();

        int i = 0;
        for (HangoutsChat chat : loadChats("Hangouts.json")) {
            String filename = "chat log dump/" + fixDirectoryName(chat.CHAT_NAME) + " (" + chat.CHAT_ID + ").txt";
            PrintStream out = new PrintStream(new FileOutputStream(filename));
            chat.print(out);
            out.close();
            System.out.println("Done Printing Chat #" + ++i);
        }

        readAndPrintTime("Printed " + i + " Chat Logs to files");
    }

    private static String fixDirectoryName(String name) {
        return name.replace('/', '_').replace(':', '_');
    }

    private static void liveDataParse() throws IOException {
        Map<String, HangoutsChat> chatIDToChatMap = new HashMap<>();
        Map<String, List<String>> chatNameToIDMap = new HashMap<>();
        for (HangoutsChat chat : loadChats("Hangouts.json")) {
            chatIDToChatMap.put(chat.CHAT_ID, chat);
            chatNameToIDMap.computeIfAbsent(chat.CHAT_NAME, k -> new LinkedList<>()).add(chat.CHAT_ID);
        }
        System.out.println("Finished processing data. Ready to search.");
        System.out.println();

        java.util.Scanner scanner = new java.util.Scanner(System.in);
        System.out.println("Please enter a search mode. Use \"HELP\" to access the help menu.");

        outsideLoop:
        while (true) {
            switch (scanner.nextLine().toUpperCase()) {
                case "HELP":
                    printHelpMenu();
                    break;

                case "CHAT":
                    searchChats(scanner, chatNameToIDMap, chatIDToChatMap);
                    break;

                case "QUIT":
                    break outsideLoop;

                default:
                    System.err.print("Invalid search mode. ");
                    System.out.println("Use \"HELP\" to access the help menu.");
                    break;
            }
            System.out.println("Please enter a search mode.");
        }
        System.out.println("Terminating processes...");
    }

    private static void searchChats(java.util.Scanner s, Map<String, List<String>> chatNameToIDMap,
                                    Map<String, HangoutsChat> chatIDToChatMap) {
        throw new RuntimeException("Complete searchChats(...)");
    }

    private static void printHelpMenu() {
        System.out.println("Hangouts History Parser Help Menu");
        System.out.println("---------------------------------");
        System.out.println("\"CHAT\" - Search chats");
        System.out.println("\"QUIT\" - Quit program");
        System.out.println("\"HELP\" - Access this menu");
    }

    private static Set<HangoutsChat> loadChats(String filename) throws IOException {
        Scanner scanner = new Scanner(new FileInputStream(filename));
        System.out.println("Loading Hangouts data from \"" + filename + "\"...");

        JSONValue json = JSONParser.parseNonRecursive(scanner);
        System.out.println("Finished loading data. Resolving Chat IDs...");

        Collection<JSONValue> conversations = json.findElements("conversations[*]");

        Set<HangoutsChat> chats = new HashSet<>();
        for (JSONValue conversation : conversations)
            if (conversation.type() == JSONValue.ValueType.OBJECT)
                chats.add(generateChatLog((JSONObject) conversation));

        return chats;
    }

    private static void parsingBenchmark() throws IOException {
        System.out.println("Starting JSON Parsing Benchmark\n");
        Scanner scanner = new Scanner(new FileInputStream("Hangouts.json"));

        markStartTime();
        JSONValue json = JSONParser.parseNonRecursive(scanner);
        long t = readAndPrintTime("Time to parse JSON");
        System.out.println("Average time per token: " + t / 1000.0 / scanner.tokensPassed() + " μs");

        Collection<JSONValue> conversations = json.findElements("conversations[*]");
        System.out.println();

        markStartTime();
        Set<HangoutsChat> chats = new HashSet<>();
        for (JSONValue conversation : conversations)
            if (conversation.type() == JSONValue.ValueType.OBJECT)
                chats.add(generateChatLog((JSONObject) conversation));
        readAndPrintTime("Time to process chats");

        System.out.println();
        System.out.println("Number of Chats: " + chats.size());
    }

    private static HangoutsChat generateChatLog(JSONObject hangoutsConversation) {
        JSONObject conversation = (JSONObject) hangoutsConversation.getValue("conversation");
        JSONArray events = (JSONArray) hangoutsConversation.getValue("events");
        return new HangoutsChat(conversation, events);
    }

    private static void scanningBenchmark() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("Hangouts.json")));
        String s;
        char[] arr;
        markStartTime();
        while ((s = br.readLine()) != null) {
            arr = s.toCharArray();
            for (char c : arr) ;
        }
        readAndPrintTime("BufferedReader");

        System.out.println();

        Scanner sc = new Scanner(new FileInputStream("Hangouts.json"));
        markStartTime();
        while (sc.hasMore())
            sc.advance();
        long t = readAndPrintTime("Scanner");

        System.out.println();

        System.out.println("Number of tokens: " + sc.tokensPassed());
        System.out.println("Time per token: " + t / 1000.0 / sc.tokensPassed() + " μs");
    }
}
