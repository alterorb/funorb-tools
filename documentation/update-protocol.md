# The Update Protocol

Since all of the funorb games are based of RuneTek 4 engine they all use the standard JS5 protocol with two exceptions. 
The first exception is that when sending the handshake packet (packet 15) the game sends the parameter `gamecrc` value (available in the game metadata) as the `version`. 
The second is that before sending the handshake packet the client must send a 8 byte packet that is used to identify the game.
The packet structure is:

| Name  | Type | Value | Description |
| :---: | :---: | :---: | :---: |
| packetId | `byte` `constant` | 12 | The packet identifier | 
| unknown | `short` `constant` | 17 | Unknown constant |
| gameId | `short` | Refer to the [Game Ids](gameIds.md) page for the possible values | The game id. |
| severNum | `short` | - | The server number. This value is present as a parameter in the game metadata files identified by the key `servernum`. |
| langId | `byte` | View the possible values below | The language id. |

#### Available Languages

| Language | Id |
| :---: | :---: |
| English | 0 |
| German | 1 |
| French | 2 |
| Brazilian Portuguese | 3 |