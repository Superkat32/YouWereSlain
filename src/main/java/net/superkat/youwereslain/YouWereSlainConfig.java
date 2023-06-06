package net.superkat.youwereslain;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.config.ConfigEntry;
import dev.isxander.yacl.config.ConfigInstance;
import dev.isxander.yacl.config.GsonConfigInstance;
import dev.isxander.yacl.gui.controllers.BooleanController;
import dev.isxander.yacl.gui.controllers.ColorController;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl.gui.controllers.string.StringController;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.*;
import java.nio.file.Path;

public class YouWereSlainConfig {

    public static final ConfigInstance<YouWereSlainConfig> INSTANCE = new GsonConfigInstance<>(YouWereSlainConfig.class, Path.of("./config/youwereslain.json"));

    @ConfigEntry public String deathMessage = "You were slain...";
    @ConfigEntry public Color deathMessageColor = new Color(240, 130, 132);
    @ConfigEntry public boolean fadeInDeathMessage = true;
    @ConfigEntry public boolean deathReason = false;
    @ConfigEntry public Color deathReasonColor = Color.WHITE;
    @ConfigEntry public boolean fadeInDeathReason = true;
    @ConfigEntry public boolean score = false;
    @ConfigEntry public Color scoreColor = new Color(101, 255, 95);
    @ConfigEntry public boolean respawnButton = false;
    @ConfigEntry public boolean titleScreenButton = false;
    @ConfigEntry public boolean showCoords = false;
    @ConfigEntry public Color coordsColor = Color.WHITE;
    @ConfigEntry public boolean sendCoordsInChat = false;
    @ConfigEntry public boolean useCustomGradients = false;
    @ConfigEntry public Color gradientStart = new Color(0x60500000, true);
    @ConfigEntry public Color gradientEnd = new Color(-1602211792, true);
    @ConfigEntry public boolean shouldRespawnDelay = true;
    @ConfigEntry public boolean respawnTimer = true;
    @ConfigEntry public int respawnDelay = 15;
    @ConfigEntry public String respawningMessage = "Respawning in <time>";
    @ConfigEntry public Color respawnMessageColor = new Color(240, 130, 132);
    @ConfigEntry public boolean shiftOverridesDelay = false;
    @ConfigEntry public String emergencyRespawnMessage = "Emergency respawn button activating in <time> seconds";
    @ConfigEntry public boolean disableHud = false;

    public static Screen makeScreen(Screen parent) {
        return YetAnotherConfigLib.create(INSTANCE, (defaults, config, builder) -> {
            var defaultCategoryBuilder = ConfigCategory.createBuilder()
                    .name(Text.translatable("youwereslain.category.default"));

            var textGroup = OptionGroup.createBuilder()
                    .name(Text.translatable("youwereslain.text.group"))
                    .tooltip(Text.translatable("youwereslain.text.group.tooltip"));

            var deathMessage = Option.createBuilder(String.class)
                    .name(Text.translatable("youwereslain.deathmessage"))
                    .tooltip(Text.translatable("youwereslain.deathmessage.tooltip"))
                    .binding(
                        defaults.deathMessage,
                        () -> config.deathMessage,
                        val -> config.deathMessage = val
                    )
                    .controller(StringController::new)
                    .build();
            var deathMessageColor = Option.createBuilder(Color.class)
                    .name(Text.translatable("youwereslain.deathmessage.color"))
                    .binding(
                            defaults.deathMessageColor,
                            () -> config.deathMessageColor,
                            val -> config.deathMessageColor = val
                    )
                    .controller(ColorController::new)
                    .build();
            var fadeInDeathMessage = Option.createBuilder(Boolean.class)
                    .name(Text.translatable("youwereslain.deathmessage.fadein"))
                    .tooltip(Text.translatable("youwereslain.deathmessage.fadein.tooltip"))
                    .binding(
                            defaults.fadeInDeathMessage,
                            () -> config.fadeInDeathMessage,
                            val -> config.fadeInDeathMessage = val
                    )
                    .controller(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var deathReason = Option.createBuilder(Boolean.class)
                    .name(Text.translatable("youwereslain.deathreason"))
                    .tooltip(Text.translatable("youwereslain.deathreason.tooltip"))
                    .binding(
                            defaults.deathReason,
                            () -> config.deathReason,
                            val -> config.deathReason = val
                    )
                    .controller(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var deathReasonColor = Option.createBuilder(Color.class)
                    .name(Text.translatable("youwereslain.deathreason.color"))
                    .binding(
                            defaults.deathReasonColor,
                            () -> config.deathReasonColor,
                            val -> config.deathReasonColor = val
                    )
                    .controller(ColorController::new)
                    .build();
            var fadeInDeathReason = Option.createBuilder(Boolean.class)
                    .name(Text.translatable("youwereslain.deathreason.fadein"))
                    .tooltip(Text.translatable("youwereslain.deathreason.fadein.tooltip"))
                    .binding(
                            defaults.fadeInDeathReason,
                            () -> config.fadeInDeathReason,
                            val -> config.fadeInDeathReason = val
                    )
                    .controller(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var score = Option.createBuilder(Boolean.class)
                    .name(Text.translatable("youwereslain.score"))
                    .tooltip(Text.translatable("youwereslain.score.tooltip"))
                    .binding(
                            defaults.score,
                            () -> config.score,
                            val -> config.score = val
                    )
                    .controller(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var scoreColor = Option.createBuilder(Color.class)
                    .name(Text.translatable("youwereslain.score.color"))
                    .binding(
                            defaults.scoreColor,
                            () -> config.scoreColor,
                            val -> config.scoreColor = val
                    )
                    .controller(ColorController::new)
                    .build();
            textGroup.option(deathMessage);
            textGroup.option(deathMessageColor);
            textGroup.option(fadeInDeathMessage);
            textGroup.option(deathReason);
            textGroup.option(deathReasonColor);
            textGroup.option(fadeInDeathReason);
            textGroup.option(score);
            textGroup.option(scoreColor);

            var buttonsGroup = OptionGroup.createBuilder()
                    .name(Text.translatable("youwereslain.buttons.group"))
                    .tooltip(Text.translatable("youwereslain.buttons.group.tooltip"));
            var respawnButton = Option.createBuilder(Boolean.class)
                    .name(Text.translatable("youwereslain.button.respawn"))
                    .tooltip(Text.translatable("youwereslain.button.respawn.tooltip"))
                    .binding(
                            defaults.respawnButton,
                            () -> config.respawnButton,
                            val -> config.respawnButton = val
                    )
                    .controller(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var titleScreenButton = Option.createBuilder(Boolean.class)
                    .name(Text.translatable("youwereslain.button.title"))
                    .tooltip(Text.translatable("youwereslain.button.title.tooltip"))
                    .binding(
                            defaults.titleScreenButton,
                            () -> config.titleScreenButton,
                            val -> config.titleScreenButton = val
                    )
                    .controller(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            buttonsGroup.option(respawnButton);
            buttonsGroup.option(titleScreenButton);

            var coordGroup = OptionGroup.createBuilder()
                    .name(Text.translatable("youwereslain.coord.group"))
                    .tooltip(Text.translatable("youwereslain.coord.group.tooltip"));
            var showCoords = Option.createBuilder(Boolean.class)
                    .name(Text.translatable("youwereslain.showcoords"))
                    .tooltip(Text.translatable("youwereslain.showcoords.tooltip"))
                    .binding(
                            defaults.showCoords,
                            () -> config.showCoords,
                            val -> config.showCoords = val
                    )
                    .controller(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var coordsColors = Option.createBuilder(Color.class)
                    .name(Text.translatable("youwereslain.showcoords.color"))
                    .binding(
                            defaults.coordsColor,
                            () -> config.coordsColor,
                            val -> config.coordsColor = val
                    )
                    .controller(ColorController::new)
                    .build();
            var sendCoordsInChat = Option.createBuilder(Boolean.class)
                    .name(Text.translatable("youwereslain.chatcoords"))
                    .tooltip(Text.translatable("youwereslain.chatcoords.tooltip"))
                    .binding(
                            defaults.sendCoordsInChat,
                            () -> config.sendCoordsInChat,
                            val -> config.sendCoordsInChat = val
                    )
                    .controller(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            coordGroup.option(showCoords);
            coordGroup.option(coordsColors);
            coordGroup.option(sendCoordsInChat);

            var gradientGroup = OptionGroup.createBuilder()
                    .name(Text.translatable("youwereslain.gradient.group"))
                    .tooltip(Text.translatable("youwereslain.gradient.group.tooltip"));
            var useGradients = Option.createBuilder(Boolean.class)
                    .name(Text.translatable("youwereslain.gradient"))
                    .tooltip(Text.translatable("youwereslain.gradient.tooltip"))
                    .binding(
                            defaults.useCustomGradients,
                            () -> config.useCustomGradients,
                            val -> config.useCustomGradients = val
                    )
                    .controller(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var gradientStart = Option.createBuilder(Color.class)
                    .name(Text.translatable("youwereslain.gradientstart.color"))
                    .binding(
                            defaults.gradientStart,
                            () -> config.gradientStart,
                            val -> config.gradientStart = val
                    )
                    .controller(opt -> new ColorController(opt, true))
                    .build();
            var gradientEnd = Option.createBuilder(Color.class)
                    .name(Text.translatable("youwereslain.gradientend.color"))
                    .binding(
                            defaults.gradientEnd,
                            () -> config.gradientEnd,
                            val -> config.gradientEnd = val
                    )
                    .controller(opt -> new ColorController(opt, true))
                    .build();
            gradientGroup.option(useGradients);
            gradientGroup.option(gradientStart);
            gradientGroup.option(gradientEnd);

            var respawnGroup = OptionGroup.createBuilder()
                    .name(Text.translatable("youwereslain.respawn.group"))
                    .tooltip(Text.translatable("youwereslain.respawn.group.tooltip"));
            var shouldRespawnDelay = Option.createBuilder(Boolean.class)
                    .name(Text.translatable("youwereslain.respawndelay"))
                    .tooltip(Text.translatable("youwereslain.respawndelay.tooltip"))
                    .binding(
                            defaults.shouldRespawnDelay,
                            () -> config.shouldRespawnDelay,
                            val -> config.shouldRespawnDelay = val
                    )
                    .controller(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var respawnDelayText = Option.createBuilder(Boolean.class)
                    .name(Text.translatable("youwereslain.timer"))
                    .tooltip(Text.translatable("youwereslain.timer.tooltip"))
                    .binding(
                            defaults.respawnTimer,
                            () -> config.respawnTimer,
                            val -> config.respawnTimer = val
                    )
                    .controller(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var respawnDelay = Option.createBuilder(Integer.class)
                    .name(Text.translatable("youwereslain.delay"))
                    .tooltip(Text.translatable("youwereslain.delay.tooltip"))
                    .binding(
                            defaults.respawnDelay,
                            () -> config.respawnDelay,
                            val -> config.respawnDelay = val
                    )
                    .controller(opt -> new <Integer>IntegerSliderController(opt, 1, 100, 1))
                    .build();
            var respawningMessage = Option.createBuilder(String.class)
                    .name(Text.translatable("youwereslain.respawnmessage"))
                    .tooltip(Text.translatable("youwereslain.respawnmessage.tooltip"))
                    .tooltip(Text.translatable("youwereslain.respawnmessage.tooltip.time"))
                    .binding(
                            defaults.respawningMessage,
                            () -> config.respawningMessage,
                            val -> config.respawningMessage = val
                    )
                    .controller(StringController::new)
                    .build();
            var respawnMessageColor = Option.createBuilder(Color.class)
                    .name(Text.translatable("youwereslain.respawnmessage.color"))
                    .binding(
                            defaults.respawnMessageColor,
                            () -> config.respawnMessageColor,
                            val -> config.respawnMessageColor = val
                    )
                    .controller(ColorController::new)
                    .build();
            var shiftOverridesDelay = Option.createBuilder(Boolean.class)
                    .name(Text.translatable("youwereslain.delayoverride"))
                    .tooltip(Text.translatable("youwereslain.delayoverride.tooltip"))
                    .binding(
                            defaults.shiftOverridesDelay,
                            () -> config.shiftOverridesDelay,
                            val -> config.shiftOverridesDelay = val
                    )
                    .controller(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var emergencyRespawnMessage = Option.createBuilder(String.class)
                    .name(Text.translatable("youwereslain.emergencyrespawn"))
                    .tooltip(Text.translatable("youwereslain.emergencyrespawn.tooltip"))
                    .binding(
                            defaults.emergencyRespawnMessage,
                            () -> config.emergencyRespawnMessage,
                            val -> config.emergencyRespawnMessage = val
                    )
                    .controller(StringController::new)
                    .build();
            var disableHud = Option.createBuilder(Boolean.class)
                    .name(Text.translatable("youwereslain.hud"))
                    .tooltip(Text.translatable("youwereslain.hud.tooltip"))
                    .binding(
                            defaults.disableHud,
                            () -> config.disableHud,
                            val -> config.disableHud = val
                    )
                    .controller(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            respawnGroup.option(shouldRespawnDelay);
            respawnGroup.option(respawnDelayText);
            respawnGroup.option(respawnDelay);
            respawnGroup.option(respawningMessage);
            respawnGroup.option(respawnMessageColor);
            respawnGroup.option(shiftOverridesDelay);
            respawnGroup.option(emergencyRespawnMessage);
            respawnGroup.option(disableHud);

            defaultCategoryBuilder.group(textGroup.build());
            defaultCategoryBuilder.group(buttonsGroup.build());
            defaultCategoryBuilder.group(coordGroup.build());
            defaultCategoryBuilder.group(gradientGroup.build());
            defaultCategoryBuilder.group(respawnGroup.build());
            return builder
                    .title(Text.translatable("youwereslain.title"))
                    .category(defaultCategoryBuilder.build());
        }).generateScreen(parent);
    }

}