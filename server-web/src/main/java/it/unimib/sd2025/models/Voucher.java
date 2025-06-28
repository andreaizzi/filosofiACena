package it.unimib.sd2025.models;

import java.util.Date;

public class Voucher {

    private String id;
    private String userId;
    private int value;
    private VoucherType type;
    private boolean used;
    private Date creationDate;
    private Date consumeDate;

    public enum VoucherType {
        CINEMA,
        MUSIC,
        CONCERT,
        CULTURE_EVENT,
        BOOK,
        MUSEUM,
        MUSICAL_INSTRUMENT,
        THEATER,
        DANCE;

        public static VoucherType fromString(String type) {
            try {
                return VoucherType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid voucher type: " + type);
            }
        }
    }

    public Voucher() {
        // Default constructor for serialization/deserialization
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public VoucherType getType() {
        return type;
    }

    public void setType(VoucherType type) {
        this.type = type;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getConsumeDate() {
        return consumeDate;
    }

    public void setConsumeDate(Date consumeDate) {
        this.consumeDate = consumeDate;
    }
}
