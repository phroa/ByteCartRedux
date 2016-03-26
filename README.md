```
   ___       _         ___           _
  / __\_   _| |_ ___  / __\__ _ _ __| |_
 /__\// | | | __/ _ \/ /  / _` | '__| __|
/ \/  \ |_| | ||  __/ /__| (_| | |  | |_
\_____/\__, |\__\___\____/\__,_|_|   \__|
       |___/
```

# ByteCart Redux

ByteCart Redux is a [Sponge](https://spongepowered.org) plugin that allows to create a Minecraft rail network using basic principles of Internet routing.

ByteCart Redux is a port of the Bukkit plugin [ByteCart](https://github.com/catageek/ByteCart), by Catageek. Maintained with permission.

## Overview

A cart with a player or chest with a destination address recorded in its inventory will be
routed through the network to the destination station matching the address.

ByteCart Redux provides a set of blocks to create routers and stations according to a predefined network topology.
It supports up to 45000 stations in 53 regions.

Routing tables are stored in-game using a chest-based storage system.

A tool is provided to configure the network automatically if you do not have routing skills.

## Videos

http://www.dailymotion.com/video/xyf5s6_bytecart-plugin-presentation_videogames, scroll down for more.

## Features

* Minimal CPU and memory usage, even with a huge network: ByteCart Redux is fully scalable, meaning that whatever the size of your network, the same amount of resources are used to route carts.

* Reliable, scalable anti-collision system: When a collision is about to occur, carts are rerouted temporarily to avoid collisions.

* Well-known pathfinding algorithm: ByteCart Redux implements the Djikstra algorithm, used successfully in Internet routing. All paths are precomputed and stored in routing tables. Finding the best path is as simple as a read operation on the routing table.

* Possibility to form logical trains with a unique destination: Tag the "engine", or first, cart. Others behind it will form a train of followers.

* Supports the Permissions API: Use any compatible plugin to control who can do what.

* World modification compatibility: Since there is no external data storage, all world editing tools are compatible with ByteCart. You can edit large swaths of the network in-game or offline.

## Installation

Place `ByteCartRedux.jar` in your server's `mods` folder.

## Contributing

- Ensure your code builds, or mark the pull request as a work in progress.
- Use the [Sponge code style](https://github.com/SpongePowered/SpongeAPI/tree/master/extra) for Java, IntelliJ defaults for Kotlin

## Credits

Developed by [Catageek](https://github.com/catageek) <catageek@free.fr>

Maintained with permission by [phroa](https://github.com/phroa) <jack@phroa.net>

## License

GPLv3:

```
ByteCart, ByteCart Redux
Copyright (C) Catageek
Copyright (C) phroa

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```
