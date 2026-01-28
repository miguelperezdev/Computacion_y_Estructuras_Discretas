package ui;
import java.util.Scanner;

import exceptions.MatchException;
import model.Controller;

/**
 * Main user interface class for the UEFA Champions League system.
 * Provides a console-based menu system for interacting with the application.
 */
public class Main {

    private static Scanner reader = new Scanner(System.in);
    private static  Controller controller = new Controller();
    private static boolean flag;

    public Main() {
        controller = new Controller();
    }
    /**
     * Entry point for the UEFA Champions League system.
     *
     * @param args command line arguments (not used)
     * @throws MatchException if match-related errors occur during execution
     */
    public static void main(String[] args) throws MatchException {

        flag = false;
        System.out.println("Welcome to the UEFA Champions League system");

        do {
            System.out.println("Choose an option: \n" +
                    "1. Register a team. \n" +
                    "2. Register a new match. \n" +
                    "3. Manage match schedule. \n" +
                    "4. Show match schedule. \n" +
                    "5. Undo last action. \n" +
                    "6. Team Ranking and Classification.\n" +
                    "7. Enter to Search Menu.\n" +
                    "0. Exit the program.");

            int option = reader.nextInt();
            reader.nextLine();

            switch (option) {
                case 1:
                    registerTeam();
                    break;
                case 2:
                    registerMatch();
                    break;
                case 3:
                    manageMatchSchedule();
                    break;
                case 4:
                    scheduleMatch();
                    break;
                case 5:
                    undoLastAction();
                    break;
                case 6:
                    teamRanking();
                    break;
                case 7:
                    searchMenu(flag);
                    break;
                case 0:
                    System.out.println("Program ended. ");
                    flag = true;
                    reader.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. ");
                    break;
            }
        } while (!flag);
    }


    public static void registerTeam() {
        System.out.println("Enter the name of the team:");
        String name = reader.nextLine();
        System.out.println("Enter the country of the team: ");
        String country = reader.nextLine();
        System.out.println("Enter the titles of the team: ");
        int titles = reader.nextInt();
        System.out.println("Enter the UEFA coefficient of the team:");
        int coefficient = reader.nextInt();
        reader.nextLine();
        String result = controller.addTeam(name, country, titles, coefficient);
        System.out.println(result);
    }

    public static void registerMatch() {
        System.out.println("Enter the name of the home team: ");
        String homeTeam = reader.nextLine();
        System.out.println("Enter the name of the away team: ");
        String awayTeam = reader.nextLine();
        System.out.println("Enter the goals of the home team: ");
        int homeGoals = reader.nextInt();
        System.out.println("Enter the goals of the away team: ");
        int awayGoals = reader.nextInt();
        reader.nextLine();
        System.out.println("Enter the date of the match (ej: 13/04/2025): ");
        String date = reader.nextLine();
        String result = controller.addMatch(homeTeam, awayTeam, homeGoals, awayGoals, date);
        System.out.println(result);
    }

    public static void manageMatchSchedule() {
        System.out.println("Enter the ID of the match you want schedule: ");
        String id = reader.nextLine();
        String result = controller.enqueueMatch(id);
        System.out.println(result);
    }
    public static void scheduleMatch(){
        String result = controller.matchSchedule();
        System.out.println(result);
    }

    public static void undoLastAction() {
        String result = controller.undo();
        System.out.println(result);
    }


public static void teamRanking() {
    System.out.println("=================================");
    System.out.println("| Pos  | Team Name    | Points | ");
    System.out.println("=================================");
    System.out.print(controller.teamRanking());
    System.out.println("=================================");
}


public static void searchMenu(boolean flag) throws MatchException {
    flag = false;
    System.out.println("Welcome to the Search Menu");

    do {
        System.out.println("Choose an option: \n" +
                "1. Search a team. \n" +
                "2. Search Match. \n" +
                "0. Exit the program.");

        int option = reader.nextInt();
        reader.nextLine();

        switch(option){
            case 1:
                System.out.println("Enter the team name:\n");
                String name = reader.nextLine();
                String result = controller.publicSearchTeam(name);
                System.out.println(result);
                break;
            case 2:
                System.out.println("Enter the match ID:\n");
                String id = reader.nextLine();
                result = controller.publicSearchMatch(id);
                System.out.println(result);
                break;
            case 0:
                System.out.println("Exit. ");
                flag = true;
                reader.close();
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. ");
                break;
        }
    } while (!flag);

}


}
