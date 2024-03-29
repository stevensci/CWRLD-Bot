package com.customwrld.bot.timers;

import com.customwrld.bot.Bot;
import com.customwrld.bot.util.config.Config;
import com.customwrld.bot.util.Giveaway;
import com.customwrld.bot.util.Logger;
import com.customwrld.commonlib.CommonLib;
import com.customwrld.commonlib.util.TimeUtil;
import com.customwrld.commonlib.util.timer.Timer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.Instant;

public class GiveawayTimer extends Timer {

    private final Giveaway giveaway;
    private Config config;
    private Guild guild;

    public GiveawayTimer(Giveaway giveaway) {
        super(TimerType.COUNTDOWN, CommonLib.getCommonLib().getTimerManager());
        setDuration(giveaway.getEnds() - System.currentTimeMillis());
        this.giveaway = giveaway;
    }

    int i;

    @Override
    public void tick() {
        i++;
        if(i >= 5) {
            i = 0;
            TextChannel channel = guild.getTextChannelById(giveaway.getChannelId());

            if(channel != null) {
                channel.retrieveMessageById(giveaway.getMessageId()).queue(message -> message.editMessage(new EmbedBuilder()
                        .setColor(config.getBotColor())
                        .setTitle(giveaway.getPrize())
                        .setDescription("React with :tada: to enter!")
                        .addField("Time Remaining", TimeUtil.millisToRoundedTime(giveaway.getEnds() - System.currentTimeMillis()), false)
                        .setFooter("Ends at")
                        .setTimestamp(Instant.ofEpochMilli(giveaway.getEnds()))
                        .build()).queue(), (error) -> this.stop());
            }
        }
    }

    @Override
    public void onStart() {
        Bot bot = Bot.getBot();
        config = bot.getConfig();
        guild = bot.getGuild();
    }

    @Override
    public void onComplete() {
        TextChannel channel = guild.getTextChannelById(giveaway.getChannelId());

        if(channel != null) {
            channel.retrieveMessageById(giveaway.getMessageId()).queue(giveaway::drawWinner, (error) -> Logger.log(Logger.LogType.WARNING, "Giveaway was marked as NULL."));
        }
    }

    @Override
    public void onCancel() {}

}
