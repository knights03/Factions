package com.massivecraft.factions.integration;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;

import com.earth2me.essentials.chat.EssentialsChat;
import com.earth2me.essentials.chat.EssentialsLocalChatEvent;


/*
 * This Essentials integration handler is for newer 3.x.x versions of Essentials which don't have "IEssentialsChatListener"
 */

public class EssentialsFeatures
{
	private static EssentialsChat essChat;

	public static void integrateChat(EssentialsChat instance)
	{
		essChat = instance;
		try
		{
			Bukkit.getServer().getPluginManager().registerEvents(new LocalChatListener(), P.p);
			P.p.log("Found and will integrate chat with newer "+essChat.getDescription().getFullName());

			// curly braces used to be accepted by the format string EssentialsChat but no longer are, so... deal with chatTagReplaceString which might need updating
			if (Conf.chatTagReplaceString.contains("{"))
			{
				Conf.chatTagReplaceString = Conf.chatTagReplaceString.replace("{", "[").replace("}", "]");
				P.p.log("NOTE: as of Essentials 2.8+, we've had to switch the default chat replacement tag from \"{FACTION}\" to \"[FACTION]\". This has automatically been updated for you.");
			}
		}
		catch (NoSuchMethodError ex)
		{
			essChat = null;
		}
	}

	private static class LocalChatListener implements Listener
	{
		@EventHandler(priority = EventPriority.NORMAL)
		public void onPlayerChat(EssentialsLocalChatEvent event)
		{
			Player speaker = event.getPlayer();
			String format = event.getFormat();
			format = format.replace(Conf.chatTagReplaceString, P.p.getPlayerFactionTag(speaker)).replace("[FACTION_TITLE]", P.p.getPlayerTitle(speaker));
			event.setFormat(format);
			// NOTE: above doesn't do relation coloring. if/when we can get a local recipient list from EssentialsLocalChatEvent, we'll probably
			// want to pass it on to FactionsPlayerListener.onPlayerChat(PlayerChatEvent event) rather than duplicating code
		}
	}
}
