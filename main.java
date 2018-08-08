import javax.swing.JOptionPane;
import java.io.*;
import java.util.*;

/**
 * Admin.java creates leagues that can be edited and updated by administrators. Admins can have multiple leagues but
 * each league can only have one admin. Fixtures are generated when a new league is created and stored. The fixtures
 * can be recalled and shown. The points are automatically calculated from the scores, and then displayed on a table.
 *
 * @author Daniel Dalton    17219477
 * @author Stephen Cliffe   17237157
 * @author Alan Finnin      17239621
 * @author William Cummins  17234956
 */
public class Admin {
    private static boolean successfulLogin = false;
    private static int currentAdminID = -1;
    private static ArrayList<ArrayList<String>> teams;
    private static ArrayList<ArrayList<Integer>> fixtures;
    private static ArrayList<ArrayList<Integer>> results;
    private static ArrayList<ArrayList<String>> leagues;
    private static int[][] leaderBoard;

    /**
     * The main method first calls the method initialiseArrayLists(), then presents a menu asking whether you would like
     * to log in or create an account. Each of these buttons calls a method to do this, login() and createAccount()
     * respectively. It allows 3 attempts to login. When you login in it call the mainLeagueMenu() method which loads
     * the next menu.
     */
    public static void main(String[] args) throws IOException {
        boolean validLogin = false;
        initialiseArrayLists();
        Object[] options = {"Create Account", "Login"};
        String message = "Would you like to login or create a new account?";
        String msgTitle = "Welcome";
        int selection = JOptionPane.showOptionDialog(null, message, msgTitle, JOptionPane.YES_NO_OPTION, -1, null, options, options[1]);
        if(selection == JOptionPane.YES_OPTION)
            createAccount();    //Is called when "Create Account" is pressed
        else if(selection == JOptionPane.NO_OPTION) {
            for(int i = 2; !(validLogin = login()) && i > 0; --i) { //Allows 3 tries to login
                System.out.println(i + " Attempts Remaining");
            }
            if(validLogin) {
                System.out.println("Successful Login");
            } else {
                System.out.print("You failed to login within 3 tries.");
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
        if(successfulLogin)
            mainLeagueMenu();
    }

    /**
     * The initialiseArrayLists() method creates a new array list for our teams, our fixtures, our results and one for
     * our league data.
     */
    private static void initialiseArrayLists() {
        teams = new ArrayList<ArrayList<String>>();
        teams.clear();
        teams.add(new ArrayList<String>());
        teams.add(new ArrayList<String>());
        teams.add(new ArrayList<String>());

        fixtures = new ArrayList<ArrayList<Integer>>();
        fixtures.clear();
        fixtures.add(new ArrayList<Integer>());
        fixtures.add(new ArrayList<Integer>());
        fixtures.add(new ArrayList<Integer>());

        results = new ArrayList<ArrayList<Integer>>();
        results.clear();
        results.add(new ArrayList<Integer>());
        results.add(new ArrayList<Integer>());
        results.add(new ArrayList<Integer>());

        leagues = new ArrayList<ArrayList<String>>();
        leagues.add(new ArrayList<String>());
        leagues.add(new ArrayList<String>());
        leagues.add(new ArrayList<String>());
        leagues.add(new ArrayList<String>());
    }

    /**
     * The loadFileToArray() method is passed down a filename and an array. It checks if the filename it was given
     * actually exists as a file. If a file does exist, a scanner reads the data from the file and it is inputted into
     * the array.
     */
    private static boolean loadFileToArray(String fileName, ArrayList<ArrayList<String>> array) throws IOException {
        int size = array.size();
        array.clear();
        for(int x = 0; x < size; x++)
            array.add(new ArrayList<String>());
        File file = new File(fileName);
        if(!file.exists())
            file.createNewFile();
        Scanner in = new Scanner(file);
        String[] fileElements;
        if(file.exists()) {
            while(in.hasNext()) {
                fileElements = (in.nextLine()).split(",");
                for(int i = 0; i < array.size(); i++) {
                    (array.get(i)).add(fileElements[i]);
                }
            }
            in.close();
            return true;
        } else
            System.out.println("File not found or is empty!");
        return false;
    }

    /**
     * The createAccount() method prompts the user to input a username and password. It initiates the methods userInputChk(),
     * userDuplicateChk() and passwordLengthChk() to make sure the inputted strings are made up of letters and numbers,
     * are duplicated or if the password is over a set length. The method also writes the account index, the username
     * and the password to the Admins.txt file.
     */
    private static void createAccount() throws IOException {
        String message = "Please enter a username."; //Message displayed in username prompt
        String message1 = "Please enter a password."; //message displayed in password prompty
        String msgTitle = "Create Account"; //title of msgbox
        int minPassLength = 8, accIndex = 1; //Max password length & Index of number of accounts in file

        File adminFile = new File("Admin.txt");
        FileWriter add = new FileWriter(adminFile, true);

        String username = (JOptionPane.showInputDialog(null, message, msgTitle, -1));
        if(username == null)
            System.exit(0);
        if(!userInputChk(username)) {
            String msg1 = "Username: " + username + "\nMust contain only letters and numbers, and be at least 5 characters long.";
            JOptionPane.showMessageDialog(null, msg1, "Doesn't meet criteria", 2);
            createAccount();
        } else if(userDuplicateChk(username)) {
            String msg1 = "Username: " + username + "\nAlready Exists, please choose another username.";
            JOptionPane.showMessageDialog(null, msg1, "Duplicate Username", 2);
            createAccount();
        }
        String password = (JOptionPane.showInputDialog(null, message1, msgTitle, -1));
        if(password == null)
            System.exit(0);
        if(passwordLengthChk(password, minPassLength)) {
            FileReader fr = new FileReader(adminFile);
            LineNumberReader lnr = new LineNumberReader(fr);
            while(lnr.readLine() != null) {
                accIndex++;
            }
            add.write(accIndex + "," + username + "," + password + "\r\n");
            lnr.close();
            add.close();
            main(null);
        } else {
            JOptionPane.showMessageDialog(null, "The password must be at least 8 characters long!", "Error", 0);
            createAccount();
        }
    }

    /**
     * The userInputChk() is passed down a string containing the username, it checks if this string contains only numbers
     * or letters and nothing else. It returns a boolean value depending on whether the string matches the pattern.
     */
    private static boolean userInputChk(String user) {
        String pattern = "[a-zA-Z0-9]+";
        boolean userValid = false;
        if(user.length() > 5 && (user.matches(pattern))) {
            userValid = true;
        }
        return userValid;
    }

    /**
     * The userDuplicateChk() method scans the Admin.txt file for a username identical to the one that was passed down
     * to it. If there is a duplicate, a true value  is returned, if not a false value is returned.
     */
    private static boolean userDuplicateChk(String user) throws IOException {
        File file = new File("Admin.txt");
        boolean userExists = false;
        Scanner scanner = new Scanner(file);
        while(scanner.hasNextLine() && !userExists) {
            String line = scanner.nextLine();
            System.out.println(line);
            int firstIndexOf = line.indexOf(',') + 1;
            int lastIndexOf = line.lastIndexOf(',');
            String subUser = "";
            if(lastIndexOf > firstIndexOf)
                subUser = line.substring(firstIndexOf, lastIndexOf);
            if(firstIndexOf < 0 || lastIndexOf < 0) {
                System.out.println("Doesn't Exist");
                userExists = false;
            } else if(user.equals(subUser))
                userExists = true;
            System.out.println(subUser);
        }
        scanner.close();
        return userExists;
    }

    /**
     * The passwordLengthChk() method input the password and a minimum length. If the password length is not over this
     * minimum length it returns a false value. If it is above the min length then the value returned is true.
     */
    private static boolean passwordLengthChk(String pass, int minLength) {
        boolean passValid = false;
        if(pass.length() >= minLength)
            passValid = true;
        return passValid;
    }

    /**
     * The login() method generates a menu to input your username then another to input your password. It also checks
     * if there is an Admin.txt file existing, if not it returns an error message, there is also an error message if
     * there are no admins registered. It then checks the inputted details against the ones on the array list, which
     * read them from the file itself.
     */
    private static boolean login() throws IOException {
        currentAdminID = -1;
        String message = "Please enter your username."; //Message displayed in username prompt
        String message1 = "Please enter your password."; //message displayed in password prompty
        String msgTitle = "Login"; //title of msgbox
        String username, password;

        username = (JOptionPane.showInputDialog(null, message, msgTitle, -1));
        if(username == null)
            main(null);
        password = (JOptionPane.showInputDialog(null, "User: " + username + "\n" + message1, msgTitle, -1));
        if(password == null)
            main(null);

        ArrayList<ArrayList<String>> admins = new ArrayList<ArrayList<String>>();
        String filename = "Admin.txt";
        String errorMessage1 = filename + " not found\nPlease create an account";
        String errorMessage2 = "No administrators registered";
        File adminFile = new File(filename);
        String fileElements[];
        int recordCount;
        boolean found = false;

        if(!(adminFile.exists()))
            System.out.println(errorMessage1);
        else if(adminFile.length() == 0)
            System.out.println(errorMessage2);
        else {
            Scanner in = new Scanner(adminFile);
            admins.add(new ArrayList<String>());
            admins.add(new ArrayList<String>());
            admins.add(new ArrayList<String>());
            while(in.hasNext()) {
                fileElements = (in.nextLine()).split(",");
                for(int i = 0; i < fileElements.length; i++)
                    (admins.get(i)).add(fileElements[i]);
            }
            in.close();
            for(recordCount = 0; recordCount < admins.get(0).size() && !found; recordCount++) {
                if((admins.get(1).get(recordCount)).equals(username) && (admins.get(2).get(recordCount)).equals(password))
                    found = true;
            }
            if(found) {
                currentAdminID = Integer.parseInt(admins.get(0).get(--recordCount));
                System.out.println("Welcome Administrator " + currentAdminID);
                successfulLogin = true;
            } else
                System.out.println("Invalid user details");
        }
        return successfulLogin;
    }

    /**
     * The mainLeagueMenu() method generates a window with a drop down menu. These options are functions that are vital
     * to our program, they include creating a new league, updating a league, viewing fixtures and existing leagues
     * and finally logging out. These options call on other methods in the program.
     */
    private static void mainLeagueMenu() throws IOException {
        String[] options = {"Create A New League", "Update League Results", "View League Table", "View League Fixtures", "Logout"};
        String msgTitle = "Welcome User " + currentAdminID, message = "Please choose an option to proceed...";
        String selection = (String) JOptionPane.showInputDialog(null, message, msgTitle, -1, null, options, options[0]);

        if(selection == null)
            System.exit(0);

        switch(selection) {
            case "Create A New League":
                createNewLeague();
                break;
            case "Update League Results":
                String leagueName = leagueSelection(selection);
                editLeaguesDriver(leagueName);
                break;
            case "View League Table":
                leagueName = leagueSelection(selection);
                printLeague(leagueName);
                break;
            case "View League Fixtures":
                leagueName = leagueSelection(selection);
                int numberOfTeams = getNumberOfTeams(leagueName);
                generateFixtures(numberOfTeams, leagueName, 0, false);//optionNumber = 3; //
                break;
            case "Logout":
                main(null); //optionNumber = 4; //
                break;
            default:
                System.out.println("Please select an option");
        }
    }

    /**
     * The createNewLeague() method creates a new league with a user defined number of teams and names for the league
     * and teams. It also creates a file for fixtures, teams, results and if there isn't already one, a file for all
     * the league data. This method runs the generateFixtures() method to fill in the fixtures file when its finished.
     * This method also fills in the Leagues.txt file with the name of the League, the number of teams and the admin
     * that can access this file.
     */
    private static void createNewLeague() throws IOException {
        loadFileToArray("Leagues.txt", leagues);
        String leagueName;
        String teamNumString;
        String teamName;
        String pattern = "[0-9]+";
        int teamNum;
        boolean dupeCheck;

        File inputFile4 = new File("Leagues.txt");
        if(!inputFile4.exists())
            inputFile4.createNewFile();

        String query1 = "Name of League:";
        String query2 = "No. of Teams:  (<2, >99)";
        String query3 = "Name of Team:";

        leagueName = JOptionPane.showInputDialog(query1);
        dupeCheck = leagueDuplicateChk(leagueName);
        if(leagueName == null)
            mainLeagueMenu();
        if(dupeCheck == true) {
            String msg1 = "League Name: " + leagueName + " already exists, please choose another league name.";
            JOptionPane.showMessageDialog(null, msg1, "Duplicate League Name", 2);
            createNewLeague();
        }
        teamNumString = JOptionPane.showInputDialog(query2);
        if(teamNumString == null)
            mainLeagueMenu();
        if((teamNumString.matches(pattern)) == false) {
            System.out.println("Not an integer!");
            createNewLeague();
        }
        teamNum = Integer.parseInt(teamNumString);
        if(!((teamNum > 1) && (teamNum < 100))) {
            System.out.println("Number out of range!");
            createNewLeague();
        }
        File inputFile1 = new File("Teams_" + leagueName + ".txt");
        File inputFile2 = new File("Fixtures_" + leagueName + ".txt");
        File inputFile3 = new File("Results_" + leagueName + ".txt");
        inputFile2.createNewFile();
        inputFile3.createNewFile();

        FileReader fr = new FileReader("Leagues.txt");
        LineNumberReader lnr = new LineNumberReader(fr);
        int leaguesLinePos = 0;
        while(lnr.readLine() != null) {
            leaguesLinePos++;
        }
        lnr.close();
        FileWriter add4 = new FileWriter(inputFile4, true);
        add4.write((leaguesLinePos + 1) + "," + currentAdminID + "," + leagueName + "," + teamNum + "\r\n");
        add4.close();

        (leagues.get(0)).add("" + (leaguesLinePos + 1));
        (leagues.get(1)).add("" + currentAdminID);
        (leagues.get(2)).add(leagueName);
        (leagues.get(3)).add("" + teamNum);

        FileWriter add1 = new FileWriter(inputFile1, true);
        int index = 0;
        String prevName = "";
        for(int i = 0; i < teamNum; i++) {
            teamName = JOptionPane.showInputDialog(prevName + query3);
            if(teamName == null || teamName.length() < 1)
                teamName = "Team" + (i + 1);
            prevName = "Last team entry: " + teamName + "\n";
            add1.write((i + 1) + "," + teamName + "\r\n");
            index = i;
        }
        if((teamNum % 2) == 1)
            add1.write((index + 1) + ",Bye Team" + "\r\n");
        add1.close();
        loadFileToArray("Leagues.txt", leagues);
        String message = "Generating fixtures complete, would you like to display them?";
        int confirm = JOptionPane.YES_NO_OPTION;
        confirm = JOptionPane.showConfirmDialog(null, message, "Display?", confirm);
        generateFixtures(teamNum, leagueName, confirm, true);
    }

    /**
     * The leagueDuplicateChk() method sets up a temporary array list contain the names of all the leagues and checks
     * if the given name already exists in the leagues array list and therefore the Leagues.txt file.
     */
    private static boolean leagueDuplicateChk(String leagueName) {
        boolean leagueExists;
        ArrayList<String> teamNames = leagues.get(2);
        leagueExists = teamNames.contains(leagueName);
        return leagueExists;
    }

    /**
     * The generateFixtures() method inputs the number of teams and uses it to generate a number of rounds in which each
     * team faces off against each other. The method also has a view fixtures option, so that it can be used to either
     * generate new fixtures or view current fixtures.
     */
    private static void generateFixtures(int teamNum, String leagueName, int display, boolean fileWrite) throws IOException {
        int totalRounds, matchesPerRound, roundNum, matchNum, homeTeam, awayTeam, even, odd;
        boolean extraTeam = false;
        boolean readFile = readFilesIntoArrayLists(leagueName);
        int index = 0;
        String[][] fixtures;
        String[][] revFixtures;
        String[] fixtureElements;
        String fixText;

        if(teamNum % 2 == 1) {
            teamNum++;
            extraTeam = true;
        }
        totalRounds = teamNum - 1;
        matchesPerRound = teamNum / 2;
        fixtures = new String[totalRounds][matchesPerRound];
        for(roundNum = 0; roundNum < totalRounds; roundNum++) {
            for(matchNum = 0; matchNum < matchesPerRound; matchNum++) {
                homeTeam = (roundNum + matchNum) % (teamNum - 1);
                awayTeam = (teamNum - 1 - matchNum + roundNum) % (teamNum - 1);
                if(matchNum == 0)
                    awayTeam = teamNum - 1;
                fixtures[roundNum][matchNum] = (homeTeam + 1) + "," + (awayTeam + 1);
            }
        }
        revFixtures = new String[totalRounds][matchesPerRound];
        even = 0;
        odd = teamNum / 2;
        for(int i = 0; i < fixtures.length; i++) {
            if(i % 2 == 0) {
                revFixtures[i] = fixtures[even++];
            } else
                revFixtures[i] = fixtures[odd++];
        }
        fixtures = revFixtures;
        for(roundNum = 0; roundNum < fixtures.length; roundNum++) {
            if(roundNum % 2 == 1) {
                fixText = fixtures[roundNum][0];
                fixtureElements = fixText.split(",");
                fixtures[roundNum][0] = fixtureElements[1] + "," + fixtureElements[0];
            }
        }
        if(fileWrite) {
            File inputFile2 = new File("Fixtures_" + leagueName + ".txt");
            FileWriter add = new FileWriter(inputFile2, true);
            for(roundNum = 0; roundNum < totalRounds; roundNum++) {
                for(matchNum = 0; matchNum < matchesPerRound; matchNum++) {
                    add.write((index + 1) + "," + fixtures[roundNum][matchNum] + "\r\n");
                    index++;
                }
            }
            add.close();
        }
        if(display != 0) {
        } else {
            for(roundNum = 0; roundNum < totalRounds; roundNum++) {
                System.out.println("Round " + (roundNum + 1) + ":\t\t");
                for(matchNum = 0; matchNum < matchesPerRound; matchNum++) {
                    String[] temp = (fixtures[roundNum][matchNum]).split(",");
                    int pos1 = Integer.parseInt(temp[0]), pos2 = Integer.parseInt(temp[1]);
                    System.out.println("\tMatch " + (matchNum + 1) + ": " + (teams.get(1)).get(pos1 - 1) + " vs " + (teams.get(1)).get(pos2 - 1) + "\t");
                }
            }
        }
        mainLeagueMenu();
    }

    /**
     * Brings up a JOptionpane dropdown that allows an admin
     * to select from the applicable list of leagues that belong
     * to them. A string is containing the league name is returned.
     */
    private static String leagueSelection(String prevSelection) throws IOException {
        boolean filePopulated = loadFileToArray("Leagues.txt", leagues);
        ArrayList<String> currentAdminsLeagues = new ArrayList<String>();
        ArrayList<String> adminNumberList = new ArrayList<String>();
        String errorMsg1 = "Leagues.txt not found or no leagues created";
        String leagueSelected = "";

        if(!filePopulated) {
            System.out.print(errorMsg1);
            System.exit(50);
        } else {
            adminNumberList = leagues.get(1);
            for(int i = 0; i < adminNumberList.size(); i++) {
                int x = Integer.parseInt(adminNumberList.get(i));
                if(x == currentAdminID) {
                    currentAdminsLeagues.add((leagues.get(2)).get(i));
                }
            }
            adminNumberList.clear();
            if(currentAdminsLeagues.size() >= 1) {
                Object[] options = currentAdminsLeagues.toArray();
                String msgTitle = "League Selection", message = "Please select a league";
                leagueSelected = (String) JOptionPane.showInputDialog(null, message, msgTitle, -1, null, options, options[0]);
                if(leagueSelected == "") {
                    System.out.println("No league selected");
                    leagueSelection("do any operations");
                } else if(leagueSelected == null) {
                    mainLeagueMenu();
                }
            } else {
                JOptionPane.showMessageDialog(null, "You have not created any leagues yet! " + "\nYou must have at least one league\nbefore you can: " + prevSelection, "Error", 2);
                mainLeagueMenu();
            }
        }
        return leagueSelected;
    }

    /**
     * The getNumberOfTeams() method accesses the leagues array list. It uses the league name it was given to find the
     * number of teams in that league. It returns the number.
     */
    private static int getNumberOfTeams(String leagueName) {
        ArrayList<String> temp = leagues.get(2);
        String numberAsString = (leagues.get(3)).get(temp.indexOf(leagueName));
        int numberOfTeams = Integer.parseInt(numberAsString);
        return numberOfTeams;
    }

    /**
     * The changingLeaguePoints() method is used to manually input the results into a text file. It cycles week by week
     * giving the option to either move onto another week. It writes the results directly to the results file.
     */
    private static void editLeagueScores(String leagueName) throws IOException {
        String filename = "Results_" + leagueName + ".txt";
        File file = new File(filename); //Change this to a variable file name, so the admin can select which to edit
        FileWriter add = new FileWriter(file, true); //Problem... creates random files
        String errorMsg = ("file does not exist");
        if(!(file.exists())) {
            System.out.print(errorMsg);
            System.exit(50);
        }
        String scoreFirstTeamRaw = JOptionPane.showInputDialog(null, "Input score");
        if(scoreFirstTeamRaw == null)
            mainLeagueMenu();
        String scoreSecondTeamRaw = JOptionPane.showInputDialog(null, "Input score");
        if(scoreSecondTeamRaw == null)
            mainLeagueMenu();
        int scoreFirstTeam = converter(scoreFirstTeamRaw);
        int scoreSecondTeam = converter(scoreSecondTeamRaw);
        int index = 1;
        FileReader fr = new FileReader(filename);
        LineNumberReader lnr = new LineNumberReader(fr);
        while(lnr.readLine() != null) {
            index++;
        }
        add.write(index + "," + scoreFirstTeam + "," + scoreSecondTeam + "\r\n");
        add.close();
        lnr.close();
    }

    /**
     * The editLeagueScoresAutofill() method is used to automatically input the results into a text file. It cycles week by week
     * giving the option to either move onto another week. It writes the results directly to the results file.
     */
    private static void editLeagueScoresAutofill(String leagueName) throws IOException {
        String filename = "Results_" + leagueName + ".txt";
        File file = new File(filename);
        FileWriter add = new FileWriter(file, true);
        int teams = getNumberOfTeams(leagueName);
        if(!(file.exists())) {
            System.out.println("file does not exist");
            System.exit(0);
        }
        int scoreFirstTeam = (int) (Math.random() * 10);
        int scoreSecondTeam = (int) (Math.random() * 10);
        int index = 1;
        FileReader fr = new FileReader(filename);
        LineNumberReader lnr = new LineNumberReader(fr);
        while(lnr.readLine() != null) {
            index++;
        }
        add.write(index + "," + scoreFirstTeam + "," + scoreSecondTeam + "\r\n");
        add.close();
        lnr.close();
    }

    /**
     * The editLeaguesDriver calls either editLeagueScores or editLeagueScoresAutofill based on the option selected
     * in the JOptionPane. Calls mainLeagueMenu when method is complete.
     */
    private static void editLeaguesDriver(String leagueName) throws IOException {
        readFilesIntoArrayLists(leagueName);
        loadFileToArray("Leagues.txt", leagues);
        boolean randomDone = true;
        int numberOfWeeklyMatches = (getNumberOfTeams(leagueName) / 2);
        int numberOfWeeks = ((getNumberOfTeams(leagueName) - 1));
        int numberOfMatchesOverall = numberOfWeeklyMatches * numberOfWeeks;
        int numberOfResults = (results.get(0)).size();
        Object[] options = {"Manually Add", "Randomly Add"};
        String message = "Which Method Would You Like \n To Use For Entering Results?";
        int n = JOptionPane.showOptionDialog(null, message, "Input Method", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if(n == JOptionPane.YES_OPTION) { //Manual add
            boolean through = true;
            while(through) {
                for(int i = 0; i < numberOfWeeklyMatches; i++) {
                    editLeagueScores(leagueName);
                }
                int x = JOptionPane.showConfirmDialog(null, "Would you like to carry on?");
                if(!(x == JOptionPane.YES_NO_OPTION))
                    through = false;
            }
        }
        if(n == JOptionPane.NO_OPTION) { //auto add
            System.out.println(leagueName + " has been filled with random scores");
            int x = numberOfMatchesOverall - numberOfResults;
            for(int i = 0; i < x; i++) {
                editLeagueScoresAutofill(leagueName);
            }
            randomDone = false;
        }
        if(randomDone) {
            int x = JOptionPane.showConfirmDialog(null, "Would you like the results finished for you? \n Note: It may take a few seconds");
            if(x == JOptionPane.YES_OPTION) {
                int y = numberOfMatchesOverall - numberOfResults;
                for(int i = 0; i < y; i++) {
                    //numberOfResults();
                    editLeagueScoresAutofill(leagueName);
                }
            }
            if(x == JOptionPane.NO_OPTION) {
                mainLeagueMenu();
            }
        } else
            mainLeagueMenu();
    }

    /**
     * When taking in an input it checks its formatting and whether the input is of int format. if it fails any of these
     * conditions it returns 0, otherwise it returns the input as an int.
     */
    private static int converter(String input) {
        if(input.replaceAll(" ", "").length() == 0)
            input = "0";
        int x = 0;
        try {
            x = Integer.parseInt(input);
        } catch(NumberFormatException e) {
            x = 0;
        }
        if(x > 20 || x < 0) {
            System.out.println("Out Of Bounds");
            x = 0;
        }
        return x;
    }

    /**
     * This method uses the readFilesIntoArrayLists() method, this makes sure each file exists. Then initialises the
     * methods used to create, process, order and display the results. When finished it goes back to the main menu.
     */
    private static void printLeague(String leagueName) throws IOException {
        boolean readFile;
        readFile = readFilesIntoArrayLists(leagueName);
        if(!readFile)
            System.out.println("One or more files do not exist.");
        else {
            createEmptyLeaderBoard();
            processResults();
            orderLeaderBoard();
            displayLeaderboard();
            mainLeagueMenu();
        }
    }

    /**
     * The readFilesIntoArrayLists() method checks if a teams, fixtures and results file exist for the current league.
     * If they do not exists it returns a false value. If true, they load all the file data into the corresponding array
     * list.
     */
    private static boolean readFilesIntoArrayLists(String leagueName) throws IOException {
        initialiseArrayLists();
        boolean fileExists;
        String filename1 = "Teams_" + leagueName + ".txt";
        String filename2 = "Fixtures_" + leagueName + ".txt";
        String filename3 = "Results_" + leagueName + ".txt";

        String fileElements[];
        File inputFile1 = new File(filename1);
        File inputFile2 = new File(filename2);
        File inputFile3 = new File(filename3);

        if(inputFile1.exists() && inputFile2.exists() && inputFile3.exists()) {
            fileExists = true;
            Scanner in;
            in = new Scanner(inputFile1);
            while(in.hasNext()) {
                fileElements = (in.nextLine()).split(",");
                teams.get(0).add(fileElements[0]);
                teams.get(1).add(fileElements[1]);
            }
            in.close();
            in = new Scanner(inputFile2);
            while(in.hasNext()) {
                fileElements = (in.nextLine()).split(",");
                fixtures.get(0).add(Integer.parseInt(fileElements[0]));
                fixtures.get(1).add(Integer.parseInt(fileElements[1]));
                fixtures.get(2).add(Integer.parseInt(fileElements[2]));
            }
            in.close();
            in = new Scanner(inputFile3);
            while(in.hasNext()) {
                fileElements = (in.nextLine()).split(",");
                results.get(0).add(Integer.parseInt(fileElements[0]));
                results.get(1).add(Integer.parseInt(fileElements[1]));
                results.get(2).add(Integer.parseInt(fileElements[2]));
            }
            in.close();
        } else
            fileExists = false;
        return fileExists;
    }

    /**
     * The createEmptyLeaderboard() method creates a two dimensional array and then places the team numbers into column
     * 0 of the leader board.
     */
    private static void createEmptyLeaderBoard() {
        // find out the number of teams/players which will determine
        // the number of rows
        int rows = teams.get(0).size();
        int columns = 14;
        leaderBoard = new int[rows][columns];
        // place team numbers in column 0 of leader board
        for(int i = 0; i < leaderBoard.length; i++)
            leaderBoard[i][0] = Integer.parseInt(teams.get(0).get(i));
    }

    /**
     * the processResultsMethod() generates the results and uses both the recordFixtureResultForHomeTeam() and the
     * equivalent away tema method to record the results.
     */
    private static void processResults() {
        int fixtureNumber, homeTeamScore, awayTeamScore, homeTeamNumber, awayTeamNumber;
        int position;
        for(int i = 0; i < results.get(0).size(); i++) {
            fixtureNumber = results.get(0).get(i);
            homeTeamScore = results.get(1).get(i);
            awayTeamScore = results.get(2).get(i);
            position = fixtures.get(0).indexOf(fixtureNumber);
            homeTeamNumber = fixtures.get(1).get(position);
            awayTeamNumber = fixtures.get(2).get(position);
            if(homeTeamScore == awayTeamScore) {
                recordFixtureResultForHomeTeam(homeTeamNumber, 0, 1, 0, homeTeamScore, awayTeamScore, 1);
                recordFixtureResultForAwayTeam(awayTeamNumber, 0, 1, 0, homeTeamScore, awayTeamScore, 1);
            } else if(homeTeamScore > awayTeamScore) {
                recordFixtureResultForHomeTeam(homeTeamNumber, 1, 0, 0, homeTeamScore, awayTeamScore, 3);
                recordFixtureResultForAwayTeam(awayTeamNumber, 0, 0, 1, homeTeamScore, awayTeamScore, 0);
            } else {
                recordFixtureResultForHomeTeam(homeTeamNumber, 0, 0, 1, homeTeamScore, awayTeamScore, 0);
                recordFixtureResultForAwayTeam(awayTeamNumber, 1, 0, 0, homeTeamScore, awayTeamScore, 3);
            }
        }
    }

    /**
     * This method record the fixture results for the home team, it is given all the results and assigns them to their
     * correct spaces inside the leader board two dimensional array.
     */
    private static void recordFixtureResultForHomeTeam(int hTN, int w, int d, int l, int hTS, int aTS, int p) {
        leaderBoard[hTN - 1][1]++;                    // gamesPlayed
        leaderBoard[hTN - 1][2] += w;                // homeWin
        leaderBoard[hTN - 1][3] += d;                // homeDraw
        leaderBoard[hTN - 1][4] += l;                // homeLoss
        leaderBoard[hTN - 1][5] += hTS;                // homeTeamScore
        leaderBoard[hTN - 1][6] += aTS;                // awayTeamScore
        leaderBoard[hTN - 1][12] += (hTS - aTS);        // goalDifference
        leaderBoard[hTN - 1][13] += p;                // points
    }

    /**
     * This method record the fixture results for the away team, it is given all the results and assigns them to their
     * correct spaces inside the leader board two dimensional array.
     */
    private static void recordFixtureResultForAwayTeam(int aTN, int w, int d, int l, int hTS, int aTS, int p) {
        leaderBoard[aTN - 1][1]++;                    // gamesPlayed
        leaderBoard[aTN - 1][7] += w;                // awayWin
        leaderBoard[aTN - 1][8] += d;                // awayDraw
        leaderBoard[aTN - 1][9] += l;                // awayLoss
        leaderBoard[aTN - 1][10] += aTS;                // awayTeamScore
        leaderBoard[aTN - 1][11] += hTS;                // homeTeamScore
        leaderBoard[aTN - 1][12] += (aTS - hTS);        // goalDifference
        leaderBoard[aTN - 1][13] += p;                // points
    }

    /**
     * The orderLeaderBoard() method uses a bubble sort to sort the leaderBoard array.
     */
    private static void orderLeaderBoard() {
        int[][] temp = new int[leaderBoard.length][leaderBoard[0].length];
        boolean finished = false;
        while(!finished) {
            finished = true;
            for(int i = 0; i < leaderBoard.length - 1; i++) {
                if(leaderBoard[i][13] < leaderBoard[i + 1][13]) {
                    for(int j = 0; j < leaderBoard[i].length; j++) {
                        temp[i][j] = leaderBoard[i][j];
                        leaderBoard[i][j] = leaderBoard[i + 1][j];
                        leaderBoard[i + 1][j] = temp[i][j];
                    }
                    finished = false;
                }
            }
        }
    }

    /**
     * The displayLeaderboard() method gets the longest team name to establish a format for the team names. It prints a
     * string with all the titles first, then goes through the entire array while printing the results for each team.
     */
    private static void displayLeaderboard() {
        int aTeamNumber;
        String aTeamName, formatStringTeamName;
        String longestTeamName = teams.get(1).get(0);
        int longestTeamNameLength = longestTeamName.length();

        for(int i = 1; i < teams.get(1).size(); i++) {
            longestTeamName = teams.get(1).get(i);
            if(longestTeamNameLength < longestTeamName.length())
                longestTeamNameLength = longestTeamName.length();
        }
        if(longestTeamNameLength < 8)
            longestTeamNameLength = 7;

        formatStringTeamName = "%-" + (longestTeamNameLength + 2) + "s";
        System.out.printf(formatStringTeamName, "Team Name");
        String titles = ("  GP  ¦  HW  ¦  HD  ¦  HL  ¦  GF  ¦  GA  ¦  AW  ¦  AD  ¦  AL  ¦  GF  ¦  GA  ¦  GD  ¦  TP  ¦\n");
        System.out.print(titles);
        for(int i = 0; i < (titles.length() + longestTeamNameLength + 1); i++) {
            System.out.print("=");
        }
        System.out.println();
        char divChar = '|';
        for(int i = 0; i < leaderBoard.length; i++) {
            aTeamNumber = leaderBoard[i][0];
            aTeamName = teams.get(1).get(aTeamNumber - 1);
            System.out.printf(formatStringTeamName, aTeamName);

            for(int c = 1; c <= 13; c++) {
                System.out.printf("%4d", leaderBoard[i][c]);
                System.out.print("  " + divChar);
            }
            System.out.println();
        }
    }
}
