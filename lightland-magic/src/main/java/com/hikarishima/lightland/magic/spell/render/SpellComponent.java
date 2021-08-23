package com.hikarishima.lightland.magic.spell.render;

import com.hikarishima.lightland.magic.recipe.MagicRecipeRegistry;
import com.hikarishima.lightland.proxy.Proxy;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.lcy0x1.core.util.SerialClass;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SerialClass
public class SpellComponent {

    public static SpellComponent getFromConfig(String id) {
        return ConfigRecipe.getObject(Proxy.getClientWorld(), MagicRecipeRegistry.SPELL_ENTITY, id);
    }

    @SerialClass.SerialField(generic = Stroke.class)
    public ArrayList<Stroke> strokes = new ArrayList<>();

    @SerialClass.SerialField(generic = Layer.class)
    public ArrayList<Layer> layers = new ArrayList<>();

    public void render(RenderHandle handle) {
        handle.matrix.pushPose();
        for (Stroke stroke : strokes) {
            stroke.render(handle);
        }
        for (Layer layer : layers) {
            layer.render(handle);
        }
        handle.matrix.popPose();
    }

    @SerialClass
    public static class Value {

        @SerialClass.SerialField
        public float value, delta, amplitude, period = 300, dt;

        public float get(float tick) {
            return value + amplitude * (float) Math.sin((tick - dt) * 2 * Math.PI / period) + delta * tick;
        }

    }

    @SerialClass
    public static class Stroke {

        @SerialClass.SerialField
        public int vertex, cycle = 1;

        @SerialClass.SerialField
        public String color;

        @SerialClass.SerialField
        public float width, radius, z;

        public void render(RenderHandle handle) {
            float da = (float) Math.PI * 2 * cycle / vertex;
            float a = 0;
            float w = width / (float) Math.cos(da / 2);
            int col = getColor();
            for (int i = 0; i < vertex; i++) {
                rect(handle.builder, handle.matrix.last(), a, da, radius, w, z, col);
                a += da;
            }

        }

        private int getColor() {
            String str = color;
            if (str.startsWith("0x")) {
                str = str.substring(2);
            }
            return Integer.parseUnsignedInt(str, 16);
        }

        private static void rect(IVertexBuilder builder, MatrixStack.Entry last, float a, float da, float r, float w, float z, int col) {
            vertex(builder, last, a, r - w / 2, z, col);
            vertex(builder, last, a, r + w / 2, z, col);
            vertex(builder, last, a + da, r + w / 2, z, col);
            vertex(builder, last, a + da, r - w / 2, z, col);
        }

        private static void vertex(IVertexBuilder builder, MatrixStack.Entry last, float a, float r, float z, int col) {
            builder.vertex(last.pose(), r * (float) Math.cos(a), r * (float) Math.sin(a), z)
                    .color(
                            col >> 16 & 0xff,
                            col >> 8 & 0xff,
                            col & 0xff,
                            col >> 24 & 0xff)
                    .endVertex();
        }

    }

    @SerialClass
    public static class Layer {

        @SerialClass.SerialField(generic = String.class)
        public ArrayList<String> children = new ArrayList<>();

        private List<SpellComponent> _children;

        @SerialClass.SerialField
        public Value z_offset, scale, radius, rotation, alpha;

        public void render(RenderHandle handle) {
            if (_children == null) {
                _children = children.stream().map(SpellComponent::getFromConfig).collect(Collectors.toList());
            }
            int n = _children.size();
            float z = z_offset == null ? 0 : z_offset.get(handle.tick);
            float s = scale == null ? 1 : scale.get(handle.tick);
            float a = rotation == null ? 0 : rotation.get(handle.tick);
            double r = radius.get(handle.tick);
            float al = handle.alpha;
            if (alpha != null) {
                handle.alpha *= alpha.get(handle.tick); //TODO
            }
            handle.matrix.pushPose();
            handle.matrix.translate(0, 0, z);
            handle.matrix.scale(s, s, s);
            for (SpellComponent child : _children) {
                handle.matrix.pushPose();
                handle.matrix.mulPose(Vector3f.ZP.rotationDegrees(a));
                handle.matrix.translate(r, 0, 0);
                child.render(handle);
                handle.matrix.popPose();
                a += 360f / n;
            }
            handle.matrix.popPose();
            handle.alpha = al;
        }


    }

    public static class RenderHandle {

        public final MatrixStack matrix;
        public final IVertexBuilder builder;
        public final float tick;
        public final int light;

        public float alpha = 1;

        public RenderHandle(MatrixStack matrix, IVertexBuilder builder, float tick, int light) {
            this.matrix = matrix;
            this.builder = builder;
            this.tick = tick;
            this.light = light;
        }
    }

}
