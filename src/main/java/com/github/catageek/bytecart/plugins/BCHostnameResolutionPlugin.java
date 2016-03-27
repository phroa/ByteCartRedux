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
package com.github.catageek.bytecart.plugins;

import code.husky.Database;
import code.husky.mysql.MySQL;
import code.husky.sqlite.SQLite;
import com.github.catageek.bytecart.address.Address;
import com.github.catageek.bytecart.address.Resolver;
import com.github.catageek.bytecart.ByteCartRedux;
import com.github.catageek.bytecart.event.custom.SignCreateEvent;
import com.github.catageek.bytecart.event.custom.SignRemoveEvent;
import com.github.catageek.bytecart.event.custom.UpdaterClearStationEvent;
import com.github.catageek.bytecart.event.custom.UpdaterPassStationEvent;
import com.github.catageek.bytecart.event.custom.UpdaterSetStationEvent;
import com.github.catageek.bytecart.sign.Station;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BCHostnameResolutionPlugin implements Resolver, Listener, CommandExecutor {

    private Database mysql;
    private Connection con;
    private Statement s;
    boolean err = false;
    private String database = ByteCartRedux.rootNode.getNode("database", "name").getString("BCHostnames");
    private String sql = ByteCartRedux.rootNode.getNode("database", "type").getString("sqlite");

    public void onLoad() {
        if (sql.equalsIgnoreCase("mysql")) {
            String host = ByteCartRedux.rootNode.getNode("database", "hostname").getString();
            String port = ByteCartRedux.rootNode.getNode("database", "port").getString("3306");
            String user = ByteCartRedux.rootNode.getNode("database", "user").getString();
            String password = ByteCartRedux.rootNode.getNode("database", "password").getString();;
            mysql = new MySQL(ByteCartRedux.myPlugin, host, port, database, user, password);
        } else {
            mysql = new SQLite(ByteCartRedux.myPlugin, database);
        }
        con = mysql.openConnection();
        try {
            s = con.createStatement();
            s.execute(
                    "create table if not exists cart_dns (ip varchar(11) not null primary key,username varchar(20) not null,uuid varchar(128) not "
                            + "null,name varchar(20) not null unique)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender a, Command b, String c, String[] d) {
        return onCommand(a, b, c, d, 0);
    }

    public boolean onCommand(CommandSender a, Command b, String c, String[] d, int n) {
        if (b.getName().equalsIgnoreCase("host")) {
            if (d.length == 0) {
                return false;
            } else if (d.length >= 1) {
                // Strip diacritics from Station name
                try {
                    switch (d[0]) {
                        case "create":
                            if (a.hasPermission("bytecart.host.manager")) {
                                String hostname = getName(d, 1, 1);
                                hostname = Normalizer.normalize(hostname, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                                if (!safeName(hostname)) {
                                    a.sendMessage(ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.RED + "No hacking this time");
                                    return true;
                                } else if (d.length < 3 || d[d.length - 1].equals("") || hostname.length() > 20) {
                                    a.sendMessage(ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.RED + "Wrong IP/Name or not enough args.");
                                    return true;
                                }
                                if (!existEntryByName(hostname, a)) {
                                    String uu = "Console", user = "Console";
                                    if ((a instanceof Player)) {
                                        uu = ((Player) a).getUniqueId().toString();
                                        user = ((Player) a).getName();
                                    }
                                    createEntry(hostname, safeIP(d[d.length - 1]), uu, user);
                                    a.sendMessage(ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.YELLOW + "Hostname added");
                                }
                                return true;
                            } else {
                                a.sendMessage(
                                        ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.RED + "You don't have permission to use this command.");
                                return true;
                            }
                        case "remove":
                            if (a.hasPermission("bytecart.host.manager")) {
                                String hostname = getName(d, 1, 0);
                                hostname = Normalizer.normalize(hostname, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                                if (!safeName(hostname)) {
                                    a.sendMessage(ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.RED + "No hacking this time");
                                    return true;
                                }
                                if (removeEntry(hostname)) {
                                    a.sendMessage(ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.YELLOW + "Hostname deleted");
                                    return true;
                                }
                            } else {
                                a.sendMessage(
                                        ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.RED + "You don't have permission to use this command.");
                                return true;
                            }
                        case "list":
                            if (!a.hasPermission("bytecart.host.list")) {
                                a.sendMessage(
                                        ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.RED + "You don't have permission to use this command.");
                                return true;
                            } else {
                                if (d.length == 0) {
                                    return false;
                                } else if (d.length >= 1) {
                                    int listc = 0;
                                    if (d.length >= 2 && d[1].matches("-?\\d+")) {
                                        listc = Integer.parseInt(d[1]);
                                    }
                                    try {
                                        ResultSet res = s.executeQuery("SELECT * FROM `cart_dns` LIMIT " + (listc * 10) + ", " + (listc * 10 + 10));
                                        if (!res.next()) {
                                            a.sendMessage(ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.YELLOW + "No DNS records");
                                        } else {
                                            a.sendMessage(
                                                    ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.YELLOW + "DNS record table page (" + (listc + 1)
                                                            + ")");
                                            listc *= 10;
                                            do {
                                                a.sendMessage(listc + ": " + res.getString("ip") + "    " + res.getString("name"));
                                                listc++;
                                            }
                                            while (res.next());
                                        }
                                    } catch (SQLException e) {
                                        if (n < 2) {
                                            n++;
                                            con = mysql.getConnection();
                                            if (con == null) {
                                                con = mysql.openConnection();
                                            }
                                            return onCommand(a, b, c, d, n);
                                        } else {
                                            ByteCartRedux.log.info("SQL error code: " + e.getErrorCode());
                                            ByteCartRedux.log.info("SQL error msg: " + e.getMessage());
                                            ByteCartRedux.log.info("SQL error state: " + e.getSQLState());
                                            return false;
                                        }
                                    }
                                }
                                return true;
                            }
                        default:
                            if (!a.hasPermission("bytecart.host.user")) {
                                a.sendMessage(
                                        ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.RED + "You don't have permission to use this command.");
                                return true;
                            }
                            // HAXX, if not /dns lookup, then make [name] first parameter
                            String hostname;
                            if (d[0].equalsIgnoreCase("lookup")) {
                                if (d.length != 2) {
                                    return false;
                                }
                                hostname = this.getName(d, 1, 0);
                            } else {
                                hostname = this.getName(d, 0, 0);
                            }
                            hostname = Normalizer.normalize(hostname, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                            if (!safeName(hostname)) {
                                a.sendMessage(ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.RED + "No hacking this time");
                                return true;
                            }
                            if (!existEntryByName(hostname)) {
                                if (hostname.equals("")) {
                                    a.sendMessage(ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.RED + "No station with this name/ip");
                                    return true;
                                }
                                if (!existEntryByIP(safeIP(hostname))) {
                                    a.sendMessage(ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.RED + "No station with this name/ip");
                                } else {
                                    ResultSet ress = getEntryByIP(safeIP(hostname));
                                    a.sendMessage(
                                            ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.YELLOW + "Name: " + ress.getString("name") + " IP: "
                                                    + ress.getString("ip"));
                                    a.sendMessage(ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.YELLOW + "Author: " + ress.getString("username"));
                                }
                                return true;
                            } else {
                                ResultSet ress = getEntryByName(hostname);
                                a.sendMessage(
                                        ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.YELLOW + "Name: " + ress.getString("name") + " IP: " + ress
                                                .getString("ip"));
                                a.sendMessage(ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.YELLOW + "Author: " + ress.getString("username"));
                                return true;
                            }
                    }
                } catch (SQLException e) {
                    if (n < 2) {
                        n++;
                        con = mysql.getConnection();
                        if (con == null) {
                            con = mysql.openConnection();
                        }
                        return onCommand(a, b, c, d, n);
                    } else {
                        ByteCartRedux.log.info("SQL error code: " + e.getErrorCode());
                        ByteCartRedux.log.info("SQL error msg: " + e.getMessage());
                        ByteCartRedux.log.info("SQL error state: " + e.getSQLState());
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public void onSignCreate(SignCreateEvent event) {
        if (event.getIc() instanceof Station) {
            try {
                Station station = (Station) event.getIc();
                String ip = event.getStrings()[3];
                String name = event.getStrings()[2];
                Player player = event.getPlayer();
                if (!ip.equals("") && !name.equals("")) {
                    name = Normalizer.normalize(name, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                    if (safeName(name) && !existEntryByName(name, player)) {
                        createEntry(name, safeIP(ip), player.getUniqueId().toString(), player.getName());
                    }
                }
            } catch (SQLException e) {
                ByteCartRedux.log.info("SQL error code: " + e.getErrorCode());
                ByteCartRedux.log.info("SQL error msg: " + e.getMessage());
                ByteCartRedux.log.info("SQL error state: " + e.getSQLState());
            }
        }
    }

    @EventHandler
    public void onSignRemove(SignRemoveEvent event) {
        if (event.getIc() instanceof Station) {
            try {
                String name = ((Station) event.getIc()).getStationName();
                name = Normalizer.normalize(name, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                if (safeName(name)) {
                    removeEntry(name);
                }
            } catch (SQLException e) {
                ByteCartRedux.log.info("SQL error code: " + e.getErrorCode());
                ByteCartRedux.log.info("SQL error msg: " + e.getMessage());
                ByteCartRedux.log.info("SQL error state: " + e.getSQLState());
            }
        }
    }

    @EventHandler
    public void onUpdaterSetStation(UpdaterSetStationEvent event) {
        try {
            String ip = event.getNewAddress().toString();
            String name = event.getName();
            if (!name.equals("")) {
                name = Normalizer.normalize(name, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                if (safeName(name)) {
                    removeEntry(name);
                    createEntry(name, ip, "updater", "updater");
                }
            }
        } catch (SQLException e) {
            ByteCartRedux.log.info("SQL error code: " + e.getErrorCode());
            ByteCartRedux.log.info("SQL error msg: " + e.getMessage());
            ByteCartRedux.log.info("SQL error state: " + e.getSQLState());
        }
    }

    @EventHandler
    public void onUpdaterClearStation(UpdaterClearStationEvent event) {
        try {
            String name = event.getName();
            if (!name.equals("")) {
                name = Normalizer.normalize(name, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                if (safeName(name)) {
                    removeEntry(name);
                }
            }
        } catch (SQLException e) {
            ByteCartRedux.log.info("SQL error code: " + e.getErrorCode());
            ByteCartRedux.log.info("SQL error msg: " + e.getMessage());
            ByteCartRedux.log.info("SQL error state: " + e.getSQLState());
        }
    }

    @EventHandler
    public void onUpdaterPassStation(UpdaterPassStationEvent event) {
        try {
            String name = event.getName();
            if (!name.equals("")) {
                Address ip = event.getAddress();
                name = Normalizer.normalize(name, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                if (ip.isValid() && safeName(name) && !existEntryByName(name)) {
                    createEntry(name, ip.toString(), "updater", "updater");
                }
            }
        } catch (SQLException e) {
            ByteCartRedux.log.info("SQL error code: " + e.getErrorCode());
            ByteCartRedux.log.info("SQL error msg: " + e.getMessage());
            ByteCartRedux.log.info("SQL error state: " + e.getSQLState());
        }
    }

    public String resolve(String name) {
        name = Normalizer.normalize(name, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        try {
            if (safeName(name) && existEntryByName(name)) {
                return getEntryByName(name).getString("ip");
            }
        } catch (SQLException e) {
            ByteCartRedux.log.info("SQL error code: " + e.getErrorCode());
            ByteCartRedux.log.info("SQL error msg: " + e.getMessage());
            ByteCartRedux.log.info("SQL error state: " + e.getSQLState());
        }
        return "";
    }

    private boolean removeEntry(String name) throws SQLException {
        if (existEntryByName(name)) {
            s.executeUpdate("DELETE FROM `cart_dns` WHERE LOWER(`name`)='" + name.toLowerCase() + "'");
            return true;
        }
        return false;
    }


    private boolean existEntryByName(String name, CommandSender a) throws SQLException {
        if (existEntryByName(name)) {
            ResultSet res = getEntryByName(name);
            a.sendMessage(ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.RED + "hostname " + res.getString("name") + " exist with ip " + res
                    .getString("ip") + " .");
            return true;
        }
        return false;
    }

    private boolean existEntryByName(String name) throws SQLException {
        ResultSet res = s.executeQuery("SELECT * FROM `cart_dns` WHERE LOWER(`name`)='" + name.toLowerCase() + "'");
        return res.next();
    }

    private boolean existEntryByIP(String ip) throws SQLException {
        ResultSet res = s.executeQuery("SELECT * FROM `cart_dns` WHERE `ip`='" + ip + "'");
        return res.next();
    }

    private ResultSet getEntryByName(String name) throws SQLException {
        return s.executeQuery("SELECT * FROM `cart_dns` WHERE LOWER(`name`)='" + name.toLowerCase() + "'");
    }

    private ResultSet getEntryByIP(String ip) throws SQLException {
        return s.executeQuery("SELECT * FROM `cart_dns` WHERE `ip`='" + ip + "'");
    }

    private void createEntry(String name, String ip, String uu, String username) throws SQLException {
        String ds = "INSERT INTO `cart_dns` (`ip`,`name`,`username`,`uuid`) VALUES('" + ip + "','" + name + "','" + username + "','" + uu + "')";
        s.executeUpdate(ds);
    }

    private String safeIP(String input) {
        char[] ch = input.toCharArray();
        String output = "";
        int cnt = 0, cnt2 = 0;
        for (char c : ch) {
            if (Character.isDigit(c)) {
                if (cnt >= 5) {
                    return "";
                } else {
                    output += c;
                }
            } else if (c == '.') {
                if (cnt == 0) {
                    return "";
                }
                if (cnt2 == 2) {
                    return "";
                }
                output += c;
                cnt = 0;
                cnt2++;
            } else {
                return "";
            }
            cnt++;
        }
        return output;
    }

    private boolean safeName(String input) {
        if (input.toLowerCase().contains(" or ") || input.toLowerCase().contains(" and ")
                || input.toLowerCase().contains(" union ")) {
            return false;
        }
        Pattern p = Pattern.compile("[^a-z0-9/*+$!:.@_\\-#&\\s]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(input);
        return !m.find();
    }

    private String getName(String[] input, int s, int e) {
        String output = input[s];
        for (int i = s + 1; i < input.length - e; i++) {
            output += " " + input[i];
        }
        return output;
    }
}