/*
 * (C) Copyright 2018 The DKBans Project (Davide Wietlisbach)
 *
 * @author Davide Wietlisbach
 * @since 30.12.18 14:39
 * @Website https://github.com/DevKrieger/DKBans
 *
 * The DKBans Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package ch.dkrieger.bansystem.bukkit.event;

import ch.dkrieger.bansystem.lib.player.NetworkPlayer;

import java.util.UUID;

public class BukkitNetworkPlayerColorSetEvent extends BukkitDKBansEvent {

    private final NetworkPlayer player;
    private String color;

    public BukkitNetworkPlayerColorSetEvent(UUID uuid, long timeStamp, boolean onThisServer, NetworkPlayer player, String color) {
        super(uuid, timeStamp, onThisServer);
        this.player = player;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public NetworkPlayer getPlayer() {
        return player;
    }
}
