package structure;

import java.util.Objects;

public class Node {
    private String id;
    private double x, y;
    private boolean isWalkable;
    private boolean isHacked = false;

    public Node(String id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.isWalkable = true;
    }

    public String getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isWalkable() {
        return isWalkable;
    }

    public boolean isHacked() {
        return isHacked;
    }

    public void setHacked(boolean hacked) {
        this.isHacked = hacked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
