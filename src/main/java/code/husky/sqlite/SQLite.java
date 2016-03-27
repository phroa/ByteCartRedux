/**
 * ByteCart, ByteCart Redux
 * Copyright (C) Catageek
 * Copyright (C) phroa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package code.husky.sqlite;

import code.husky.Database;
import com.github.catageek.ByteCart.ByteCartPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connects to and uses a SQLite database
 *
 * @author tips48
 */
public class SQLite extends Database {

    private final String dbLocation;

    private Connection connection;

    /**
     * Creates a new SQLite instance
     *
     * @param plugin
     *            Plugin instance
     * @param dbLocation
     *            Location of the Database (Must end in .db)
     */
    public SQLite(ByteCartPlugin plugin, String dbLocation) {
        super(plugin);
        this.dbLocation = dbLocation;
        this.connection = null;
    }

    @Override
    public Connection openConnection() {
        File file = new File(dbLocation);
        if (!(file.exists())) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLog().error("Unable to create database!");
            }
        }
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().toPath().toString() + "/" + dbLocation);
        } catch (SQLException e) {
            plugin.getLog().error("Could not connect to SQLite server! because: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            plugin.getLog().error("JDBC Driver not found!");
        }
        return connection;
    }

    @Override
    public boolean checkConnection() {
        try {
            return !(connection.isClosed());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                plugin.getLog().error("Error closing the SQLite Connection!");
                e.printStackTrace();
            }
        }
    }

}
