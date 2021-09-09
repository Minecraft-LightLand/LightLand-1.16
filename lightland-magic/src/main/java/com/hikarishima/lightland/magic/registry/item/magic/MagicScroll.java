package com.hikarishima.lightland.magic.registry.item.magic;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.capabilities.MagicAbility;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.registry.MagicItemRegistry;
import com.hikarishima.lightland.magic.registry.item.combat.IGlowingTarget;
import com.hikarishima.lightland.magic.spell.internal.Spell;
import com.hikarishima.lightland.proxy.Proxy;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicScroll extends Item implements IGlowingTarget {

    public final ScrollType type;

    public MagicScroll(ScrollType type, Properties props) {
        super(type.apply(props));
        this.type = type;
    }

    public static void initItemStack(Spell<?, ?> spell, ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTagElement("spell");
        tag.putString("spell", spell.getID());
        stack.getOrCreateTag().remove("CustomPotionEffects");
    }

    public static void initEffect(List<EffectInstance> list, ItemStack stack) {
        stack.getOrCreateTag().remove("CustomPotionEffects");
        stack.getOrCreateTagElement("spell").remove("spell");
        PotionUtils.setCustomEffects(stack, list);
    }

    public static void setTarget(TargetType type, ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTagElement("spell");
        tag.putString("target", type.name());
    }

    public static TargetType getTarget(ItemStack stack) {
        String type = stack.getOrCreateTagElement("spell").getString("target");
        try {
            return Enum.valueOf(TargetType.class, type);
        } catch (Exception e) {
            return TargetType.ALL;
        }
    }

    public static void setRadius(double radius, ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTagElement("spell");
        tag.putDouble("radius", radius);
    }

    public static double getRadius(ItemStack stack) {
        return Math.max(5, stack.getOrCreateTagElement("spell").getDouble("radius"));
    }

    @Nullable
    public static Spell<?, ?> getSpell(ItemStack stack) {
        String id = stack.getOrCreateTagElement("spell").getString("spell");
        if (id.length() == 0)
            return null;
        ResourceLocation rl = new ResourceLocation(id);
        return MagicRegistry.SPELL.getValue(rl);
    }

    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        Spell<?, ?> spell = getSpell(stack);
        if (spell != null)
            list.add(new TranslationTextComponent(spell.getDescriptionId()));
        else if (stack.getTagElement("CustomPotionEffects") != null) {
            list.add(Translator.get("tooltip.potion.target." + getTarget(stack).name().toLowerCase()));
            list.add(Translator.get("tooltip.potion.radius", getRadius(stack)));
            PotionUtils.addPotionTooltip(stack, list, 1);
        }
    }

    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    public double getDurabilityForDisplay(ItemStack stack) {
        PlayerEntity pl = Proxy.getPlayer();
        if (pl == null)
            return 1;
        MagicAbility ability = MagicHandler.get(pl).magicAbility;
        int id = -1;
        for (int i = 0; i < ability.getMaxSpellSlot(); i++) {
            if (pl.inventory.getItem(i) == stack) {
                id = i;
                break;
            }
        }
        if (id == -1)
            return 1;
        return ability.getSpellActivation(id);

    }

    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return getDurabilityForDisplay(stack) == 0 ? 0xFFFFFF : 0xFF5555;
    }

    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int selected = player.inventory.selected;
        MagicHandler handler = MagicHandler.get(player);
        if (handler.magicAbility.getMaxSpellSlot() <= selected)
            return ActionResult.fail(stack);
        if (player.inventory.getItem(selected) != stack)
            return ActionResult.fail(stack);
        if (handler.magicAbility.getSpellActivation(selected) != 0)
            return ActionResult.fail(stack);
        List<EffectInstance> list = PotionUtils.getCustomEffects(stack);
        if (list.size() > 0) {
            if (!world.isClientSide()) {
                double radius = getRadius(stack);
                TargetType target = getTarget(stack);
                for (Entity e : world.getEntities(null, new AxisAlignedBB(player.blockPosition()).inflate(radius))) {
                    if (!(e instanceof LivingEntity)) {
                        continue;
                    }
                    if (e.distanceTo(player) > radius) {
                        continue;
                    }
                    if (target == TargetType.ALLIES && !e.isAlliedTo(player) || target == TargetType.ENEMY && e.isAlliedTo(player)) {
                        continue;
                    }
                    for (EffectInstance ins : list) {
                        ((LivingEntity) e).addEffect(ins);
                    }
                }
            }
            return ActionResult.success(stack);
        }
        Spell<?, ?> spell = getSpell(stack);
        if (spell == null || !spell.attempt(Spell.Type.SCROLL, world, player))
            return ActionResult.fail(stack);
        player.getCooldowns().addCooldown(this, 10);
        if (!player.abilities.instabuild) {
            stack.shrink(1);
        }
        return ActionResult.sidedSuccess(stack, world.isClientSide());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public int getDistance(ItemStack stack) {
        Spell<?, ?> spell = getSpell(stack);
        if (spell == null)
            return 0;
        return spell.getDistance(Proxy.getClientPlayer());
    }

    public enum ScrollType {
        CARD(64, MagicItemRegistry.SPELL_CARD),
        PARCHMENT(16, MagicItemRegistry.SPELL_PARCHMENT),
        SCROLL(2, MagicItemRegistry.SPELL_SCROLL);

        public final int stack;
        private final Supplier<MagicScroll> item;

        ScrollType(int stack, Supplier<MagicScroll> item) {
            this.stack = stack;
            this.item = item;
        }

        public Properties apply(Properties props) {
            props.stacksTo(stack);
            return props;
        }

        public MagicScroll toItem() {
            return item.get();
        }
    }

    public enum TargetType {
        ALLIES, ENEMY, ALL
    }

}
