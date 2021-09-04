package me.cubixor.sheepquest.spigot.gameInfo;

public enum Language {
    EN {
        @Override
        public String getLanguageCode() {
            return "en";
        }
    },
    ZH {
        @Override
        public String getLanguageCode() {
            return "zh";
        }
    },
    PL {
        @Override
        public String getLanguageCode() {
            return "pl";
        }
    },
    RU {
        @Override
        public String getLanguageCode() {
            return "ru";
        }
    };

    public abstract String getLanguageCode();
}
