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

import com.github.catageek.bytecart.ByteCartRedux;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public final class Messaging {

    private static final LiteralText MESSAGE_PREFIX = Text.of(ByteCartRedux.rootNode.getNode("message", "prefix")
            .getString("[ByteCart] "));

    public static void sendError(MessageReceiver receiver, Text message) {
        send(receiver, TextColors.RED, message);
    }

    public static void sendSuccess(MessageReceiver receiver, Text message) {
        send(receiver, TextColors.YELLOW, message);
    }

    private static void send(MessageReceiver receiver, TextColor color, Text message) {
        receiver.sendMessage(Text.builder()
                .color(TextColors.DARK_GREEN)
                .append(MESSAGE_PREFIX)
                .color(color)
                .append(message)
                .build());
    }
}
