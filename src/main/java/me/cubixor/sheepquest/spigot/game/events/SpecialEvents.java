package me.cubixor.sheepquest.spigot.game.events;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.gameInfo.GameState;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.Random;

public class SpecialEvents {

    private final SheepQuest plugin;

    public SpecialEvents() {
        plugin = SheepQuest.getInstance();
    }

    public void setupSpecialEvents(String arenaString) {
        if (plugin.getConfig().getBoolean("special-events.enabled")) {
            LocalArena localArena = plugin.getLocalArenas().get(arenaString);
            localArena.setSpecialEventsData(new SpecialEventsData());

            localArena.getSpecialEventsData().setSpecialEventsTimer(setRate());

            runEventTimer(arenaString);
        }
    }

    public void reset(LocalArena localArena) {
        for (SpecialEvent event : SpecialEvent.values()) {
            if (event.isEnabled()) {
                event.runReset(localArena);
            }
        }
    }

    private int setRate() {
        String[] rateString = plugin.getConfig().getString("special-events.rate").split("-");
        int rate1 = Integer.parseInt(rateString[0]);
        int rate2 = Integer.parseInt(rateString[1]);

        Random random = new Random();

        return random.nextInt(rate2 - rate1 + 1) + rate1;
    }

    private void runEventTimer(String arenaString) {
        new BukkitRunnable() {
            @Override
            public void run() {
                LocalArena localArena = plugin.getLocalArenas().get(arenaString);
                if (localArena == null || !localArena.getState().equals(GameState.GAME)) {
                    this.cancel();
                    return;
                }

                if (localArena.getSpecialEventsData().getSpecialEventsTimer() == 0) {
                    Random random = new Random();

                    LinkedList<SpecialEvent> specialEvents = new LinkedList<>();
                    for (SpecialEvent evt : SpecialEvent.values()) {
                        if (evt.isEnabled()) {
                            specialEvents.add(evt);
                        }
                    }

                    int evtId = random.nextInt(specialEvents.size());
                    SpecialEvent event = specialEvents.get(evtId);
                    event.runEvent(localArena);

                    localArena.getSpecialEventsData().setSpecialEventsTimer(setRate());
                } else {
                    localArena.getSpecialEventsData().setSpecialEventsTimer(localArena.getSpecialEventsData().getSpecialEventsTimer() - 1);
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }


}
