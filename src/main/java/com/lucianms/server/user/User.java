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

    private long applicationGuildID;
    private boolean applicationEditMode;
    private int applicationStatus;
    private String[] applicationResponses;

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

    public long getApplicationGuildID() {
        return applicationGuildID;
    }

    public void setApplicationGuildID(long applicationGuildID) {
        this.applicationGuildID = applicationGuildID;
    }

    public boolean isApplicationEditMode() {
        return applicationEditMode;
    }

    public void setApplicationEditMode(boolean applicationEditMode) {
        this.applicationEditMode = applicationEditMode;
    }

    public int getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(int applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public String[] getApplicationResponses() {
        return applicationResponses;
    }

    public void setApplicationResponses(String[] applicationResponses) {
        this.applicationResponses = applicationResponses;
    }
}
