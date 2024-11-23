package me.cubixor.sheepquest;

import me.cubixor.minigamesapi.proxy.bungee.MinigamesAPIBungee;
import me.cubixor.minigamesapi.proxy.velocity.MinigamesAPIVelocity;

public class IncludeClasses {

    /*
     * This block prevents the Maven Shade plugin to remove the specified classes
     */
    static {
        @SuppressWarnings("unused") Class<?>[] classes = new Class<?>[]{
                MinigamesAPIBungee.class,
                MinigamesAPIVelocity.class,
                Main.class
        };
    }

    private IncludeClasses() {
    }
}
