package fr.fromage.cheeseshop.model;

public enum Cheese {

    Camembert(7),
    Roquefort(20),
    Brie(8),
    Salers(4);

    public final int price;

    Cheese(int price) {
        this.price = price;
    }
}
