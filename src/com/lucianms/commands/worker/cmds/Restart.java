package com.lucianms.commands.worker.cmds;

import com.lucianms.commands.worker.BaseCommand;
import com.lucianms.Discord;
import com.lucianms.commands.Command;
import com.lucianms.net.maple.Headers;
import com.lucianms.net.maple.ServerSession;
import com.lucianms.scheduler.TaskExecutor;
import com.lucianms.utils.packet.send.MaplePacketWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

import java.io.File;
import java.io.IOException;

/**
 * @author izarooni
 */
public class Restart extends BaseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(Restart.class);

    @Override
    public String getDescription() {
        return "Attempt to safely shutdown and restart the server";
    }

    @Override
    public void invoke(MessageReceivedEvent event, Command command) {
        if (Discord.getServer() == null) {
            event.getChannel().sendMessage("Could not find the server process");
            return;
        }
        IMessage message = event.getChannel().sendMessage("Stopping the server...");
        MaplePacketWriter writer = new MaplePacketWriter(1);
        writer.write(Headers.Shutdown.value);
        ServerSession.sendPacket(writer.getPacket());

        TaskExecutor.executeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Discord.getServer().destroyForcibly();
                    if (ServerSession.getSession().isActive()) {
                        ServerSession.getSession().closeNow();
                    }

                    ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/k", "start " + Discord.getConfig().getString("launcher"));
                    File file = new File(Discord.getConfig().getString("ServerDirectory"));
                    processBuilder.directory(file);
                    Process process = processBuilder.start();
                    Discord.setServer(process);
                    message.edit("The server is now starting up!");
                } catch (IOException e) {
                    message.edit("An error occurred");
                    LOGGER.error("Unable to start server", e);
                }
            }
        }, 30000);
    }
}
