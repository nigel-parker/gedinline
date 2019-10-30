package gedinline.tagtree;

public class SubtreeReference {

    private String id;

    public SubtreeReference(String id) {
        this.id = id.substring(2, id.length() - 2);
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return "<<" + id + ">>";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubtreeReference that = (SubtreeReference) o;
        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
