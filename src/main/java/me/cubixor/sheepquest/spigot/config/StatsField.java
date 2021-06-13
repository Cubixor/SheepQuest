package me.cubixor.sheepquest.spigot.config;

public enum StatsField {
    SHEEP_TAKEN {
        @Override
        public String getCode() {
            return "sheep-taken";
        }
    }, DEATHS {
        @Override
        public String getCode() {
            return "deaths";
        }
    }, KILLS {
        @Override
        public String getCode() {
            return "kills";
        }
    }, GAMES_PLAYED {
        @Override
        public String getCode() {
            return "games-played";
        }
    }, WINS {
        @Override
        public String getCode() {
            return "wins";
        }
    }, LOOSES {
        @Override
        public String getCode() {
            return "looses";
        }
    }, BONUS_SHEEP_TAKEN {
        @Override
        public String getCode() {
            return "bonus-sheep-taken";
        }
    }, PLAYTIME {
        @Override
        public String getCode() {
            return "playtime";
        }
    };

    public abstract String getCode();
}
