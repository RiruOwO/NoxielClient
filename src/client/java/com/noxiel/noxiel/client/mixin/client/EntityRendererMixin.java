package com.noxiel.noxiel.client.mixin.client;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {

    @Inject(method = "getShadowRadius", at = @At("HEAD"), cancellable = true)
    private void disableShadowRadius(T entity, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(0.0f);
    }
}