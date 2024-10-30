package dev.padrewin.coldbroadcast.setting;

import dev.padrewin.colddev.config.CommentedConfigurationSection;
import dev.padrewin.colddev.config.ColdSetting;
import dev.padrewin.colddev.config.ColdSettingSerializer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import dev.padrewin.coldbroadcast.ColdBroadcast;
import static dev.padrewin.colddev.config.ColdSettingSerializers.*;

public class SettingKey {

    private static final List<ColdSetting<?>> KEYS = new ArrayList<>();

    public static final ColdSetting<String> BASE_COMMAND_REDIRECT = create("base-command-redirect", STRING, "", "Which command should we redirect to when using '/giveall' with no subcommand specified?", "You can use a value here such as 'version' to show the output of '/coldbroadcast version'", "If you have any aliases defined, do not use them here", "If left as blank, the default behavior of showing '/coldbroadcast version' with bypassed permissions will be used");

    // Settings for message above player's action bar.
    public static final ColdSetting<Boolean> enableActionBarMessage = create("enableActionBarMessage", BOOLEAN, true, "Enable or disable the message above player's action bar.");
    public static final ColdSetting<String> actionBarMessage = create("actionBarMessage", STRING, "&#635AA7g&#6D63ADi&#776DB3t&#8176BAh&#8B80C0u&#9589C6b&#9F92CC.&#AA9CD3c&#B4A5D9o&#BEAEDFm&#C8B8E5/&#D2C1ECC&#DCCBF2o&#E6D4F8l&#E0C9F8d&#DBBEF8-&#D5B4F8D&#D0A9F7e&#CA9EF7v&#C593F7e&#BF89F7l&#BA7EF7o&#B473F7p&#AF68F6m&#A95EF6e&#A453F6n&#9E48F6t &f| &3%player_name%&#2B32B2.", "Message displayed above the user's action bar.");

    private static <T> ColdSetting<T> create(String key, ColdSettingSerializer<T> serializer, T defaultValue, String... comments) {
        ColdSetting<T> setting = ColdSetting.backed(ColdBroadcast.getInstance(), key, serializer, defaultValue, comments);
        KEYS.add(setting);
        return setting;
    }

    private static ColdSetting<CommentedConfigurationSection> create(String key, String... comments) {
        ColdSetting<CommentedConfigurationSection> setting = ColdSetting.backedSection(ColdBroadcast.getInstance(), key, comments);
        KEYS.add(setting);
        return setting;
    }

    public static List<ColdSetting<?>> getKeys() {
        return Collections.unmodifiableList(KEYS);
    }

    private SettingKey() {}

}
