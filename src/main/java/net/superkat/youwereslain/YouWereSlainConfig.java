package net.superkat.youwereslain;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.ConfigEntry;
import dev.isxander.yacl3.config.GsonConfigInstance;
import dev.isxander.yacl3.gui.controllers.BooleanController;
import dev.isxander.yacl3.gui.controllers.ColorController;
import dev.isxander.yacl3.gui.controllers.slider.FloatSliderController;
import dev.isxander.yacl3.gui.controllers.slider.IntegerSliderController;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.*;
import java.nio.file.Path;

public class YouWereSlainConfig {

    public static final GsonConfigInstance<YouWereSlainConfig> INSTANCE = GsonConfigInstance.<YouWereSlainConfig>createBuilder(YouWereSlainConfig.class)
        .setPath(Path.of("./config/youwereslain.json")).build();

    @ConfigEntry public String deathMessage = "You were slain...";
    @ConfigEntry public Color deathMessageColor = new Color(240, 130, 132);
    @ConfigEntry public boolean fadeInDeathMessage = true;
    @ConfigEntry public boolean deathReason = true;
    @ConfigEntry public Color deathReasonColor = new Color(240, 159, 161);
    @ConfigEntry public boolean fadeInDeathReason = true;
    @ConfigEntry public boolean score = false;
    @ConfigEntry public Color scoreColor = new Color(101, 255, 95);
    @ConfigEntry public boolean fadeInScore = false;
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
    @ConfigEntry public boolean fadeInRespawnMessage = true;
    @ConfigEntry public boolean shiftOverridesDelay = false;
    @ConfigEntry public String emergencyRespawnMessage = "Emergency respawn button activating in <time> seconds";
    @ConfigEntry public boolean disableHud = false;
    @ConfigEntry public boolean modEnabled = true;
    @ConfigEntry public boolean showPreventedSoftlockMessage = true;
    @ConfigEntry public boolean fadeInWorkaround = false;
    @ConfigEntry public boolean hideHudWorkaround = false;
    @ConfigEntry public boolean deathSound = true;
    @ConfigEntry public float soundVolume = 1.0f;
    @ConfigEntry public boolean loopSound = false;

    public static Screen makeScreen(Screen parent) {
        return YetAnotherConfigLib.create(INSTANCE, (defaults, config, builder) -> {
            var defaultCategoryBuilder = ConfigCategory.createBuilder()
                    .name(Text.translatable("youwereslain.category.default"));

            var textGroup = OptionGroup.createBuilder()
                    .name(Text.translatable("youwereslain.text.group"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.text.group.tooltip"))
                            .build());

            var deathMessage = Option.<String>createBuilder()
                    .name(Text.translatable("youwereslain.deathmessage"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.deathmessage.tooltip"))
                            .build())
                    .binding(
                        defaults.deathMessage,
                        () -> config.deathMessage,
                        val -> config.deathMessage = val
                    )
                    .controller(StringControllerBuilder::create)
                    .build();
            var deathMessageColor = Option.<Color>createBuilder()
                    .name(Text.translatable("youwereslain.deathmessage.color"))
                    .binding(
                            defaults.deathMessageColor,
                            () -> config.deathMessageColor,
                            val -> config.deathMessageColor = val
                    )
                    .controller(ColorControllerBuilder::create)
                    .build();
            var fadeInDeathMessage = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.deathmessage.fadein"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.deathmessage.fadein.tooltip"))
                            .build())
                    .binding(
                            defaults.fadeInDeathMessage,
                            () -> config.fadeInDeathMessage,
                            val -> config.fadeInDeathMessage = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var deathReason = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.deathreason"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.deathreason.tooltip"))
                            .build())
                    .binding(
                            defaults.deathReason,
                            () -> config.deathReason,
                            val -> config.deathReason = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var deathReasonColor = Option.<Color>createBuilder()
                    .name(Text.translatable("youwereslain.deathreason.color"))
                    .binding(
                            defaults.deathReasonColor,
                            () -> config.deathReasonColor,
                            val -> config.deathReasonColor = val
                    )
                    .controller(ColorControllerBuilder::create)
                    .build();
            var fadeInDeathReason = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.deathreason.fadein"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.deathreason.fadein.tooltip"))
                            .build())
                    .binding(
                            defaults.fadeInDeathReason,
                            () -> config.fadeInDeathReason,
                            val -> config.fadeInDeathReason = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var score = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.score"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.score.tooltip"))
                            .build())
                    .binding(
                            defaults.score,
                            () -> config.score,
                            val -> config.score = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var scoreColor = Option.<Color>createBuilder()
                    .name(Text.translatable("youwereslain.score.color"))
                    .binding(
                            defaults.scoreColor,
                            () -> config.scoreColor,
                            val -> config.scoreColor = val
                    )
                    .controller(ColorControllerBuilder::create)
                    .build();
            var fadeInScore = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.score.fadein"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.score.fadein.tooltip"))
                            .build())
                    .binding(
                            defaults.fadeInScore,
                            () -> config.fadeInScore,
                            val -> config.fadeInScore = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            textGroup.option(deathMessage);
            textGroup.option(deathMessageColor);
            textGroup.option(fadeInDeathMessage);
            textGroup.option(deathReason);
            textGroup.option(deathReasonColor);
            textGroup.option(fadeInDeathReason);
            textGroup.option(score);
            textGroup.option(scoreColor);
            textGroup.option(fadeInScore);

            var buttonsGroup = OptionGroup.createBuilder()
                    .name(Text.translatable("youwereslain.buttons.group"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.buttons.group.tooltip"))
                            .build());
            var respawnButton = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.button.respawn"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.button.respawn.tooltip"))
                            .build())
                    .binding(
                            defaults.respawnButton,
                            () -> config.respawnButton,
                            val -> config.respawnButton = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var titleScreenButton = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.button.title"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.button.title.tooltip"))
                            .build())
                    .binding(
                            defaults.titleScreenButton,
                            () -> config.titleScreenButton,
                            val -> config.titleScreenButton = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            buttonsGroup.option(respawnButton);
            buttonsGroup.option(titleScreenButton);

            var coordGroup = OptionGroup.createBuilder()
                    .name(Text.translatable("youwereslain.coord.group"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.coord.group.tooltip"))
                            .build());
            var showCoords = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.showcoords"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.showcoords.tooltip"))
                            .build())
                    .binding(
                            defaults.showCoords,
                            () -> config.showCoords,
                            val -> config.showCoords = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var coordsColors = Option.<Color>createBuilder()
                    .name(Text.translatable("youwereslain.showcoords.color"))
                    .binding(
                            defaults.coordsColor,
                            () -> config.coordsColor,
                            val -> config.coordsColor = val
                    )
                    .controller(ColorControllerBuilder::create)
                    .build();
            var sendCoordsInChat = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.chatcoords"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.chatcoords.tooltip"))
                            .build())
                    .binding(
                            defaults.sendCoordsInChat,
                            () -> config.sendCoordsInChat,
                            val -> config.sendCoordsInChat = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            coordGroup.option(showCoords);
            coordGroup.option(coordsColors);
            coordGroup.option(sendCoordsInChat);

            var gradientGroup = OptionGroup.createBuilder()
                    .name(Text.translatable("youwereslain.gradient.group"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.gradient.group.tooltip"))
                            .build());
            var useGradients = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.gradient"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.gradient.tooltip"))
                            .build())
                    .binding(
                            defaults.useCustomGradients,
                            () -> config.useCustomGradients,
                            val -> config.useCustomGradients = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var gradientStart = Option.<Color>createBuilder()
                    .name(Text.translatable("youwereslain.gradientstart.color"))
                    .binding(
                            defaults.gradientStart,
                            () -> config.gradientStart,
                            val -> config.gradientStart = val
                    )
                    .customController(opt -> new ColorController(opt, true))
                    .build();
            var gradientEnd = Option.<Color>createBuilder()
                    .name(Text.translatable("youwereslain.gradientend.color"))
                    .binding(
                            defaults.gradientEnd,
                            () -> config.gradientEnd,
                            val -> config.gradientEnd = val
                    )
                    .customController(opt -> new ColorController(opt, true))
                    .build();
            gradientGroup.option(useGradients);
            gradientGroup.option(gradientStart);
            gradientGroup.option(gradientEnd);

            var respawnGroup = OptionGroup.createBuilder()
                    .name(Text.translatable("youwereslain.respawn.group"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.respawn.group.tooltip"))
                            .build());
            var shouldRespawnDelay = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.respawndelay"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.respawndelay.tooltip"))
                            .build())
                    .binding(
                            defaults.shouldRespawnDelay,
                            () -> config.shouldRespawnDelay,
                            val -> config.shouldRespawnDelay = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
        var respawnDelayText = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.timer"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.timer.tooltip"))
                            .build())
                    .binding(
                            defaults.respawnTimer,
                            () -> config.respawnTimer,
                            val -> config.respawnTimer = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var respawnDelay = Option.<Integer>createBuilder()
                    .name(Text.translatable("youwereslain.delay"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.delay.tooltip"))
                            .build())
                    .binding(
                            defaults.respawnDelay,
                            () -> config.respawnDelay,
                            val -> config.respawnDelay = val
                    )
                    .customController(opt -> new <Integer>IntegerSliderController(opt, 1, 100, 1))
                    .build();
            var respawningMessage = Option.<String>createBuilder()
                    .name(Text.translatable("youwereslain.respawnmessage"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.respawnmessage.tooltip"))
                            .text(Text.translatable("youwereslain.respawnmessage.tooltip.time"))
                            .build())
                    .binding(
                            defaults.respawningMessage,
                            () -> config.respawningMessage,
                            val -> config.respawningMessage = val
                    )
                    .controller(StringControllerBuilder::create)
                    .build();
            var respawnMessageColor = Option.<Color>createBuilder()
                    .name(Text.translatable("youwereslain.respawnmessage.color"))
                    .binding(
                            defaults.respawnMessageColor,
                            () -> config.respawnMessageColor,
                            val -> config.respawnMessageColor = val
                    )
                    .controller(ColorControllerBuilder::create)
                    .build();
            var fadeInRespawnMessage = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.respawnmessage.fadein"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.respawnmessage.fadein.tooltip"))
                            .build())
                    .binding(
                            defaults.fadeInRespawnMessage,
                            () -> config.fadeInRespawnMessage,
                            val -> config.fadeInRespawnMessage = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var shiftOverridesDelay = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.delayoverride"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.delayoverride.tooltip"))
                            .build())
                    .binding(
                            defaults.shiftOverridesDelay,
                            () -> config.shiftOverridesDelay,
                            val -> config.shiftOverridesDelay = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var emergencyRespawnMessage = Option.<String>createBuilder()
                    .name(Text.translatable("youwereslain.emergencyrespawn"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.emergencyrespawn.tooltip"))
                            .build())
                    .binding(
                            defaults.emergencyRespawnMessage,
                            () -> config.emergencyRespawnMessage,
                            val -> config.emergencyRespawnMessage = val
                    )
                    .controller(StringControllerBuilder::create)
                    .build();
            var disableHud = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.hud"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.hud.tooltip"))
                            .build())
                    .binding(
                            defaults.disableHud,
                            () -> config.disableHud,
                            val -> config.disableHud = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            respawnGroup.option(shouldRespawnDelay);
            respawnGroup.option(respawnDelayText);
            respawnGroup.option(respawnDelay);
            respawnGroup.option(respawningMessage);
            respawnGroup.option(respawnMessageColor);
            respawnGroup.option(fadeInRespawnMessage);
            respawnGroup.option(shiftOverridesDelay);
            respawnGroup.option(emergencyRespawnMessage);
            respawnGroup.option(disableHud);

            defaultCategoryBuilder.group(textGroup.build());
            defaultCategoryBuilder.group(buttonsGroup.build());
            defaultCategoryBuilder.group(coordGroup.build());
            defaultCategoryBuilder.group(gradientGroup.build());
            defaultCategoryBuilder.group(respawnGroup.build());



            var extrasCategoryBuilder = ConfigCategory.createBuilder()
                    .name(Text.translatable("youwereslain.category.extra"));

            var extrasGroup = OptionGroup.createBuilder()
                    .name(Text.translatable("youwereslain.extra.group"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.extra.group.tooltip"))
                            .build());

            var modEnabled = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.enabled"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.enabled.tooltip"))
                            .build())
                    .binding(
                            defaults.modEnabled,
                            () -> config.modEnabled,
                            val -> config.modEnabled = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var showPreventedSoftlockMessage = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.nosoftlock.showmessage"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.nosoftlock.showmessage.tooltip"))
                            .build())
                    .binding(
                            defaults.showPreventedSoftlockMessage,
                            () -> config.showPreventedSoftlockMessage,
                            val -> config.showPreventedSoftlockMessage = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var fadeInWorkaround = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.fadeinworkaround"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.fadeinworkaround.tooltip"))
                            .text(Text.translatable("youwereslain.workarounds.note"))
                            .build())
                    .binding(
                            defaults.fadeInWorkaround,
                            () -> config.fadeInWorkaround,
                            val -> config.fadeInWorkaround = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var hideHudWorkaround = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.hidehudworkaround"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.hidehudworkaround.tooltip"))
                            .text(Text.translatable("youwereslain.workarounds.note"))
                            .build())
                    .binding(
                            defaults.hideHudWorkaround,
                            () -> config.hideHudWorkaround,
                            val -> config.hideHudWorkaround = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            extrasGroup.option(modEnabled);
            extrasGroup.option(showPreventedSoftlockMessage);
            extrasGroup.option(fadeInWorkaround);
            extrasGroup.option(hideHudWorkaround);

            var packGroup = OptionGroup.createBuilder()
                    .name(Text.translatable("youwereslain.pack.group"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.pack.group.tooltip"))
                            .build());
            var deathSound = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.sound"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.sound.tooltip"))
                            .build())
                    .binding(
                            defaults.deathSound,
                            () -> config.deathSound,
                            val -> config.deathSound = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            var soundVolume = Option.<Float>createBuilder()
                    .name(Text.translatable("youwereslain.sound.volume"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.sound.volume.tooltip"))
                            .build())
                    .binding(
                            defaults.soundVolume,
                            () -> config.soundVolume,
                            val -> config.soundVolume = val
                    )
                    .customController(opt -> new <Float>FloatSliderController(opt, 0.10f, 1.0f, 0.05f))
                    .build();
            var loopSound = Option.<Boolean>createBuilder()
                    .name(Text.translatable("youwereslain.sound.loop"))
                    .description(OptionDescription.createBuilder()
                            .text(Text.translatable("youwereslain.sound.loop.tooltip"))
                            .build())
                    .binding(
                            defaults.loopSound,
                            () -> config.loopSound,
                            val -> config.loopSound = val
                    )
                    .customController(booleanOption -> new BooleanController(booleanOption, true))
                    .build();
            packGroup.option(deathSound);
            packGroup.option(soundVolume);
            packGroup.option(loopSound);
            extrasCategoryBuilder.group(extrasGroup.build());
            extrasCategoryBuilder.group(packGroup.build());

            return builder
                    .title(Text.translatable("youwereslain.title"))
                    .category(defaultCategoryBuilder.build())
                    .category(extrasCategoryBuilder.build());
        }).generateScreen(parent);
    }

}