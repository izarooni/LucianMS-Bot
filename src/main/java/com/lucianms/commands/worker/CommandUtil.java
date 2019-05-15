package com.lucianms.commands.worker;

import com.lucianms.commands.CommandCategory;
import com.lucianms.commands.CommandType;
import com.lucianms.commands.worker.cmds.*;

/**
 * @author izarooni
 */
public enum CommandUtil {

    //@formatter:off
    Help         (false, CommandCategory.General,       CommandType.Both,   CmdHelp.class),
    Apply        (false, CommandCategory.General,       CommandType.Public, CmdApply.class),
    Ticket       (false, CommandCategory.General,       CommandType.Public, CmdTicket.class),
    Embed        (true,  CommandCategory.Utility,       CommandType.Public, CmdEmbed.class),
    Forbid       (true,  CommandCategory.Utility,       CommandType.Public, CmdForbid.class),
    Pardon       (true,  CommandCategory.Utility,       CommandType.Public, CmdPardon.class),
    Connect      (true,  CommandCategory.Utility,       CommandType.Public, CmdConnect.class),
    GetRoles     (true,  CommandCategory.Utility,       CommandType.Public, CmdGetRoles.class),
    Set          (true,  CommandCategory.Administrator, CommandType.Public, CmdSet.class),
    News         (true,  CommandCategory.Administrator, CommandType.Public, CmdNews.class),
    Updates      (true,  CommandCategory.Administrator, CommandType.Public, CmdUpdates.class),
    SafeShutdown (true,  CommandCategory.Administrator, CommandType.Public, CmdSafeShutdown.class),
    Permission   (true,  CommandCategory.Administrator, CommandType.Public, CmdPermission.class),
    Bind         (false, CommandCategory.Game,          CommandType.Both,   CmdBind.class),
    Online       (false, CommandCategory.Game,          CommandType.Both,   CmdOnline.class),
    Unstuck      (false, CommandCategory.Game,          CommandType.Both,   CmdUnstuck.class),
    Job          (true,  CommandCategory.Game,          CommandType.Public, CmdJob.class),
    Warp         (true,  CommandCategory.Game,          CommandType.Public, CmdWarp.class),
    Strip        (true,  CommandCategory.Game,          CommandType.Public, CmdStrip.class),
    Search       (true,  CommandCategory.Game,          CommandType.Public, CmdSearch.class),
    SetFace      (true,  CommandCategory.Game,          CommandType.Public, CmdSetFace.class),
    setHair      (true,  CommandCategory.Game,          CommandType.Public, CmdSetHair.class),
    Reserve      (true,  CommandCategory.Game,          CommandType.Public, CmdReserve.class),
    ReloadCS     (true,  CommandCategory.Game,          CommandType.Public, CmdReloadCS.class),
    Disconnect   (true,  CommandCategory.Game,          CommandType.Both,   CmdDisconnect.class),
    Register     (false, CommandCategory.Game,          CommandType.Private,CmdRegister.class);
    //@formatter:on
    public final boolean requirePermission;
    public final CommandCategory category;
    public final CommandType type;
    public final Class<? extends BaseCommand> command;

    CommandUtil(boolean requirePermission, CommandCategory category, CommandType type, Class<? extends BaseCommand> command) {
        this.requirePermission = requirePermission;
        this.category = category;
        this.type = type;
        this.command = command;
    }
}
