package net.superkat.youwereslain.mixin;

import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.superkat.youwereslain.YouWereSlainConfig.INSTANCE;
import static net.superkat.youwereslain.YouWereSlainMain.LOGGER;

@Mixin(DeathScreen.class)
public class ExampleMixin extends Screen {

//	@Shadow @Nullable protected abstract Style getTextComponentUnderMouse(int mouseX);

	public int ticksSinceDeath;
	String ticksSinceDeathString;
	private Text message; //The player's death message
	public final boolean isHardcore;
	private Text scoreText;
//	public final List<ButtonWidget> buttons = Lists.newArrayList();

	public ExampleMixin(@Nullable Text message, boolean isHardcore) {
		super(Text.of(""));
		this.message = message;
		this.isHardcore = isHardcore;
	}

	@Inject(method = "init", at = @At("RETURN"))
	private void init(CallbackInfo callbackInfo) {
		LOGGER.info("You died...");
		ticksSinceDeath = 0;
//		this.buttons.clear();
		//I don't think this does anything
//		this.buttons.removeAll(buttons);
		//Respawn or spectate button
//		this.buttons.add((ButtonWidget) this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72, 200, 20, isHardcore ? Text.translatable("deathScreen.spectate") : Text.translatable("deathScreen.respawn"), (button) -> {
//			this.client.player.requestRespawn();
//			this.client.setScreen((Screen)null);
//		})));

		//Exit game button
//        this.buttons.add((ButtonWidget)this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96, 200, 20, Text.translatable("deathScreen.titleScreen"), (button) -> {
//            if (this.isHardcore) {
//                this.quitLevel();
//            } else {
//                ConfirmScreen confirmScreen = new ConfirmScreen(this::onConfirmQuit, Text.translatable("deathScreen.quit.confirm"), ScreenTexts.EMPTY, Text.translatable("deathScreen.titleScreen"), Text.translatable("deathScreen.respawn"));
//                this.client.setScreen(confirmScreen);
//                confirmScreen.disableButtons(20);
//            }
//        })));

//		ButtonWidget buttonWidget;
//		for(Iterator var1 = this.buttons.iterator(); var1.hasNext(); buttonWidget.active = false) {
//			buttonWidget = (ButtonWidget)var1.next();
//		}

		//Sets the score text
//		this.scoreText = Text.translatable("deathScreen.score").append(": ").append(Text.literal(Integer.toString(this.client.player.getScore())).formatted(Formatting.YELLOW));
		//Deletes the score text
//		this.scoreText = Text.literal("");
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
		matrices.pop();
		//Death message text renderer
		if (this.message != null) {
			drawCenteredText(matrices, this.textRenderer, this.message, this.width / 2, 85, 16777215);
		}

		//Score text renderer
		drawCenteredText(matrices, this.textRenderer, this.scoreText, this.width / 2, 100, 16777215);

		//Button stuff
//		if (this.message != null && mouseY > 85) {
//			Objects.requireNonNull(this.textRenderer);
//			if (mouseY < 85 + 9) {
//				Style style = this.getTextComponentUnderMouse(mouseX);
//				this.renderTextHoverEffect(matrices, style, mouseX, mouseY);
//			}
//		}

//		super.render(matrices, mouseX, mouseY, delta);
	}

	@Inject(method = "tick", at = @At("RETURN"))
	private void tick(CallbackInfo ci) {
		super.tick();
		++this.ticksSinceDeath;
		ticksSinceDeathString = String.valueOf(ticksSinceDeath);
//		ButtonWidget buttonWidget;
		this.scoreText = Text.of(ticksSinceDeathString);
		if (this.ticksSinceDeath == 100) {
//			for(Iterator var1 = this.buttons.iterator(); var1.hasNext(); buttonWidget.active = true) {
//				buttonWidget = (ButtonWidget)var1.next();
//			}
			this.client.player.requestRespawn();
		}
	}
}