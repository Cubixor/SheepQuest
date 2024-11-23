package me.cubixor.sheepquest.game.events;

import com.google.common.collect.ImmutableMap;
import me.cubixor.minigamesapi.spigot.events.GameChatEvent;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.sheepquest.arena.SQArena;
import me.cubixor.sheepquest.arena.Team;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatManager implements Listener {

    @EventHandler
    public void onChat(GameChatEvent evt) {
        SQArena arena = (SQArena) evt.getLocalArena();
        Team team = arena.getPlayerTeam().get(evt.getPlayer());

        String parsedMessage = Messages.get("game.chat-format", ImmutableMap.of(
                "%color%", team.getChatColor().toString(),
                "%player%", evt.getPlayer().getName(),
                "%message%", evt.getMessage()
        ));

        evt.setMessage(parsedMessage);
    }
}
