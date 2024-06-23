package link.infra.borderlessmining;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class BorderlessMiningMixinPlugin implements IMixinConfigPlugin {
    private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger();

    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        LOGGER.info("Checking if mixin should be applied: " + mixinClassName + " to " + targetClassName);
        if (mixinClassName.equals("link.infra.borderlessmining.mixin.FullScreenOptionMixin") || mixinClassName.equals("link.infra.borderlessmining.mixin.VideoModeFixMixin")) {
            return !FabricLoader.getInstance().isModLoaded("iris");
        }
        return true;
    }

    // Boilerplate

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}