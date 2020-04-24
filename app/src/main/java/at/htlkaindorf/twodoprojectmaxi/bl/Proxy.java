package at.htlkaindorf.twodoprojectmaxi.bl;

public class Proxy {
    private static CategoryListModel clm;

    public static CategoryListModel getClm() {
        return clm;
    }

    public static void setClm(CategoryListModel clm) {
        Proxy.clm = clm;
    }
}
