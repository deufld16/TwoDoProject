package at.htlkaindorf.twodoprojectmaxi.enums;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;

/**
 * Enum for Sorting which is used to determine how the entries should be sorted
 */
public enum SortingType {
    DUE_DATE_DOWNWARDS(Proxy.getLanguageContext().getString(R.string.sorting_1)),
    DUE_DATE_UPWARDS(Proxy.getLanguageContext().getString(R.string.sorting_2)),
    PRIORITY_DOWNWARDS(Proxy.getLanguageContext().getString(R.string.sorting_3)),
    PRIORITY_UPWARDS(Proxy.getLanguageContext().getString(R.string.sorting_4));

    private String display_text;

    SortingType(String display_text) {
        this.display_text = display_text;
    }

    public String getDisplay_text() {
        return display_text;
    }

    public void setDisplay_text(String display_text) {
        this.display_text = display_text;
    }
}
