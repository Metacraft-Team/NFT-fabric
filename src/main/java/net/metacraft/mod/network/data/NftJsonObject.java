package net.metacraft.mod.network.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NftJsonObject {
    @SerializedName("nft_list")
    private List<NftEntity> nftEntityList;

    @SerializedName("collections")
    private List<NftCollection> nftCollections;

    private Page page;

    public List<NftEntity> getNftEntityList() {
        return nftEntityList;
    }

    public void setNftEntityList(List<NftEntity> nftEntityList) {
        this.nftEntityList = nftEntityList;
    }

    public List<NftCollection> getNftCollections() {
        return nftCollections;
    }

    public void setNftCollections(List<NftCollection> nftCollections) {
        this.nftCollections = nftCollections;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "NftEntityList{" +
                "nftEntityList=" + nftEntityList +
                ", nftCollections=" + nftCollections +
                ", page=" + page +
                '}';
    }
}
