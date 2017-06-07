/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.gov.sbc.qsystem;

import java.io.File;
import org.flywaydb.core.Flyway;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 *
 * @author George Walker
 */
public class Seeder {

    public static void migrateDatabase(String url, String rootUser, String rootPassword, String sqlDir) {
        // Create the Flyway instance
        Flyway flyway = new Flyway();

        // Point it to the database
        flyway.setDataSource(url, rootUser, rootPassword);

        File temp = new File(sqlDir);
        String filepath = "filesystem:" + temp.getAbsolutePath();

        System.out.println("Migration source is " + filepath);

        flyway.setLocations(filepath);
        flyway.migrate();
    }
    
    public static void createDatabaseIfNotExist (String name, String url, String rootUser, String rootPassword)
    {
            // Create database if it does not exist
            String sqlCreate = "CREATE DATABASE IF NOT EXISTS `" + name + "` DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_unicode_ci";
            try (Connection conn = DriverManager.getConnection(url, rootUser, rootPassword);
                    PreparedStatement stmtCreate = conn.prepareStatement(sqlCreate)) {
                stmtCreate.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }        
    }
    
    public static void grantPermissions (String name, String url, String rootUser, String rootPassword, String databaseUser)
    {
        
            String sqlGrant = "GRANT ALL ON `" + name + "`.* TO '"+ databaseUser +"'";
            // grant permissions            
            try (Connection conn = DriverManager.getConnection(url, rootUser, rootPassword);
                    PreparedStatement stmtGrant = conn.prepareStatement(sqlGrant)) {

                stmtGrant.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

    }
    
    public static void main(String[] args) {

        System.out.println("QSystem Seeder");

        if (args.length < 1) {
            System.out.println("ERROR - no database script folder location specified as Parameter 1 to the Seeder command.");
        } else {
            // get parameters from the environment
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://" + System.getenv("MYSQL_SERVICE");

            System.out.println("URL is " + url);

            String rootUser = System.getenv("MYSQL_ROOT_USER");
            if (rootUser == null || rootUser.isEmpty()) {
                rootUser = "root";
            }

            String rootPassword = System.getenv("MYSQL_ROOT_PASSWORD");
            String databaseUser = System.getenv("MYSQL_USER");
            String name = System.getenv("MYSQL_DATABASE");

            // Create the QSystem Database
            createDatabaseIfNotExist (name, url, rootUser, rootPassword);
            // Create the QSky Database
            createDatabaseIfNotExist ("qsky", url, rootUser, rootPassword);

            // Flyway will need the database to be part of the URL  
            url = "jdbc:mysql://" + System.getenv("MYSQL_SERVICE") + "/" + System.getenv("MYSQL_DATABASE");
            migrateDatabase(url, rootUser, rootPassword, args [0]);
            grantPermissions (name, url, rootUser, rootPassword, databaseUser);
            
            // qsky migrations.
            url = "jdbc:mysql://" + System.getenv("MYSQL_SERVICE") + "/qsky"; 
            migrateDatabase(url, rootUser, rootPassword, args [1]);
            grantPermissions ("qsky", url, rootUser, rootPassword, databaseUser);
            
            // At this point the configuration file could be created. 
        }
    }
}
