package gedinline.tagtree;

public class SyntaxElementId {

    private String id;

    public SyntaxElementId(String id) {

        this.id = id.startsWith("<") ? id.substring(1, id.length() - 1):id;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return id;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SyntaxElementId that = (SyntaxElementId) o;
        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
