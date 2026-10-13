package dev.snprb;

import java.io.File;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class del extends Screen {
	final Screen p;
	final File f;

	public del(Screen p, File f) {
		super(Component.literal("Delete this screenshot?"));
		this.p = p;
		this.f = f;
	}

	@Override
	protected void init() {
		int y = height / 2, x = width / 2;
		addRenderableWidget(Button.builder(Component.literal("Yes"), b -> {
			f.delete();
			minecraft.setScreen(p instanceof viw v ? v.p : p);
		}).bounds(x - 105, y, 100, 20).build());
		addRenderableWidget(Button.builder(Component.literal("No"), b -> onClose()).bounds(x + 5, y, 100, 20).build());
	}

	@Override
	public void render(GuiGraphics c, int mx, int my, float d) {
		super.render(c, mx, my, d);
		c.drawCenteredString(font, title, width / 2, height / 2 - 40, 0xFFFF5555);
		c.drawCenteredString(font, f.getName(), width / 2, height / 2 - 25, 0xFFAAAAAA);
	}

	@Override
	public void onClose() {
		minecraft.setScreen(p);
	}
}
