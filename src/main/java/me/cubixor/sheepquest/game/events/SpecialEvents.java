package me.cubixor.sheepquest.game.events;

import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.gameInfo.Arena;
import me.cubixor.sheepquest.gameInfo.GameState;
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
            Arena arena = plugin.getArenas().get(arenaString);
            arena.setSpecialEventsData(new SpecialEventsData());

            arena.getSpecialEventsData().setSpecialEventsTimer(setRate());

            runEventTimer(arenaString);
        }
    }

    public void reset(Arena arena) {
        for (SpecialEvent event : SpecialEvent.values()) {
            if (event.isEnabled()) {
                event.runReset(arena);
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
                Arena arena = plugin.getArenas().get(arenaString);
                if (arena == null || !arena.getState().equals(GameState.GAME)) {
                    this.cancel();
                    return;
                }

                if (arena.getSpecialEventsData().getSpecialEventsTimer() == 0) {
                    Random random = new Random();

                    LinkedList<SpecialEvent> specialEvents = new LinkedList<>();
                    for (SpecialEvent evt : SpecialEvent.values()) {
                        if (evt.isEnabled()) {
                            specialEvents.add(evt);
                        }
                    }

                    int evtId = random.nextInt(specialEvents.size());
                    SpecialEvent event = specialEvents.get(evtId);
                    event.runEvent(arena);

                    arena.getSpecialEventsData().setSpecialEventsTimer(setRate());
                } else {
                    arena.getSpecialEventsData().setSpecialEventsTimer(arena.getSpecialEventsData().getSpecialEventsTimer() - 1);
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }


}
