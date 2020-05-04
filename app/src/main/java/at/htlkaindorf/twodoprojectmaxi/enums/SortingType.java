package at.htlkaindorf.twodoprojectmaxi.enums;

public enum SortingType {
    DUE_DATE_UPWARDS("Due Date ⋀"),
    DUE_DATE_DOWNWARDS("Due Date ⋁"),
    PRIORITY_UPWARDS("Priority ⋀"),
    PRIORITY_DOWNWARDS("Priority ⋁");

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
