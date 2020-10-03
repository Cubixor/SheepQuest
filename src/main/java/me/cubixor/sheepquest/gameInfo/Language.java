package me.cubixor.sheepquest.gameInfo;

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
    };

    public abstract String getLanguageCode();
}
