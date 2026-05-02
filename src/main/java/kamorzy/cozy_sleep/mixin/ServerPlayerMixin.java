package kamorzy.cozy_sleep.mixin;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import kamorzy.cozy_sleep.SleepConditionChecks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(
            method = "startSleepInBed",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cozy_sleep$checkExtraSleepConditions(
            BlockPos bedPos,
            CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> cir
    ) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        ServerLevel level = (ServerLevel) player.level();

        if (!SleepConditionChecks.hasLitCampfireNearby(level, bedPos)) {
            player.connection.player.sendSystemMessage(
                    Component.literal("You need a lit campfire nearby to sleep."),
                    true
            );

            cir.setReturnValue(Either.left(Player.BedSleepingProblem.OTHER_PROBLEM));
            return;
        }

        if (!SleepConditionChecks.hasRoofCoverageAboveBed(level, bedPos)) {
            player.connection.player.sendSystemMessage(
                    Component.literal("You need some kind of roof coverage above the bed to sleep."),
                    true
            );

            cir.setReturnValue(Either.left(Player.BedSleepingProblem.OTHER_PROBLEM));
        }
    }
}