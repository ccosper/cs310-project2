package edu.jsu.mcis.cs310;


import com.mysql.cj.protocol.Resultset;
import java.sql.*;
import java.sql.Connection;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Database {
    
    private final Connection connection;
    
    private final int TERMID_SP22 = 1;
    
    /* CONSTRUCTOR */

    public Database(String username, String password, String address) {
        
        this.connection = openConnection(username, password, address);
        
        
    }
    
    /* PUBLIC METHODS */

    public String getSectionsAsJSON(int termid, String subjectid, String num) throws SQLException {
        
        String result = null;
        
        try{
         String query= "Select *FROM section WHERE termid = ? AND subjectid = ? AND num = ?;";
         PreparedStatement pstmt = connection.prepareStatement(query);
         pstmt.setInt(1,termid);
         pstmt.setString(2,subjectid);
         pstmt.setString(3,num);
        
         boolean hasresults = pstmt.execute();
       
         if (hasresults)
            {
             ResultSet resultset = pstmt.getResultSet(); 
             result = getResultSetAsJSON((ResultSet) resultset);
            }
        }
        catch (Exception e) {
         e.printStackTrace(); 
        }
        
        return result;
        
    }
    
    public int register(int studentid, int termid, int crn) {
        
        int result = 0;
        
       try{
         String query = "INSERT INTO registration (studentid, termid, crn) VALUES (?,?,?);";
         PreparedStatement pstmt = connection.prepareStatement(query);
         pstmt.setInt(1,studentid);
         pstmt.setInt(2,termid);
         pstmt.setInt(3,crn);
        
          result = pstmt.executeUpdate();
         }  
        catch (Exception e){
          e.printStackTrace(); 
        }
        return result;
        
    }

    public int drop(int studentid, int termid, int crn) {
        
        int result = 0;
        
       try{
         String query= "DELETE FROM registration WHERE studentid=? And termid=? And crn =?;";
         PreparedStatement ptms = connection.prepareStatement(query);
         ptms.setInt(1,studentid);
         ptms.setInt(2,termid);
         ptms.setInt(3,crn);
        
         result = ptms.executeUpdate();
       }
        
        catch (Exception e) {
            e.printStackTrace(); 
        }
        return result;
         
    }
    
    public int withdraw(int studentid, int termid) {
        
        int result = 0;
        try{
         String query = "DELETE FROM registration WHERE studentid=? AND termid=?;";
         PreparedStatement pstmt = connection.prepareStatement(query);
         pstmt.setInt(1,studentid);
         pstmt.setInt(2,termid);
        
         result=pstmt.executeUpdate();
         
        }
        catch (Exception e) 
        { 
            e.printStackTrace(); 
        } 
        return result;
    }
        
    
    public String getScheduleAsJSON(int studentid, int termid) {
        
        String result = null;
        try{
            // JOIN SECTION AND REGISTER. ON REGISTER.CRN=SECTION.CRN
            String query = "SELECT * FROM registration JOIN section ON registration.crn =section.crn;";
         PreparedStatement pstmt = connection.prepareStatement(query);
         
          boolean hasresults = pstmt.execute();
       
         if (hasresults)
            {
              ResultSet resultset = pstmt.getResultSet(); 
             result = getResultSetAsJSON((ResultSet) resultset);
            }
        }
        
        catch(Exception e) 
        { 
            e.printStackTrace(); 
        } 
        return result;
        
    }
    
    public int getStudentId(String username) {
        
        int id = 0;
        
        try {
        
            String query = "SELECT * FROM student WHERE username = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            
            boolean hasresults = pstmt.execute();
            
            if ( hasresults ) {
                
                ResultSet resultset = pstmt.getResultSet();
                
                if (resultset.next())
                    
                    id = resultset.getInt("id");
                
            }
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return id;
        
    }
    
    public boolean isConnected() {

        boolean result = false;
        
        try {
            
            if ( !(connection == null) )
                
                result = !(connection.isClosed());
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return result;
        
    }
    
    /* PRIVATE METHODS */

    private Connection openConnection(String u, String p, String a) {
        
        Connection c = null;
        
        if (a.equals("") || u.equals("") || p.equals(""))
            
            System.err.println("*** ERROR: MUST SPECIFY ADDRESS/USERNAME/PASSWORD BEFORE OPENING DATABASE CONNECTION ***");
        
        else {
        
            try {

                String url = "jdbc:mysql://" + a + "/jsu_sp22_v1?autoReconnect=true&useSSL=false&zeroDateTimeBehavior=EXCEPTION&serverTimezone=America/Chicago";
                // System.err.println("Connecting to " + url + " ...");

                c = DriverManager.getConnection(url, u, p);

            }
            catch (Exception e) { e.printStackTrace(); }
        
        }
        
        return c;
        
    }
    
    private String getResultSetAsJSON(ResultSet resultset) {
        
        String result;
        
        /* Create JSON Containers */
        
        JSONArray json = new JSONArray();
        JSONArray keys = new JSONArray();
        
        try {
            
            /* Get Metadata */
        
            ResultSetMetaData metadata = resultset.getMetaData();
            int columnCount = metadata.getColumnCount();
            
            /* Get Keys */
            
            for (int i = 1; i <= columnCount; ++i) {

                keys.add(metadata.getColumnLabel(i));

            }
            
            /* Get ResultSet Data */
            
            while(resultset.next()) {
                
                /* Create JSON Container for New Row */
                
                JSONObject row = new JSONObject();
                
                /* Get Row Data */

                for (int i = 1; i <= columnCount; ++i) {
                    
                    /* Get Value; Pair with Key */

                    Object value = resultset.getObject(i);
                    row.put(keys.get(i - 1), String.valueOf(value));

                }
                
                /* Add Row Data to Collection */
                
                json.add(row);

            }
        
        }
        catch (Exception e) { e.printStackTrace(); }
        
        /* Encode JSON Data and Return */
        
        result = JSONValue.toJSONString(json);
        return result;
        
    }
    
}