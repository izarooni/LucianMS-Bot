package com.lucianms.commands.worker;

import com.lucianms.commands.CommandCategory;
import com.lucianms.commands.CommandType;

/**
 * @author izarooni
 */
public enum CPermissions {

    //@formatter:off
    Help         (CommandCategory.General,       CommandType.Both),
    Apply        (CommandCategory.General,       CommandType.Public),
    Ticket       (CommandCategory.General,       CommandType.Public),
    Embed        (CommandCategory.Utility,       CommandType.Public),
    Forbid       (CommandCategory.Utility,       CommandType.Public),
    Pardon       (CommandCategory.Utility,       CommandType.Public),
    Connect      (CommandCategory.Utility,       CommandType.Public),
    GetRoles     (CommandCategory.Utility,       CommandType.Public),
    Set          (CommandCategory.Administrator, CommandType.Public),
    News         (CommandCategory.Administrator, CommandType.Public),
    Updates      (CommandCategory.Administrator, CommandType.Public),
    SafeShutdown (CommandCategory.Administrator, CommandType.Public),
    Bind         (CommandCategory.Game,          CommandType.Both),
    Online       (CommandCategory.Game,          CommandType.Both),
    Unstuck      (CommandCategory.Game,          CommandType.Both),
    Job          (CommandCategory.Game,          CommandType.Public),
    Warp         (CommandCategory.Game,          CommandType.Public),
    Strip        (CommandCategory.Game,          CommandType.Public),
    Search       (CommandCategory.Game,          CommandType.Public),
    SetFace      (CommandCategory.Game,          CommandType.Public),
    setHair      (CommandCategory.Game,          CommandType.Public),
    Reserve      (CommandCategory.Game,          CommandType.Public),
    ReloadCS     (CommandCategory.Game,          CommandType.Public),
    Disconnect   (CommandCategory.Game,          CommandType.Both);
    //@formatter:on
    public final CommandCategory category;
    public final CommandType type;

    CPermissions(CommandCategory category, CommandType type) {
        this.category = category;
        this.type = type;
    }
}
