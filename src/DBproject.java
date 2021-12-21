import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Scanner;
import java.sql.SQLException;

public class DBproject {

//************************************************************************************************
    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:autosDB.sqlite";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

//************************************************************************************************
    public void insertAcc(String date, String city, String state) {
        
        int aid = 0;
        int aidn = 0;
        String sql2 = "SELECT aid FROM accidents ORDER BY aid DESC LIMIT 1";
        
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql2)){
            
                
            aid = rs.getInt("aid");
            aidn = aid + 1;

            //System.out.println(aid);
            //System.out.println(aidn);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    	String sql = "INSERT or REPLACE INTO accidents(aid,accident_date,city,state) VALUES(?,?,?,?)";

        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	pstmt.setInt(1, aidn);
        	pstmt.setString(2, date);
            pstmt.setString(3, city);
            pstmt.setString(4, state);
            pstmt.executeUpdate();
            
            System.out.println("\nInformation of accident entered into the database and generated aid is: " + aidn);
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }



//************************************************************************************************
    public void insertInv(String vin, int damages, String driver_ssn) {
        
        int aid = 0;
        String sql2 = "SELECT aid FROM accidents ORDER BY aid DESC LIMIT 1";
        
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql2)){
            
                
            aid = rs.getInt("aid");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        String sql = "INSERT INTO involvements(aid, vin, damages, driver_ssn) VALUES(?,?,?,?)";

        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	 pstmt.setInt(1, aid);
        	pstmt.setString(2, vin);
            pstmt.setInt(3, damages);
            pstmt.setString(4, driver_ssn);
            pstmt.executeUpdate();
                                
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

//************************************************************************************************
    public void selectApp(int aid){
        String sql1 = "SELECT * "
                   + "FROM accidents "
                   + "WHERE aid = ?";
 
        try (Connection conn = this.connect();
              PreparedStatement pstmt  = conn.prepareStatement(sql1)){
     
         // set the value
             pstmt.setInt(1,aid);
         //
         ResultSet rs  = pstmt.executeQuery();
         
         System.out.println("----------------------------------");
         System.out.println("\nBELOW ARE THE DETAILS OF THE ACCIDENT:");
         // loop through the result set
         
         while (rs.next()) {
        	 System.out.println("aid: " + rs.getInt(1) +  "\t" + "\t" +
        			 			"Date: " + rs.getString(2) + "\t" +
        			 			"City: " + rs.getString(3) + "\t" + "\t" +
        			 			"State: " + rs.getString(4));
         }
     } catch (SQLException e) {
         System.out.println(e.getMessage());
     }
    
    	String sql2 = "SELECT * "
    			    + "FROM involvements "
    			    + "WHERE aid = ?";

    try (Connection conn = this.connect();
    		PreparedStatement pstmt  = conn.prepareStatement(sql2)){

    	// set the value
    	pstmt.setInt(1,aid);
    	//
    	ResultSet rs  = pstmt.executeQuery();
        
        System.out.println("----------------------------------");
    	System.out.println("\nBELOW ARE THE DETAILS OF THE VEHICLES INVOLVED IN THE ACCIDENT:");
    	// loop through the result set
  
    	while (rs.next()) {
    		System.out.println("\nVin: " + rs.getString(2) + 
    						   "\nDamages: " + rs.getString(3) + 
    						   "\nDriver_SSN: " + rs.getString(4) + "\n");
    	}
    } catch (SQLException e) {
    	System.out.println(e.getMessage());
    }
    
    }

//************************************************************************************************
    public void searchApp(String low_d, String high_d, int low_avg, int high_avg, int low_t, int high_t){
        String sql = "SELECT * \r\n"
        		   + "FROM (SELECT * \r\n"
        		   + "      FROM accidents \r\n"
        		   + "      WHERE aid in (SELECT aid \r\n"
        		   + "                    FROM (SELECT i.aid, SUM(i.damages) AS tdamages, AVG(i.damages) AS adamages FROM involvements i GROUP BY i.aid)\r\n"
        		   + "        	          WHERE tdamages > ? AND tdamages < ? AND adamages > ? AND adamages < ?))\r\n"
        		   + "WHERE accident_date > ? AND accident_date < ?";
 
    try (Connection conn = this.connect();
          PreparedStatement pstmt  = conn.prepareStatement(sql)){
     
         // set the value
         pstmt.setInt(1,low_t);
         pstmt.setInt(2, high_t);
         pstmt.setInt(3, low_avg);
         pstmt.setInt(4, high_avg);
         pstmt.setString(5, low_d);
         pstmt.setString(6, high_d);
         //
         ResultSet rs  = pstmt.executeQuery();
         System.out.println("----------------------------------");
         System.out.println("\nBELOW ARE THE RESULTS OF YOUR SEARCH:");
         
         // loop through the result set
         
         while (rs.next()) {
        	 System.out.println("\naid: " + rs.getInt(1) + 
        			 			"\nDate: " + rs.getString(2) + 
        			 			"\nCity: " + rs.getString(3) + 
        			 			"\nState: " + rs.getString(4) + "\n");
         }
     } catch (SQLException e) {
         System.out.println(e.getMessage());
     }
    
    }
    
//************************************************************************************************
    
    private void helpMenu() {
        System.out.println("		    CIS - 452 Project Assignment");
        System.out.println("                             =========");
        System.out.println("                             Main Menu");
        System.out.println("                             =========");
        System.out.println("---------------------------------------------------------------------");
        System.out.println("1 	  (Insert)    Insert a new accident in the database");
        System.out.println("2         (Find)      Find details of a particular accident");
        System.out.println("3         (Search)    Search for accident details using filters");
        System.out.println("0         (Quit)      Quit program");
        System.out.println("---------------------------------------------------------------------");
        
    }

//************************************************************************************************
    
private void mainMenu() {
    Scanner nscan = new Scanner(System.in);
    System.out.println("---------------------------------------------------------------------");
    System.out.println("\nDo you want to go back to the main menu? (Y/N)");
    String menu = nscan.nextLine();

    if (menu.equals("Y") || menu.equals("y")){
        processInput();
        }
    else if (menu.equals("N") || menu.equals("n")){
        System.out.println("---------------------------------------------------------------------");
        System.out.println("\nProgram shutdown.");
        System.exit(0);
    }
    else {
        System.out.println("---------------------------------------------------------------------");
        System.out.println("\nInvalid input. Try again. ");
        mainMenu();
    }
    
}
    
//************************************************************************************************
    
private void processInput() {
       
    helpMenu();
    Scanner nscan = new Scanner(System.in);
    
    System.out.println("\nEnter your option number to execute the required action: ");
    int key = nscan.nextInt();        
    System.out.println("---------------------------------------------------------------------");
    
    if (key == 1) {

        System.out.println("\nEnter details of the new accident: ");
        //int aid = nscan.nextInt();
        nscan.nextLine();
        System.out.println("\nEnter date of when the accident occurred in YYYY-MM-DD format: ");
        String date = nscan.nextLine();
        System.out.println("\nEnter the City where the accident occurred: ");
        String city = nscan.nextLine();
        System.out.println("\nEnter the State where the accident occurred: ");
        String state = nscan.nextLine();

        
        insertAcc(date, city, state);
        
        
        System.out.println("\nDo you want to add details of the vehicles involved in the accident? (Y/N)");
        
        String autos = nscan.nextLine();

        if (autos.equals("Y") || autos.equals("y")){
            System.out.println("\nEnter number of automobiles involved: ");
            int auton = nscan.nextInt();
            nscan.nextLine();
            for (int i = 1; i <= auton; i++){
                System.out.println("\nDetails of vehicle " + i + ": ");

                System.out.println("\nEnter vin of vehicle " + i + ": ");
                String vin = nscan.nextLine();
                System.out.println("\nEnter damages suffered by vehicle " + i + ": ");
                int damage = nscan.nextInt();
                nscan.nextLine();
                System.out.println("\nEnter SSN of the driver in vehicle " + i + ": ");
                String ssn = nscan.nextLine();

                insertInv(vin, damage, ssn);
                System.out.println("\nInformation about vehicle " + i + " added in the database.");

            }

            //New Insert
            mainMenu();
            

        }
        else if (autos.equals("N") || autos.equals("n")){
            mainMenu();
        }
    }
    else if (key == 2) {
        
        System.out.println("\nEnter aid of the accident: ");
        int y = nscan.nextInt();
        selectApp(y); 

        //New Select
        mainMenu();
    }
    else if (key == 3) {
        String lower_d = "0000-00-00";
        String upper_d = "9999-99-99";
        int lower_avg = 0;
        int upper_avg = 999999;
        int lower_tot = 0;
        int upper_tot = 999999;

        nscan.nextLine();
        
        System.out.println("\nDo you want to add a range of dates? (Y/N)"); 
        String range_d = nscan.nextLine();
        if (range_d.equals("Y") || range_d.equals("y")){
            System.out.println("\nAdd the lower limit of date in YYYY-MM-DD format:"); 
            lower_d = nscan.nextLine();
            System.out.println("\nAdd the upper limit of date in YYYY-MM-DD format:"); 
            upper_d = nscan.nextLine();
            }
        else if (range_d.equals("N") || range_d.equals("n")){
            lower_d = "0000-00-00";
            upper_d = "9999-99-99";
        }
        System.out.println("----------------------------------");
        System.out.println("\nDo you want to add a range of average damages? (Y/N)"); 
        String range_avg = nscan.nextLine();
        if (range_avg.equals("Y") || range_avg.equals("y")){
            System.out.println("\nAdd the lower limit of average damages:"); 
            lower_avg = nscan.nextInt();
            System.out.println("\nAdd the upper limit of average damages:"); 
            upper_avg = nscan.nextInt();
            nscan.nextLine();
            }
        else if (range_d.equals("N") || range_d.equals("n")){
            lower_avg = 0;
            upper_avg = 9999999;
        }
        System.out.println("----------------------------------");
        System.out.println("\nDo you want to add a range of total damages? (Y/N)"); 
        
        //nscan.nextLine();
        String range_tot = nscan.nextLine();
        //nscan.nextLine();
        if (range_tot.equals("Y") || range_tot.equals("y")){
            System.out.println("\nAdd the lower limit of total damages:"); 
            lower_tot = nscan.nextInt();
            System.out.println("\nAdd the upper limit of total damages:"); 
            upper_tot = nscan.nextInt();
            }
        else if (range_d.equals("N") || range_d.equals("n")){
            lower_tot = 0;
            upper_tot = 9999999;
        }

        searchApp(lower_d, upper_d, lower_avg, upper_avg, lower_tot, upper_tot);

        //New Search
        mainMenu();
    }
    else if (key == 0) {
        
        System.out.println("\nProgram shutdown.");
        System.exit(0);
    
    }
    else {
        
        System.out.println("\nINVALID OPTION. Restarting program...\n");
        processInput();
    
    }
}

       
//************************************************************************************************
    public static void main(String[] args) throws SQLIntegrityConstraintViolationException {

        DBproject app = new DBproject();
        
        app.processInput();
        //app.insertAcc("2027-04-30", "New Bedford", "MA");
        //app.insertInv(111, "ergegregewrt5", 3664, "012-93-9925");
        //app.selectApp(119);
        //app.searchApp("0000-00-00", "2009-99-99", 0, 4000, 0, 5000);

    }
}

