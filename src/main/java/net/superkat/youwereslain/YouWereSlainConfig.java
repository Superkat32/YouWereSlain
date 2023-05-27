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
import dev.isxander.yacl.gui.controllers.string.StringController;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.*;
import java.nio.file.Path;

public class YouWereSlainConfig {

    public static final ConfigInstance<YouWereSlainConfig> INSTANCE = new GsonConfigInstance<>(YouWereSlainConfig.class, Path.of("./config/youwereslain.json"));

    @ConfigEntry public String deathMessage = "You were slain...";
    @ConfigEntry public Color deathMessageColor = new Color(240, 130, 132);
    @ConfigEntry public boolean deathReason = false;
    @ConfigEntry public Color deathReasonColor = Color.WHITE;
    @ConfigEntry public boolean score = false;
    @ConfigEntry public boolean showCoords = false;
    @ConfigEntry public Color coordsColor = Color.WHITE;

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
            textGroup.option(deathMessage);
            textGroup.option(deathMessageColor);
            textGroup.option(deathReason);
            textGroup.option(deathReasonColor);
            textGroup.option(score);
            textGroup.option(showCoords);
            textGroup.option(coordsColors);
            defaultCategoryBuilder.group(textGroup.build());

            return builder
                    .title(Text.translatable("youwereslain.title"))
                    .category(defaultCategoryBuilder.build());
        }).generateScreen(parent);
    }

}