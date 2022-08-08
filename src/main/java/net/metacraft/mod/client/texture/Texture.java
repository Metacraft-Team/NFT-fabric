package net.metacraft.mod.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class Texture extends AbstractTexture {
    public final int width;
    public final int height;

    public Texture(Identifier texture, int width, int height) {
        super(texture);
        this.width = width;
        this.height = height;
    }

    public Texture(Identifier texture, int width, int height, int width1) {
        this(texture, width, height);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
