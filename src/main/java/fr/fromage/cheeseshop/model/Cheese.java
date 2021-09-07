package fr.fromage.cheeseshop.model;

public enum Cheese {

    Camembert(7),
    Roquefort(20),
    Brie(8),
    Salers(4);

    private final int dollarPrice;

    Cheese(int dollarPrice) {
        this.dollarPrice = dollarPrice;
    }

    public int getDollarPrice() {
        return dollarPrice;
    }
}
