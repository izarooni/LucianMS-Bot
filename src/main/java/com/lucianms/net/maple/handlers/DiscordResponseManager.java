package com.lucianms.net.maple.handlers;

import com.lucianms.net.maple.Headers;

/**
 * @author izarooni
 */
public class DiscordResponseManager {

    private static DiscordResponse[] responses;

    static {
        responses = new DiscordResponse[Headers.values().length];
        responses[Headers.MessageChannel.value] = new MessageChannel();
//        responses[Headers.Shutdown.value] = new ServerShutdown();
        responses[Headers.SetFace.value] = new FaceChangeResponse();
        responses[Headers.SetHair.value] = new HairChangeResponse();
        responses[Headers.Online.value] = new OnlineResponse();
        responses[Headers.Bind.value] = new BindResponse();
        responses[Headers.Search.value] = new SearchResponse();
        responses[Headers.Disconnect.value] = new DisconnectResponse();
        responses[Headers.ReloadCS.value] = new ReloadCSResponse();
    }

    private DiscordResponseManager() {
    }

    public static DiscordResponse getResponse(int i) {
        return responses[i];
    }
}
