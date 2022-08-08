package net.metacraft.mod.network.data;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class NftCollection {
    @SerializedName("name")
    String name;

    @SerializedName("img_url")
    String imageUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object obj) {
        NftCollection compare = (NftCollection) obj;
        return Objects.equals(name, compare.name) && Objects.equals(imageUrl, compare.imageUrl);
    }

    @Override
    public String toString() {
        return "NftCollection{" +
                "name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
