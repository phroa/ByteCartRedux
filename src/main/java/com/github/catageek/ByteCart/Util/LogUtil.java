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
package com.github.catageek.ByteCart.Util;

import com.github.catageek.ByteCart.ByteCartRedux;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class LogUtil {

    public static void sendError(CommandSender sender, String message) {
        display(sender, ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.RED + message);
    }

    public static void sendSuccess(CommandSender sender, String message) {
        display(sender, ChatColor.DARK_GREEN + "[Bytecart] " + ChatColor.YELLOW + message);
    }

    private static void display(CommandSender sender, String message) {
        if (sender != null && (sender instanceof Player) && ((Player) sender).isOnline()) {
            sender.sendMessage(message);
        } else {
            ByteCartRedux.log.info(message);
        }
    }
}
