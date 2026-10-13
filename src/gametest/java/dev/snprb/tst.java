package dev.snprb;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

public class tst implements FabricClientGameTest {
	static class hk extends Screen {
		hk() {
			super(Component.empty());
		}

		static void go(ClickEvent e, Minecraft m) {
			defaultHandleGameClickEvent(e, m, null);
		}
	}

	ClientGameTestContext c;

	void ck(boolean b, String s) {
		if (!b) throw new AssertionError(s);
		System.out.println("SNPRB OK: " + s);
	}

	void clk(double x, double y) {
		double sc = c.computeOnClient(m -> m.getWindow().getGuiScale());
		c.getInput().setCursorPos(x * sc, y * sc);
		c.getInput().pressMouse(0);
		c.waitTicks(5);
	}

	@Override
	public void runTest(ClientGameTestContext ctx) {
		c = ctx;
		try (TestSingleplayerContext s = ctx.worldBuilder().create()) {
			s.getClientWorld().waitForChunksRender();
			ctx.takeScreenshot("a");
			s.getServer().runCommand("time set night");
			ctx.waitTicks(5);
			ctx.takeScreenshot("b");
			s.getServer().runCommand("time set day");
			ctx.waitTicks(5);
			ctx.takeScreenshot("c");

			ctx.runOnClient(m -> m.setScreen(new gal(null)));
			ctx.waitTicks(40);
			ck(ctx.computeOnClient(m -> ((gal) m.screen).l.stream().filter(i -> i.t != null).count()) >= 3, "gallery loaded thumbs");
			ctx.takeScreenshot("gallery");

			int[] bx = ctx.computeOnClient(m -> {
				gal g = (gal) m.screen;
				g.lay();
				gal.it i = g.l.get(0);
				return new int[] {i.x, i.y, i.w, i.h, g.at(i.x + 5, i.y - 1), g.at(i.x + 5, i.y + i.h), g.at(i.x + 5, i.y), g.at(i.x + i.w - 1, i.y + i.h - 1)};
			});
			ck(bx[4] == -1 && bx[5] == -1, "box is tight, just outside the image is not a hit");
			ck(bx[6] == 0 && bx[7] == 0, "image corners are hits");
			ck(bx[3] < 60, "wide screenshot is shorter than the cell " + bx[3]);

			double gs = ctx.computeOnClient(m -> m.getWindow().getGuiScale());
			ctx.getInput().setCursorPos((bx[0] + bx[2] / 2.0) * gs, (bx[1] + bx[3] / 2.0) * gs);
			ctx.waitTicks(3);
			ctx.takeScreenshot("hover");
			clk(bx[0] + bx[2] / 2.0, bx[1] + bx[3] / 2.0);
			ck(ctx.computeOnClient(m -> m.screen instanceof viw), "click opens viewer");
			ctx.takeScreenshot("viewer");
			ck(ctx.computeOnClient(m -> {
				viw v = (viw) m.screen;
				int sc = (int) m.getWindow().getGuiScale();
				return v.t != null && v.t.pw == v.t.gw * sc && v.t.ph == v.t.gh * sc;
			}), "viewer texture is exactly the on screen pixel size");

			File f = ctx.computeOnClient(m -> ((viw) m.screen).f);
			try {
				snprb.cp(f);
				ck(Toolkit.getDefaultToolkit().getSystemClipboard().isDataFlavorAvailable(DataFlavor.imageFlavor), "image is on clipboard");
			} catch (Throwable e) {
				throw new AssertionError("clipboard failed " + e, e);
			}

			ctx.runOnClient(m -> m.setScreen(null));
			String n = f.getName();
			ctx.runOnClient(m -> m.gui.getChat().addMessage(Component.translatable("screenshot.success", Component.literal(n))));
			ctx.waitTicks(5);
			ctx.takeScreenshot("chat");

			ClickEvent ev = snprb.bar(n).getSiblings().get(2).getStyle().getClickEvent();
			ck(ev instanceof ClickEvent.RunCommand, "view button has run command");
			ctx.runOnClient(m -> hk.go(ev, m));
			ctx.waitTicks(10);
			ck(ctx.computeOnClient(m -> m.screen instanceof viw), "chat view button opens viewer");

			ctx.runOnClient(m -> m.setScreen(new del(null, f)));
			ctx.waitTicks(3);
			ctx.takeScreenshot("delete");
			int[] yes = ctx.computeOnClient(m -> new int[] {m.screen.width / 2 - 55, m.screen.height / 2 + 10});
			clk(yes[0], yes[1]);
			ck(!f.exists(), "delete removed file");

			ctx.runOnClient(m -> m.setScreen(new opt(null)));
			ctx.waitTicks(3);
			ctx.takeScreenshot("settings");

			ctx.runOnClient(m -> m.setScreen(new gal(null)));
			ctx.waitTicks(30);
			ck(ctx.computeOnClient(m -> ((gal) m.screen).l.size()) >= 2, "gallery refreshed after delete");
			ctx.runOnClient(m -> m.setScreen(null));
		}
	}
}
