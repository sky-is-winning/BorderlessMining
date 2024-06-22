package link.infra.borderlessmining.mixin;

import link.infra.borderlessmining.config.ConfigHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Monitor;

import java.util.function.Consumer;

@Mixin(VideoOptionsScreen.class)
public abstract class FullScreenOptionMixin extends GameOptionsScreen {
    private static final Text TITLE_TEXT = Text.translatable("options.videoTitle");

    public FullScreenOptionMixin(Screen parent, MinecraftClient client, GameOptions gameOptions) {
        super(parent, gameOptions, TITLE_TEXT);
    }

    @Shadow
    private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
        return new SimpleOption[] {
            gameOptions.getGraphicsMode(), gameOptions.getViewDistance(), gameOptions.getChunkBuilderMode(), gameOptions.getSimulationDistance(), gameOptions.getAo(), gameOptions.getMaxFps(), gameOptions.getEnableVsync(), gameOptions.getBobView(), gameOptions.getGuiScale(), gameOptions.getAttackIndicator(), gameOptions.getGamma(), gameOptions.getCloudRenderMode(), gameOptions.getFullscreen(), gameOptions.getParticles(), gameOptions.getMipmapLevels(), gameOptions.getEntityShadows(), gameOptions.getDistortionEffectScale(), gameOptions.getEntityDistanceScaling(), gameOptions.getFovEffectScale(), gameOptions.getShowAutosaveIndicator(), gameOptions.getGlintSpeed(), gameOptions.getGlintStrength(), gameOptions.getMenuBackgroundBlurriness()
        };
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
            j = (Integer) optional.map(monitor::findClosestVideoModeIndex).orElse(-1);
        }

        // Add one extra option at the end for Borderless Windowed
        SimpleOption.ValidatingIntSliderCallbacks cb = new SimpleOption.ValidatingIntSliderCallbacks(-1, monitor != null ? monitor.getVideoModeCount() - 1 : -1);
        int bmOption = cb.maxInclusive() + 1;
        cb = new SimpleOption.ValidatingIntSliderCallbacks(cb.minInclusive(), bmOption);

        // Modify the text getter to show Borderless Mining text
        SimpleOption.ValueTextGetter<Integer> oldTextGetter = (optionText, value) -> {
            if (monitor == null) {
                return Text.translatable("options.fullscreen.unavailable");
            } else if (value == -1) {
                return GameOptions.getGenericValueText(optionText, Text.translatable("options.fullscreen.current"));
            } else {
                VideoMode videoMode = monitor.getVideoMode(value);
                return GameOptions.getGenericValueText(optionText, Text.translatable("options.fullscreen.entry", new Object[] {
                    videoMode.getWidth(), videoMode.getHeight(), videoMode.getRefreshRate(), videoMode.getRedBits() + videoMode.getGreenBits() + videoMode.getBlueBits()
                }));
            }
        };

        SimpleOption.ValueTextGetter<Integer> textGetter = (SimpleOption.ValueTextGetter<Integer> )(optionText, value) -> {
            if (value == bmOption) {
                return Text.translatable("text.borderlessmining.videomodename");
            }
            return oldTextGetter.toString(optionText, value);
        };

        // Change the default based on the existing option selection
        j = ConfigHandler.getInstance().isEnabledOrPending() ? bmOption : j;

        // Update BM settings when the slider is changed
        Consumer<Integer> oldConsumer = (value) -> {
            if (monitor != null) {
                window.setVideoMode(value == -1 ? Optional.empty() : Optional.of(monitor.getVideoMode(value)));
            }
        };

        Consumer<Integer> consumer = (value) -> {
            if (value == bmOption) {
                ConfigHandler.getInstance().setEnabledPending(true);
                // Set the actual value to "Current"
                oldConsumer.accept(-1);
            } else {
                ConfigHandler.getInstance().setEnabledPending(false);
                oldConsumer.accept(value);
            }
        };

        SimpleOption<Integer> simpleOption = new SimpleOption<Integer> ("options.fullscreen.resolution", SimpleOption.emptyTooltip(), textGetter, cb, j, consumer);

        this.body.addSingleOptionEntry(simpleOption);
        this.body.addSingleOptionEntry(this.gameOptions.getBiomeBlendRadius());
        this.body.addAll(getOptions(this.gameOptions));
    }
}