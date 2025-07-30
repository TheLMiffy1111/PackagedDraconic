package thelm.packageddraconic.client.screen;

import com.brandon3055.brandonscore.BCConfig;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thelm.packagedauto.client.screen.BaseScreen;
import thelm.packageddraconic.menu.FusionCrafterMenu;

public class FusionCrafterScreen extends BaseScreen<FusionCrafterMenu> {

	public static final ResourceLocation BACKGROUND_LIGHT = ResourceLocation.parse("packageddraconic:textures/gui/fusion_crafter_light.png");
	public static final ResourceLocation BACKGROUND_DARK = ResourceLocation.parse("packageddraconic:textures/gui/fusion_crafter_dark.png");

	public FusionCrafterScreen(FusionCrafterMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@Override
	protected ResourceLocation getBackgroundTexture() {
		return BCConfig.darkMode ? BACKGROUND_DARK : BACKGROUND_LIGHT;
	}

	@Override
	public void init() {
		clearWidgets();
		super.init();
		addRenderableWidget(new ButtonTheme(leftPos+161, topPos+3));
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(graphics, partialTicks, mouseX, mouseY);
		graphics.blit(getBackgroundTexture(), leftPos+75, topPos+35, 176, 0, menu.blockEntity.getScaledProgress(22), 16);
		int scaledEnergy = menu.blockEntity.getScaledEnergy(40);
		graphics.blit(getBackgroundTexture(), leftPos+10, topPos+10+40-scaledEnergy, 176, 16+40-scaledEnergy, 12, scaledEnergy);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		String s = menu.blockEntity.getDisplayName().getString();
		int color = BCConfig.darkMode ? 0xAFB1B3 : 0x111111;
		graphics.drawString(font, s, Math.max(25, imageWidth/2 - font.width(s)/2), 6, color, false);
		graphics.drawString(font, menu.inventory.getDisplayName().getString(), menu.getPlayerInvX(), menu.getPlayerInvY()-11, color, false);
		if(mouseX-leftPos >= 10 && mouseY-topPos >= 10 && mouseX-leftPos <= 21 && mouseY-topPos <= 49) {
			graphics.renderTooltip(font, Component.literal(menu.blockEntity.getEnergyStorage().getEnergyStored()+" / "+menu.blockEntity.getEnergyStorage().getMaxEnergyStored()+" FE"), mouseX-leftPos, mouseY-topPos);
		}
	}

	class ButtonTheme extends AbstractButton {

		final Tooltip lightTooltip = Tooltip.create(Component.translatable("gui_tkt.brandonscore.theme.light"));
		final Tooltip darkTooltip = Tooltip.create(Component.translatable("gui_tkt.brandonscore.theme.dark"));

		public ButtonTheme(int x, int y) {
			super(x, y, 12, 12, Component.empty());
		}

		@Override
		public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
			setTooltip(BCConfig.darkMode ? lightTooltip : darkTooltip);
			super.renderWidget(graphics, mouseX, mouseY, partialTicks);
			if(isHoveredOrFocused()) {
				graphics.fill(getX(), getY(), getX()+12, getY()+12, BCConfig.darkMode ? 0xFF475B6A : 0xFF647BAF);
			}
			graphics.blit(getBackgroundTexture(), getX(), getY(), 176, 56, 12, 12);
		}

		@Override
		public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

		@Override
		public void onPress() {
			BCConfig.modifyClientProperty("darkMode", e->e.setBoolean(!BCConfig.darkMode));
		}
	}
}
