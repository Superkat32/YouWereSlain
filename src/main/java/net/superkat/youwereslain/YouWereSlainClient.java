package net.superkat.youwereslain;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class YouWereSlainClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        YouWereSlainConfig.INSTANCE.load();
        YouWereSlainMain.LOGGER.info("yay");
    }
}
