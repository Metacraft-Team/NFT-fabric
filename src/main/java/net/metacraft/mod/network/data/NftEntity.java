package net.metacraft.mod.network.data;

import com.google.gson.annotations.SerializedName;

public class NftEntity {
    @SerializedName("contract_address")
    private String contractAddress;

    @SerializedName("token_id")
    private String tokenId;

    private String name;

    private String symbol;

    @SerializedName("image_url")
    private String imageUrl;

    private String permalink;

    private NftCollection collection;

    private String currentRequestId;

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public NftCollection getCollection() {
        return collection;
    }

    public void setCollection(NftCollection collection) {
        this.collection = collection;
    }

    public String getCurrentRequestId() {
        return currentRequestId;
    }

    public void setCurrentRequestId(String currentRequestId) {
        this.currentRequestId = currentRequestId;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }
}
