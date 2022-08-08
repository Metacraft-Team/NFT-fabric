package net.metacraft.mod.network.data;

public class Page {
    private int total;

    private int ps;

    private int pn;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPs() {
        return ps;
    }

    public void setPs(int ps) {
        this.ps = ps;
    }

    public int getPn() {
        return pn;
    }

    public void setPn(int pn) {
        this.pn = pn;
    }

    @Override
    public String toString() {
        return "Page{" +
                "total='" + total + '\'' +
                ", ps=" + ps +
                ", pn=" + pn +
                '}';
    }
}
