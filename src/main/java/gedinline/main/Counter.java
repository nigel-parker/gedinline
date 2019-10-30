package gedinline.main;

public class Counter {

    private String type;
    private String forklaring;
    private int antall;

    public Counter(String type, String forklaring) {
        this.type = type;
        this.forklaring = forklaring;
    }

    public String getType() {
        return type;
    }

    public int getAntall() {
        return antall;
    }

    public String getForklaring() {
        return forklaring;
    }

    public void incrementTeller() {
        antall++;
    }

    public void setAntall(int antall) {
        this.antall = antall;
    }
}
