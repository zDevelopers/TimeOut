/*
 * Copyright or © or Copr. AmauryCarrade (2015)
 * 
 * http://amaury.carrade.eu
 * 
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */
package eu.carrade.amaury.TimeOut.players;

import eu.carrade.amaury.TimeOut.Config;
import eu.carrade.amaury.TimeOut.events.PlayerGainsTimeEvent;
import eu.carrade.amaury.TimeOut.events.PlayerLosesTimeEvent;
import eu.carrade.amaury.TimeOut.events.PlayerTimesOutEvent;
import fr.zcraft.quartzlib.components.i18n.I;
import fr.zcraft.quartzlib.tools.PluginLogger;
import fr.zcraft.quartzlib.tools.text.MessageSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.UUID;


public class TimedPlayer
{
    private static final NumberFormat formatter = new DecimalFormat("00");

    /**
     * The player UUID.
     */
    private UUID id;

    /**
     * If {@code true}, this player will collect seconds while killing mobs, players, etc.
     */
    private boolean timeCollected = false;

    /**
     * If {@code true}, the time of this player will consume.
     */
    private boolean timeConsumed = true;

    /**
     * If {@code true}, the time left will be displayed in the player's action bar.
     */
    private boolean timeDisplayed = true;

    /**
     * The seconds left.
     */
    private long timeLeft = 0;


    public TimedPlayer(UUID id)
    {
        this.id = id;
        this.timeLeft = Config.INITIAL_TIME.get();

        PluginLogger.info("Timed player created for {0}. Time left: {1}", id, timeLeft);
    }


    public UUID getPlayerUniqueId()
    {
        return id;
    }

    public Player getPlayer()
    {
        return Bukkit.getPlayer(id);
    }

    public boolean isOnline()
    {
        Player player = getPlayer();
        return player != null && player.isOnline();
    }


    public boolean isTimeCollected()
    {
        return timeCollected;
    }

    public boolean isTimeConsumed()
    {
        return timeConsumed;
    }

    public boolean isTimeDisplayed()
    {
        return timeDisplayed;
    }

    public long getTimeLeft()
    {
        return timeLeft;
    }

    public void setTimeCollected(boolean timeCollected)
    {
        this.timeCollected = timeCollected;
    }

    public void setTimeConsumed(boolean timeConsumed)
    {
        this.timeConsumed = timeConsumed;
    }

    public void setTimeDisplayed(boolean timeDisplayed)
    {
        this.timeDisplayed = timeDisplayed;
    }

    public void setTimeLeft(long timeLeft)
    {
        this.timeLeft = timeLeft;
    }

    /**
     * Gives some time to this player.
     *
     * The application is not guaranteed as this calls a cancellable event.
     *
     * @param seconds The seconds given. If negative, {@link #takeTime(long, String)} will be called.
     * @param reason The reason for this gain. Can be null.
     */
    public void giveTime(long seconds, String reason)
    {
        if (seconds == 0)
        {
            return;
        }
        else if (seconds < 0)
        {
            takeTime(seconds, reason);
            return;
        }


        PlayerGainsTimeEvent event = new PlayerGainsTimeEvent(this, seconds, reason);
        event.call();

        if (!event.isCancelled() && event.getSecondsDiff() != 0)
            updateTime(event.getSecondsDiff());
    }

    /**
     * Takes some time to this player.
     *
     * The application is not guaranteed as this calls a cancellable event.
     *
     * @param seconds The seconds given. May be positive or negative: the absolute value will be subtracted.
     * @param reason The reason for this loss. Can be null.
     */
    public void takeTime(long seconds, String reason)
    {
        if (seconds == 0)
            return;

        else if (seconds > 0)
            seconds = -seconds;


        PlayerLosesTimeEvent event = new PlayerLosesTimeEvent(this, seconds, reason);
        event.call();

        if (!event.isCancelled() && event.getSecondsDiff() != 0)
            updateTime(event.getSecondsDiff());
    }

    /**
     * Adds the given time.
     *
     * @param seconds The seconds to mathematically add (can be negative).
     */
    private void updateTime(long seconds)
    {
        this.timeLeft += seconds;
        checkTimeOut();
    }


    /**
     * To be called every second.
     */
    public void timeFlies()
    {
        if (timeLeft > 0 && timeConsumed)
        {
            timeLeft--;
            displayTime();
            checkTimeOut();

            PluginLogger.info("Removed 1 second from {0}, {1} seconds left", getPlayerUniqueId(), getTimeLeft());
        }
    }

    private void checkTimeOut()
    {
        if (timeLeft == 0)
        {
            new PlayerTimesOutEvent(this).call();

            Player player;
            if (Config.NOTIFY_ON_TIME_OUT.get() && (player = getPlayer()) != null)
            {
                /// Message sent when a player times out.
                player.sendMessage(I.t("{red}{bold}TIME OUT!"));
            }
        }
    }

    private void displayTime()
    {
        Player player;
        if (timeDisplayed && (player = getPlayer()) != null)
        {
            int hours  = (int) Math.floor(timeLeft / 3600);
            int minutes = (int) (Math.floor(timeLeft / 60) - (hours * 60));
            int seconds = (int) (timeLeft - (minutes * 60 + hours * 3600));

            String text;
            if (hours == 0)
                /// Action bar displayed when less than one hour is left.
                text = I.t("{gold}{0}:{1}", formatter.format(minutes), formatter.format(seconds));
            else
                /// Action bar displayed when more than one hour is left.
                text = I.t("{gold}{0}:{1}:{2}", formatter.format(hours), formatter.format(minutes), formatter.format(seconds));

            MessageSender.sendActionBarMessage(player, text);
        }
    }
}
