package net.metacraft.mod.network.data;

import com.google.gson.annotations.SerializedName;

public class LoginInfo {

    private String id;

    private String name;

    @SerializedName("fruit")
    private String food;

    @SerializedName("fruit_claimed")
    private boolean claimed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }
}
