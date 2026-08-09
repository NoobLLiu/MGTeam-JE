package cn.gmzc.mgteam.gui;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.event.EventBus;
import org.geysermc.geyser.api.event.EventRegistrar;
import org.geysermc.geyser.api.event.bedrock.SessionDisconnectEvent;
import org.geysermc.geyser.api.event.bedrock.SessionJoinEvent;

final class BedrockFormSender {
    private static final long RETRY_INTERVAL_TICKS = 5L;
    private static final int MAX_RETRIES = 120;

    private final JavaPlugin plugin;
    private final Map<UUID, PendingForm> pendingForms = new ConcurrentHashMap<>();
    private EventBus<EventRegistrar> eventBus;
    private EventRegistrar registrar;
    private BukkitTask retryTask;

    BedrockFormSender(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    void start() {
        if (retryTask != null) {
            return;
        }
        subscribeToGeyser();
        retryTask = Bukkit.getScheduler().runTaskTimer(
            plugin,
            this::flushPendingForms,
            1L,
            RETRY_INTERVAL_TICKS
        );
    }

    void stop() {
        if (retryTask != null) {
            retryTask.cancel();
            retryTask = null;
        }
        if (eventBus != null && registrar != null) {
            eventBus.unregisterAll(registrar);
            eventBus = null;
            registrar = null;
        }
        pendingForms.clear();
    }

    void remove(UUID playerId) {
        if (playerId != null) {
            pendingForms.remove(playerId);
        }
    }

    boolean isBedrock(Player player) {
        return player != null && isBedrock(player.getUniqueId());
    }

    boolean send(Player player, FormBuilder<?, ?, ?> builder) {
        return builder != null && send(player, builder.build());
    }

    boolean send(Player player, Form form) {
        if (player == null || form == null || !player.isOnline()) {
            return false;
        }
        UUID playerId = player.getUniqueId();
        if (!isBedrock(playerId)) {
            return false;
        }
        if (sendNow(playerId, form)) {
            pendingForms.remove(playerId);
            return true;
        }
        pendingForms.put(playerId, new PendingForm(form));
        return true;
    }

    private void subscribeToGeyser() {
        try {
            GeyserApi geyser = GeyserApi.api();
            eventBus = geyser.eventBus();
            registrar = EventRegistrar.of(plugin);
            eventBus.subscribe(registrar, SessionJoinEvent.class, event ->
                flushOnMainThread(event.connection().javaUuid())
            );
            eventBus.subscribe(registrar, SessionDisconnectEvent.class, event ->
                remove(event.connection().javaUuid())
            );
        } catch (Throwable ignored) {
            // The retry loop still covers servers where Geyser finishes initializing later.
        }
    }

    private void flushOnMainThread(UUID playerId) {
        if (playerId == null) {
            return;
        }
        Bukkit.getScheduler().runTask(plugin, () -> flushPendingForm(playerId));
    }

    private void flushPendingForms() {
        if (eventBus == null) {
            subscribeToGeyser();
        }
        for (UUID playerId : pendingForms.keySet()) {
            flushPendingForm(playerId);
        }
    }

    private void flushPendingForm(UUID playerId) {
        PendingForm pending = pendingForms.get(playerId);
        if (pending == null) {
            return;
        }
        Player player = Bukkit.getPlayer(playerId);
        if (player == null || !player.isOnline() || !isBedrock(playerId)) {
            pendingForms.remove(playerId, pending);
            return;
        }
        if (sendNow(playerId, pending.form())) {
            pendingForms.remove(playerId, pending);
            return;
        }
        if (pending.incrementRetries() >= MAX_RETRIES) {
            pendingForms.remove(playerId, pending);
        }
    }

    private boolean isBedrock(UUID playerId) {
        try {
            FloodgateApi floodgate = FloodgateApi.getInstance();
            return floodgate.isFloodgatePlayer(playerId) || floodgate.isFloodgateId(playerId);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean sendNow(UUID playerId, Form form) {
        try {
            FloodgateApi floodgate = FloodgateApi.getInstance();
            return floodgate.isFloodgatePlayer(playerId) && floodgate.sendForm(playerId, form);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static final class PendingForm {
        private final Form form;
        private int retries;

        private PendingForm(Form form) {
            this.form = form;
        }

        private Form form() {
            return form;
        }

        private int incrementRetries() {
            return ++retries;
        }
    }
}
