/*--------------------------------------------------------

 1. Afshin Jamali / Date: 9/29/2017

 2. Java version used: Version "9"

 4. Precise examples / instructions to run this program:

 From browser type in: http://24.14.126.142:8080/WIKI/bento?n=logic

 All acceptable commands are displayed on the various consoles.

 This runs with ip 24.14.126.142 and port 8080:

 5. List of files needed for running the program.

 a. GettingToPhilosophy.java
 b. Constants.java
 c. DBUtilities.java
 d. GettingToPhilosophyServlet.java

 5. Notes:

 ----------------------------------------------------------*/
package servlet;

import main.GettingToPhilosophy;

import java.io.*;
import java.sql.SQLException;
import javax.servlet.*;
import javax.servlet.http.*;

public class GettingToPhilosophyServlet
        extends javax.servlet.http.HttpServlet{

  private GettingToPhilosophy g2p = null;

  @Override
  public void init() {
    this.g2p = new GettingToPhilosophy();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {

    final String link = request.getParameter("n");


    try {
      this.g2p.run(link);
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();

    // Write the response message, in an HTML page
    try {
      out.println("<html>");
      out.println("<head><title>Bento Coding Challenge</title></head>");
      out.println("<body>");
      out.println("<h1>Bento Coding Challange!</h1>");
      for(String path : this.g2p.getPathList()){
        out.println("<p>" + path + "</p>");
      }
      out.println("<p>" + "Total Hops: " + this.g2p.getPathSize() + "</p>");
      out.println("</body></html>");
    } finally {
      out.close();
    }
  }
}
