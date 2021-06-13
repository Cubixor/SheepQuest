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
    };

    public abstract String getLanguageCode();
}
