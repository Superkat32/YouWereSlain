package net.superkat.youwereslain;

import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YouWereSlainMain implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("youwereslain");
    public static final String MOD_ID = "youwereslain";
    public static final Identifier DEATH_SOUND_ID = new Identifier("youwereslain:death");
    public static SoundEvent DEATH_SOUND_EVENT = SoundEvent.of(DEATH_SOUND_ID);

    @Override
    public void onInitialize() {
        Registry.register(Registries.SOUND_EVENT, YouWereSlainMain.DEATH_SOUND_ID, DEATH_SOUND_EVENT);
    }
}