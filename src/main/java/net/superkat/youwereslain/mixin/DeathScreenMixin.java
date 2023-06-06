package net.superkat.youwereslain.mixin;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.option.MouseOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.superkat.youwereslain.YouWereSlainConfig.INSTANCE;
import static net.superkat.youwereslain.YouWereSlainMain.LOGGER;

@Environment(EnvType.CLIENT)
@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen {
	//Welcome to the most spaghetti of spaghetti code there is

	//KNOWN COMPATIBILITIES
	//You're in Grave Danger
	//Universal Graves
	//Gravestones
	//Forgotten Graves
	//Your Items Are Safe
	//Better Respawn

	//KNOWN INCOMPATIBILITIES
	//Respawn Delay(turns the player into a spectator instead of seeing the death screen upon death)

	//FIXME - FIXED - Chat coords doesn't seem to work outside of dev env (try client.player.sendMessage() ?)
	//FIXME - FIXED - Hide HUD doesn't work outside of dev env
	//FIXME - FIXED - Respawn buttons stay greyed out outside of dev env (try overriding or mixing into the button style method ?)
	//FIXME - FIXED - Respawn buttons are technically still working, but invisible outside of dev env
	//FIXME - FIXED - Death reason is null (WHYYYYYYY)
	//FIXME - FIXED - Respawn buttons don't grey out
	//FIXME - FIXED - HUD doesn't unhide if the respawn button from the confirm title screen button screen was used
	//FIXME - FIXED(HOPEFULLY) - HUD doesn't always become unhidden when it should
	public boolean showRespawnButton = INSTANCE.getConfig().respawnButton;
	public boolean showTitleScreenButton = INSTANCE.getConfig().titleScreenButton;
	public boolean overrideButtonOptions = true;
	public boolean shiftIsHeldDown = false;
	public int ticksSinceShiftPress;
	public int ticksSinceDeath;
	String respawnMessage;
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
	public DeathScreenMixin() {
		super(Text.of(""));
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onInit(Text message, boolean isHardcore, CallbackInfo ci) {
        this.deathReasonMessage = message;
		this.hardcore = isHardcore;
    }

	@Inject(method = "init", at = @At("RETURN"))
	private void init(CallbackInfo callbackInfo) {
		ticksSinceDeath = 0;
		ticksUntilRespawn = respawnDelayTicks;
		secondsUntilRespawn = respawnDelayTicks / 20;
		ticksToSeconds = 0;
		ticksSinceShiftPress = 0;
		wasHudHidden = this.client.options.hudHidden;

		//Prevent softlock with config options
		if(!showRespawnButton && !INSTANCE.getConfig().shouldRespawnDelay) {
			showRespawnButton = true;
			LOGGER.warn("Possible softlock prevented");
			LOGGER.warn("Respawn button status: " + INSTANCE.getConfig().respawnButton);
			LOGGER.warn("Respawn delay status: " + INSTANCE.getConfig().shouldRespawnDelay);
		}

		this.buttons.clear();
		if(showRespawnButton || showTitleScreenButton) {
			//Respawn or spectate button
			if(showRespawnButton) {
				this.buttons.add(this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72, 200, 20, this.hardcore ? Text.translatable("deathScreen.spectate") : Text.translatable("deathScreen.respawn"), button -> {
					this.client.player.requestRespawn();
					this.client.setScreen(null);
					if(!wasHudHidden && hudWasHiddenByMod) {
						this.client.options.hudHidden = false;
					}
					sendDeathCoords();
				})));
			}

			//Exit game button
			if(showTitleScreenButton) {
				this.buttons.add(this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96, 200, 20, Text.translatable("deathScreen.titleScreen"), button -> {
					if (this.hardcore) {
						this.quitLevel();
						return;
					}
					ConfirmScreen confirmScreen = new ConfirmScreen(this::onConfirmQuit, Text.translatable("deathScreen.quit.confirm"), ScreenTexts.EMPTY, Text.translatable("deathScreen.titleScreen"), Text.translatable("deathScreen.respawn"));
					this.client.setScreen(confirmScreen);
					confirmScreen.disableButtons(20);
					if(!wasHudHidden && hudWasHiddenByMod) {
						this.client.options.hudHidden = false;
					}
				})));
			}
			for (ButtonWidget buttonWidget : this.buttons) {
				buttonWidget.active = false;
			}
		}

		//Score and death coords
		if(INSTANCE.getConfig().score) {
			this.scoreText = Text.translatable("deathScreen.score").append(": ").append(Text.literal(Integer.toString(this.client.player.getScore())).formatted(Formatting.YELLOW));
		}
		this.deathCoords = Text.of(this.client.player.getBlockX() + ", " + this.client.player.getBlockY() + ", " + this.client.player.getBlockZ());
		this.deathCoordsMessage = "Death coordinates: " + this.client.player.getBlockX() + ", " + this.client.player.getBlockY() + ", " + this.client.player.getBlockZ();
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	private void onConfirmQuit(boolean quit) {
		if (quit) {
			this.quitLevel();
		} else {
			this.client.player.requestRespawn();
			this.client.setScreen(null);
			sendDeathCoords();
		}
	}

	private void quitLevel() {
		if (this.client.world != null) {
			this.client.world.disconnect();
		}
		this.client.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
		this.client.setScreen(new TitleScreen());
	}

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		ci.cancel(); //Done to prevent the default death screen from rendering
		int defaultFadeDelay = 7;
		int fade = 50;

		//Death gradient renderer
		if(INSTANCE.getConfig().useCustomGradients) {
			int gradientStart = INSTANCE.getConfig().gradientStart.hashCode();
			int gradientEnd = INSTANCE.getConfig().gradientEnd.hashCode();
			this.fillGradient(matrices, 0, 0, this.width, this.height, gradientStart, gradientEnd);
		} else {
			this.fillGradient(matrices, 0, 0, this.width, this.height, 0x60500000, -1602211792);
		}
		matrices.push();
		matrices.scale(2.0F, 2.0F, 2.0F);


		//You Died! text renderer
		int fadeDelayDeathMessage = INSTANCE.getConfig().fadeInDeathMessage ? defaultFadeDelay : 0;
		if(ticksSinceDeath >= fadeDelayDeathMessage) {
			float alpha = (float) Math.min(1.0, (float) ticksSinceDeath / fade);
			int color = INSTANCE.getConfig().deathMessageColor.getRGB();
			int fadeColor = (color & 0x00FFFFFF) | ((int)(alpha * 255) << 24);
			drawCenteredText(matrices, this.textRenderer, INSTANCE.getConfig().deathMessage, this.width / 2 / 2, 30, INSTANCE.getConfig().fadeInDeathMessage ? fadeColor : color);
		}

		//Respawn timer renderer
		int fadeDelayRespawnMessage = INSTANCE.getConfig().fadeInRespawnMessage ? defaultFadeDelay : 0;
		if(ticksSinceDeath >= fadeDelayRespawnMessage && INSTANCE.getConfig().respawnTimer && !showRespawnButton && INSTANCE.getConfig().shouldRespawnDelay) {
			float alpha = (float) Math.min(1.0, (float) ticksSinceDeath / fade);
			int color = INSTANCE.getConfig().respawnMessageColor.getRGB();
			int fadeColor = (color & 0x00FFFFFF) | ((int)(alpha * 255) << 24);
			drawCenteredText(matrices, this.textRenderer, this.respawnText, this.width / 2 / 2, 68, INSTANCE.getConfig().fadeInRespawnMessage ? fadeColor : color);
		}
//		int respawnMessageColor = INSTANCE.getConfig().respawnMessageColor.getRGB();
//		if(INSTANCE.getConfig().respawnTimer && !showRespawnButton && INSTANCE.getConfig().shouldRespawnDelay) {
//			drawCenteredText(matrices, this.textRenderer, this.respawnText, this.width / 2 / 2, 68, respawnMessageColor);
//		}
		matrices.pop();

		//Respawn timer renderer
		if(ticksSinceDeath >= fadeDelayRespawnMessage && INSTANCE.getConfig().respawnTimer && showRespawnButton && INSTANCE.getConfig().shouldRespawnDelay) {
			float alpha = (float) Math.min(1.0, (float) ticksSinceDeath / fade);
			int color = INSTANCE.getConfig().respawnMessageColor.getRGB();
			int fadeColor = (color & 0x00FFFFFF) | ((int)(alpha * 255) << 24);
			drawCenteredText(matrices, this.textRenderer, this.respawnText, this.width / 2, 123, INSTANCE.getConfig().fadeInRespawnMessage ? fadeColor : color);
		}
//		if(INSTANCE.getConfig().respawnTimer && showRespawnButton && INSTANCE.getConfig().shouldRespawnDelay) {
//			drawCenteredText(matrices, this.textRenderer, this.respawnText, this.width / 2, 123, respawnMessageColor);
//		}

		//Death reason text renderer
		int fadeDelayDeathReason = INSTANCE.getConfig().fadeInDeathReason ? defaultFadeDelay : 0;
		if(ticksSinceDeath >= fadeDelayDeathReason && this.deathReasonMessage != null && INSTANCE.getConfig().deathReason) {
			float alpha = (float) Math.min(1.0, (float) ticksSinceDeath / fade);
			int color = INSTANCE.getConfig().deathReasonColor.getRGB();
			int fadeColor = (color & 0x00FFFFFF) | ((int)(alpha * 255) << 24);
			drawCenteredText(matrices, this.textRenderer, this.deathReasonMessage, this.width / 2, 85, INSTANCE.getConfig().fadeInDeathReason ? fadeColor : color);
		}

		//Score text renderer
		int fadeDelayScore = INSTANCE.getConfig().fadeInScore ? defaultFadeDelay : 0;
		if(ticksSinceDeath >= fadeDelayScore && this.scoreText != null && INSTANCE.getConfig().score) {
			float alpha = (float) Math.min(1.0, (float) ticksSinceDeath / fade);
			int color = INSTANCE.getConfig().scoreColor.getRGB();
			int fadeColor = (color & 0x00FFFFFF) | ((int)(alpha * 255) << 24);
			drawCenteredText(matrices, this.textRenderer, this.scoreText, this.width / 2, 100, INSTANCE.getConfig().fadeInScore ? fadeColor : color);
		}

		//Death coords text renderer
		if(this.deathCoords != null && INSTANCE.getConfig().showCoords) {
			int deathcoordscolor = INSTANCE.getConfig().coordsColor.getRGB();
			drawCenteredText(matrices, this.textRenderer, this.deathCoords, this.width / 2, 112, deathcoordscolor);
		} else if (INSTANCE.getConfig().showCoords){
			if(ticksSinceDeath == 3) {
				LOGGER.warn("DEATH COORDS NULL");
			}
		}

		//Button stuff
		if(showRespawnButton || showTitleScreenButton) {
			if (this.deathReasonMessage != null && mouseY > 85 && mouseY < 85 + this.textRenderer.fontHeight) {
				Style style = this.getTextComponentUnderMouse(mouseX);
				this.renderTextHoverEffect(matrices, style, mouseX, mouseY);
			}

			for (ButtonWidget buttonWidget : this.buttons) {
				buttonWidget.render(matrices, mouseX, mouseY, delta);
			}
		}

		//Emergency respawn button text renderer
		if(shiftIsHeldDown && !showRespawnButton) {
			String emergencyRespawnString = INSTANCE.getConfig().emergencyRespawnMessage;
			float determineTime = 1.5f - (ticksSinceShiftPress / 20f);
			String formattedTime = String.format("%.1f", determineTime);
			Text emergencyRespawn = Text.of(emergencyRespawnString.replaceAll("<time>", formattedTime));
			drawCenteredText(matrices, this.textRenderer, emergencyRespawn, this.width / 2, 185, 12632256);
		}
	}

	private void sendDeathCoords() {
		if(INSTANCE.getConfig().sendCoordsInChat) {
			this.client.player.sendMessage(Text.literal(deathCoordsMessage).formatted(Formatting.RED));
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
		ci.cancel();
		//Counting ticks stuff
		super.tick();
		++this.ticksSinceDeath;
		++this.ticksToSeconds;
		--this.ticksUntilRespawn;

		//Respawning message
		respawnMessage = INSTANCE.getConfig().respawningMessage;
		this.respawnText = Text.of(respawnMessage.replaceAll("<time>", String.valueOf(secondsUntilRespawn)));

		//Hud disabling
		if(ticksUntilRespawn == respawnDelayTicks - 1 && INSTANCE.getConfig().disableHud) {
			if(!wasHudHidden) {
				this.client.options.hudHidden = true;
				hudWasHiddenByMod = true;
			}
		}

		//Chat death coords message
//		if(ticksUntilRespawn == respawnDelayTicks - 1 && INSTANCE.getConfig().sendCoordsInChat) {
//			this.client.player.sendMessage(Text.literal(deathCoordsMessage).formatted(Formatting.RED));
//		}

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
				this.buttons.add(this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72, 200, 20, this.hardcore ? Text.translatable("deathScreen.spectate") : Text.translatable("deathScreen.respawn"), button -> {
					this.client.player.requestRespawn();
					this.client.setScreen(null);
					if(hudWasHiddenByMod) {
						this.client.options.hudHidden = false;
					}
					sendDeathCoords();
				})));
			} else if(MouseOptionsScreen.hasShiftDown() && overrideButtonOptions) {
				ticksSinceShiftPress++;
				shiftIsHeldDown = true;
			} else if(!MouseOptionsScreen.hasShiftDown()) {
				ticksSinceShiftPress = 0;
				shiftIsHeldDown = false;
			}
		}

		//Respawning
		if(ticksSinceDeath == 20) {
			for (ButtonWidget buttonWidget : this.buttons) {
				buttonWidget.active = true;
			}
		}
		if (this.ticksUntilRespawn == 0 && INSTANCE.getConfig().shouldRespawnDelay) {
			this.client.player.requestRespawn();
			if(!wasHudHidden && hudWasHiddenByMod) {
				this.client.options.hudHidden = false;
			}
			sendDeathCoords();
		}
	}
}