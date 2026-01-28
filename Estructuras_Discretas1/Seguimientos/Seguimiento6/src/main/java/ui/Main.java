package ui;
import model.Controller;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Controller controller = new Controller();

        System.out.println("Esto es un sistema de Arbol AVL ");
        System.out.println("Indica cuantas operaciones vas a realizar:");
        int n = Integer.parseInt(scanner.nextLine().trim());

        System.out.println("\ningresa tus operaciones una por linea con el este formato:");
        System.out.println("1 x -> Insertar el numero x");
        System.out.println("2 x -> Eliminar el numero x");
        System.out.println("3   -> Imprimir el arbol en preorden con factores de balanceo");

        for (int i = 0; i < n; i++) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Linea vacia intenta de nuevo.");
                i--;
                continue;
            }

            String[] parts = input.split(" ");
            int op;
            Integer value = null;

            try {
                op = Integer.parseInt(parts[0]);
                if (parts.length > 1) value = Integer.parseInt(parts[1]);
                controller.performOperation(op, value);

            } catch (NumberFormatException e) {
                System.out.println("Entrada incorrecta.");
                i--;
            } catch (Exception e) {
                System.out.println("Paso un error: " + e.getMessage());
                i--;
            }
        }

        System.out.println("\nOperaciones realizadas");
        scanner.close();
    }
}




