package model;
import structures.Tree;

public class Controller {
    private Tree tree = new Tree();

    public void performOperation(int op, Integer value) {
        switch (op) {
            case 1 -> tree.insert(value);
            case 2 -> tree.delete(value);
            case 3 -> tree.printPreOrderWithBalance();
        }
    }
}
