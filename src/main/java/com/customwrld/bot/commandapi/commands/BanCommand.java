//package com.customwrld.bot.commandapi.commands;
//
//import com.customwrld.bot.commandapi.CommandContext;
//import com.customwrld.bot.commandapi.CommandPermission;
//import com.customwrld.bot.commandapi.ICommand;
//import com.customwrld.bot.profile.Profile;
//import com.customwrld.bot.profile.punishment.Punishment;
//import com.customwrld.bot.profile.punishment.PunishmentType;
//import com.customwrld.bot.util.timer.timers.BanTimer;
//import com.customwrld.bot.util.Duration;
//import com.customwrld.bot.util.Util;
//import com.customwrld.bot.util.enums.EmbedTemplate;
//import net.dv8tion.jda.api.JDA;
//import net.dv8tion.jda.api.Permission;
//import net.dv8tion.jda.api.entities.Guild;
//import net.dv8tion.jda.api.entities.TextChannel;
//import net.dv8tion.jda.api.entities.User;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//public class BanCommand implements ICommand {
//
//    @Override
//    public CommandPermission permission() {
//        return new CommandPermission(CommandPermission.Type.ROLE, Util.ADMIN_ROLE, Util.OWNER_ROLE);
//    }
//
//    @Override
//    public String commandName() {
//        return "ban";
//    }
//
//    @Override
//    public String usage() {
//        return this.commandName() + " [user] [duration] [reason]";
//    }
//
//    @Override
//    public void handle(CommandContext ctx) {
//        Guild guild = ctx.getGuild();
//        JDA jda = ctx.getJDA();
//        TextChannel channel = ctx.getChannel();
//        User author = ctx.getAuthor();
//        List<String> args = ctx.getArgs();
//
//        String userId = Util.getId(args.get(0));
//
//        jda.retrieveUserById(userId).queue(user -> {
//            Duration duration = Duration.fromString(args.get(1));
//
//            if (duration.getValue() == -1) {
//                EmbedTemplate.DURATION_EXCEPTION.send(channel, author);
//                return;
//            }
//
//            List<String> reasonList = new ArrayList<>(args);
//            reasonList.remove(0);
//            reasonList.remove(0);
//
//            String reason = String.join(" ", reasonList);
//
//            Profile profile = new Profile(userId);
//
//            Punishment activeBan = profile.getActivePunishmentByType(PunishmentType.BAN);
//
//            if (activeBan != null) {
//                if (!activeBan.isRemoved()) {
//                    activeBan.setRemoved(true);
//                    activeBan.setRemovedReason("Punishment Removed");
//                    activeBan.setRemovedAt(System.currentTimeMillis());
//                    activeBan.setRemovedBy(author.getId());
//                    profile.save();
//                }
//                return;
//            }
//
//            Punishment punishment = new Punishment(UUID.randomUUID(), PunishmentType.BAN, System.currentTimeMillis(), reason, duration.getValue());
//
//            punishment.setAddedBy(author.getId());
//            profile.getPunishments().add(punishment);
//            profile.save();
//
//            guild.retrieveMember(user).queue(member -> guild.retrieveMember(jda.getSelfUser()).queue(selfMember -> {
//                if (selfMember.canInteract(member)) {
//                    guild.ban(member, 7, reason).queue(success -> {
//                        punishment.sendEmbed(user, channel);
//                        if (!punishment.isPermanent()) {
//                            new BanTimer(profile, punishment).start();
//                        }
//                    });
//                } else {
//                    Util.sendEmbed(channel, member.getUser(), "**Permissions Exception**", "You do not have permission to ban this user.");
//                }
//            }), (error) -> guild.ban(user, 7, reason).queue(success -> {
//                punishment.sendEmbed(user, channel);
//                if (!punishment.isPermanent()) {
//                    new BanTimer(profile, punishment).start();
//                }
//            }));
//        }, (error) -> EmbedTemplate.USER_EXCEPTION.send(channel, author));
//    }
//}
