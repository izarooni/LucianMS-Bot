package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.server.Guild;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.EmbedBuilder;

public class CmdSet extends BaseCommand {

    private static final String CmdTicketDescription = "Modify settings for the ticket system";

    public CmdSet(CommandUtil permission) {
        super(permission);
    }

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
                    .appendDesc("\r\n**syntax**: `").appendDesc(getName()).appendDesc(" <config>`")
                    .appendDesc("\r\n`<config>` may be the following: status, gtop, name, apps and ticket");
            createResponse(event).withEmbed(embed.build()).build();
            return;
        }
        switch (command.args[0].toString()) {
            case "status":
                updateDiscordPresence(event, command);
                break;
            case "vote":
                updateVoteLink(event, command);
                break;
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

    private void updateDiscordPresence(MessageReceivedEvent event, Command command) {
        if (command.args.length == 1) {
            EmbedBuilder embed = createEmbed()
                    .withTitle("How to use the command")
                    .appendField("description", getDescription(), false)
                    .appendDesc("\r\n**syntax**: `").appendDesc(getName()).appendDesc(" <text>`");
            createResponse(event).withEmbed(embed.build()).build();
            return;
        }
        String content = command.concatFrom(1, " ");
        Discord.getBot().getClient().changePresence(StatusType.ONLINE, ActivityType.PLAYING, content);
        event.getMessage().reply("Presence updated");
    }

    private void updateVoteLink(MessageReceivedEvent event, Command command) {
        if (command.args.length == 1) {
            EmbedBuilder embed = createEmbed()
                    .withTitle("How to use the command")
                    .appendField("description", getDescription(), false)
                    .appendDesc("\r\n**syntax**: `").appendDesc(getName()).appendDesc(" <URL>`");
            createResponse(event).withEmbed(embed.build()).build();
            return;
        }
        Guild guild = Discord.getGuilds().get(event.getGuild().getLongID());
        String URL = command.args[1].toString(); // URL has no spaces, right?
        guild.getGuildConfig().setVoteURL(URL);
        guild.getGuildConfig().save(guild);
        event.getMessage().reply("Vote URL updated");
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
