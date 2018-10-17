package com.lucianms.commands;

import com.lucianms.Discord;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

/**
 * @author izarooni
 */
public class Command {


    public static class CommandArg {

        private final int index;
        private final String name;

        public CommandArg(int index, String name) {
            this.index = index;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public int index() {
            return index;
        }

        public boolean equals(String message) {
            return name.equalsIgnoreCase(message);
        }

        public Long parseNumber() {
            try {
                return Long.parseLong(name);
            } catch (NumberFormatException ignore) {
                return null;
            }
        }

        public Double parseDouble() {
            try {
                return Double.parseDouble(name);
            } catch (NumberFormatException ignore) {
                return null;
            }
        }
    }

    private final String name; // name of command
    public final CommandArg[] args; // array of args

    Command(String name, String[] args) {
        this.name = name;
        this.args = new CommandArg[args.length];
        for (int i = 0; i < args.length; i++) {
            this.args[i] = new CommandArg(i, args[i]);
        }
    }

    public static boolean isValidCommand(MessageReceivedEvent event) {
        return event.getMessage().getAuthor().getLongID() != Discord.getBot().getClient().getOurUser().getLongID() // bot mustn't self-execute
                && event.getMessage().getContent().startsWith(Discord.getConfig().getString("CommandTrigger"));
    }

    public static Command parse(String text) {
        String[] mSplit = text.split(" "); // original message as splits
        String[] sp = mSplit[0].toLowerCase().split(Discord.getConfig().getString("CommandTrigger"));
        if (sp.length > 0) {
            String name = sp[1]; // the command put lowercase
            String[] args; // n-1 of sp; args of the command
            args = new String[mSplit.length - 1];
            System.arraycopy(mSplit, 1, args, 0, args.length);
            return new Command(name, args);
        } else {
            return null;
        }
    }

    public String getCommand() {
        return name;
    }

    public CommandArg[] getArgs() {
        return args;
    }

    public CommandArg getArg(int i, String d) {
        if (i < 0 || i > args.length) {
            return new CommandArg(-1, d);
        }
        return args[i];
    }

    public CommandArg getArg(String pre, String d) {
        for (int i = 0; i < args.length; i++) {
            CommandArg arg = args[i];
            if (arg.equals(pre)) {
                return args[i + 1];
            }
        }
        return new CommandArg(-1, d);
    }

    /**
     * Compare the command name with a string
     *
     * @param message a message to compare to the command name
     * @return true if the specified message equals (non case-sensitive) the command name
     */
    public boolean equals(String message) {
        return name.equalsIgnoreCase(message);
    }

    /**
     * Compare the command name with several messages
     *
     * @param message messages to compare to the command name
     * @return true if any of the specified messages match with the command name
     */
    public boolean equals(String... message) {
        for (String s : message) {
            if (name.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    public String concatFrom(int i, CharSequence separator) {
        StringBuilder sb = new StringBuilder();
        for (int j = i; j < args.length; j++) {
            sb.append(args[j].toString()).append(separator);
        }
        return sb.toString();
    }
}
