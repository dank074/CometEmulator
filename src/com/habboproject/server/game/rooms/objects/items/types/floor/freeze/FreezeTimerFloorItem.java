package com.habboproject.server.game.rooms.objects.items.types.floor.freeze;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.components.games.GameType;
import com.habboproject.server.game.rooms.types.components.games.freeze.FreezeGame;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by brend on 04/02/2017.
 */
public class FreezeTimerFloorItem extends RoomItemFloor {
    private String lastTime;

    private boolean interrupted = false;
    private boolean running = false;

    public FreezeTimerFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (!isWiredTrigger) {
            if (!(entity instanceof PlayerEntity)) {
                return false;
            }

            PlayerEntity pEntity = (PlayerEntity) entity;
            if (!pEntity.getRoom().getRights().hasRights(pEntity.getPlayerId()) && !pEntity.getPlayer().getPermissions().getRank().roomFullControl()) {
                return true;
            }
        }

        if (this.getExtraData().isEmpty() || Integer.parseInt(this.getExtraData()) < 0) {
            this.setExtraData("30");
        }

        if (isWiredTrigger) {
            return this.trigger(requestData);
        }

        if (requestData == 3) {
            if (this.getRoom().getGame().getInstance() != null && this.getRoom().getGame().getInstance() instanceof FreezeGame) {
                this.running = false;
                this.getRoom().getGame().getInstance().onGameEnds();
                this.getRoom().getGame().stop();
            }
        } else if (requestData == 2) {
            if (this.getRoom().getGame().getInstance() != null && this.getRoom().getGame().getInstance() instanceof FreezeGame) {
                this.running = false;
                this.getRoom().getGame().getInstance().onGameEnds();
                this.getRoom().getGame().stop();
            }

            int time = 0;

            if (!this.getExtraData().isEmpty() && StringUtils.isNumeric(this.getExtraData())) {
                time = Integer.parseInt(this.getExtraData());
            }

            switch (time) {
                case 0: {
                    time = 30;
                    break;
                }

                case 30: {
                    time = 60;
                    break;
                }

                case 60: {
                    time = 120;
                    break;
                }

                case 120: {
                    time = 180;
                    break;
                }

                case 180: {
                    time = 300;
                    break;
                }

                case 300: {
                    time = 600;
                    break;
                }

                default: {
                    time = 30;
                }
            }

            if (time < 0) {
                time = 0;
            }

            this.setExtraData(String.valueOf(time));
            this.sendUpdate();
            this.saveData();
        } else if (requestData == 1) {
            if (this.getRoom().getGame().getInstance() != null && this.getRoom().getGame().getInstance() instanceof FreezeGame) {
                this.running = false;
                this.getRoom().getGame().getInstance().onGameEnds();
                this.getRoom().getGame().stop();
            }

            if (this.getExtraData().equals("0") && this.lastTime != null && !this.lastTime.isEmpty() && Integer.parseInt(this.lastTime) > 0) {
                this.setExtraData(this.lastTime);
            }

            int gameLength = Integer.parseInt(this.getExtraData());
            this.lastTime = this.getExtraData();

            if (gameLength == 0) {
                return true;
            }

            if (this.getRoom().getGame().getInstance() == null) {
                this.getRoom().getGame().createNew(GameType.FREEZE);
                this.getRoom().getGame().getInstance().startTimer(gameLength);
            } else if (this.getRoom().getGame().getInstance() instanceof FreezeGame) {
                this.running = false;
                this.getRoom().getGame().getInstance().onGameEnds();
                this.getRoom().getGame().stop();
            }
        }

        return true;
    }

    public boolean trigger(int requestData) {
        if (requestData == -1) {
            this.getRoom().getGame().getInstance().onGameEnds();
            this.getRoom().getGame().stop();
            this.running = false;
            return false;
        }
        if (this.interrupted && requestData != -2) {
            if (requestData == 0) {
                if (this.getRoom().getGame().getInstance() != null && this.getRoom().getGame().getInstance() instanceof FreezeGame) {
                    this.running = false;
                    this.getRoom().getGame().getInstance().onGameEnds();
                    this.getRoom().getGame().stop();
                }
                if (this.getExtraData().equals("0") && this.lastTime != null && !this.lastTime.isEmpty() && Integer.parseInt(this.lastTime) > 0) {
                    this.setExtraData(this.lastTime);
                }
                int gameLength = Integer.parseInt(this.getExtraData());
                this.lastTime = this.getExtraData();
                if (gameLength == 0) {
                    gameLength = 30;
                    this.lastTime = "30";
                }
                if (this.getRoom().getGame().getInstance() == null) {
                    this.getRoom().getGame().createNew(GameType.FREEZE);
                    this.getRoom().getGame().getInstance().startTimer(gameLength);
                }
                return false;
            }
            if (this.getRoom().getGame().getInstance() == null) {
                this.getRoom().getGame().createNew(GameType.FREEZE);
                this.getRoom().getGame().getInstance().startTimer(requestData);
            }
            return false;
        }
        if (this.running) {
            if (this.getRoom().getGame().getInstance() == null) {
                this.running = false;
                return false;
            }
            this.running = false;
            this.getRoom().getGame().getInstance().onGameEnds();
            this.getRoom().getGame().stop();
        } else {
            if (this.getRoom().getGame().getInstance() != null && this.getRoom().getGame().getInstance() instanceof FreezeGame) {
                this.running = false;
                this.getRoom().getGame().getInstance().onGameEnds();
                this.getRoom().getGame().stop();
            }
            if (this.getExtraData().equals("0") && this.lastTime != null && !this.lastTime.isEmpty() && Integer.parseInt(this.lastTime) > 0) {
                this.setExtraData(this.lastTime);
            }
            int gameLength = Integer.parseInt(this.getExtraData());
            this.lastTime = this.getExtraData();
            if (gameLength == 0) {
                gameLength = 30;
                this.lastTime = "30";
            }
            if (this.getRoom().getGame().getInstance() == null) {
                this.getRoom().getGame().createNew(GameType.FREEZE);
                this.getRoom().getGame().getInstance().startTimer(requestData == 0 || requestData == -2 ? gameLength : requestData);
            }
        }
        return true;
    }

    @Override
    public void onPlaced() {
        this.setExtraData("30");
    }

    public boolean isInterrupted() {
        return this.interrupted;
    }

    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
