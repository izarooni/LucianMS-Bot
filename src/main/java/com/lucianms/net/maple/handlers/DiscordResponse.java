package com.lucianms.net.maple.handlers;

import com.lucianms.Discord;
import com.lucianms.utils.packet.receive.MaplePacketReader;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;

import java.util.function.Consumer;

/**
 * @author izarooni
 */
public abstract class DiscordResponse {

    public final void createMessageResponse(long channelID, Consumer<MessageCreateSpec> m) {
        Discord.getBot().getClient().getChannelById(Snowflake.of(channelID))
                .ofType(TextChannel.class).blockOptional().ifPresent(ch -> ch.createMessage(m).block());
    }

    public final void createEmbedResponse(long channelID, Consumer<EmbedCreateSpec> c) {
        Discord.getBot().getClient().getChannelById(Snowflake.of(channelID))
                .ofType(TextChannel.class).blockOptional().ifPresent(ch -> ch.createEmbed(c).block());
    }

    public abstract void handle(MaplePacketReader reader);
}
