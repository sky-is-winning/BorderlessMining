package link.infra.borderlessmining.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This Mixin fixes a bug in Keyboard that causes video mode changes to not be applied when pressing Esc.
 * See: https://bugs.mojang.com/browse/MC-175437
 */
@Mixin(VideoOptionsScreen.class)
public class VideoModeFixMixin extends GameOptionsScreen {
	private VideoModeFixMixin(Screen parent, GameOptions gameOptions, Text title) {
		super(parent, gameOptions, title);
	}

	@Inject(at = @At("HEAD"), method = "removed()V")
	public void screenRemoved(CallbackInfo ci) {
		if (this.client != null) {
			this.client.getWindow().applyFullscreenVideoMode();
		}
	}

	@Override
	protected void addOptions() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'addOptions'");
	}
}
