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
package com.github.catageek.bytecart.util;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;

public final class LogUtil {

    public static void sendError(MessageReceiver receiver, String message) {
        display(receiver, TextColors.DARK_GREEN + "[Bytecart] " + TextColors.RED + message);
    }

    public static void sendSuccess(MessageReceiver receiver, String message) {
        display(receiver, TextColors.DARK_GREEN + "[Bytecart] " + TextColors.YELLOW + message);
    }

    private static void display(MessageReceiver receiver, String message) {
        receiver.sendMessage(Text.of(message));
    }
}
