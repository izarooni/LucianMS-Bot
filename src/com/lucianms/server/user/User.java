package com.lucianms.server.user;

import com.lucianms.utils.Permissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IUser;

/**
 * @author izarooni
 */
public class User {

    private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

    private final IUser user;
    private final Permissions permissions;

    public User(IUser user) {
        this.user = user;
        this.permissions = new Permissions(user);

        this.permissions.load();
        LOGGER.info("Loaded permissions for user {} {}", user.getName(), user.getLongID());
    }

    public IUser getUser() {
        return user;
    }

    public Permissions getPermissions() {
        return permissions;
    }
}
