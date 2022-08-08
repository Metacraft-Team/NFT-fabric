package net.metacraft.mod.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.metacraft.mod.utils.Constants;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class Textures {

    private static final String MOD_PREFIX = Constants.MOD_ID + ":";

    private static final String GUI = MOD_PREFIX + "textures/gui/";

    public static final ITexture SCROLLBAR_HOR = setTexture("scrollbar_hor.png", 8, 7);

    public static final ITexture SCROLLBAR_VER = setTexture("scrollbar_ver.png", 8, 7);

    public static final ITexture FRAME_ON = setTexture("frame_on.png", 65,65);

    public static final ITexture ARROW_PREVIOUS = setTexture("arrow_previous.png", 9,9);

    public static final ITexture BTN_PRE_UP = setTexture("btn_pre_up.png", 10,10);

    public static final ITexture BTN_PRE_DOWN = setTexture("btn_pre_down.png", 10,10);

    public static final ITexture BTN_NEXT_UP = setTexture("btn_next_up.png", 10,10);

    public static final ITexture BTN_NEXT_DOWN = setTexture("btn_next_down.png", 10,10);

    public static final ITexture BTN_ADD = setTexture("btn_add.png", 10,10);

    public static final ITexture BTN_DEC = setTexture("btn_dec.png", 10,10);

    public static final ITexture BTN_ADD_COLLECTION = setTexture("btn_add_collection.png", 10,10);

    public static final ITexture IMAGE_ALL = setTexture("image_all.png", 45,45);

    public static final ITexture IMAGE_FRAME = setTexture("tribe_selection.png", 0, 0);

    public static final ITexture IMAGE_PANEL = setTexture("image_panel.png", 0, 0);

    public static final ITexture COLLECTION_FRAME = setTexture("collection_frame.png", 45, 45);

    public static final ITexture COLLECTION_SELECTED = setTexture("collection_selected.png", 45, 45);

    public static final ITexture NFT_FRAME = setTexture("nft_frame.png", 72, 90);

    public static final ITexture PANEL_BG = setTexture("panel_bg.png", 420, 240);

    public static final ITexture SEARCH_BG = setTexture("search_bg.png", 160, 20);

    public static final ITexture SEARCH_ICON = setTexture("ic_search.png", 8, 8);

    public static final ITexture LEFT_PANEL_BG = setTexture("left_bg.png", 54, 228);

    public static final ITexture COLLECTION_OPERA_PANEL = setTexture("collection_opera_panel.png", 250, 200);

    public static final ITexture BTN_CLOSE = setTexture("btn_close.png", 10, 10);

    private static ITexture setTexture(String fileName, int width, int height) {
        return new Texture(new Identifier(GUI + fileName), width, height);
    }

}
