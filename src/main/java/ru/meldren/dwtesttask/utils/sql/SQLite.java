package ru.meldren.dwtesttask.utils.sql;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Meldren on 23/06/2021
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class SQLite {

    @NonFinal
    Connection connection;
    @NonFinal
    ExecutorService service;
    AtomicInteger queriesInProgress = new AtomicInteger(0);

    String database;

    public void connect() {
        if (!isConnected()) {
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:" + this.database);
                service = Executors.newFixedThreadPool(15);
                System.out.println("Successful SQL database connection!");
            } catch (SQLException ex) {
                System.out.println("Failed to connect to SQL database!");
                ex.printStackTrace();
            }
        } else {
            System.out.println("SQL connection already established.");
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                while (queriesInProgress.get() != 0)
                    Thread.sleep(10);
                service.shutdown();
                connection.close();
            } catch (SQLException | InterruptedException ex) {
                System.out.println("Can't close SQL database connection.");
            }
            System.out.println("SQL connection closed.");
        } else {
            System.out.println("SQL connection not established.");
        }
    }

    public boolean isConnected() {
        return connection != null;
    }

    public ResultSet executeSyncQuery(String query) {
        queriesInProgress.getAndIncrement();
        ResultSet result = null;
        try {
            final Statement statement = this.connection.createStatement();
            if (query.contains("SELECT")) {
                result = statement.executeQuery(query);
            } else {
                statement.executeUpdate(query);
            }
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        queriesInProgress.getAndDecrement();
        return result;
    }

    public Future<ResultSet> executeAsyncQuery(String query) {
        return service.submit(() -> this.executeSyncQuery(query));
    }

}