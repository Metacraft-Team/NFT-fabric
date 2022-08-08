package net.metacraft.mod.network.data;

public class ImageInfo {
    private byte[] colors;

    private int width;

    private int height;

    public ImageInfo() {
    }

    public ImageInfo(int width, int height, byte[] colors) {
        this.width = width;
        this.height = height;
        this.colors = colors;
    }

    public byte[] getColors() {
        return colors;
    }

    public void setColors(byte[] colors) {
        this.colors = colors;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
