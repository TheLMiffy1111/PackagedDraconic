package thelm.packageddraconic.client.screen;

import com.brandon3055.brandonscore.BCConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thelm.packagedauto.client.screen.BaseScreen;
import thelm.packageddraconic.menu.FusionCrafterMenu;

public class FusionCrafterScreen extends BaseScreen<FusionCrafterMenu> {

	public static final ResourceLocation BACKGROUND_LIGHT = new ResourceLocation("packageddraconic:textures/gui/fusion_crafter_light.png");
	public static final ResourceLocation BACKGROUND_DARK = new ResourceLocation("packageddraconic:textures/gui/fusion_crafter_dark.png");

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
	protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(poseStack, partialTicks, mouseX, mouseY);
		blit(poseStack, leftPos+75, topPos+35, 176, 0, menu.blockEntity.getScaledProgress(22), 16);
		int scaledEnergy = menu.blockEntity.getScaledEnergy(40);
		blit(poseStack, leftPos+10, topPos+10+40-scaledEnergy, 176, 16+40-scaledEnergy, 12, scaledEnergy);
	}

	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		String s = menu.blockEntity.getDisplayName().getString();
		int color = BCConfig.darkMode ? 0xAFB1B3 : 0x111111;
		font.draw(poseStack, s, Math.max(25, imageWidth/2 - font.width(s)/2), 6, color);
		font.draw(poseStack, menu.inventory.getDisplayName().getString(), menu.getPlayerInvX(), menu.getPlayerInvY()-11, color);
		if(mouseX-leftPos >= 10 && mouseY-topPos >= 10 && mouseX-leftPos <= 21 && mouseY-topPos <= 49) {
			renderTooltip(poseStack, new TextComponent(menu.blockEntity.getEnergyStorage().getEnergyStored()+" / "+menu.blockEntity.getEnergyStorage().getMaxEnergyStored()+" FE"), mouseX-leftPos, mouseY-topPos);
		}
		for(GuiEventListener child : children()) {
			if(child.isMouseOver(mouseX, mouseY) && child instanceof AbstractWidget button) {
				button.renderToolTip(poseStack, mouseX-leftPos, mouseY-topPos);
				break;
			}
		}
	}

	class ButtonTheme extends AbstractWidget {

		public ButtonTheme(int x, int y) {
			super(x, y, 12, 12, TextComponent.EMPTY);
		}

		@Override
		public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
			if(isHoveredOrFocused()) {
				fill(poseStack, x, y, x+12, y+12, BCConfig.darkMode ? 0xFF475B6A : 0xFF647BAF);
			}
			RenderSystem.setShaderTexture(0, getBackgroundTexture());
			blit(poseStack, x, y, 176, 56, 12, 12);
		}

		@Override
		public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY) {
			renderTooltip(poseStack, new TranslatableComponent("gui_tkt.brandonscore."+(BCConfig.darkMode ? "theme.light" : "theme.dark")), mouseX, mouseY);
		}

		@Override
		public void onClick(double mouseX, double mouseY) {
			BCConfig.modifyClientProperty("darkMode", e->e.setBoolean(!BCConfig.darkMode));
		}

		@Override
		public void updateNarration(NarrationElementOutput narrationElementOutput) {

		}
	}
}
