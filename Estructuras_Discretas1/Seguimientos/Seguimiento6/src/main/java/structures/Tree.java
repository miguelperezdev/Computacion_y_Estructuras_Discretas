package structures;

public class Tree {
    private Node root;

    public void insert(int value) {
        root = insert(root, value);
    }
    public void delete(int value) {
        root = delete(root, value);
    }
    public void printPreOrderWithBalance() {
        printPreOrderWithBalance(root);
        System.out.println();
    }

    private Node insert(Node node, int value) {
        if (node == null) return new Node(value);
        if (value < node.value)
            node.left = insert(node.left, value);
        else if (value > node.value)
            node.right = insert(node.right, value);
        else
            return node;
        updateHeight(node);
        return balance(node);
    }

    private Node delete(Node node, int value) {
        if (node == null) return null;

        if (value < node.value)
            node.left = delete(node.left, value);
        else if (value > node.value)
            node.right = delete(node.right, value);
        else {if (node.left == null || node.right == null) {
                node = (node.left != null) ? node.left : node.right;
            } else {
                Node minLargerNode = getMin(node.right);
                node.value = minLargerNode.value;
                node.right = delete(node.right, minLargerNode.value);
            }
        }

        if (node == null) return null;
        updateHeight(node);
        return balance(node);
    }
    private Node getMin(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }
    private void updateHeight(Node node) {
        int left = (node.left != null) ? node.left.height : 0;
        int right = (node.right != null) ? node.right.height : 0;
        node.height = 1 + Math.max(left, right);
    }

    private Node balance(Node node) {
        int balanceFactor = node.getBalanceFactor();

        if (balanceFactor < -1) {
            if (node.left.getBalanceFactor() > 0)
                node.left = rotateLeft(node.left);
            return rotateRight(node);
        }if (balanceFactor > 1) {
            if (node.right.getBalanceFactor() < 0)
                node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        return node;
    }
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;
        x.right = y;
        y.left = T2;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;
        y.left = x;
        x.right = T2;
        updateHeight(x);
        updateHeight(y);
        return y;
    }
    private void printPreOrderWithBalance(Node node) {
        if (node == null) return;
        System.out.print("(" + node.value + "," + node.getBalanceFactor() + ") ");
        printPreOrderWithBalance(node.left);
        printPreOrderWithBalance(node.right);
    }
}