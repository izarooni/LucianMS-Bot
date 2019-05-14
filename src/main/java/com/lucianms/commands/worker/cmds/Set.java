package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.server.Guild;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

public class Set extends BaseCommand {

    private static final String CmdTicketDescription = "Modify settings for the ticket system";

    @Override
    public String getDescription() {
        return "Modify specific settings for the Discord server";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        if (command.args.length == 0) {
            EmbedBuilder embed = createEmbed()
                    .withTitle("How to use the command")
                    .appendField("description", getDescription(), false)
                    .appendDesc("\r\n**syntax**: `").appendDesc(getName()).appendDesc(" <ticket>`");
            createResponse(event).withEmbed(embed.build()).build();
            return;
        }
        switch (command.args[0].toString()) {
            case "name":
                updateUsername(event, command);
                break;
            case "apps":
                manageApplicationSystem(event, command);
                break;
            case "ticket":
                manageTicketSystem(event, command);
                break;
        }
    }

    private void updateUsername(MessageReceivedEvent event, Command command) {
        if (command.args.length == 1) {
            EmbedBuilder embed = createEmbed()
                    .withTitle("How to use the command")
                    .appendField("description", getDescription(), false)
                    .appendDesc("\r\n**syntax**: `").appendDesc(getName()).appendDesc(" name <username>`");
            createResponse(event).withEmbed(embed.build()).build();
            return;
        }
        String username = command.concatFrom(1, " ");
        Discord.getBot().getClient().changeUsername(username);
        event.getMessage().reply("Username changed");
    }

    private void manageApplicationSystem(MessageReceivedEvent event, Command command) {
        Guild guild = Discord.getGuilds().get(event.getGuild().getLongID());
        if (command.args.length != 3) {
            EmbedBuilder embed = createEmbed()
                    .withTitle("How to use the command")
                    .appendField("description", CmdTicketDescription, false)
                    .appendDesc("\r\n**syntax**: `").appendDesc(getName()).appendDesc(" <destination> <channel ID>`");
            createResponse(event).withEmbed(embed.build()).build();
            return;
        }
        String channelID = command.args[2].toString();
        if ("destination".equals(command.args[1].toString())) {
            guild.getGuildConfig().setCIDApplicationDestination(channelID);
            guild.getGuildConfig().save(guild);
            event.getMessage().reply(String.format("Applications destination has been set to <#%s>", channelID));
        } else {
            EmbedBuilder embed = createEmbed()
                    .withTitle("How to use the command")
                    .appendField("description", CmdTicketDescription, false)
                    .appendDesc("\r\n**syntax**: `").appendDesc(getName()).appendDesc(" <creation/destination> <channel ID>`");
            createResponse(event).withEmbed(embed.build()).build();
        }
    }

    private void manageTicketSystem(MessageReceivedEvent event, Command command) {
        Guild guild = Discord.getGuilds().get(event.getGuild().getLongID());
        if (command.args.length != 3) {
            EmbedBuilder embed = createEmbed()
                    .withTitle("How to use the command")
                    .appendField("description", CmdTicketDescription, false)
                    .appendDesc("\r\n**syntax**: `").appendDesc(getName()).appendDesc(" <creation/destination> <channel ID>`");
            createResponse(event).withEmbed(embed.build()).build();
            return;
        }
        String channelID = command.args[2].toString();
        switch (command.args[1].toString()) {
            case "creation":
                guild.getGuildConfig().setCIDTicketCreation(channelID);
                guild.getGuildConfig().save(guild);
                event.getMessage().reply(String.format("Ticket creation has been enabled in <#%s>", channelID));
                break;
            case "destination":
                guild.getGuildConfig().setCIDTicketDestination(channelID);
                guild.getGuildConfig().save(guild);
                event.getMessage().reply(String.format("Ticket destination has been set to <#%s>", channelID));
                break;
            default:
                EmbedBuilder embed = createEmbed()
                        .withTitle("How to use the command")
                        .appendField("description", CmdTicketDescription, false)
                        .appendDesc("\r\n**syntax**: `").appendDesc(getName()).appendDesc(" <creation/destination> <channel ID>`");
                createResponse(event).withEmbed(embed.build()).build();
                break;
        }
    }
}
