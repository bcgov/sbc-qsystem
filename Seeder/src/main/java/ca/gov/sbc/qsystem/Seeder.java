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

        if (args.length < 2) {
            System.out.println("ERROR - no database script folder locations specified as Parameter 1 & 2 to the Seeder command.");
        } else {
            // get parameters from the environment
            String databaseName = System.getenv("MYSQL_DATABASE");
            String databaseUser = System.getenv("MYSQL_USER");
            String mysqlReplication = System.getenv("MYSQL_REPLICATION");
            String mysqlService = "";
            String mysqlSlave = "";
            String rootPassword = System.getenv("MYSQL_ROOT_PASSWORD");
            String rootUser = System.getenv("MYSQL_ROOT_USER");
            String url = "";
            
            if (rootUser == null || rootUser.isEmpty()) {
                rootUser = "root";
            }

            if ("1".equals(mysqlReplication)) {
                mysqlService = System.getenv("MYSQL_MASTER_SERVICE");
                mysqlSlave = System.getenv("MYSQL_SLAVE_SERVICE");
                url = "jdbc:mysql:replication://" + mysqlService + "," + mysqlSlave;
            } else {
                mysqlService = System.getenv("MYSQL_SERVICE");
                url = "jdbc:mysql://" + mysqlService;
            }

            // Create the QSystem Database
            createDatabaseIfNotExist (databaseName, url, rootUser, rootPassword);
            // Create the QSky Database
            createDatabaseIfNotExist ("qsky", url, rootUser, rootPassword);

            // Flyway will need the database to be part of the URL
            String flywayUrl = url + "/" + databaseName;
            migrateDatabase(flywayUrl, rootUser, rootPassword, args [0]);
            grantPermissions (databaseName, flywayUrl, rootUser, rootPassword, databaseUser);

            // qsky migrations.
            String qskyUrl = url + "/qsky";
            migrateDatabase(qskyUrl, rootUser, rootPassword, args [1]);
            grantPermissions ("qsky", qskyUrl, rootUser, rootPassword, databaseUser);
            
            // At this point the configuration file could be created. 
        }
    }
}
