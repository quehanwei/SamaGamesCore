package net.samagames.core.api.stats;

import net.samagames.api.player.AbstractPlayerData;
import net.samagames.api.stats.IPlayerStat;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.restfull.RestAPI;
import net.samagames.restfull.request.Request;
import net.samagames.restfull.response.Response;
import net.samagames.restfull.response.StatusResponse;
import net.samagames.restfull.response.ValueResponse;
import net.samagames.restfull.response.elements.LeaderboradElement;
import org.bukkit.Bukkit;

import java.util.UUID;

@Deprecated
/**
 * @deprecated Will be remplaaced as soon as possible with PlayerData
 */
public class PlayerStat implements IPlayerStat
{
    private UUID playerUUID;
    private String game;
    private String stat;
    private ApiImplementation api;
    private double value;
    private Long rank;

    private AbstractPlayerData playerData;

    public PlayerStat(String game, String stat)
    {
        this.game = game;
        this.api = (ApiImplementation) ApiImplementation.get();
        this.stat = stat;
    }

    public PlayerStat(UUID playerUUID, String game, String stat)
    {
        this(game, stat);
        this.playerUUID = playerUUID;
        this.playerData = api.getPlayerManager().getPlayerData(playerUUID);
    }

    public boolean fill()
    {
        Object response = RestAPI.getInstance().sendRequest("player/statistic", new Request().addProperty("playerUUID", playerUUID).addProperty("category", game).addProperty("key", stat), ValueResponse.class, "POST");
        if (response instanceof ValueResponse)
        {
            String newValue = ((ValueResponse) response).getValue();
            if (newValue == null || newValue.isEmpty())
            {
                this.value = 0;
                return false;
            }
            else
                this.value = Integer.parseInt(newValue);
            this.rank = 1L; // TODO: implement Rank into RestAPI
            return true;
        }
        return false;
    }

    public PlayerStat readResponse(LeaderboradElement leaderboradElement)
    {
        this.value = leaderboradElement.getValue();
        this.playerUUID = leaderboradElement.getPlayerUUID();
        return this;
    }

    public UUID getPlayerUUID()
    {
        return this.playerUUID;
    }

    public String getGame()
    {
        return this.game;
    }

    public String getStat()
    {
        return this.stat;
    }

    public long getRank()
    {
        return this.rank;
    }

    public double getValue()
    {
        return this.value;
    }


    public void setValue(double value)
    {
        this.value = value;
    }

    public void send()
    {
        Object response = RestAPI.getInstance().sendRequest("player/statistic", new Request().addProperty("playerUUID", playerUUID).addProperty("category", game).addProperty("key", stat).addProperty("value", value), StatusResponse.class, "PUT");
        boolean isErrored = true;
        if (response instanceof StatusResponse)
            isErrored = !((StatusResponse) response).getStatus();

        if (isErrored)
            Bukkit.getLogger().warning("Cannot set key " + stat + " with value " + value + "for uuid " + playerUUID + " (DEBUG: " + response + ")");
    }
}
