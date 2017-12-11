package com.lucianms.net.maple.handlers;

import com.lucianms.net.maple.Headers;

/**
 * @author izarooni
 */
public class DiscordResponseManager {

    private static DiscordResponse[] responses;

    static {
        responses = new DiscordResponse[Headers.values().length];
        responses[Headers.Shutdown.value] = new ServerShutdown();
        responses[Headers.SetFace.value] = new FaceChangeResponse();
        responses[Headers.SetHair.value] = new HairChangeResponse();
        responses[Headers.Online.value] = new OnlineResponse();
        responses[Headers.Bind.value] = new BindResponse();
    }

    private DiscordResponseManager() {}

    public static DiscordResponse getResponse(int i) {
        return responses[i];
    }
}
