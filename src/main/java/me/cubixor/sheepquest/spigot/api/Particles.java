package me.cubixor.sheepquest.spigot.api;

import com.cryptomorin.xseries.particles.XParticle;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import org.bukkit.Location;
import org.inventivetalent.particle.ParticleEffect;

public class Particles {

    public static void spawnParticle(LocalArena localArena, Location loc, String path) {
        SheepQuest plugin = SheepQuest.getInstance();

        if (!plugin.getConfig().getBoolean("particles." + path + ".enabled")) {
            return;
        }

        String particleString = plugin.getConfig().getString("particles." + path + ".particle");
        int count = plugin.getConfig().getInt("particles." + path + ".count");
        float offset = (float) plugin.getConfig().getDouble("particles." + path + ".offset");
        float speed = (float) plugin.getConfig().getDouble("particles." + path + ".speed");

        if (VersionUtils.is1_8()) {
            ParticleEffect.valueOf(particleString).send(localArena.getPlayerTeam().keySet(), loc, offset, offset, offset, speed, count);
        } else {
            loc.getWorld().spawnParticle(XParticle.getParticle(particleString), loc, count, offset, offset, offset, speed);
        }
    }
}
