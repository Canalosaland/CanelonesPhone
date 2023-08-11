package me.pk2.canalosaland.cphone.api.models.info;

public class PlayerInfoModel {
    public final boolean valid;
    public final double balance;
    public final String carrier;
    public final boolean isCarrierOwner;
    public PlayerInfoModel(boolean valid, double balance, String carrier, boolean isCarrierOwner) {
        this.valid = valid;
        this.balance = balance;
        this.carrier = carrier;
        this.isCarrierOwner = isCarrierOwner;
    }
    public PlayerInfoModel() {
        this(false, 0, null, false);
    }
}