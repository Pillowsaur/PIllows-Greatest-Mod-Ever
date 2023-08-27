package mod.a.util.price;

public class WellPrice {
    private final double swordPrice;
    private final double bowPrice;
    private final double pantsPrice;
    private final double t1Price;
    private final double t2Price;
    private final double t3Price;

    public WellPrice() {
        this(-1, -1, -1, -1, -1, -1);
    }

    public WellPrice(double sword, double bow, double pants, double t1, double t2, double t3) {
        this.swordPrice =sword;
        this.bowPrice = bow;
        this.pantsPrice = pants;
        this.t1Price = t1;
        this.t2Price = t2;
        this.t3Price = t3;
    }

    public double getBowPrice() {
        return bowPrice;
    }

    public double getPantsPrice() {
        return pantsPrice;
    }

    public double getSwordPrice() {
        return swordPrice;
    }

    public double getT1Price() {
        return t1Price;
    }

    public double getT2Price() {
        return t2Price;
    }

    public double getT3Price() {
        return t3Price;
    }
}
