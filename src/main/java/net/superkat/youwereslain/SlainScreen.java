package net.superkat.youwereslain;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static net.superkat.youwereslain.YouWereSlainMain.LOGGER;

public class SlainScreen extends DeathScreen {

    private final Text message;
    private final boolean isHardcore;
    private Text scoreText;
    private final List<ButtonWidget> buttons = Lists.newArrayList();

    public SlainScreen(@Nullable Text message, boolean isHardcore) {
        super(Text.literal("You were slain..."), MinecraftClient.getInstance().world.getLevelProperties().isHardcore());
        this.message = message;
        this.isHardcore = isHardcore;
    }

    @Override
    protected void init() {
        ticksSinceDeath = 0;
        this.buttons.clear();
        //Respawn or spectate button
        this.buttons.add((ButtonWidget) this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72, 200, 20, isHardcore ? Text.translatable("deathScreen.spectate") : Text.translatable("deathScreen.respawn"), (button) -> {
            this.client.player.requestRespawn();
            this.client.setScreen((Screen)null);
        })));

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

        ButtonWidget buttonWidget;
        for(Iterator var1 = this.buttons.iterator(); var1.hasNext(); buttonWidget.active = false) {
            buttonWidget = (ButtonWidget)var1.next();
        }

        this.scoreText = Text.translatable("deathScreen.score").append(": ").append(Text.literal(Integer.toString(this.client.player.getScore())).formatted(Formatting.YELLOW));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.fillGradient(matrices, 0, 0, this.width, this.height, 1615855616, -1602211792);
        matrices.push();
        matrices.scale(2.0F, 2.0F, 2.0F);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2 / 2, 30, 16777215);
        matrices.pop();
        if (this.message != null) {
            drawCenteredText(matrices, this.textRenderer, this.message, this.width / 2, 85, 16777215);
        }

        drawCenteredText(matrices, this.textRenderer, this.scoreText, this.width / 2, 100, 16777215);
        if (this.message != null && mouseY > 85) {
            Objects.requireNonNull(this.textRenderer);
            if (mouseY < 85 + 9) {
                Style style = this.getTextComponentUnderMouse(mouseX);
                this.renderTextHoverEffect(matrices, style, mouseX, mouseY);
            }
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Nullable
    private Style getTextComponentUnderMouse(int mouseX) {
        if (this.message == null) {
            return null;
        } else {
            int i = this.client.textRenderer.getWidth(this.message);
            int j = this.width / 2 - i / 2;
            int k = this.width / 2 + i / 2;
            return mouseX >= j && mouseX <= k ? this.client.textRenderer.getTextHandler().getStyleAt(this.message, mouseX - j) : null;
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.message != null && mouseY > 85.0) {
            Objects.requireNonNull(this.textRenderer);
            if (mouseY < (double)(85 + 9)) {
                Style style = this.getTextComponentUnderMouse((int)mouseX);
                if (style != null && style.getClickEvent() != null && style.getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
                    this.handleTextClick(style);
                    return false;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        LOGGER.info("tick");
        ++this.ticksSinceDeath;
        ButtonWidget buttonWidget;
        if (this.ticksSinceDeath == 20) {
            for(Iterator var1 = this.buttons.iterator(); var1.hasNext(); buttonWidget.active = true) {
                buttonWidget = (ButtonWidget)var1.next();
            }
        }
    }



    private void onConfirmQuit(boolean quit) {
        if (quit) {
            this.quitLevel();
        } else {
            this.client.player.requestRespawn();
            this.client.setScreen((Screen)null);
        }
    }

    private void quitLevel() {
        if (this.client.world != null) {
            this.client.world.disconnect();
        }

        this.client.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
        this.client.setScreen(new TitleScreen());
    }
}
