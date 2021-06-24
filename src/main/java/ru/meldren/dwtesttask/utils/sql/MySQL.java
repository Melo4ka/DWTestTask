package ru.meldren.dwtesttask.utils.sql;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.sql.*;

/**
 * Created by Meldren on 23/06/2021
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class MySQL {
    @NonFinal
    Connection connection;

    @NonNull
    String host;
    @NonNull
    String database;
    @NonNull
    String username;
    @NonNull
    String password;
    @NonNull
    int port;

    public void connect() {
        if (!isConnected()) {
            try {
                String url = "JDBC:mysql://" +
                        this.host +
                        ":" +
                        this.port +
                        "/" +
                        this.database +
                        "?autoReconnect=true&useSSL=false&useUnicode=true&characterEncoding=utf-8";
                connection = DriverManager.getConnection(url, this.username, this.password);
                System.out.println("Successful MySQL database connection!");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException ex) {
            System.out.println("Can't close MySQL database connection.");
        }
    }

    public boolean isConnected() {
        return connection != null;
    }


    public void executeUpdate(String query) {
        new Thread(() -> {
            try {
                connection.createStatement().executeUpdate(query);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
    }

}