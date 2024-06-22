package link.infra.borderlessmining.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.util.Monitor;

@Mixin(VideoOptionsScreen.class)
public abstract class FullScreenOptionMixin extends GameOptionsScreen{
	private static final Text TITLE_TEXT = Text.translatable("options.videoTitle");

	public FullScreenOptionMixin(Screen parent, MinecraftClient client, GameOptions gameOptions) {
		super(parent, gameOptions, TITLE_TEXT);
	}

	@Shadow
	private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
		return new SimpleOption[]{gameOptions.getGraphicsMode(), gameOptions.getViewDistance(), gameOptions.getChunkBuilderMode(), gameOptions.getSimulationDistance(), gameOptions.getAo(), gameOptions.getMaxFps(), gameOptions.getEnableVsync(), gameOptions.getBobView(), gameOptions.getGuiScale(), gameOptions.getAttackIndicator(), gameOptions.getGamma(), gameOptions.getCloudRenderMode(), gameOptions.getFullscreen(), gameOptions.getParticles(), gameOptions.getMipmapLevels(), gameOptions.getEntityShadows(), gameOptions.getDistortionEffectScale(), gameOptions.getEntityDistanceScaling(), gameOptions.getFovEffectScale(), gameOptions.getShowAutosaveIndicator(), gameOptions.getGlintSpeed(), gameOptions.getGlintStrength(), gameOptions.getMenuBackgroundBlurriness()};
	}

	@Overwrite
	protected void addOptions() {
      Window window = this.client.getWindow();
      Monitor monitor = window.getMonitor();
      int j;
      if (monitor == null) {
         j = -1;
      } else {
         Optional<VideoMode> optional = window.getVideoMode();
         Objects.requireNonNull(monitor);
         j = (Integer)optional.map(monitor::findClosestVideoModeIndex).orElse(-1);
      }

      SimpleOption<Integer> simpleOption = new SimpleOption<Integer>("options.fullscreen.resolution", SimpleOption.emptyTooltip(), (optionText, value) -> {
         if (monitor == null) {
            return Text.translatable("options.fullscreen.unavailable");
         } else if (value == -1) {
            return GameOptions.getGenericValueText(optionText, Text.translatable("options.fullscreen.current"));
         } else {
            VideoMode videoMode = monitor.getVideoMode(value);
            Text oldTextGetter = GameOptions.getGenericValueText(optionText, Text.translatable("options.fullscreen.entry", new Object[]{videoMode.getWidth(), videoMode.getHeight(), videoMode.getRefreshRate(), videoMode.getRedBits() + videoMode.getGreenBits() + videoMode.getBlueBits()}));
			if (value == monitor.getVideoModeCount()) {
				return Text.translatable("text.borderlessmining.videomodename");
			} else {
				return oldTextGetter;
			}
		}
      }, new SimpleOption.ValidatingIntSliderCallbacks(-1, monitor != null ? monitor.getVideoModeCount(): -1), j, (value) -> {
         if (monitor != null) {
            window.setVideoMode(value == -1 ? Optional.empty() : Optional.of(monitor.getVideoMode(value)));
         }
      });
      this.body.addSingleOptionEntry(simpleOption);
      this.body.addSingleOptionEntry(this.gameOptions.getBiomeBlendRadius());
      this.body.addAll(getOptions(this.gameOptions));
   }
}
