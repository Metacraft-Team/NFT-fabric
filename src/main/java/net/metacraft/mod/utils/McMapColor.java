package net.metacraft.mod.utils;

public class McMapColor {
    public static final McMapColor[] COLORS = new McMapColor[64];
    public static final McMapColor CLEAR = new McMapColor(0, 0);
    public static final McMapColor PALE_GREEN = new McMapColor(1, 8368696);
    public static final McMapColor PALE_YELLOW = new McMapColor(2, 16247203);
    public static final McMapColor WHITE_GRAY = new McMapColor(3, 13092807);
    public static final McMapColor BRIGHT_RED = new McMapColor(4, 16711680);
    public static final McMapColor PALE_PURPLE = new McMapColor(5, 10526975);
    public static final McMapColor IRON_GRAY = new McMapColor(6, 10987431);
    public static final McMapColor DARK_GREEN = new McMapColor(7, 31744);
    public static final McMapColor WHITE = new McMapColor(8, 16777215);
    public static final McMapColor LIGHT_BLUE_GRAY = new McMapColor(9, 10791096);
    public static final McMapColor DIRT_BROWN = new McMapColor(10, 9923917);
    public static final McMapColor STONE_GRAY = new McMapColor(11, 7368816);
    public static final McMapColor WATER_BLUE = new McMapColor(12, 4210943);
    public static final McMapColor OAK_TAN = new McMapColor(13, 9402184);
    public static final McMapColor OFF_WHITE = new McMapColor(14, 16776437);
    public static final McMapColor ORANGE = new McMapColor(15, 14188339);
    public static final McMapColor MAGENTA = new McMapColor(16, 11685080);
    public static final McMapColor LIGHT_BLUE = new McMapColor(17, 6724056);
    public static final McMapColor YELLOW = new McMapColor(18, 15066419);
    public static final McMapColor LIME = new McMapColor(19, 8375321);
    public static final McMapColor PINK = new McMapColor(20, 15892389);
    public static final McMapColor GRAY = new McMapColor(21, 5000268);
    public static final McMapColor LIGHT_GRAY = new McMapColor(22, 10066329);
    public static final McMapColor CYAN = new McMapColor(23, 5013401);
    public static final McMapColor PURPLE = new McMapColor(24, 8339378);
    public static final McMapColor BLUE = new McMapColor(25, 3361970);
    public static final McMapColor BROWN = new McMapColor(26, 6704179);
    public static final McMapColor GREEN = new McMapColor(27, 6717235);
    public static final McMapColor RED = new McMapColor(28, 10040115);
    public static final McMapColor BLACK = new McMapColor(29, 1644825);
    public static final McMapColor GOLD = new McMapColor(30, 16445005);
    public static final McMapColor DIAMOND_BLUE = new McMapColor(31, 6085589);
    public static final McMapColor LAPIS_BLUE = new McMapColor(32, 4882687);
    public static final McMapColor EMERALD_GREEN = new McMapColor(33, 55610);
    public static final McMapColor SPRUCE_BROWN = new McMapColor(34, 8476209);
    public static final McMapColor DARK_RED = new McMapColor(35, 7340544);
    public static final McMapColor TERRACOTTA_WHITE = new McMapColor(36, 13742497);
    public static final McMapColor TERRACOTTA_ORANGE = new McMapColor(37, 10441252);
    public static final McMapColor TERRACOTTA_MAGENTA = new McMapColor(38, 9787244);
    public static final McMapColor TERRACOTTA_LIGHT_BLUE = new McMapColor(39, 7367818);
    public static final McMapColor TERRACOTTA_YELLOW = new McMapColor(40, 12223780);
    public static final McMapColor TERRACOTTA_LIME = new McMapColor(41, 6780213);
    public static final McMapColor TERRACOTTA_PINK = new McMapColor(42, 10505550);
    public static final McMapColor TERRACOTTA_GRAY = new McMapColor(43, 3746083);
    public static final McMapColor TERRACOTTA_LIGHT_GRAY = new McMapColor(44, 8874850);
    public static final McMapColor TERRACOTTA_CYAN = new McMapColor(45, 5725276);
    public static final McMapColor TERRACOTTA_PURPLE = new McMapColor(46, 8014168);
    public static final McMapColor TERRACOTTA_BLUE = new McMapColor(47, 4996700);
    public static final McMapColor TERRACOTTA_BROWN = new McMapColor(48, 4993571);
    public static final McMapColor TERRACOTTA_GREEN = new McMapColor(49, 5001770);
    public static final McMapColor TERRACOTTA_RED = new McMapColor(50, 9321518);
    public static final McMapColor TERRACOTTA_BLACK = new McMapColor(51, 2430480);
    public static final McMapColor DULL_RED = new McMapColor(52, 12398641);
    public static final McMapColor DULL_PINK = new McMapColor(53, 9715553);
    public static final McMapColor DARK_CRIMSON = new McMapColor(54, 6035741);
    public static final McMapColor TEAL = new McMapColor(55, 1474182);
    public static final McMapColor DARK_AQUA = new McMapColor(56, 3837580);
    public static final McMapColor DARK_DULL_PINK = new McMapColor(57, 5647422);
    public static final McMapColor BRIGHT_TEAL = new McMapColor(58, 1356933);
    public static final McMapColor DEEPSLATE_GRAY = new McMapColor(59, 6579300);
    public static final McMapColor RAW_IRON_PINK = new McMapColor(60, 14200723);
    public static final McMapColor LICHEN_GREEN = new McMapColor(61, 8365974);
    public final int color;
    public final int id;

    private McMapColor(int id, int color) {
        if (id < 0 || id > 63) {
            throw new IndexOutOfBoundsException("Map colour ID must be between 0 and 63 (inclusive)");
        }
        this.id = id;
        this.color = color;
        COLORS[id] = this;
    }

    public int getRenderColor(int shade) {
        int i = 220;
        if (shade == 3) {
            i = 135;
        }
        if (shade == 2) {
            i = 255;
        }
        if (shade == 1) {
            i = 220;
        }
        if (shade == 0) {
            i = 180;
        }
        return (-16777216) | ((((this.color & 255) * i) / 255) << 16) | (((((this.color >> 8) & 255) * i) / 255) << 8) | ((((this.color >> 16) & 255) * i) / 255);
    }
}