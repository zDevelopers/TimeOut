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
package eu.carrade.amaury.TimeOut.events;

import eu.carrade.amaury.TimeOut.events.core.CancellableEvent;
import eu.carrade.amaury.TimeOut.players.TimedPlayer;
import org.bukkit.event.HandlerList;


/**
 * Called when a player time change because of something (not because the time flies).
 */
public class PlayerChangesTimeEvent extends CancellableEvent
{
    private static HandlerList handlerList = new HandlerList();

    private TimedPlayer player;
    private long secondsDiff;
    private String reason;

    public PlayerChangesTimeEvent(TimedPlayer player, long secondsDiff, String reason)
    {
        this.player = player;
        this.secondsDiff = secondsDiff;
        this.reason = reason;
    }


    public TimedPlayer getPlayer()
    {
        return player;
    }

    /**
     * @return {@code true} if this event represents a time gain.
     */
    public boolean isGain()
    {
        return secondsDiff >= 0;
    }

    public long getSecondsDiff()
    {
        return secondsDiff;
    }

    public void setSecondsDiff(long secondsDiff)
    {
        this.secondsDiff = secondsDiff;
    }


    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }


    @Override
    public HandlerList getHandlers()
    {
        return handlerList;
    }

    public static HandlerList getHandlerList()
    {
        return handlerList;
    }
}
