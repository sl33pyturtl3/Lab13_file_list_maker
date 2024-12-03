import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class fileListMaker {
    private static ArrayList<String> list = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static String currentFilename = null;
    private static boolean needsToBeSaved = false;

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMenu();
            String command = SafeInput.getRegExString(scanner, "Choose a command: ", "[AaDdIiPpOoSsMmCcQq]");

            switch (command.toUpperCase()) {
                case "A":
                    addItem();
                    break;
                case "D":
                    deleteItem();
                    break;
                case "I":
                    insertItem();
                    break;
                case "V": // Changed from P (Print) to V (View)
                    printList();
                    break;
                case "O":
                    openList();
                    break;
                case "S":
                    saveList();
                    break;
                case "M":
                    moveItem();
                    break;
                case "C":
                    clearList();
                    break;
                case "Q":
                    if (needsToBeSaved) {
                        System.out.println("You have unsaved changes in the current list.");
                        System.out.print("Would you like to save your changes before quitting? (S to Save, Q to Quit without saving, C to Cancel): ");
                        String quitChoice = SafeInput.getRegExString(scanner, "Choose an option: ", "[SsQqCc]");

                        switch (quitChoice.toUpperCase()) {
                            case "S":
                                saveList();
                                running = false;
                                break;
                            case "Q":
                                System.out.println("Warning: Unsaved changes will be lost.");
                                if (SafeInput.getYNConfirm(scanner, "Are you sure you want to quit without saving?")) {
                                    running = false;
                                }
                                break;
                            case "C":
                                System.out.println("Quit canceled. Returning to the menu.");
                                break;
                        }
                    } else {
                        if (SafeInput.getYNConfirm(scanner, "Are you sure you want to quit?")) {
                            running = false;
                        }
                    }
                    break;

            }
        }
        System.out.println("Goodbye!");
    }
    private static void printMenu() {
        System.out.println("\nMenu:");
        System.out.println("A – Add an item to the list");
        System.out.println("D – Delete an item from the list");
        System.out.println("I – Insert an item into the list");
        System.out.println("V – View the list");
        System.out.println("O – Open a list file from disk");
        System.out.println("S – Save the current list to disk");
        System.out.println("M – Move an item in the list");
        System.out.println("C – Clear the list");
        System.out.println("Q – Quit the program");
    }

    private static void addItem() {
        String item = SafeInput.getNonZeroLenString(scanner, "Enter item to add");
        list.add(item);
        needsToBeSaved = true;
        System.out.println("Item added: " + item);
        printList();
    }

    private static void deleteItem() {
        if (list.isEmpty()) {
            System.out.println("The list is empty. Nothing to delete.");
            return;
        }
        printList();
        int index = SafeInput.getRangedInt(scanner, "Enter the item number to delete", 1, list.size());
        String deletedItem = list.remove(index - 1);
        needsToBeSaved = true;
        System.out.println("Item deleted: " + deletedItem);
        printList();
    }

    private static void insertItem() {
        String item = SafeInput.getNonZeroLenString(scanner, "Enter item to insert");
        int position = SafeInput.getRangedInt(scanner, "Enter position to insert at", 1, list.size() + 1);
        list.add(position - 1, item);
        needsToBeSaved = true;
        System.out.println("Item inserted: " + item);
        printList();
    }

    private static void moveItem() {
        if (list.isEmpty()) {
            System.out.println("The list is empty. Nothing to move.");
            return;
        }
        printList();
        int fromIndex = SafeInput.getRangedInt(scanner, "Enter the item number to move", 1, list.size()) - 1;
        int toIndex = SafeInput.getRangedInt(scanner, "Enter the new position for the item", 1, list.size()) - 1;
        String item = list.remove(fromIndex);
        list.add(toIndex, item);
        needsToBeSaved = true;
        System.out.println("Item moved: " + item);
        printList();
    }

    private static void clearList() {
        if (SafeInput.getYNConfirm(scanner, "Are you sure you want to clear the entire list?")) {
            list.clear();
            needsToBeSaved = true;
            System.out.println("The list has been cleared.");
        }
    }
    private static void openList() {
        if (needsToBeSaved) {
            System.out.println("You have unsaved changes in the current list.");
            if (SafeInput.getYNConfirm(scanner, "Would you like to save the current list before loading a new one?")) {
                saveList();
            } else {
                System.out.println("Warning: Unsaved changes will be lost.");
            }
        }
        System.out.print("Enter filename to open (without .txt): ");
        String filename = scanner.nextLine().trim() + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            list.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
            currentFilename = filename;
            needsToBeSaved = false;
            System.out.println("File loaded: " + filename);
            printList();
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    private static void saveList() {
        if (currentFilename == null) {
            System.out.print("Enter filename to save as (without .txt): ");
            currentFilename = scanner.nextLine().trim() + ".txt";
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(currentFilename))) {
            for (String item : list) {
                writer.println(item);
            }
            needsToBeSaved = false;
            System.out.println("File saved: " + currentFilename);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
    private static void printList() {
        if (list.isEmpty()) {
            System.out.println("The list is empty.");
        } else {
            System.out.println("\nCurrent List:");
            for (int i = 0; i < list.size(); i++) {
                System.out.println((i + 1) + ". " + list.get(i));
            }
        }
    }
}