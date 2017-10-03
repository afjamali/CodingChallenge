/*--------------------------------------------------------

 1. Afshin Jamali / Date: 9/29/2017

 2. Java version used: Version "9"

 4. Precise examples / instructions to run this program:

 From browser type in: http://24.14.126.142:8080/WIKI/bento?n=logic

 or

 From browser type in: http://24.14.126.142:8080/WIKI/bento?n=https://en.wikipedia.org/wiki/logic

 All acceptable commands are displayed on the various consoles.

 This runs with ip 24.14.126.142 and port 8080:

 5. List of files needed for running the program.

 a. GettingToPhilosophy.java
 b. Constants.java
 c. DBUtilities.java
 d. GettingToPhilosophyServlet.java

 5. Notes:

 ----------------------------------------------------------*/

package main;

import db.DBUtilities;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class GettingToPhilosophy {

    private final String GOAL = "philosophy";
    private final int HOP_LIMIT = 500;
    private static String completionMessage;
    private String str;
    private int hops;
    private ArrayList<String> pathList;
    private ArrayDeque<String> tableDeque;
    private  ArrayDeque<String> ulDeque;
    private ArrayDeque<String> pDeque;

    public GettingToPhilosophy(){
        pathList = new ArrayList<>();
        tableDeque = new ArrayDeque<>();
        ulDeque = new ArrayDeque<>();
        pDeque = new ArrayDeque<>();
    }

    public void run(String link) throws IOException, SQLException, ClassNotFoundException {
        boolean search = false;

        // Add initial path to path list
        link = link.substring(link.lastIndexOf("/") + 1).trim();
        link = link.substring(0,1).toUpperCase() + link.substring(1).toLowerCase();
        addPath(link);

        do {
            try {
                // Create a URL for the desired page
                URL url = new URL("https://en.wikipedia.org/wiki/" + link);

                try ( // Read all the text returned by the server
                      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {

                    while ((str = in.readLine()) != null) {

                        // Clear unnecessary links
                        removeCitationLinks();
                        removeHelpLinks();
                        removeDuplicateLinks();

                        // Ignore links in side tables or containers
                        if (skipLinksInTables()) {
                            continue;
                        }

                        // Check constraints
                        search = canSearch();

                        if (search) {

                            removeAsideLinks();
                            link = getFirstLinkName();

                            if (link != "") {
                                addPath(link);
                                System.out.println(link);
                                search = false;
                                hops++;
                                break;
                            }
                        }
                    }
                }
            } catch (MalformedURLException e) {
                System.out.println(e);
                System.exit(1);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(1);
            }
        } while (!link.toLowerCase().equals(getGOAL()) && hops <= HOP_LIMIT);

        // Store path in a database
        storePathInDb();
        processCompletionMessage();
        System.out.println("YAY!!  Total Hops: " + (getPathSize()-1));
    }

    private void storePathInDb() throws SQLException, ClassNotFoundException {


        int id = 0;
        String statement = "DELETE FROM wiki";
        DBUtilities.executeUpdate(statement);

        for(String name : pathList){
            statement = "INSERT INTO wiki (id, name, parent) VALUES(" + id + ", '" + name + "', " + (id == 0 ? null : id-1) + ")";
            DBUtilities.executeUpdate(statement);
            id++;
        }
    }

    private String getFirstLinkName(){
        String name = "";
        int index = -1;
        index = str.indexOf("href=\"");

        if (index != -1) {
            str = str.substring(index + 6);
            index = str.indexOf("\"");
            name = str.substring(0, index);
            name = name.substring(name.lastIndexOf("/") + 1).trim();

        }
        return name;
    }

    private void removeAsideLinks(){
        boolean clean = true;
        do {
            int openingParenthesisIndex = str.indexOf("(");
            int linkIndex = str.indexOf("href=\"");

            if (openingParenthesisIndex != -1 && openingParenthesisIndex < linkIndex) {
                ArrayDeque<Character> deque = new ArrayDeque<>();
                char[] strArray = str.toCharArray();
                for (int i = 0; i < strArray.length; i++) {
                    if (strArray[i] == '(') {
                        deque.push(strArray[i]);
                    } else if (strArray[i] == ')') {
                        deque.pop();
                        if (deque.isEmpty()) {
                            str = str.substring(i + 1);
                            break;
                        }
                    }
                }
            } else {
                clean = false;
            }
        } while (clean);
    }

    private boolean canSearch(){
        if (str.contains("<p>") || str.contains("<ul>")) {
            return true;
        } else if (ulDeque.isEmpty() && pDeque.isEmpty()) {
            return false;
        }
        return false;
    }

    private boolean skipLinksInTables(){
        int openingTableIndex = str.indexOf("<table");
        int endingTableIndex = str.indexOf("</table");
        int openingPIndex = str.indexOf("<p");
        int endingPIndex = str.indexOf("</p>");
        int openingUlIndex = str.indexOf("<ul");
        int endingUlIndex = str.indexOf("</ul>");

        if (openingTableIndex != -1) tableDeque.push("<table");
        if (endingTableIndex != -1) tableDeque.pop();
        if (openingPIndex != -1) pDeque.push("<p");
        if (endingPIndex != -1) pDeque.pop();
        if (openingUlIndex != -1) ulDeque.push("<ul");
        if (endingUlIndex != -1) ulDeque.pop();

        return !tableDeque.isEmpty();
    }

    private void removeCitationLinks(){
        str = str.replaceAll("href=\"#", " ");
    }

    private void removeHelpLinks(){
        str = str.replaceAll("href=\"/wiki/Help:IPA", " ");
    }

    private void removeDuplicateLinks(){
        for(String prevLink : pathList) {
            str = str.replaceAll("href=\"/wiki/" + prevLink, " ");
        }
    }

    private void addPath(String p){
        pathList.add(p);
    }

    private void processCompletionMessage(){
        if(hops <= HOP_LIMIT && pathList.get(pathList.size()-1).toLowerCase().equals(GOAL))
            completionMessage = "Path completed successfully. Total Hops: " + hops;
        else
            completionMessage = "Path did not complete successfully: Total Hops: " + hops;
    }

    public String getCompletionMessage(){
        return  completionMessage;
    }

    public ArrayList<String> getPathList() {
        return pathList;
    }

    public int getPathSize() {
        return pathList.size();
    }

    private String getGOAL(){
        return GOAL;
    }
}
