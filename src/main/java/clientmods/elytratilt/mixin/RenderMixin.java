package clientmods.elytratilt.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class RenderMixin {
    @Inject(at = @At("HEAD"), method = "renderWorld")
    private void render(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }
        LivingEntity player = client.player;
        if (player.deathTime > 0) {
            float f = ((float) player.deathTime + tickDelta - 1.0f) / 20.0f * 1.6f;
            if ((f = MathHelper.sqrt(f)) > 1.0f) {
                f = 1.0f;
            }
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(f * -90f));
        } else if (player.isUsingRiptide()) {
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(((float)player.age + tickDelta) * -75.0f));
        } else if (player.isFallFlying()) {
            Vec3d vec3d = player.getRotationVec(tickDelta);
            Vec3d vec3d2 = player.getVelocity();
            double d = vec3d2.horizontalLengthSquared();
            double e = vec3d.horizontalLengthSquared();
            if (d > 0.0 && e > 0.0) {
                double l = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(d * e);
                double m = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
                matrices.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion((float) (Math.signum(m) * Math.acos(l))));
            }
        }
    }
}
