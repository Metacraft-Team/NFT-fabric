package net.metacraft.mod.client;

import net.metacraft.mod.client.gui.core.CollectionImage;
import net.metacraft.mod.network.data.*;
import net.metacraft.mod.utils.MetaCraftUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public enum DataHandler {
    INSTANCE;

    private List<NftEntity> nftEntities = new ArrayList<>();

    private List<NftCollection> nftCollections = new ArrayList<>();

    private Map<String, ImageInfo> imageMaps = new HashMap<>();

    private Map<String, ImageInfo> imagesColorMap = new ConcurrentHashMap<>();

    private Set<NftCollection> currentCollections = new HashSet<>();

    private Page page;

    private volatile int currentPage = 1;

    private volatile String currentCollection = "";

    private int totalPage;

    private String playerId = "";

    private String requestId;

    private CollectionImage collectionImage;

    public List<NftEntity> getNftEntities() {
        return nftEntities;
    }

    public List<NftCollection> getNftCollections() {
        return nftCollections;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Map<String, ImageInfo> getImageMaps() {
        return imageMaps;
    }

    public void setImageMaps(Map<String, ImageInfo> imageMaps) {
        this.imageMaps = imageMaps;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getCurrentCollection() {
        return currentCollection;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setCurrentCollection(String currentCollection) {
        this.currentCollection = currentCollection;
    }

    public Map<String, ImageInfo> getImagesColorMap() {
        return imagesColorMap;
    }

    public CollectionImage getCollectionImage() {
        return collectionImage;
    }

    public void setCollectionImage(CollectionImage collectionImage) {
        this.collectionImage = collectionImage;
    }

    public Set<NftCollection> getCurrentCollections() {
        return currentCollections;
    }

    public void setCurrentCollections(Set<NftCollection> currentCollections) {
        this.currentCollections = currentCollections;
    }

    public void updateNftInfo(NftJsonObject nftJsonObject) {
        if (nftJsonObject.getNftEntityList() != null) {
            this.nftEntities = nftJsonObject.getNftEntityList();
        }
        List<NftCollection> collect = nftJsonObject.getNftCollections().stream().
                filter(nftCollection -> !MetaCraftUtils.isEmpty(nftCollection.getImageUrl())).collect(Collectors.toList());
        if (nftCollections.isEmpty() || collect != null && nftCollections.size() < collect.size()) {
            this.nftCollections = collect;
        }
        this.page = nftJsonObject.getPage();
        if (page != null) {
            totalPage = page.getTotal() / page.getPs() + 1;
        }
    }
}
