package steve6472.orbiter.ui.panel;

import steve6472.core.registry.Key;
import steve6472.core.registry.StringValue;
import steve6472.core.setting.EnumSetting;
import steve6472.core.setting.FloatSetting;
import steve6472.core.setting.IntSetting;
import steve6472.core.util.MathUtil;
import steve6472.flare.settings.VisualSettings;
import steve6472.moondust.MoonDust;
import steve6472.moondust.view.PanelView;
import steve6472.moondust.view.property.BooleanProperty;
import steve6472.moondust.view.property.StringProperty;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.settings.Settings;
import steve6472.orbiter.ui.MDUtil;

/**
 * Created by steve6472
 * Date: 8/18/2025
 * Project: Orbiter <br>
 */
public class SettingsMenu extends PanelView
{
    public SettingsMenu(Key key)
    {
        super(key);
    }

    @Override
    protected void createProperties()
    {
        // ### Orbiter ### //
        bindTextSetting(Settings.FOV, findProperty("fov:text"));
        bindTextSetting(Settings.SENSITIVITY, findProperty("sensitivity:text"));
        bindSetting(VisualSettings.USERNAME, findProperty("username:text"));

        BooleanProperty multiplayerBackendEnabled = findProperty("multiplayer_beckend:enabled");
        multiplayerBackendEnabled.set(OrbiterApp.getInstance().getClient().getWorld() == null);
        StringProperty multiplayerBackend = findProperty("multiplayer_beckend:text");
        bindTextSetting(Settings.MULTIPLAYER_BACKEND, multiplayerBackend);

        multiplayerBackend.addListener((_, _, nVal) -> {

            StringValue[] enumConstants = Settings.MULTIPLAYER_BACKEND.get().getDeclaringClass().getEnumConstants();

            String trim = nVal.trim();

            for (StringValue enumConstant : enumConstants)
            {
                if (trim.equals(enumConstant.stringValue()))
                {
                    OrbiterApp.getInstance().swapNetworkBackend((Settings.MultiplayerBackend) enumConstant);
                    return;
                }
            }
        });

        StringProperty uiScale = findProperty("ui_scale:text");
        bindTextSetting(Settings.UI_SCALE, uiScale);

        uiScale.addListener((_, _, nVal) -> {
            String trim = nVal.trim();
            if (MathUtil.isInteger(trim) && !trim.isBlank())
            {
                MoonDust.getInstance().setPixelScale(Math.max(Integer.parseInt(trim), 2));
            }
        });

        // ### Visual ### //
        bindSetting(VisualSettings.DEBUG_LINE_SINGLE_BUFFER, findProperty("debug_line_single_buffer:checked"));
        bindSetting(VisualSettings.ENABLE_WIDE_LINES, findProperty("enable_wide_lines:checked"));
        bindSetting(VisualSettings.FONT_GEN_LOGS, findProperty("font_gen_logs:checked"));
        bindSetting(VisualSettings.GENERATE_STARTUP_ATLAS_DATA, findProperty("generate_startup_atlas_data:checked"));
        bindSetting(VisualSettings.RENDER_CENTER_POINT, findProperty("render_center_point:checked"));
        bindSetting(VisualSettings.TITLE_FPS, findProperty("title_fps:checked"));
        bindTextSetting(VisualSettings.LINE_WIDTH, findProperty("line_width:text"));
        bindTextSetting(VisualSettings.PRESENT_MODE, findProperty("present_mode:text"));
    }

    @Override
    protected void createCommandListeners()
    {
        addCommandListener(Constants.key("back"), _ ->
        {
            OrbiterApp orbiter = OrbiterApp.getInstance();
            if (orbiter.getClient().getWorld() != null)
            {
                MDUtil.removePanel(Constants.UI.SETTINGS);
                MDUtil.addPanel(Constants.UI.IN_GAME_MENU);
            } else
            {
                MDUtil.removePanel(Constants.UI.SETTINGS);
                MDUtil.addPanel(Constants.UI.MAIN_MENU);
            }
        });
    }

    protected void bindTextSetting(EnumSetting<?> setting, StringProperty dest)
    {
        StringProperty settingProperty = fromSetting(setting);
        settingProperty.setDebugName("setting/" + setting.key().toString());
        dest.set(settingProperty.get());
        settingProperty.bind(dest.copyFrom());
    }

    protected void bindTextSetting(FloatSetting setting, StringProperty dest)
    {
        StringProperty settingProperty = fromSetting(setting);
        settingProperty.setDebugName("setting/" + setting.key().toString());
        dest.set(settingProperty.get());
        settingProperty.bind(dest.copyFrom());
    }

    protected void bindTextSetting(IntSetting setting, StringProperty dest)
    {
        StringProperty settingProperty = fromSetting(setting);
        settingProperty.setDebugName("setting/" + setting.key().toString());
        dest.set(settingProperty.get());
        settingProperty.bind(dest.copyFrom());
    }

    public static StringProperty fromSetting(EnumSetting<?> setting)
    {
        StringProperty property = new StringProperty(setting.get().stringValue());
        property.addListener((_, _, nVal) ->
        {
            StringValue[] enumConstants = setting.get().getDeclaringClass().getEnumConstants();

            String trim = nVal.trim();

            for (StringValue enumConstant : enumConstants)
            {
                if (trim.equals(enumConstant.stringValue()))
                {
                    //noinspection rawtypes,unchecked
                    ((EnumSetting) setting).set((Enum) enumConstant);
                    return;
                }
            }
        });
        return property;
    }

    public static StringProperty fromSetting(FloatSetting setting)
    {
        StringProperty property = new StringProperty(Float.toString(setting.get()));
        property.addListener((_, _, nVal) ->
        {
            String trim = nVal.trim();
            if (MathUtil.isDecimal(trim) && !trim.isBlank())
            {
                setting.set(Float.parseFloat(trim));
            }
        });
        return property;
    }

    public static StringProperty fromSetting(IntSetting setting)
    {
        StringProperty property = new StringProperty(Integer.toString(setting.get()));
        property.addListener((_, _, nVal) ->
        {
            String trim = nVal.trim();
            if (MathUtil.isInteger(trim) && !trim.isBlank())
            {
                setting.set(Integer.parseInt(trim));
            }
        });
        return property;
    }
}
