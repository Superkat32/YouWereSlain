package net.superkat.youwereslain;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YouWereSlainMain implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("youwereslain");
    public static final String MOD_ID = "youwereslain";

    @Override
    public void onInitialize() {
        LOGGER.info("You were slain has been loaded");
    }
}