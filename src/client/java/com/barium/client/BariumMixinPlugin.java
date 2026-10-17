package com.barium.client;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class BariumMixinPlugin implements IMixinConfigPlugin {

    private boolean sodiumLoaded = false;

    @Override
    public void onLoad(String mixinPackage) {
        this.sodiumLoaded = FabricLoader.getInstance().isModLoaded("sodium");
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // O Sodium substitui o mesher de blocos; não mexemos nele nesse caso.
        if (sodiumLoaded && mixinClassName.endsWith("BlockRenderDispatcherMixin")) {
            return false;
        }
        return true;
    }

    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String t, ClassNode c, String m, IMixinInfo i) { }
    @Override public void postApply(String t, ClassNode c, String m, IMixinInfo i) { }
}
