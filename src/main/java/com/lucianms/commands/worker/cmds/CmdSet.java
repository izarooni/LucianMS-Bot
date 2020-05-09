package com.lucianms.commands.worker.cmds;

import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.commands.worker.CommandUtil;
import com.lucianms.server.DGuild;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;

public class CmdSet extends BaseCommand {

    private static final String CmdDescriptionStatus = "Modify the bot's Discord presence";
    private static final String CmdDescriptionVote = "Modify server's vote URL";
    private static final String CmdDescriptionApps = "Modify settings for the application system";
    private static final String CmdDescriptionTicket = "Modify settings for the ticket system";

    public CmdSet(CommandUtil permission) {
        super(permission);
    }

    @Override
    public String getDescription() {
        return "Modify specific settings for the Discord server";
    }

    @Override
    public void invoke(MessageCreateEvent event, Command command) {
        Message message = event.getMessage();
        TextChannel ch = message.getChannel().ofType(TextChannel.class).blockOptional().orElse(null);
        if (ch == null) return;

        if (command.args.length == 0) {
            ch.createEmbed(e -> {
                e.setTitle("How to use the command");
                e.addField("description", getDescription(), false);
                e.setDescription("\r\n**syntax**: `" + getName() + " <config>`"
                        + "\r\n`<config>` may be the following: `status`, `vote`, `name`, `apps` and `ticket`");
            }).block();
            return;
        }
        switch (command.args[0].toString()) {
            case "status":
                updateDiscordPresence(ch, command);
                break;
            case "vote":
                updateVoteLink(ch, command);
                break;
            case "apps":
                manageApplicationSystem(ch, command);
                break;
            case "ticket":
                manageTicketSystem(ch, command);
                break;
        }
    }

    private void updateDiscordPresence(TextChannel ch, Command command) {
        if (command.args.length == 1) {
            ch.createEmbed(e -> {
                e.setTitle("How to use the command");
                e.addField("description", CmdDescriptionStatus, false);
                e.setDescription("\r\n**syntax**: `" + getName() + " <text>`");
            }).block();
            return;
        }
        String content = command.concatFrom(1, " ");
        Discord.getBot().getClient().updatePresence(Presence.online(Activity.playing(content)));
        ch.createEmbed(e -> e.setDescription("Presence updated")).block();
    }

    private void updateVoteLink(TextChannel ch, Command command) {
        if (command.args.length == 1) {
            ch.createEmbed(e -> {
                e.setTitle("How to use the command");
                e.addField("description", CmdDescriptionVote, false);
                e.setDescription("\r\n**syntax**: `" + getName() + " <URL>`");
            }).block();
            return;
        }
        DGuild guild = Discord.getGuild(ch.getGuild());
        String URL = command.args[1].toString(); // URL has no spaces, right?
        guild.getGuildConfig().setVoteURL(URL);
        guild.getGuildConfig().save(guild);
        ch.createEmbed(e -> e.setDescription("Vote URL updated")).block();
    }

    private void manageApplicationSystem(TextChannel ch, Command command) {
        DGuild guild = Discord.getGuild(ch.getGuild());
        if (command.args.length != 3) {
            ch.createEmbed(e -> {
                e.setTitle("How to use the command");
                e.addField("description", CmdDescriptionApps, false);
                e.setDescription("\r\n**syntax**: `" + getName() + " <destination> <channel ID>`");
            }).block();
            return;
        }
        String channelID = command.args[2].toString();
        if ("destination".equals(command.args[1].toString())) {
            guild.getGuildConfig().setCIDApplicationDestination(channelID);
            guild.getGuildConfig().save(guild);
            ch.createEmbed(e -> e.setDescription(String.format("Applications destination has been set to <#%s>", channelID))).block();
        } else {
            ch.createEmbed(e -> {
                e.setTitle("How to use the command");
                e.addField("description", CmdDescriptionApps, false);
                e.setDescription("\r\n**syntax**: `" + getName() + " <destination> <channel ID>`");
            }).block();
        }
    }

    private void manageTicketSystem(TextChannel ch, Command command) {
        DGuild guild = Discord.getGuild(ch.getGuild());
        if (command.args.length != 3) {
            ch.createEmbed(e -> {
                e.setTitle("How to use the command");
                e.addField("description", CmdDescriptionTicket, false);
                e.setDescription("\r\n**syntax**: `" + getName() + " <creation/destination> <channel ID>`");
            }).block();
            return;
        }
        String channelID = command.args[2].toString();
        switch (command.args[1].toString()) {
            case "creation":
                guild.getGuildConfig().setCIDTicketCreation(channelID);
                guild.getGuildConfig().save(guild);
                ch.createEmbed(e -> e.setDescription(String.format("Ticket creation has been enabled in <#%s>", channelID))).block();
                break;
            case "destination":
                guild.getGuildConfig().setCIDTicketDestination(channelID);
                guild.getGuildConfig().save(guild);
                ch.createEmbed(e -> e.setDescription(String.format("Ticket destination has been set to <#%s>", channelID))).block();
                break;
            default:
                ch.createEmbed(e -> {
                    e.setTitle("How to use the command");
                    e.addField("description", CmdDescriptionTicket, false);
                    e.setDescription("\r\n**syntax**: `" + getName() + " <creation/destination> <channel ID>`");
                }).block();
                break;
        }
    }
}
