package me.cubixor.sheepquest.game.inventories;

import me.cubixor.minigamesapi.spigot.game.arena.LocalArena;
import me.cubixor.minigamesapi.spigot.game.inventories.Menu;
import me.cubixor.minigamesapi.spigot.game.inventories.MenuRegistry;
import me.cubixor.sheepquest.game.BossBarManager;

public class SQMenuRegistry extends MenuRegistry {

    private final Menu teamsMenu;
    private final Menu kitsMenu;

    public SQMenuRegistry(LocalArena arena, BossBarManager bossBarManager) {
        teamsMenu = new TeamsMenu(arena, bossBarManager);
        kitsMenu = new KitsMenu(arena, bossBarManager);

        getMenus().add(teamsMenu);
        getMenus().add(kitsMenu);
    }

    public Menu getTeamsMenu() {
        return teamsMenu;
    }

    public Menu getKitsMenu() {
        return kitsMenu;
    }
}
