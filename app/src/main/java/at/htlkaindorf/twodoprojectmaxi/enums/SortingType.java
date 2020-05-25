package at.htlkaindorf.twodoprojectmaxi.enums;

/**
 * Enum for Sorting which is used to determine how the entries should be sorted
 */
public enum SortingType {
    DUE_DATE_DOWNWARDS("Due Date ⋀"),
    DUE_DATE_UPWARDS("Due Date ⋁"),
    PRIORITY_DOWNWARDS("Priority ⋀"),
    PRIORITY_UPWARDS("Priority ⋁");

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
