package net.superkat.youwereslain.mixin;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.option.MouseOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.superkat.youwereslain.YouWereSlainMain;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

import static net.superkat.youwereslain.YouWereSlainConfig.INSTANCE;
import static net.superkat.youwereslain.YouWereSlainMain.LOGGER;

@Environment(EnvType.CLIENT)
@Mixin(DeathScreen.class)
public class DeathScreenMixin extends Screen {

	public boolean showRespawnButton = INSTANCE.getConfig().respawnButton;
	public boolean showTitleScreenButton = INSTANCE.getConfig().titleScreenButton;
	public boolean overrideButtonOptions = true;
	public boolean shiftIsHeldDown = false;
	public int ticksSinceShiftPress;
	public int ticksSinceDeath;
	public int buttonActivationTicks;
	String respawnMessage = INSTANCE.getConfig().respawningMessage;
	public int ticksUntilRespawn;
	public int secondsUntilRespawn;
	public int respawnDelayTicks = INSTANCE.getConfig().respawnDelay * 20;
	public int ticksToSeconds;
	private Text deathReasonMessage; //The player's death message
	private boolean hardcore;
	private Text scoreText;
	private Text respawnText = Text.of("0");
	private Text deathCoords;
	private String deathCoordsMessage;
	public boolean wasHudHidden;
	public boolean hudWasHiddenByMod;
	private final List<ButtonWidget> buttons = Lists.newArrayList();
	public boolean modEnabled = INSTANCE.getConfig().modEnabled;
	public boolean softlockWasPrevented = false;
	public boolean hasHudBeenDeterminedOnce = false;
	SoundInstance soundInstance = PositionedSoundInstance.master(YouWereSlainMain.DEATH_SOUND_EVENT, 1.0f, INSTANCE.getConfig().soundVolume);
	public DeathScreenMixin() {
		super(Text.of(""));
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onInit(Text message, boolean isHardcore, CallbackInfo ci) {
        this.deathReasonMessage = message;
		this.hardcore = isHardcore;
    }

	@Inject(method = "init", at = @At("HEAD"), cancellable = true)
	private void init(CallbackInfo ci) {
		if(modEnabled) {
			ci.cancel();
			if(!hasHudBeenDeterminedOnce) {
				wasHudHidden = this.client.options.hudHidden;
				hasHudBeenDeterminedOnce = true;
			}
			buttonActivationTicks = 0; //In here instead of in the onDisplayed() because this method(init) is called when the window gets resized

			//Prevent softlock with config options
			if(!showRespawnButton && !showTitleScreenButton && !INSTANCE.getConfig().shouldRespawnDelay) {
				showRespawnButton = true;
				softlockWasPrevented = true;
				LOGGER.warn("Possible softlock prevented");
				LOGGER.warn("Respawn button status: " + INSTANCE.getConfig().respawnButton);
				LOGGER.warn("Respawn delay status: " + INSTANCE.getConfig().shouldRespawnDelay);
			}

			this.buttons.clear();
			this.buttons.removeAll(buttons);
			if(showRespawnButton || showTitleScreenButton) {
				//Respawn or spectate button
				if(showRespawnButton) {
					this.buttons.add((ButtonWidget)this.addDrawableChild(ButtonWidget.builder(this.hardcore ? Text.translatable("deathScreen.spectate") : Text.translatable("deathScreen.respawn"), (button) -> {
						this.client.player.requestRespawn();
						respawnReset();
						button.active = false;
					}).dimensions(this.width / 2 - 100, this.height / 4 + 72, 200, 20).build()));
				}

				//Exit game button
				if(showTitleScreenButton) {
					this.buttons.add((ButtonWidget) this.addDrawableChild(ButtonWidget.builder(Text.translatable("deathScreen.titleScreen"), (button) -> {
						this.client.getAbuseReportContext().tryShowDraftScreen(this.client, this, this::titleScreenWasClicked, true);
					}).dimensions(this.width / 2 - 100, this.height / 4 + 96, 200, 20).build()));
				}
				this.setButtonsActive(false);
			}

			//Score and death coords
			if(INSTANCE.getConfig().score) {
				this.scoreText = Text.translatable("deathScreen.score").append(": ").append(Text.literal(Integer.toString(this.client.player.getScore())).formatted(Formatting.YELLOW));
			}
			this.deathCoords = Text.of(this.client.player.getBlockX() + ", " + this.client.player.getBlockY() + ", " + this.client.player.getBlockZ());
			this.deathCoordsMessage = "Death coordinates: " + this.client.player.getBlockX() + ", " + this.client.player.getBlockY() + ", " + this.client.player.getBlockZ();
		} else {
			return;
		}
	}

	@Override
	public void onDisplayed() {
		super.onDisplayed();
		ticksSinceDeath = 0;
		ticksUntilRespawn = respawnDelayTicks;
		secondsUntilRespawn = respawnDelayTicks / 20;
		ticksToSeconds = 0;
		ticksSinceShiftPress = 0;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	private void setButtonsActive(boolean active) {
		ButtonWidget buttonWidget;
		for(Iterator var2 = this.buttons.iterator(); var2.hasNext(); buttonWidget.active = active) {
			buttonWidget = (ButtonWidget)var2.next();
		}

	}

	private void titleScreenWasClicked() {
		if (this.hardcore) {
			this.quitLevel();
		} else {
			ConfirmScreen confirmScreen = new DeathScreen.TitleScreenConfirmScreen((confirmed) -> {
				if (confirmed) {
					this.quitLevel();
				} else {
					this.client.player.requestRespawn();
					respawnReset();
					this.client.setScreen((Screen)null);
				}

			}, Text.translatable("deathScreen.quit.confirm"), ScreenTexts.EMPTY, Text.translatable("deathScreen.titleScreen"), Text.translatable("deathScreen.respawn"));
			this.client.setScreen(confirmScreen);
			confirmScreen.disableButtons(20);
		}
	}

	private void quitLevel() {
		if (this.client.world != null) {
			this.client.world.disconnect();
		}
		this.client.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
		this.client.setScreen(new TitleScreen());
	}

	public void respawnReset() {
		//unhide HUD
		if(!wasHudHidden && hudWasHiddenByMod) {
			this.client.options.hudHidden = false;
		} else if(INSTANCE.getConfig().hideHudWorkaround && !client.isInSingleplayer()) {
			this.client.options.hudHidden = false;
		}

		//send death coords
		if(INSTANCE.getConfig().sendCoordsInChat) {
			this.client.player.sendMessage(Text.literal(deathCoordsMessage).formatted(Formatting.RED));
		}

		//stop song
		if(INSTANCE.getConfig().deathSound) {
			this.client.getSoundManager().stop(soundInstance);
		}
	}

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if(modEnabled) {
			ci.cancel(); //Done to prevent the default death screen from rendering
			int defaultFadeDelay = 7;
			if(!client.isInSingleplayer() && INSTANCE.getConfig().fadeInWorkaround) {
				defaultFadeDelay = 21;
			}
			int fade = 50;

			//Death gradient renderer
			if(INSTANCE.getConfig().useCustomGradients) {
				int gradientStart = INSTANCE.getConfig().gradientStart.hashCode();
				int gradientEnd = INSTANCE.getConfig().gradientEnd.hashCode();
				context.fillGradient(0, 0, this.width, this.height, gradientStart, gradientEnd);
			} else {
				context.fillGradient(0, 0, this.width, this.height, 0x60500000, -1602211792);
			}
			context.getMatrices().push();
			context.getMatrices().scale(2.0F, 2.0F, 2.0F);

			//You Died! text renderer
			int fadeDelayDeathMessage = INSTANCE.getConfig().fadeInDeathMessage ? defaultFadeDelay : 0;
			if(ticksSinceDeath >= fadeDelayDeathMessage) {
				float alpha = (float) Math.min(1.0, (float) ticksSinceDeath / fade);
				int color = INSTANCE.getConfig().deathMessageColor.getRGB();
				int fadeColor = (color & 0x00FFFFFF) | ((int)(alpha * 255) << 24);
				context.drawCenteredTextWithShadow(this.textRenderer, INSTANCE.getConfig().deathMessage, this.width / 2 / 2, 30, INSTANCE.getConfig().fadeInDeathMessage ? fadeColor : color);
			}

			//Respawn timer renderer
			int fadeDelayRespawnMessage = INSTANCE.getConfig().fadeInRespawnMessage ? defaultFadeDelay : 0;
			if(ticksSinceDeath >= fadeDelayRespawnMessage && INSTANCE.getConfig().respawnTimer && !showRespawnButton && INSTANCE.getConfig().shouldRespawnDelay) {
				float alpha = (float) Math.min(1.0, (float) ticksSinceDeath / fade);
				int color = INSTANCE.getConfig().respawnMessageColor.getRGB();
				int fadeColor = (color & 0x00FFFFFF) | ((int)(alpha * 255) << 24);
				context.drawCenteredTextWithShadow(this.textRenderer, this.respawnText, this.width / 2 / 2, 68, INSTANCE.getConfig().fadeInRespawnMessage ? fadeColor : color);
			}
			context.getMatrices().pop();

			//Respawn timer renderer
			if(ticksSinceDeath >= fadeDelayRespawnMessage && INSTANCE.getConfig().respawnTimer && showRespawnButton && INSTANCE.getConfig().shouldRespawnDelay) {
				float alpha = (float) Math.min(1.0, (float) ticksSinceDeath / fade);
				int color = INSTANCE.getConfig().respawnMessageColor.getRGB();
				int fadeColor = (color & 0x00FFFFFF) | ((int)(alpha * 255) << 24);
				context.drawCenteredTextWithShadow(this.textRenderer, this.respawnText, this.width / 2, 123, INSTANCE.getConfig().fadeInRespawnMessage ? fadeColor : color);
			}

			//Death reason text renderer
			int fadeDelayDeathReason = INSTANCE.getConfig().fadeInDeathReason ? defaultFadeDelay : 0;
			if(ticksSinceDeath >= fadeDelayDeathReason && this.deathReasonMessage != null && INSTANCE.getConfig().deathReason) {
				float alpha = (float) Math.min(1.0, (float) ticksSinceDeath / fade);
				int color = INSTANCE.getConfig().deathReasonColor.getRGB();
				int fadeColor = (color & 0x00FFFFFF) | ((int)(alpha * 255) << 24);
				context.drawCenteredTextWithShadow(this.textRenderer, this.deathReasonMessage, this.width / 2, 85, INSTANCE.getConfig().fadeInDeathReason ? fadeColor : color);
			}

			//Score text renderer
			int fadeDelayScore = INSTANCE.getConfig().fadeInScore ? defaultFadeDelay : 0;
			if(ticksSinceDeath >= fadeDelayScore && this.scoreText != null && INSTANCE.getConfig().score) {
				float alpha = (float) Math.min(1.0, (float) ticksSinceDeath / fade);
				int color = INSTANCE.getConfig().scoreColor.getRGB();
				int fadeColor = (color & 0x00FFFFFF) | ((int)(alpha * 255) << 24);
				context.drawCenteredTextWithShadow(this.textRenderer, this.scoreText, this.width / 2, 100, INSTANCE.getConfig().fadeInScore ? fadeColor : color);
			}

			int fadeNoSoftlockText = softlockWasPrevented ? defaultFadeDelay : 0;
			if(ticksSinceDeath >= fadeNoSoftlockText && softlockWasPrevented && INSTANCE.getConfig().showPreventedSoftlockMessage) {
				Text prevented = Text.translatable("youwereslain.nosoftlock.prevented");
				Text help = Text.translatable("youwereslain.nosoftlock.help");
				float alpha = (float) Math.min(1.0, (float) ticksSinceDeath / fade);
				int color = new Color(239, 245, 255, 80).hashCode();
				int fadeColor = (color & 0x00FFFFFF) | ((int)(alpha * 255) << 24);
				context.drawCenteredTextWithShadow(this.textRenderer, prevented, this.width / 2, 200, INSTANCE.getConfig().fadeInScore ? fadeColor : color);
				context.drawCenteredTextWithShadow(this.textRenderer, help, this.width / 2, 210, INSTANCE.getConfig().fadeInScore ? fadeColor : color);
			}

			//Death coords text renderer
			if(this.deathCoords != null && INSTANCE.getConfig().showCoords) {
				int deathcoordscolor = INSTANCE.getConfig().coordsColor.getRGB();
				context.drawCenteredTextWithShadow(this.textRenderer, this.deathCoords, this.width / 2, 112, deathcoordscolor);
			} else if (INSTANCE.getConfig().showCoords){
				if(ticksSinceDeath == 3) {
					LOGGER.warn("DEATH COORDS NULL");
				}
			}

			//Button stuff
			if(showRespawnButton || showTitleScreenButton) {
				if (this.deathReasonMessage != null && mouseY > 85 && mouseY < 85 + this.textRenderer.fontHeight) {
					Style style = this.getTextComponentUnderMouse(mouseX);
					context.drawHoverEvent(this.textRenderer, style, mouseX, mouseY);
				}
				for (ButtonWidget buttonWidget : this.buttons) {
					buttonWidget.render(context, mouseX, mouseY, delta);
				}
			}

			//Emergency respawn button text renderer
			if(shiftIsHeldDown && !showRespawnButton) {
				String emergencyRespawnString = INSTANCE.getConfig().emergencyRespawnMessage;
				float determineTime = 1.5f - (ticksSinceShiftPress / 20f);
				String formattedTime = String.format("%.1f", determineTime);
				Text emergencyRespawn = Text.of(emergencyRespawnString.replaceAll("<time>", formattedTime));
				context.drawCenteredTextWithShadow(this.textRenderer, emergencyRespawn, this.width / 2, 185, 12632256);
			}
		} else {
			return;
		}
	}

	@Nullable
	private Style getTextComponentUnderMouse(int mouseX) {
		if (this.deathReasonMessage == null) {
			return null;
		}
		int i = this.client.textRenderer.getWidth(this.deathReasonMessage);
		int j = this.width / 2 - i / 2;
		int k = this.width / 2 + i / 2;
		if (mouseX < j || mouseX > k) {
			return null;
		}
		return this.client.textRenderer.getTextHandler().getStyleAt(this.deathReasonMessage, mouseX - j);
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void tick(CallbackInfo ci) {
		if(modEnabled) {
			ci.cancel();
			//Counting ticks stuff
//			super.tick();
			++this.ticksSinceDeath;
			++this.buttonActivationTicks;
			++this.ticksToSeconds;
			--this.ticksUntilRespawn;

			//Respawning message
			this.respawnText = Text.of(respawnMessage.replaceAll("<time>", String.valueOf(secondsUntilRespawn)));

			//Sound
			if(ticksSinceDeath == 3 && INSTANCE.getConfig().deathSound) {
				this.client.getSoundManager().play(soundInstance);
			}
			if(ticksSinceDeath > 5 && INSTANCE.getConfig().loopSound && !this.client.getSoundManager().isPlaying(soundInstance)) {
				this.client.getSoundManager().play(soundInstance);
			}

			//HUD disabling
			if(ticksUntilRespawn == respawnDelayTicks - 1 && INSTANCE.getConfig().disableHud) {
				if(!wasHudHidden) {
					this.client.options.hudHidden = true;
					hudWasHiddenByMod = true;
				}
			}

			//Second counter
			if(ticksToSeconds == 20) {
				--secondsUntilRespawn;
				ticksToSeconds = 0;
			}

			//Emergency respawn button
			if(INSTANCE.getConfig().shiftOverridesDelay) {
				if(MouseOptionsScreen.hasShiftDown() && overrideButtonOptions && ticksSinceShiftPress == 30) {
					showRespawnButton = true;
					overrideButtonOptions = false;
					shiftIsHeldDown = false;
					this.buttons.add((ButtonWidget)this.addDrawableChild(ButtonWidget.builder(this.hardcore ? Text.translatable("deathScreen.spectate") : Text.translatable("deathScreen.respawn"), (button) -> {
						this.client.player.requestRespawn();
						this.client.setScreen(null);
						respawnReset();
						button.active = false;
					}).dimensions(this.width / 2 - 100, this.height / 4 + 72, 200, 20).build()));
				} else if(MouseOptionsScreen.hasShiftDown() && overrideButtonOptions) {
					ticksSinceShiftPress++;
					shiftIsHeldDown = true;
				} else if(!MouseOptionsScreen.hasShiftDown()) {
					ticksSinceShiftPress = 0;
					shiftIsHeldDown = false;
				}
			}

			//Respawning
			if(buttonActivationTicks == 20) {
				this.setButtonsActive(true);
			}
			if (this.ticksUntilRespawn == 0 && INSTANCE.getConfig().shouldRespawnDelay) {
				this.client.player.requestRespawn();
				respawnReset();
			}
		} else {
			return;
		}
	}
}