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
package eu.carrade.amaury.TimeOut.listeners;

import eu.carrade.amaury.TimeOut.Config;
import eu.carrade.amaury.TimeOut.events.PlayerGainsTimeEvent;
import eu.carrade.amaury.TimeOut.events.PlayerLosesTimeEvent;
import eu.carrade.amaury.TimeOut.events.PlayerTimesOutEvent;
import eu.carrade.amaury.TimeOut.utils.ChatUtils;
import fr.zcraft.quartzlib.components.i18n.I;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;


public class TimesListener implements Listener
{
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerGainsTime(PlayerGainsTimeEvent ev)
    {
        Player player;
        if (Config.NOTIFY_ON_GAIN.get() && ev.getSecondsDiff() != 0 && (player = ev.getPlayer().getPlayer()) != null)
        {
            String reason = ev.getReason();
            if (reason == null || reason.isEmpty())
                /// Default gain/loss reason displayed if no reason was given
                reason = I.t("no reason");

            /// Message sent when a player gains some time. {0} = seconds gained, {1} = reason.
            player.sendMessage(I.tn("{green}+{0} second {gray}({1})", "{green}+{0} seconds {gray}({1})", (int) ev.getSecondsDiff(), reason));
        }
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLosesTime(PlayerLosesTimeEvent ev)
    {
        Player player;
        if (Config.NOTIFY_ON_LOSS.get() && ev.getSecondsDiff() != 0 && (player = ev.getPlayer().getPlayer()) != null)
        {
            String reason = ev.getReason();
            if (reason == null || reason.isEmpty())
                /// Default gain/loss reason displayed if no reason was given
                reason = I.t("no reason");

            /// Message sent when a player loses some time. {0} = seconds lost, {1} = reason.
            player.sendMessage(I.tn("{red}-{0} second {gray}({1})", "{green}+{0} seconds {gray}({1})", (int) ev.getSecondsDiff(), reason));
        }
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTimesOut(PlayerTimesOutEvent ev)
    {
        Player player;
        if ((player = ev.getPlayer().getPlayer()) != null)
        {
            if (Config.NOTIFY_ON_TIME_OUT.get())
                /// Message sent when a player times out.
                player.sendMessage(I.t("{red}{bold}TIME OUT!"));

            if (Config.BROADCAST_ON_TIME_OUT.get())
                /// Message broadcasted when a player times out. {0} = player display name (may contains colors).
                ChatUtils.broadcastExcepted(I.t("{red}{0}{red} timed out.", player.getDisplayName()), ev.getPlayer().getPlayerUniqueId());
        }
    }
}
