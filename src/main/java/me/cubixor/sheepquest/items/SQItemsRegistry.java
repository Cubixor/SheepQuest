package me.cubixor.sheepquest.items;

import com.google.common.collect.ImmutableSet;
import me.cubixor.minigamesapi.spigot.game.items.ClickableItem;
import me.cubixor.minigamesapi.spigot.game.items.ItemsRegistry;

import java.util.Set;

public class SQItemsRegistry extends ItemsRegistry {

    private final ClickableItem teamItem;
    private final ClickableItem kitsItem;
    private final ClickableItem sheepItem;


    public SQItemsRegistry() {
        teamItem = new TeamItem();
        kitsItem = new KitsItem();
        sheepItem = new SheepItem();
    }

    public ClickableItem getTeamItem() {
        return teamItem;
    }

    public ClickableItem getKitsItem() {
        return kitsItem;
    }

    public ClickableItem getSheepItem() {
        return sheepItem;
    }

    @Override
    public Set<ClickableItem> getClickableItems() {
        return ImmutableSet.of(
                getLeaveItem(),
                getTeamItem(),
                getKitsItem(),
                getSheepItem());
    }
}
