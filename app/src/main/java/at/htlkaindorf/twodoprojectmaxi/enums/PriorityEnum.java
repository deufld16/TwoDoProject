package at.htlkaindorf.twodoprojectmaxi.enums;

/**
 * Enum for the Priority with the Text that should be displayed and the value for the filtering/sorting
 */
public enum PriorityEnum {
    LOW_PRIORITY("!",1),
    MEDIUM_PRIORITY("! !", 5),
    HIGH_PRIORITY("! ! !", 9);

    private int prioirty_value;
    private String prioirty_text;

    PriorityEnum(String prioirty_text, int prioirty_value) {
        this.prioirty_value = prioirty_value;
        this.prioirty_text = prioirty_text;
    }

    public int getPrioirty_value() {
        return prioirty_value;
    }

    public void setPrioirty_value(int prioirty_value) {
        this.prioirty_value = prioirty_value;
    }

    public String getPrioirty_text() {
        return prioirty_text;
    }

    public void setPrioirty_text(String prioirty_text) {
        this.prioirty_text = prioirty_text;
    }
}
