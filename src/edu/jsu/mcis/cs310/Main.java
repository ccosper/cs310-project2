package edu.jsu.mcis.cs310;

import java.sql.SQLException;
import org.json.simple.*;

public class Main {

    public static void main (String[] args) throws SQLException {
        
        Database db = new Database("cs310_p2_user", "P2!user", "localhost");
        
        
        
        if (db.isConnected()){
            
            System.err.println("Connected Successfully!");
        
       // String results = db.getSectionsAsJSON(1,"CS","201");
        //int results2 = db.withdraw(1,1);
        //System.err.println(results2);
        
        }
}
}