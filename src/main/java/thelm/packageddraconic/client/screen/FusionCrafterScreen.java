package thelm.packageddraconic.client.screen;

import com.brandon3055.brandonscore.BCConfig;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import thelm.packagedauto.client.screen.BaseScreen;
import thelm.packageddraconic.container.FusionCrafterContainer;

public class FusionCrafterScreen extends BaseScreen<FusionCrafterContainer> {

	public static final ResourceLocation BACKGROUND_LIGHT = new ResourceLocation("packageddraconic:textures/gui/fusion_crafter_light.png");
	public static final ResourceLocation BACKGROUND_DARK = new ResourceLocation("packageddraconic:textures/gui/fusion_crafter_dark.png");

	public FusionCrafterScreen(FusionCrafterContainer container, PlayerInventory playerInventory, ITextComponent title) {
		super(container, playerInventory, title);
	}

	@Override
	protected ResourceLocation getBackgroundTexture() {
		return BCConfig.darkMode ? BACKGROUND_DARK : BACKGROUND_LIGHT;
	}

	@Override
	public void init() {
		buttons.clear();
		super.init();
		addButton(new ButtonTheme(leftPos+161, topPos+3));
	}

	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(matrixStack, partialTicks, mouseX, mouseY);
		blit(matrixStack, leftPos+75, topPos+35, 176, 0, menu.tile.getScaledProgress(22), 16);
		int scaledEnergy = menu.tile.getScaledEnergy(40);
		blit(matrixStack, leftPos+10, topPos+10+40-scaledEnergy, 176, 16+40-scaledEnergy, 12, scaledEnergy);
	}

	@Override
	protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
		String s = menu.tile.getDisplayName().getString();
		int color = BCConfig.darkMode ? 0xAFB1B3 : 0x111111;
		font.draw(matrixStack, s, Math.max(25, imageWidth/2 - font.width(s)/2), 6, color);
		font.draw(matrixStack, menu.playerInventory.getDisplayName().getString(), menu.getPlayerInvX(), menu.getPlayerInvY()-11, color);
		if(mouseX-leftPos >= 10 && mouseY-topPos >= 10 && mouseX-leftPos <= 21 && mouseY-topPos <= 49) {
			renderTooltip(matrixStack, new StringTextComponent(menu.tile.getEnergyStorage().getEnergyStored()+" / "+menu.tile.getEnergyStorage().getMaxEnergyStored()+" FE"), mouseX-leftPos, mouseY-topPos);
		}
		for(Widget button : buttons) {
			if(button.isMouseOver(mouseX, mouseY)) {
				button.renderToolTip(matrixStack, mouseX-leftPos, mouseY-topPos);
				break;
			}
		}
	}

	class ButtonTheme extends Widget {

		public ButtonTheme(int x, int y) {
			super(x, y, 12, 12, StringTextComponent.EMPTY);
		}

		@Override
		public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
			if(isHovered()) {
				fill(matrixStack, x, y, x+12, y+12, BCConfig.darkMode ? 0xFF475B6A : 0xFF647BAF);
			}
			minecraft.getTextureManager().bind(getBackgroundTexture());
			blit(matrixStack, x, y, 176, 56, 12, 12);
		}

		@Override
		public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
			renderTooltip(matrixStack, new TranslationTextComponent("gui_tkt.brandonscore."+(BCConfig.darkMode ? "theme.light" : "theme.dark")), mouseX, mouseY);
		}

		@Override
		public void onClick(double mouseX, double mouseY) {
			BCConfig.modifyClientProperty("darkMode", e->e.setBoolean(!BCConfig.darkMode));
		}
	}
}
