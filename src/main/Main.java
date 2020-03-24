package main;

import java.util.*;
import java.sql.*;
import main.DataClasses.Song;
import main.Database.SongDatabase;

public class Main {

    private static List<Song> songs = new ArrayList<>();
    private static SongDatabase songDatabase = new SongDatabase();
    private static Integer userChoice;

    public static void main(String[] args) {
        Main main = new Main();
        main.printMainData();
    }

    private void printMainData() {
        congratulationMessage();
        setRegistrationData();

        boolean play = true;

        while(play) {

            printMainMenu();
            System.out.print("Your choice: ");
            enterSomeIntegerValue();

            switch(userChoice) {
                case 1:
                    searchSong();
                    printResult();
                    addSongToUserPlaylist();
                    break;
                case 2:
                    printUserPlaylistData();
                    break;
                case 3:
                    break;
                case 4:
                    System.out.println("Good Luck! Your data are saved!");
                    play = false;
                    break;
                default:
                    System.out.println("Incorrect input!");
                    break;
            }
        }
    }

    private void setRegistrationData() {
        Scanner input = new Scanner(System.in);

        System.out.println("Please, register your account: ");
        System.out.print("Enter your username: ");
        String username = input.nextLine();
        System.out.print("Enter your password: ");
        String firstPasswordAttempt = input.nextLine();
        System.out.print("Confirm your password: ");
        String secondPasswordAttempt = input.nextLine();

        if(secondPasswordAttempt.equals(firstPasswordAttempt)) {
            System.out.println("Congratulations, your account has been created successfully!");
            addUserToRegistrationTable(username, firstPasswordAttempt);
        }
        else
            throw new RuntimeException("Sorry, passwords don't match! Please restart the program!");
    }

    private void addUserToRegistrationTable(String username, String password) {
        try {
            songDatabase.createRegistrationTable();
            songDatabase.addUserToRegistrationTable(username, password);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("1. Search and add song.");
        System.out.println("2. View my playlist.");
        System.out.println("3. Change your personal account data.");
        System.out.println("4. Exit.");
        System.out.println();
    }

    private void printSearchSongsMenu() {
        System.out.println();
        System.out.println("1. Search songs by title.");
        System.out.println("2. Search songs by artist name.");
        System.out.println("3. Search songs by album name.");
        System.out.println("4. Search songs by year of release.");
        System.out.println();
    }

    private void searchSong() {
        printSearchSongsMenu();
        System.out.print("Your choice: ");
        enterSomeIntegerValue();

        Scanner input = new Scanner(System.in);

        System.out.print("Please, enter your request: ");
        String userLine = input.nextLine();

        try {
            songs = songDatabase.findSong(userLine, userChoice);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void printResult() {
        if(songs.isEmpty())
            System.out.println("No such song found!");
        else
            printResultData();
    }

    private void printUserPlaylistData() {
        if(songs.isEmpty())
            System.out.println("Your playlist is empty!");
        else
            printResult();
    }

    private void printResultData() {
        Iterator<Song> songIterator = songs.iterator();

        System.out.println();
        System.out.printf("%-5s %-25s %-25s %-25s %-25s", "Id:", "Title:", "Artist:", "Album:", "Year:");
        System.out.println();

        while(songIterator.hasNext())
            songIterator.next().printSong();
    }

    private void addSongToUserPlaylist() {
        System.out.println();
        System.out.print("Please, enter the id of song you want to add: ");
        enterSomeIntegerValue();

        try {
            songs = songDatabase.addSongToPlaylist(userChoice);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        System.out.println();
        System.out.println("The song has been successfully added to your playlist!");
    }

    private void congratulationMessage() {
        System.out.println("Welcome to the awesome playlist! You can easily search and add different songs to your own playlist!");
    }

    private void enterSomeIntegerValue() {
        Scanner input = new Scanner(System.in);

        try {
            userChoice = input.nextInt();
        } catch (InputMismatchException exception) {
            System.out.println("Mismatch! Restart the program!");
        }
    }
}