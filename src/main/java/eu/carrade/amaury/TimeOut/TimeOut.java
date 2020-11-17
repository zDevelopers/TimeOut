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
package eu.carrade.amaury.TimeOut;

import eu.carrade.amaury.TimeOut.listeners.ServerConnectionsListener;
import eu.carrade.amaury.TimeOut.listeners.TimesListener;
import eu.carrade.amaury.TimeOut.players.TimedPlayer;
import fr.zcraft.quartzlib.core.QuartzPlugin;
import fr.zcraft.quartzlib.components.configuration.Configuration;
import fr.zcraft.quartzlib.components.i18n.I18n;
import fr.zcraft.quartzlib.core.QuartzPlugin;
import fr.zcraft.quartzlib.tools.runners.RunTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class TimeOut extends QuartzPlugin
{
    private static TimeOut INSTANCE;

    private Map<UUID, TimedPlayer> players = new ConcurrentHashMap<>();


    @Override
    public void onEnable()
    {
        INSTANCE = this;

        loadComponents(I18n.class);
        Configuration.init(Config.class);

        getServer().getPluginManager().registerEvents(new ServerConnectionsListener(), this);
        getServer().getPluginManager().registerEvents(new TimesListener(), this);

        // Makes the time flies
        RunTask.timer(() -> players.forEach(((uuid, timedPlayer) -> timedPlayer.timeFlies())), 20l, 20l);
    }


    /**
     * @param id A player UUID
     * @return A data object for this player. Created on-the-fly if needed: this never returns {@code null}.
     */
    public TimedPlayer getTimedPlayer(UUID id)
    {
        TimedPlayer player = players.get(id);

        if (player == null)
        {
            player = new TimedPlayer(id);
            players.put(id, player);
        }

        return player;
    }



    /* **  STATIC API  ** */

    public static TimeOut get()
    {
        return INSTANCE;
    }

    /**
     * @see #getTimedPlayer(UUID)
     */
    public static TimedPlayer getPlayer(UUID id)
    {
        return get().getTimedPlayer(id);
    }
}
