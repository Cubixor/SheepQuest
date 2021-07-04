package me.cubixor.sheepquest.spigot.config;

public enum ConfigField {
    SERVER {
        @Override
        public String getCode() {
            return "server";
        }

        @Override
        public boolean savedInDatabase() {
            return true;
        }
    },
    ACTIVE {
        @Override
        public String getCode() {
            return "active";
        }

        @Override
        public boolean savedInDatabase() {
            return true;
        }
    },
    VIP {
        @Override
        public String getCode() {
            return "vip";
        }

        @Override
        public boolean savedInDatabase() {
            return true;
        }
    },
    MIN_PLAYERS {
        @Override
        public String getCode() {
            return "min-players";
        }

        @Override
        public boolean savedInDatabase() {
            return true;
        }
    },
    MAX_PLAYERS {
        @Override
        public String getCode() {
            return "max-players";
        }

        @Override
        public boolean savedInDatabase() {
            return true;
        }
    },
    TEAMS {
        @Override
        public String getCode() {
            return "teams";
        }

        @Override
        public boolean savedInDatabase() {
            return false;
        }
    },
    MAIN_LOBBY {
        @Override
        public String getCode() {
            return "main-lobby";
        }

        @Override
        public boolean savedInDatabase() {
            return false;
        }
    },
    WAITING_LOBBY {
        @Override
        public String getCode() {
            return "waiting-lobby";
        }

        @Override
        public boolean savedInDatabase() {
            return false;
        }
    },
    SHEEP_SPAWN {
        @Override
        public String getCode() {
            return "sheep-spawn";
        }

        @Override
        public boolean savedInDatabase() {
            return false;
        }

    },
    RED_SPAWN {
        @Override
        public String getCode() {
            return "red-spawn";
        }

        @Override
        public boolean savedInDatabase() {
            return false;
        }

    },
    GREEN_SPAWN {
        @Override
        public String getCode() {
            return "green-spawn";
        }

        @Override
        public boolean savedInDatabase() {
            return false;
        }

    },
    BLUE_SPAWN {
        @Override
        public String getCode() {
            return "blue-spawn";
        }

        @Override
        public boolean savedInDatabase() {
            return false;
        }

    },
    YELLOW_SPAWN {
        @Override
        public String getCode() {
            return "yellow-spawn";
        }

        @Override
        public boolean savedInDatabase() {
            return false;
        }

    },
    RED_AREA {
        @Override
        public String getCode() {
            return "red-area";
        }

        @Override
        public boolean savedInDatabase() {
            return false;
        }

    },
    GREEN_AREA {
        @Override
        public String getCode() {
            return "green-area";
        }

        @Override
        public boolean savedInDatabase() {
            return false;
        }

    },
    BLUE_AREA {
        @Override
        public String getCode() {
            return "blue-area";
        }

        @Override
        public boolean savedInDatabase() {
            return false;
        }

    },
    YELLOW_AREA {
        @Override
        public String getCode() {
            return "yellow-area";
        }

        @Override
        public boolean savedInDatabase() {
            return false;
        }

    };

    public abstract String getCode();

    public abstract boolean savedInDatabase();
}
