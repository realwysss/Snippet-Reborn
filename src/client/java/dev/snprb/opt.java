package dev.snprb;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class opt extends Screen {
	final Screen p;

	public opt(Screen p) {
		super(Component.literal("Snippet Reborn"));
		this.p = p;
	}

	@Override
	protected void init() {
		int x = width / 2 - 100, y = height / 2 - 40;
		addRenderableWidget(Button.builder(Component.literal("Gallery"), b -> minecraft.setScreen(new gal(this))).bounds(x, y, 200, 20).build());
		addRenderableWidget(CycleButton.onOffBuilder(cfg.c.dwn).create(x, y + 25, 200, 20, Component.literal("Sharp Thumbnails"), (b, v) -> {
			cfg.c.dwn = v;
			cfg.sv();
		}));
		addRenderableWidget(CycleButton.onOffBuilder(cfg.c.msg).create(x, y + 50, 200, 20, Component.literal("Chat Buttons"), (b, v) -> {
			cfg.c.msg = v;
			cfg.sv();
		}));
		addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose()).bounds(x, y + 75, 200, 20).build());
	}

	@Override
	public void render(GuiGraphics c, int mx, int my, float d) {
		super.render(c, mx, my, d);
		c.drawCenteredString(font, title, width / 2, 20, 0xFFFFFFFF);
	}

	@Override
	public void onClose() {
		minecraft.setScreen(p);
	}
}
