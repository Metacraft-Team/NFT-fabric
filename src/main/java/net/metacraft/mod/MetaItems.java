package net.metacraft.mod;

import net.metacraft.mod.painting.MetaDecorationItem;
import net.metacraft.mod.painting.MetaShowFlatItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class MetaItems {
    public static Item ITEM_META_PAINTING = null;

    public static Item ITEM_META_SHOWFLAT = null;

    public static void init() {
        ITEM_META_PAINTING = Registry.register(Registry.ITEM, new Identifier("metapainting", "meta_painting"), new MetaDecorationItem(MetaEntityType.ENTITY_TYPE_META_PAINTING, (new Item.Settings()).maxCount(1).group(ItemGroup.DECORATIONS)));
        ITEM_META_SHOWFLAT = Registry.register(Registry.ITEM, new Identifier("metapainting", "meta_showflat"), new MetaShowFlatItem(MetaEntityType.ENTITY_TYPE_META_SHOWFLAT, (new Item.Settings()).maxCount(1)));
    }
}
