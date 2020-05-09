package com.lucianms.server.user;

import com.lucianms.utils.Permissions;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import javafx.beans.binding.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author izarooni
 */
public class DUser {

    private static final Logger LOGGER = LoggerFactory.getLogger(DUser.class);

    private final User user;
    private final Permissions permissions;

    private String applicationGuildID;
    private int applicationStatus;
    private String[] applicationResponses;

    private int boundAccountID;

    public DUser(User user) {
        this.user = user;
        this.permissions = new Permissions(user);

        this.permissions.load();
        LOGGER.info("Loaded permissions for user {} {}", user.getUsername(), user.getId().asLong());
    }

    public User getUser() {
        return user;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public String getApplicationGuildID() {
        return applicationGuildID;
    }

    public void setApplicationGuildID(String applicationGuildID) {
        this.applicationGuildID = applicationGuildID;
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

    public int getBoundAccountID() {
        return boundAccountID;
    }

    public void setBoundAccountID(int boundAccountID) {
        this.boundAccountID = boundAccountID;
    }

    public Snowflake getId() {
        return user.getId();
    }
}
