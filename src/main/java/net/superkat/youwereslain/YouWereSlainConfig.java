package net.superkat.youwereslain;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.config.ConfigEntry;
import dev.isxander.yacl.config.ConfigInstance;
import dev.isxander.yacl.config.GsonConfigInstance;
import dev.isxander.yacl.gui.controllers.BooleanController;
import dev.isxander.yacl.gui.controllers.string.StringController;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.nio.file.Path;

public class YouWereSlainConfig {

    public static final ConfigInstance<YouWereSlainConfig> INSTANCE = new GsonConfigInstance<>(YouWereSlainConfig.class, Path.of("./config/youwereslain.json"));

    @ConfigEntry public boolean myBoolean = true;

    @ConfigEntry public String deathMessage = "You were slain...";

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

//            var deathMessage = Option.createBuilder(String.class)
//                    .name(Text.translatable("youwereslain.deathmessage"))
//                    .tooltip(Text.translatable("youwereslain.deathmessage.tooltip"))
//                    .controller(StringController::new)
//                    .build();
//            textGroup.option(deathMessage);
            var myBoolean = Option.createBuilder(Boolean.class)
                .name(Text.literal("My boolean"))
                .tooltip(Text.literal("My boolean tooltip"))
                .binding(
                        defaults.myBoolean,
                        () -> config.myBoolean,
                        val -> config.myBoolean = val
                )
                .controller(booleanOption -> new BooleanController(booleanOption, true))
                .build();
            textGroup.option(deathMessage);
            textGroup.option(myBoolean);
            defaultCategoryBuilder.group(textGroup.build());

            return builder
                    .title(Text.translatable("youwereslain.title"))
                    .category(defaultCategoryBuilder.build());
        }).generateScreen(parent);
    }

}