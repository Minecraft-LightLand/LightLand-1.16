package com.hikarishima.lightland.magic.registry.item.summon;

import com.hikarishima.lightland.magic.registry.entity.golem.AlchemyGolemEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GolemTotem extends GolemCore {

    public GolemTotem(Properties props) {
        super(props);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockRayTraceResult ray = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
        if (ray.getType() != RayTraceResult.Type.BLOCK) {
            return ActionResult.pass(stack);
        } else if (!(world instanceof ServerWorld)) {
            return ActionResult.success(stack);
        } else {
            BlockPos pos = ray.getBlockPos();
            if (!(world.getBlockState(pos).getBlock() instanceof FlowingFluidBlock)) {
                return ActionResult.pass(stack);
            } else if (world.mayInteract(player, pos) && player.mayUseItemAt(pos, ray.getDirection(), stack)) {
                EntityType<? extends AlchemyGolemEntity> type = getType();
                AlchemyGolemEntity entity = (AlchemyGolemEntity) type.spawn((ServerWorld) world, stack, player, pos, SpawnReason.BUCKET, false, false);
                if (entity == null) {
                    return ActionResult.pass(stack);
                } else {
                    stack.shrink(1);
                    entity.setMaterials(player, getMaterials(stack));
                    return ActionResult.consume(stack);
                }
            } else {
                return ActionResult.fail(stack);
            }
        }
    }

    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        PlayerEntity player = context.getPlayer();
        if (!(world instanceof ServerWorld) || player == null) {
            return ActionResultType.SUCCESS;
        } else {
            ItemStack stack = context.getItemInHand();
            BlockPos pos = context.getClickedPos();
            Direction dire = context.getClickedFace();
            BlockState state = world.getBlockState(pos);
            BlockPos pos_1;
            if (state.getCollisionShape(world, pos).isEmpty()) {
                pos_1 = pos;
            } else {
                pos_1 = pos.relative(dire);
            }
            EntityType<? extends AlchemyGolemEntity> type = getType();
            AlchemyGolemEntity entity = (AlchemyGolemEntity) type.spawn((ServerWorld) world, stack, context.getPlayer(), pos_1, SpawnReason.SPAWN_EGG, true, !Objects.equals(pos, pos_1) && dire == Direction.UP);
            if (entity != null) {
                stack.shrink(1);
                entity.setMaterials(player, getMaterials(stack));
            }

            return ActionResultType.CONSUME;
        }
    }

}
