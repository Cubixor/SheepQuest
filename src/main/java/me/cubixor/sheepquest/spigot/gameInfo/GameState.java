package me.cubixor.sheepquest.spigot.gameInfo;

import java.io.Serializable;

public enum GameState implements Serializable {
    WAITING {
        @Override
        public String getCode() {
            return "waiting";
        }
    }, STARTING {
        @Override
        public String getCode() {
            return "starting";
        }
    }, GAME {
        @Override
        public String getCode() {
            return "game";
        }
    }, ENDING {
        @Override
        public String getCode() {
            return "ending";
        }
    };

    public abstract String getCode();
}
