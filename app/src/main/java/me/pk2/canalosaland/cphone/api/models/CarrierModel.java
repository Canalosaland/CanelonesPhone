package me.pk2.canalosaland.cphone.api.models;

public class CarrierModel {
    public final String name;
    public final int subscribers;
    public final double pricePerText;
    public CarrierModel(String name, int subscribers, double pricePerText) {
        this.name = name;
        this.subscribers = subscribers;
        this.pricePerText = pricePerText;
    }
}