package net.backslot.mixin;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
  @Shadow
  @Final
  @Mutable
  private MinecraftClient client;
  @Shadow
  private static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");
  @Shadow
  private int scaledWidth;
  @Shadow
  private int scaledHeight;

  public InGameHudMixin(MinecraftClient client) {
    this.client = client;
  }

  @Inject(method = "renderHotbar", at = @At(value = "RETURN"))
  public void renderHotbarMixin(float f, MatrixStack matrixStack, CallbackInfo info) {
    PlayerEntity playerEntity = this.getCameraPlayer();
    if (playerEntity != null) {
      ItemStack backSlotStack = playerEntity.inventory.getStack(41);
      ItemStack beltSlotStack = playerEntity.inventory.getStack(42);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      int i = this.scaledWidth / 2;
      int p = this.scaledHeight - 16 - 3;
      Arm arm = playerEntity.getMainArm().getOpposite();
      if (!backSlotStack.isEmpty() || !beltSlotStack.isEmpty()) {
        if (arm == Arm.LEFT) {
          RenderSystem.enableBlend();
          this.client.getTextureManager().bindTexture(WIDGETS_TEXTURE);
          this.drawTexture(matrixStack, i + 91, this.scaledHeight - 23, 53, 22, 29, 24);
          this.renderHotbarItem(i + 91 + 10, p, f, playerEntity, backSlotStack);
          RenderSystem.enableBlend();
          this.client.getTextureManager().bindTexture(WIDGETS_TEXTURE);
          this.drawTexture(matrixStack, i + 112, this.scaledHeight - 23, 53, 22, 29, 24);
          this.renderHotbarItem(i + 112 + 10, p, f, playerEntity, beltSlotStack);
        } else {
          RenderSystem.enableBlend();
          this.drawTexture(matrixStack, i - 91 - 29, this.scaledHeight - 23, 24, 22, 29, 24);
          this.renderHotbarItem(i - 91 - 26, p, f, playerEntity, backSlotStack);
          RenderSystem.enableBlend();
          this.client.getTextureManager().bindTexture(WIDGETS_TEXTURE);
          this.drawTexture(matrixStack, i - 70 - 29, this.scaledHeight - 23, 24, 22, 29, 24);
          this.renderHotbarItem(i - 70 - 26, p, f, playerEntity, beltSlotStack);
        }
        RenderSystem.disableBlend();
      }
    }
  }

  @Shadow
  private PlayerEntity getCameraPlayer() {
    return (PlayerEntity) this.client.getCameraEntity();
  }

  @Shadow
  private void renderHotbarItem(int i, int j, float f, PlayerEntity playerEntity, ItemStack itemStack) {
  }

}