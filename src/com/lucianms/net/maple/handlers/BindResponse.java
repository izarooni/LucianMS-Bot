package com.lucianms.net.maple.handlers;

import com.lucianms.Discord;
import com.lucianms.utils.packet.receive.MaplePacketReader;
import sun.plugin2.message.Message;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageBuilder;

/**
 * @author izarooni
 */
public class BindResponse extends DiscordResponse {

    @Override
    public void handle(MaplePacketReader reader) {
        long channelId = reader.readLong();
        long authorId = reader.readLong();
        byte result = reader.readByte();

        IChannel channel = Discord.getBot().getClient().getChannelByID(channelId);
        IUser author = Discord.getBot().getClient().getUserByID(authorId);

        if (author != null && channel != null) {
            if (result == 1) {
                channel.sendMessage(author.mention() + " check your DMs! Don't leave me hanging~");
                String key = reader.readMapleAsciiString();
                String accountUsername = reader.readMapleAsciiString();
                IPrivateChannel dm = Discord.getBot().getClient().getOrCreatePMChannel(author);
                new MessageBuilder(Discord.getBot().getClient()).withChannel(dm).appendContent("Hey there! I see you're trying to bind the account ").appendContent(accountUsername, MessageBuilder.Styles.INLINE_CODE).appendContent(". If it's truly yours, go ahead and shout this message to the world in-game on any character belonging to that account").appendCode("", key).build();
            } else if (result == 2) {
                String accountUsername = reader.readMapleAsciiString();
                new MessageBuilder(Discord.getBot().getClient()).withChannel(channelId).appendContent("Oh? Looks like the account ").appendContent(accountUsername, MessageBuilder.Styles.INLINE_CODE).appendContent(" is already bound to a Discord account").build();
            } else if (result == 3) {
                IPrivateChannel dm = Discord.getBot().getClient().getOrCreatePMChannel(author);
                new MessageBuilder(Discord.getBot().getClient()).withChannel(dm).appendContent("Success! Your in-game account is now bound to your Discord account. Congrats! ").build();
            } else {
                String accountUsername = reader.readMapleAsciiString();
                new MessageBuilder(Discord.getBot().getClient()).withChannel(channel).appendContent("The account ").appendContent(accountUsername, MessageBuilder.Styles.INLINE_CODE).appendContent(" does not exist, or does not have any characters online").build();
            }
        }
    }
}
