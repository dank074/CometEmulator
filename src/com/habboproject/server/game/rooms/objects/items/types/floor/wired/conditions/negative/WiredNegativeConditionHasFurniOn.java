package com.habboproject.server.game.rooms.objects.items.types.floor.wired.conditions.negative;

import com.habboproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive.WiredConditionHasFurniOn;
import com.habboproject.server.game.rooms.types.Room;


public class WiredNegativeConditionHasFurniOn extends WiredConditionHasFurniOn {
    public WiredNegativeConditionHasFurniOn(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
        this.isNegative = true;
    }
}
