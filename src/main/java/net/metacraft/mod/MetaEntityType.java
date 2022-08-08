package net.metacraft.mod;

import net.metacraft.mod.painting.MetaPaintingEntity;
import net.metacraft.mod.painting.MetaShowFlatEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;

public final class MetaEntityType {
    private static String ENTITY_TYPE_ID_PAINTING = "entity_meta_painting";

    private static String ENTITY_TYPE_ID_SHOWFLAT = "entity_meta_showflat";

    public static EntityType<MetaPaintingEntity> ENTITY_TYPE_META_PAINTING = null;

    public static EntityType<MetaShowFlatEntity> ENTITY_TYPE_META_SHOWFLAT = null;

    public static void init() {
        ENTITY_TYPE_META_PAINTING = Registry.register(
                Registry.ENTITY_TYPE,
                ENTITY_TYPE_ID_PAINTING,
                EntityType.Builder.create(MetaPaintingEntity::new, SpawnGroup.MISC).setDimensions(0.5F, 0.5F).maxTrackingRange(10).trackingTickInterval(2147483647).build(ENTITY_TYPE_ID_PAINTING));

        ENTITY_TYPE_META_SHOWFLAT = Registry.register(
                Registry.ENTITY_TYPE,
                ENTITY_TYPE_ID_SHOWFLAT,
                EntityType.Builder.create(MetaShowFlatEntity::new, SpawnGroup.MISC).setDimensions(0.5F, 0.5F).maxTrackingRange(10).trackingTickInterval(2147483647).build(ENTITY_TYPE_ID_SHOWFLAT));
    }
}
