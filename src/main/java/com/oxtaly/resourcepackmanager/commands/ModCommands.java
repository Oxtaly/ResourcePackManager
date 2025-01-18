package com.oxtaly.resourcepackmanager.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.oxtaly.resourcepackmanager.api.ResourcePackManagerAPI;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import com.oxtaly.resourcepackmanager.ResourcePackManager;
import com.oxtaly.resourcepackmanager.config.ConfigData;
import com.oxtaly.resourcepackmanager.utils.Utils;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public final class ModCommands {
    private ModCommands() {}
// /resourcepackmanager computehash
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            final LiteralCommandNode<ServerCommandSource> node = dispatcher.register(
                CommandManager.literal("resourcepackmanager")
                        .requires(source -> Permissions.check(source, "resourcepackmanager.command.resourcepackmanager.base", 2))
                        .then(CommandManager.literal("reload")
                                .requires(source -> Permissions.check(source, "resourcepackmanager.command.resourcepackmanager.reload", 4))
                                .executes(ctx -> reloadConfig(ctx.getSource()))
                        )
                        .then(CommandManager.literal("recomputesha1")
                                .requires(source -> Permissions.check(source, "resourcepackmanager.command.resourcepackmanager.recomputesha1", 2))
                                .executes((ctx) -> recomputeSHA1(ctx.getSource()))
                        )
                        .then(CommandManager.literal("resend")
                                .requires(source -> Permissions.check(source, "resourcepackmanager.command.resourcepackmanager.resend", 2))
                                .executes((ctx) -> resendPack(ctx.getSource(), ctx.getSource().getServer().getPlayerManager().getPlayerList()))
                                .then(CommandManager.argument("players", EntityArgumentType.players())
                                        .executes((ctx) -> resendPack(ctx.getSource(), EntityArgumentType.getPlayers(ctx,"players")))
                                )
                        )
                        .then(CommandManager.literal("sendpack")
                                .requires(source -> Permissions.check(source, "resourcepackmanager.command.resourcepackmanager.sendpack", 2))
                                .then(CommandManager.argument("players", EntityArgumentType.players())
                                        .then(CommandManager.argument("URL", StringArgumentType.string())
                                                .executes((ctx) -> sendResourcePack(
                                                        ctx.getSource(),
                                                        EntityArgumentType.getPlayers(ctx,"players"),
                                                        StringArgumentType.getString(ctx, "URL"),
                                                        null,
                                                        null,
                                                        null,
                                                        null,
                                                        null
                                                ))
                                                .then(CommandManager.argument("UUID", StringArgumentType.string())
                                                        .executes((ctx) -> sendResourcePack(
                                                                ctx.getSource(),
                                                                EntityArgumentType.getPlayers(ctx,"players"),
                                                                StringArgumentType.getString(ctx, "URL"),
                                                                StringArgumentType.getString(ctx, "UUID"),
                                                                null,
                                                                null,
                                                                null,
                                                                null
                                                        ))
                                                        .then(CommandManager.argument("SHA1", StringArgumentType.string())
                                                                .executes((ctx) -> sendResourcePack(
                                                                        ctx.getSource(),
                                                                        EntityArgumentType.getPlayers(ctx,"players"),
                                                                        StringArgumentType.getString(ctx, "URL"),
                                                                        StringArgumentType.getString(ctx, "UUID"),
                                                                        StringArgumentType.getString(ctx, "SHA1"),
                                                                        null,
                                                                        null,
                                                                        null
                                                                ))
                                                                .then(CommandManager.argument("Prompt", StringArgumentType.string())
                                                                        .executes((ctx) -> sendResourcePack(
                                                                                ctx.getSource(),
                                                                                EntityArgumentType.getPlayers(ctx,"players"),
                                                                                StringArgumentType.getString(ctx, "URL"),
                                                                                StringArgumentType.getString(ctx, "UUID"),
                                                                                StringArgumentType.getString(ctx, "SHA1"),
                                                                                StringArgumentType.getString(ctx, "Prompt"),
                                                                                null,
                                                                                null
                                                                        ))
                                                                        .then(CommandManager.argument("Forced", BoolArgumentType.bool())
                                                                                .executes((ctx) -> sendResourcePack(
                                                                                        ctx.getSource(),
                                                                                        EntityArgumentType.getPlayers(ctx,"players"),
                                                                                        StringArgumentType.getString(ctx, "URL"),
                                                                                        StringArgumentType.getString(ctx, "UUID"),
                                                                                        StringArgumentType.getString(ctx, "SHA1"),
                                                                                        StringArgumentType.getString(ctx, "Prompt"),
                                                                                        BoolArgumentType.getBool(ctx, "Forced"),
                                                                                        null
                                                                                ))
                                                                                .then(CommandManager.argument("ComputeSHA1", BoolArgumentType.bool())
                                                                                        .executes((ctx) -> sendResourcePack(
                                                                                                ctx.getSource(),
                                                                                                EntityArgumentType.getPlayers(ctx,"players"),
                                                                                                StringArgumentType.getString(ctx, "URL"),
                                                                                                StringArgumentType.getString(ctx, "UUID"),
                                                                                                StringArgumentType.getString(ctx, "SHA1"),
                                                                                                StringArgumentType.getString(ctx, "Prompt"),
                                                                                                BoolArgumentType.getBool(ctx, "Forced"),
                                                                                                BoolArgumentType.getBool(ctx, "ComputeSHA1")
                                                                                        ))
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                       )
                                )
                        )
            );
            dispatcher.register(
                    CommandManager.literal("rpm")
                            .requires(source -> Permissions.check(source, "resourcepackmanager.command.resourcepackmanager.base", 2))
                            .redirect(node)
            );
            dispatcher.register(
                    CommandManager.literal("model")
                            .requires(source -> Permissions.check(source, "resourcepackmanager.command.model.base", 0))
                            .then(CommandManager.literal("list")
                                    .requires(source -> Permissions.check(source, "resourcepackmanager.command.model.list", 0))
                                    .executes((ctx) -> {
                                        if(ctx.getSource().getPlayer() == null) {
                                            ctx.getSource().sendError(Utils.minecraftLogBuilder.error("You cannot execute this command as a non player without provided a player!"));
                                            return 0;
                                        }
                                        return listCustomModels(ctx.getSource(), ctx.getSource().getPlayer());
                                    })
                                    .then(CommandManager.argument("player", EntityArgumentType.players())
                                            .requires(source -> Permissions.check(source, "resourcepackmanager.command.model.list.others", 2))
                                            .executes((ctx) -> listCustomModels(ctx.getSource(),EntityArgumentType.getPlayer(ctx, "player")))
                                    )
                            )
                            .then(CommandManager.literal("add")
                                    .requires(source -> Permissions.check(source, "resourcepackmanager.command.model.add", 0))
                                    .then(CommandManager.argument("model", StringArgumentType.string())
                                            .executes((ctx) -> {
                                                if(ctx.getSource().getPlayer() == null) {
                                                    ctx.getSource().sendError(Utils.minecraftLogBuilder.error("You cannot execute this command as a non player without provided a player!"));
                                                    return 0;
                                                }
                                                return addCustomModel(ctx.getSource(), ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "model"));
                                            })
                                            .then(CommandManager.argument("player", EntityArgumentType.players())
                                                    .requires(source -> Permissions.check(source, "resourcepackmanager.command.model.add.others", 2))
                                                    .executes((ctx) -> addCustomModel(ctx.getSource(),EntityArgumentType.getPlayer(ctx, "player"), StringArgumentType.getString(ctx, "model")))
                                            )
                                    )
                            )
                            .then(CommandManager.literal("remove")
                                    .requires(source -> Permissions.check(source, "resourcepackmanager.command.model.remove", 0))
                                    .then(CommandManager.argument("model", StringArgumentType.string())
                                            .executes((ctx) -> {
                                                if(ctx.getSource().getPlayer() == null) {
                                                    ctx.getSource().sendError(Utils.minecraftLogBuilder.error("You cannot execute this command as a non player without provided a player!"));
                                                    return 0;
                                                }
                                                return removeCustomModel(ctx.getSource(), ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "model"));
                                            })
                                            .then(CommandManager.argument("player", EntityArgumentType.players())
                                                    .requires(source -> Permissions.check(source, "resourcepackmanager.command.model.remove.others", 2))
                                                    .executes((ctx) -> removeCustomModel(ctx.getSource(),EntityArgumentType.getPlayer(ctx, "player"), StringArgumentType.getString(ctx, "model")))
                                            )
                                    )
                            )
            );
        });
    }
    public static int reloadConfig(ServerCommandSource source) {
        try {
            ConfigData oldConfigData = ResourcePackManager.CONFIG_MANAGER.getData().clone();
            int returnCode = Utils.innitConfig();
            if(returnCode > 0) {
                ResourcePackManager.CONFIG_MANAGER.setData(oldConfigData);
                source.sendError(Utils.minecraftLogBuilder.error("An error happened trying reload config!"));
                return 0;
            }
            if(returnCode == -1) {
                source.sendFeedback(() -> Utils.minecraftLogBuilder.warn("Missing config, created config successfully!"), true);
                ResourcePackManager.LOGGER.warn("Missing config, created config successfully!");
                return 1;
            }
            source.sendFeedback(() -> Utils.minecraftLogBuilder.log("Reloaded config successfully!"), true);
            ResourcePackManager.LOGGER.info("Reloaded config successfully!");
            return 1;
        } catch (Exception e) {
            source.sendError(Utils.minecraftLogBuilder.error("An error happened trying reload config!"));
            ResourcePackManager.LOGGER.error("An error happened trying reload config!", e);
            return 0;
        }
    }

    public static int recomputeSHA1(ServerCommandSource source) {
        ConfigData config = ResourcePackManager.CONFIG_MANAGER.getData();

        if(!config.enableMod) {
            source.sendError(Utils.minecraftLogBuilder.error("Mod is disabled in the config!"));
            return 0;
        }

        if(!config.computeResourcePackSHA1FromResourcePack) {
            source.sendError(Utils.minecraftLogBuilder.error("'computeResourcePackSHA1FromResourcePack' is disabled in the config!"));
            return 0;
        }
        if(config.resourcePackURL.isEmpty() || !Utils.isValidURL(config.resourcePackURL)) {
            source.sendError(Utils.minecraftLogBuilder.error("Empty or invalid URL provided in the config!"));
            return 0;
        }

        try {
            config.computedResourcePackSHA1 = Utils.computeSHA1FromURLFile(config.resourcePackURL);
            ResourcePackManager.LOGGER.debug(String.format("Recomputed pack SHA1 successfully! (%s)", config.computedResourcePackSHA1));
            source.sendFeedback(() -> Utils.minecraftLogBuilder.log(String.format("Recomputed pack SHA1 successfully! (%s)", config.computedResourcePackSHA1)), true);
            return 1;
        } catch (Exception e) {
            String fallback = config.resourcePackSHA1.isEmpty() ? "resource_pack_sha1 value" : "SHA1 of the URL (not the file)";
            source.sendError(Utils.minecraftLogBuilder.error("An error happened trying to recompute SHA1 for resource pack! Is your URL a valid file? SHA1 will fallback to " + fallback + "."));
            ResourcePackManager.LOGGER.error("An error happened trying to recompute SHA1 for resource pack! Is your URL a valid file? SHA1 will fallback to " + fallback + ".", e);
            return 0;
        }
    }

    public static int resendPack(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        ConfigData config = ResourcePackManager.CONFIG_MANAGER.getData();

        if(!config.enableMod) {
            source.sendError(Utils.minecraftLogBuilder.error("Mod is disabled in the config!"));
            return 0;
        }

        try {
            if(config.resourcePackURL.isEmpty()) {
                source.sendError(Utils.minecraftLogBuilder.error("Missing/invalid resource pack URL provided in the config!"));
                return 0;
            }
            if(config.resourcePackUUID == null) {
                source.sendError(Utils.minecraftLogBuilder.error("Invalid resource pack UUID provided in the config! Remove the config value or provide a correct UUID."));
                return 0;
            }

            ResourcePackManagerAPI.sendConfigResourcePack(targets);

            source.sendFeedback(() -> Utils.minecraftLogBuilder.log(String.format("Resent pack to %s player(s) successfully!", targets.size())), true);
            return 1;
        } catch (Exception e) {
            source.sendError(Utils.minecraftLogBuilder.error("An error happened trying to resend resource pack"));
            ResourcePackManager.LOGGER.error(String.format("An error happened trying to re-send resource pack to %s players!", targets.size()), e);
            return 0;
        }
    }

    public static int sendResourcePack(
            ServerCommandSource source,
            Collection<ServerPlayerEntity> targets,
            String url,
            @Nullable String uuidStr,
            @Nullable String SHA1,
            @Nullable String promptStr,
            @Nullable Boolean forced,
            @Nullable Boolean computeSHA1
    ) {
        try {
            ConfigData config = ResourcePackManager.CONFIG_MANAGER.getData();
            if(!Utils.isValidURL(url)) {
                source.sendError(Utils.minecraftLogBuilder.error("Invalid URL! Please supply well formed URL."));
                return 0;
            }
            UUID uuid = null;
            if(uuidStr != null && !uuidStr.isEmpty()) {
                try {
                    uuid = UUID.fromString(uuidStr);
                } catch (Exception e) {
                    source.sendError(Utils.minecraftLogBuilder.error("Malformed UUID! Please give a properly formatted UUID or an empty one."));
                    return 0;
                }
            }
            if(promptStr != null && promptStr.isEmpty())
                promptStr = null;
            Text prompt = promptStr != null ? Text.of(promptStr) : null;

            if((computeSHA1 != null && computeSHA1) || (computeSHA1 == null && config.computeResourcePackSHA1FromResourcePack)) {
                try {
                    SHA1 = Utils.computeSHA1FromURLFile(url);
                } catch (Exception e) {
                    source.sendError(Utils.minecraftLogBuilder.error("An error happened trying to compute SHA1 for resource pack! Is your URL a valid file? Check console for more details."));
                    ResourcePackManager.LOGGER.error("An error happened trying to compute resource pack SH1!", e);
                    return 0;
                }
            }

            ResourcePackManager.LOGGER.info(String.format("Sending resource pack {url=\"%s\",uuid=%s,SHA1=%s,forced=%s,prompt=%s} to %s players",
                    url,
                    uuid,
                    SHA1,
                    forced,
                    prompt != null ? "\"" + prompt.getString() + "\"" : null,
                    targets.size()
            ));
            ResourcePackManagerAPI.sendResourcePack(targets, url, uuid, SHA1, prompt, forced, false);
            source.sendFeedback(() -> Utils.minecraftLogBuilder.log(String.format("Sent pack to %s player(s) successfully!", targets.size())), true);
            return 1;
        } catch (Exception e) {
            source.sendError(Utils.minecraftLogBuilder.error("An error happened trying to send resource pack to players! Check console for more details."));
            ResourcePackManager.LOGGER.error(String.format("An error happened trying to send resource pack to %s players!", targets.size()), e);
            return 0;
        }
    }

    public static int addCustomModel(ServerCommandSource source, ServerPlayerEntity target, String modelStr) {
        ConfigData config = ResourcePackManager.CONFIG_MANAGER.getData();
        if(!config.enableModelCommand) {
            source.sendError(Utils.minecraftLogBuilder.error("This command is disabled!"));
            return 0;
        }

        ItemStack handItem = target.getInventory().getMainHandStack();
        if(handItem == null || Objects.equals(handItem.getItem().getTranslationKey(), "block.minecraft.air")) {
            source.sendError(Utils.minecraftLogBuilder.error("You don't have any items selected!"));
            return -1;
        }

        try {
            CustomModelDataComponent customModelData = handItem.getComponents().getOrDefault(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelDataComponent.DEFAULT);
            List<String> modelStrings  = customModelData.strings();
            if(modelStrings.contains(modelStr)) {
                source.sendError(Utils.minecraftLogBuilder.error(String.format("This item already includes model \"%s\"!", modelStr)));
                return 0;
            }
            LinkedList<String> mutableModelStrings = new LinkedList<>(modelStrings);
            mutableModelStrings.add(modelStr);

            CustomModelDataComponent mutatedComponent = new CustomModelDataComponent(
                    customModelData.floats(),
                    customModelData.flags(),
                    mutableModelStrings,
                    customModelData.colors()
            );

            ResourcePackManager.LOGGER.debug(String.format("Adding custom model \"%s\" to %s, to item %s", modelStr, target.getName().getString(), handItem.getItem()));
            handItem.applyUnvalidatedChanges(ComponentChanges.builder().add(Component.of(DataComponentTypes.CUSTOM_MODEL_DATA, mutatedComponent)).build());

            source.sendFeedback(() -> Utils.minecraftLogBuilder.log(String.format("Added model \"%s\" to your item!", modelStr)), false);
            return 1;
        } catch (Exception e) {
            source.sendError(Utils.minecraftLogBuilder.error(String.format("An error happened trying to remove model \"%s\"! Check console for more details.", modelStr)));
            ResourcePackManager.LOGGER.error(String.format("An error happened trying to remove model \"%s\" from %s!", modelStr, target.getName().getString()), e);
            return 0;
        }
    }
    public static int removeCustomModel(ServerCommandSource source, ServerPlayerEntity target, String modelStr) {
        ConfigData config = ResourcePackManager.CONFIG_MANAGER.getData();
        if(!config.enableModelCommand) {
            source.sendError(Utils.minecraftLogBuilder.error("This command is disabled!"));
            return 0;
        }

        ItemStack handItem = target.getInventory().getMainHandStack();
        if(handItem == null || Objects.equals(handItem.getItem().getTranslationKey(), "block.minecraft.air")) {
            source.sendError(Utils.minecraftLogBuilder.error("You don't have any items selected!"));
            return -1;
        }
        try {

            CustomModelDataComponent customModelData = handItem.getComponents().getOrDefault(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelDataComponent.DEFAULT);
            List<String> modelStrings  = customModelData.strings();
            if(!modelStrings.contains(modelStr)) {
                source.sendError(Utils.minecraftLogBuilder.error(String.format("This item does not have model \"%s\"!", modelStr)));
                return 0;
            }
            LinkedList<String> mutableModelStrings = new LinkedList<>(modelStrings);
            mutableModelStrings.remove(modelStr);

            CustomModelDataComponent mutatedComponent = new CustomModelDataComponent(
                    customModelData.floats(),
                    customModelData.flags(),
                    mutableModelStrings,
                    customModelData.colors()
            );

            ResourcePackManager.LOGGER.debug(String.format("Removing custom model \"%s\" from %s, from item %s", modelStr, target.getName().getString(), handItem.getItem()));
            handItem.applyUnvalidatedChanges(ComponentChanges.builder().add(Component.of(DataComponentTypes.CUSTOM_MODEL_DATA, mutatedComponent)).build());

            source.sendFeedback(() -> Utils.minecraftLogBuilder.log(String.format("Removed model \"%s\" from your item!", modelStr)), false);
            return 1;
        } catch (Exception e) {
            source.sendError(Utils.minecraftLogBuilder.error(String.format("An error happened trying to remove model \"%s\"! Check console for more details.", modelStr)));
            ResourcePackManager.LOGGER.error(String.format("An error happened trying to remove model \"%s\" from %s!", modelStr, target.getName().getString()), e);
            return 0;
        }
    }
    public static int listCustomModels(ServerCommandSource source, ServerPlayerEntity target) {
        ConfigData config = ResourcePackManager.CONFIG_MANAGER.getData();
        if(!config.enableModelCommand) {
            source.sendError(Utils.minecraftLogBuilder.error("This command is disabled!"));
            return 0;
        }

        ItemStack handItem = target.getInventory().getMainHandStack();
        if(handItem == null || Objects.equals(handItem.getItem().getTranslationKey(), "block.minecraft.air")) {
            source.sendError(Utils.minecraftLogBuilder.error("You don't have any items selected!"));
            return -1;
        }
        try {
            ResourcePackManager.LOGGER.debug(String.format("Listing custom models from %s, from item %s", target.getName().getString(), handItem.getItem()));

            CustomModelDataComponent customModelData = handItem.getComponents().getOrDefault(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelDataComponent.DEFAULT);
            List<String> modelStrings  = customModelData.strings();

            if(modelStrings.isEmpty()) {
                source.sendFeedback(() -> Utils.minecraftLogBuilder.log("Your item does not have any custom models."), false);
                return 1;
            }

            MutableText returnedString = Utils.minecraftLogBuilder.log("Your item has the custom models: [");
            modelStrings.forEach((modelStr) -> {
                int i = modelStrings.indexOf(modelStr);
                returnedString.append(
                        Text.literal("\"")
                                .append(Text.literal(modelStr).formatted(Formatting.GREEN))
                                .append(Text.of("\""))
                );
                if(i != modelStrings.size() - 1)
                    returnedString.append(", ");
            });
            returnedString.append(Text.of("]"));

            source.sendFeedback(() -> returnedString, false);
            return 1;
        } catch (Exception e) {
            source.sendError(Utils.minecraftLogBuilder.error("An error happened trying to list models! Check console for more details."));
            ResourcePackManager.LOGGER.error(String.format("An error happened trying to list model from %s!", target.getName().getString()), e);
            return 0;
        }
    }

    // public static int setAfkPlaceholder(ServerCommandSource source, @NotNull String value) {
    //     try {
    //         ResourcePackManager.CONFIG_MANAGER.getData().afkPlaceholder = value;
    //         Utils.saveConfig();
    //         source.sendFeedback(() -> Utils.minecraftLogBuilder.log("Set new afk_placeholder to ").append(value), true);
    //         return 1;
    //     } catch (JsonIOException e) {
    //         source.sendError(Utils.minecraftLogBuilder.error("An error happened converting config to json! Check the console for more details."));
    //         ResourcePackManager.LOGGER.error("An error converting config to json in setAfkPlaceholder command!", e);
    //         return 0;
    //     } catch (IOException e) {
    //         source.sendError(Utils.minecraftLogBuilder.error("An error happened saving the config file! Check the console for more details."));
    //         ResourcePackManager.LOGGER.error("An error saving config file in setAfkPlaceholder command!", e);
    //         return 0;
    //     } catch (Exception e) {
    //         source.sendError(Utils.minecraftLogBuilder.error("An error happened running the command! Check the console for more details."));
    //         ResourcePackManager.LOGGER.error("An error running setAfkPlaceholder command!", e);
    //         return 0;
    //     }
    // }
    //
    // public static int setTimeUntilAfk(ServerCommandSource source, @NotNull Integer value) {
    //     try {
    //         ResourcePackManager.CONFIG_MANAGER.getData().timeUntilAfk = value;
    //         Utils.saveConfig();
    //         source.sendFeedback(() -> Utils.minecraftLogBuilder.log(String.format("Set new time_until_afk to %s", value)), true);
    //         return 1;
    //     } catch (JsonIOException e) {
    //         source.sendError(Utils.minecraftLogBuilder.error("An error happened converting config to json! Check the console for more details."));
    //         ResourcePackManager.LOGGER.error("An error converting config to json in setTimeUntilAfk command!", e);
    //         return 0;
    //     } catch (IOException e) {
    //         source.sendError(Utils.minecraftLogBuilder.error("An error happened saving the config file! Check the console for more details."));
    //         ResourcePackManager.LOGGER.error("An error saving config file in setTimeUntilAfk command!", e);
    //         return 0;
    //     } catch (Exception e) {
    //         source.sendError(Utils.minecraftLogBuilder.error("An error happened running the command! Check the console for more details."));
    //         ResourcePackManager.LOGGER.error("An error running setTimeUntilAfk command!", e);
    //         return 0;
    //     }
    // }
    //
    // public static int forceAfk(ServerCommandSource source, @NotNull final Collection<ServerPlayerEntity> targets) {
    //     try {
    //         if(targets.isEmpty()) {
    //             source.sendFeedback(() -> Utils.minecraftLogBuilder.warn("Nothing changed, 0 players provided."), false);
    //             return 0;
    //         }
    //         Text sourceText = source.getDisplayName();
    //         for (ServerPlayerEntity target : targets) {
    //             try {
    //                 AfkPlayer afkPlayer = ResourcePackManager.PLAYER_LIST.get(target.getUuid());
    //                 if(afkPlayer == null) {
    //                     source.sendError(
    //                         Utils.minecraftLogBuilder.error("Missing afkPlayer for player ")
    //                             .append(target.getDisplayName())
    //                             .append("! Ignore if player is a carpet shadowed player.")
    //                     );
    //                     ResourcePackManager.LOGGER.error(String.format("Missing afkPlayer for player %s! May be shadowed player.", target.getName().getString()));
    //                     return 0;
    //                 }
    //                 afkPlayer.setForcedAfk(true, sourceText);
    //             } catch (Exception e) {
    //                 ResourcePackManager.LOGGER.error(String.format("An error happened trying to force player [%s] afk!", target.getName().getString()), e);
    //                 source.sendError(
    //                     Utils.minecraftLogBuilder.error("An error happened trying to reset player ")
    //                         .append(target.getDisplayName())
    //                         .append("'s afk! Check console for more details.")
    //                 );
    //                 return 0;
    //             }
    //         }
    //         if(targets.size() == 1) {
    //             source.sendFeedback(() -> Utils.minecraftLogBuilder.log("Forced ").append(targets.iterator().next().getDisplayName()).append(" afk!"), true);
    //         } else {
    //             source.sendFeedback(() -> Utils.minecraftLogBuilder.log(String.format("Forced %s players afk!", targets.size())), true);
    //         }
    //         return 1;
    //     } catch (Exception e) {
    //         source.sendError(Utils.minecraftLogBuilder.error("An error happened trying to force player(s) afk! Check the console for more details."));
    //         ResourcePackManager.LOGGER.error("An error running forceAfk command!", e);
    //         return 0;
    //     }
    // }
    //
    // public static int resetAfk(ServerCommandSource source, @NotNull final Collection<ServerPlayerEntity> targets) {
    //     try {
    //         if(targets.isEmpty()) {
    //             source.sendFeedback(() -> Utils.minecraftLogBuilder.warn("Nothing changed, 0 players provided."), false);
    //             return 0;
    //         }
    //         for (ServerPlayerEntity target : targets) {
    //             try {
    //                 AfkPlayer afkPlayer = ResourcePackManager.PLAYER_LIST.get(target.getUuid());
    //                 if(afkPlayer == null) {
    //                     source.sendError(
    //                         Utils.minecraftLogBuilder.error("Missing afkPlayer for player ")
    //                             .append(target.getDisplayName())
    //                             .append("! Ignore if player is a carpet shadowed player.")
    //                     );
    //                     ResourcePackManager.LOGGER.error(String.format("Missing afkPlayer for player %s! May be shadowed player.", target.getName().getString()));
    //                     return 0;
    //                 }
    //                 afkPlayer.setLastInputTime(Util.getEpochTimeMs());
    //                 afkPlayer.setForcedAfk(false);
    //             } catch (Exception e) {
    //                 ResourcePackManager.LOGGER.error(String.format("An error happened trying to reset player [%s]'s afk!", target.getName().getString()), e);
    //                 source.sendError(
    //                     Utils.minecraftLogBuilder.error("An error happened trying to reset player ")
    //                         .append(target.getDisplayName())
    //                         .append("'s afk! Check console for more details.")
    //                 );
    //                 return 0;
    //             }
    //         }
    //         if(targets.size() == 1) {
    //             source.sendFeedback(() -> Utils.minecraftLogBuilder.log("Reset ").append(targets.iterator().next().getDisplayName()).append("'s afk status!"), true);
    //         } else {
    //             source.sendFeedback(() -> Utils.minecraftLogBuilder.log(String.format("Reset %s players' afk status!", targets.size())), true);
    //         }
    //         return 1;
    //     } catch (Exception e) {
    //         source.sendError(Utils.minecraftLogBuilder.error("An error happened trying to reset player(s)' afk status! Check the console for more details."));
    //         ResourcePackManager.LOGGER.error("An error running resetAfk command!", e);
    //         return 0;
    //     }
    // }
    //
    // //TODO: format the time instead of giving it in ms
    // public static int getAfkStatus(ServerCommandSource source, @NotNull final ServerPlayerEntity target) {
    //     try {
    //         if(target == null) {
    //             source.sendFeedback(() -> Utils.minecraftLogBuilder.warn("No players provided."), false);
    //             return 0;
    //         }
    //         AfkPlayer afkPlayer = ResourcePackManager.PLAYER_LIST.get(target.getUuid());
    //         if(afkPlayer == null) {
    //             source.sendError(
    //                 Utils.minecraftLogBuilder.error("Missing afkPlayer for player ")
    //                     .append(target.getDisplayName())
    //                     .append("! Ignore if player is a carpet shadowed player.")
    //             );
    //             ResourcePackManager.LOGGER.error(String.format("Missing afkPlayer for player %s! May be shadowed player.", target.getName().getString()));
    //             return 0;
    //         }
    //         if(!afkPlayer.isAfk()) {
    //             source.sendFeedback(() -> Utils.minecraftLogBuilder.log("").append(target.getDisplayName()).append(" is not afk."), false);
    //             return 0;
    //         }
    //         if(afkPlayer.isForcedAfk()) {
    //             source.sendFeedback(() -> Utils.minecraftLogBuilder.log("")
    //                 .append(target.getDisplayName())
    //                 .append(" has been forced afk by ")
    //                 .append(afkPlayer.getForcedAfkSource())
    //                 .append(String.format(" for %sms", Util.getEpochTimeMs() - afkPlayer.getForcedAfkTime())),
    //             false);
    //             return 1;
    //         }
    //         source.sendFeedback(() -> Utils.minecraftLogBuilder.log("")
    //             .append(target.getDisplayName())
    //             .append(String.format(" has been afk for %sms", Util.getEpochTimeMs() - afkPlayer.getLastInputTime() - ResourcePackManager.CONFIG_MANAGER.getData().timeUntilAfk*1000)),
    //         false);
    //         return 1;
    //     } catch (Exception e) {
    //         source.sendError(Utils.minecraftLogBuilder.error("An error happened trying to get player(s)' afk status! Check the console for more details."));
    //         ResourcePackManager.LOGGER.error("An error running resetAfk command!", e);
    //         return 0;
    //     }
    // }
}
