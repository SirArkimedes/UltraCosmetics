package be.isach.ultracosmetics.util;

public enum Problem {
    // Fatal
    UNKNOWN_MC_VERSION(true, "Could not parse Minecraft version, please report this issue."),
    BAD_CONFIG(true, "config.yml is invalid, please run it through a YAML checker."),
    BAD_MESSAGES(true, "Messages file is invalid, please run it through a YAML checker."),
    NMSLESS_LOAD_FAILURE(true, "Could not load NMS-less module."),

    // Non-fatal
    BAD_MC_VERSION(false, "This version of Minecraft is not fully supported, please check for an update."),
    NMS_LOAD_FAILURE(false, "Couldn't load support for this MC version, please report this issue."),
    BAD_MAPPINGS_VERSION(false, "Server internals seem to have changed since this build was created, you will likely experience issues."),
    TALL_DISGUISES_DISABLED(false, "TallSelfDisguises is disabled in LibsDisguises players.yml. This may cause morph self view to not behave as intended."),
    SQL_INIT_FAILURE(false, "SQL failed to connect, using flatfile support instead."),
    WORLDGUARD_HOOK_FAILURE(false, "Failed to hook into WorldGuard."),
    PERMISSION_COMMAND_NOT_SET(false, "Permission-Add-Command needs to be set or treasure chests will not give cosmetics."),
    SQL_MIGRATION_REQUIRED(false, "MySQL config settings have changed, please review them. SQL storage is disabled until then."),
    ;

    private final boolean severe;
    private final String description;

    private Problem(boolean severe, String description) {
        this.severe = severe;
        this.description = description;
    }

    public boolean isSevere() {
        return severe;
    }

    public String getDescription() {
        return description;
    }
}
