# Hangouts Chat Parser
## Why
A program to go through your hangouts chat logs in case you have a dispute with your friends over something you said.

## How to Use?
Simple!

1. Get a [Google Takeout](https://takeout.google.com/settings/takeout?pli=1) of you Hangouts data.
2. Find the file "Hangouts.json" and drop it into the folder which contains the "src/" directory.
3. Run the main method from "src/Main.java".

Now, all of your Hangouts data has been dumped into text files separated chat-by-chat. These files can then be searched at your leisure.

If you want to be able to perform searches directly through the program, you will need to change some code in "src/Main.java". All of the necessary methods should be present within that file. Simply uncomment the method "liveDataParse();" on line 51, comment out "dumpChats();" on line 50, and you're good to go!

## Planned Features
* Support for bold, italics, and other formatting in the file dump
* Support for other apps such as Discord
* A faster JSON parser (which I could also turn into its own library??)
* More intelligent username resolution which can cross-refer between different chat histories to identify users who may have left the chat log prior to takeout (*this last one kind of already exists, but I would like to make it better if possible*)
