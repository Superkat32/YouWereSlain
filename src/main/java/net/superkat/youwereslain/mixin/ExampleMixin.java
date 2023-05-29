package net.superkat.youwereslain.mixin;

import com.google.common.collect.Lists;
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

@Mixin(DeathScreen.class)
public abstract class ExampleMixin extends Screen {

//	@Shadow @Nullable protected abstract Style getTextComponentUnderMouse(int mouseX);

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
	private Text message; //The player's death message
	public final boolean isHardcore;
	private Text scoreText;
	private Text respawnText = Text.of("0");
	private Text deathCoords;
	private Text deathCoordsMessage;
	private final List<ButtonWidget> buttons = Lists.newArrayList();

	public ExampleMixin(@Nullable Text message, boolean isHardcore) {
		super(Text.of(""));
		this.message = message;
		this.isHardcore = isHardcore;
	}

	@Inject(method = "init", at = @At("RETURN"))
	private void init(CallbackInfo callbackInfo) {
		LOGGER.info("You died...");
		ticksSinceDeath = 0;
		ticksUntilRespawn = respawnDelayTicks;
		secondsUntilRespawn = respawnDelayTicks / 20;
		ticksToSeconds = 0;
		ticksSinceShiftPress = 0;
		this.buttons.clear();
		if(showRespawnButton || showTitleScreenButton) {
			//Respawn or spectate button
			if(showRespawnButton) {
				this.buttons.add(this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72, 200, 20, this.isHardcore ? Text.translatable("deathScreen.spectate") : Text.translatable("deathScreen.respawn"), button -> {
					this.client.player.requestRespawn();
					this.client.setScreen(null);
				})));
			}

			//Exit game button
			if(showTitleScreenButton) {
				this.buttons.add(this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96, 200, 20, Text.translatable("deathScreen.titleScreen"), button -> {
					if (this.isHardcore) {
						this.quitLevel();
						return;
					}
					ConfirmScreen confirmScreen = new ConfirmScreen(this::onConfirmQuit, Text.translatable("deathScreen.quit.confirm"), ScreenTexts.EMPTY, Text.translatable("deathScreen.titleScreen"), Text.translatable("deathScreen.respawn"));
					this.client.setScreen(confirmScreen);
					confirmScreen.disableButtons(20);
				})));
			}

			for (ButtonWidget buttonWidget : this.buttons) {
				buttonWidget.active = false;
			}
		}

		if(INSTANCE.getConfig().score) {
			//Sets the score text
			this.scoreText = Text.translatable("deathScreen.score").append(": ").append(Text.literal(Integer.toString(this.client.player.getScore())).formatted(Formatting.YELLOW));
		}
		this.deathCoords = Text.of(this.client.player.getBlockX() + ", " + this.client.player.getBlockY() + ", " + this.client.player.getBlockZ());
		this.deathCoordsMessage = Text.literal("Death coordinates: " + this.client.player.getBlockX() + ", " + this.client.player.getBlockY() + ", " + this.client.player.getBlockZ()).formatted(Formatting.RED);
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
		//Red death gradient renderer
		this.fillGradient(matrices, 0, 0, this.width, this.height, 1615855616, -1602211792);
		matrices.push();
		matrices.scale(2.0F, 2.0F, 2.0F);

		//You Died! text renderer
		int deathmessagecolor = INSTANCE.getConfig().deathMessageColor.getRGB();
		drawCenteredText(matrices, this.textRenderer, INSTANCE.getConfig().deathMessage, this.width / 2 / 2, 30, deathmessagecolor);

		//Respawn timer renderer
		int respawnMessageColor = INSTANCE.getConfig().respawnMessageColor.getRGB();
		if(INSTANCE.getConfig().respawnTimer && !showRespawnButton) {
			drawCenteredText(matrices, this.textRenderer, this.respawnText, this.width / 2 / 2, 68, respawnMessageColor);
		}
		matrices.pop();

		//Respawn timer renderer
		if(INSTANCE.getConfig().respawnTimer && showRespawnButton) {
			drawCenteredText(matrices, this.textRenderer, this.respawnText, this.width / 2, 123, respawnMessageColor);
		}

		//Death message text renderer
		if (this.message != null && INSTANCE.getConfig().deathReason) {
            int deathreasoncolor = INSTANCE.getConfig().deathReasonColor.getRGB();
			drawCenteredText(matrices, this.textRenderer, this.message, this.width / 2, 85, deathreasoncolor);
		}

		//Score text renderer
		if(this.scoreText != null && INSTANCE.getConfig().score) {
			int scorecolor = INSTANCE.getConfig().scoreColor.getRGB();
			drawCenteredText(matrices, this.textRenderer, this.scoreText, this.width / 2, 100, scorecolor);
		}

		if(this.deathCoords != null && INSTANCE.getConfig().showCoords) {
			int deathcoordscolor = INSTANCE.getConfig().coordsColor.getRGB();
			drawCenteredText(matrices, this.textRenderer, this.deathCoords, this.width / 2, 112, deathcoordscolor);
			//TODO fix issue where log says player pos is null because showcoords is disabled
		} else {
			if(ticksSinceDeath == 3) {
				LOGGER.warn("DEATH COORDS NULL");
			}
		}

		//Button stuff
		if(showRespawnButton || showTitleScreenButton) {
			if (this.message != null && mouseY > 85 && mouseY < 85 + this.textRenderer.fontHeight) {
				Style style = this.getTextComponentUnderMouse(mouseX);
				this.renderTextHoverEffect(matrices, style, mouseX, mouseY);
			}

			for (ButtonWidget buttonWidget : this.buttons) {
				buttonWidget.render(matrices, mouseX, mouseY, delta);
			}
		}

		if(shiftIsHeldDown && !showRespawnButton) {
			String emergencyRespawnString = INSTANCE.getConfig().emergencyRespawnMessage;
			float determineTime = 1.5f - (ticksSinceShiftPress / 20f);
			String formattedTime = String.format("%.1f", determineTime);
			Text emergencyRespawn = Text.of(emergencyRespawnString.replaceAll("<time>", formattedTime));
			drawCenteredText(matrices, this.textRenderer, emergencyRespawn, this.width / 2, 185, 12632256);
		}

//		super.render(matrices, mouseX, mouseY, delta);
	}

	@Nullable
	private Style getTextComponentUnderMouse(int mouseX) {
		if (this.message == null) {
			return null;
		}
		int i = this.client.textRenderer.getWidth(this.message);
		int j = this.width / 2 - i / 2;
		int k = this.width / 2 + i / 2;
		if (mouseX < j || mouseX > k) {
			return null;
		}
		return this.client.textRenderer.getTextHandler().getStyleAt(this.message, mouseX - j);
	}

	@Inject(method = "tick", at = @At("RETURN"))
	private void tick(CallbackInfo ci) {
		//Counting ticks stuff
		super.tick();
		++this.ticksToSeconds;
		--this.ticksUntilRespawn;

		//Respawning message
		respawnMessage = INSTANCE.getConfig().respawningMessage;
		this.respawnText = Text.of(respawnMessage.replaceAll("<time>", String.valueOf(secondsUntilRespawn)));

		//Chat death coords message
		if(ticksSinceDeath == 3 && INSTANCE.getConfig().sendCoordsInChat) {
			this.client.inGameHud.getChatHud().addMessage(deathCoordsMessage);
		}

		//Second counter
		if(ticksToSeconds == 20) {
			--secondsUntilRespawn;
			ticksToSeconds = 0;
		}

		if(INSTANCE.getConfig().shiftOverridesDelay) {
//			int test = 0;
//			LOGGER.info(String.valueOf(GLFW.GLFW_MOUSE_BUTTON_LEFT));
			if(MouseOptionsScreen.hasShiftDown() && overrideButtonOptions && ticksSinceShiftPress == 30) {
				LOGGER.info("Shift has been pressed!");
				showRespawnButton = true;
				overrideButtonOptions = false;
				shiftIsHeldDown = false;
				this.buttons.add(this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72, 200, 20, this.isHardcore ? Text.translatable("deathScreen.spectate") : Text.translatable("deathScreen.respawn"), button -> {
					this.client.player.requestRespawn();
					this.client.setScreen(null);
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
		if (this.ticksUntilRespawn == 0) {
			for (ButtonWidget buttonWidget : this.buttons) {
				buttonWidget.active = true;
			}
			this.client.player.requestRespawn();
		}
	}
}