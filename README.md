# LucianMS-Bot

### Usage
1. Run the bot to initliaze directories and configuration files, then close the program
2. Create a schema for the bot to use when managing information for Discord servers
3. Update the configuration file (database, tokens, command prefix, etc.)
4. Run the bot again to finalize database migrations

In most cases you can run the program via executable jar
```
java -jar LucianMSBot.jar
```
If not properly setup, you may have to set the class-path and call the main class
```
// for unix systems
java -cp "LucianMSBot.jar:YourLibraryFolder/*" com.lucianms.Discord

// for windows systems
java -cp "LucianMSBot.jar;YourLibraryFolder/*" com.lucianms.Discord
```
