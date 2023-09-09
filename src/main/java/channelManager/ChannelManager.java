package channelManager;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.ServerVoiceChannelBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.channel.server.ServerChannelChangeNameEvent;
import org.javacord.api.event.channel.server.ServerChannelCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.channel.server.voice.ServerVoiceChannelMemberJoinListener;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.event.ListenerManager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ChannelManager {

    public void unmuteAll(MessageCreateEvent event) {
        //Ist der Nachrichten-Author in einem Voicechannel?
        if (event.getMessageAuthor().getConnectedVoiceChannel().isPresent()) {
            //Wenn ja, nimm alle Benutzer, die in dem gleichen Channel sind und unmute sie
            Collection<User> connectedUser = event.getMessageAuthor().getConnectedVoiceChannel().get().getConnectedUsers();
            for (User user :
                    connectedUser) {
                if (event.getServer().isPresent()) {
                    user.unmute(event.getServer().get());
                }
            }
            event.getMessage().delete();
        }
    }

    public void muteAll(MessageCreateEvent event) {
        //Ist der Nachrichten-Author in einem Voicechannel?
        if (event.getMessageAuthor().getConnectedVoiceChannel().isPresent()) {
            //Wenn ja, nimm alle Benutzer, die in dem gleichen Channel sind und mute sie
            Collection<User> connectedUser = event.getMessageAuthor().getConnectedVoiceChannel().get().getConnectedUsers();
            for (User user : connectedUser) {
                if (event.getServer().isPresent()) {
                    user.mute(event.getServer().get());
                }
            }
            event.getMessage().delete();
        }
    }

    //Befehl zum Löschen von x Nachrichten
    public void deleteMessages(MessageCreateEvent event, String content) {
        int messageCount;
        try {
            messageCount = Integer.parseInt(content);
            event.getChannel().getMessages(++messageCount).get().deleteAll();
        } catch (Exception ignored) {
        }
    }

    //Befehl zum Löschen von allen Nachrichten, bis auf die neuesten 10
    public void autoDeleteOn(MessageCreateEvent event, String time) {
        TextChannel textChannel = event.getChannel();
        //alle Message-Create-Listener entfernen, damit bei einem Update die alten Create-Listener entfernt werden

        //speichere Stunden
        String[] tempVal = time.split("h");
        int stunden = Integer.parseInt(tempVal[0]);

        //speichere Minuten
        tempVal[1] = tempVal[1].replace("h", "");
        tempVal = tempVal[1].split("m");
        int minuten = Integer.parseInt(tempVal[0]);

        //speichere Sekunden
        tempVal[1] = tempVal[1].replace("m", "");
        tempVal = tempVal[1].split("s");
        int sekunden = Integer.parseInt(tempVal[0]);

        //Berechne das Interval
        int interval = sekunden * 1000 + minuten * 1000 * 60 + stunden * 1000 * 60 * 24;
        System.out.println("Interval: " + interval);
        System.out.println("Anzahl Listener: " + textChannel.getMessageCreateListeners().size());
        textChannel.sendMessage("Auto-Delete aktiviert! Nachrichten in diesem Channel werden nach "
                + stunden + "h "
                + minuten + "m "
                + sekunden + "s "
                + "gelöscht!").join();
        ListenerManager<MessageCreateListener> listener = textChannel.addMessageCreateListener(e -> {
            e.getMessage().deleteAfter(Duration.ofMillis(interval));
        });
        textChannel.addMessageCreateListener(e -> {
            if (e.getMessage().getContent().startsWith("!autodeleteon")) {
                MessageCreateListener messageCreateListener = e.getChannel().getMessageCreateListeners().get(0);
                listener.remove();
            }
        });
    }

    //Aufruf der Methode zur Erstellung eines Channel+, wenn ein Channel mit einem "+" am Ende des Namens erstellt wurde
    //Neuer Channel!
    public void channelPlusEvent(ServerChannelCreateEvent event) {
        if (event.getChannel().asServerVoiceChannel().isPresent()) {
            createChannelPlus((event.getChannel().asServerVoiceChannel().get()));
        }
    }

    //Aufruf der Methode zur Erstellung eines Channel+, wenn ein Channelname mit einem + am Ende versehen wurde
    //Rename!
    public void channelRenameEvent(ServerChannelChangeNameEvent event) {
        if (event.getChannel().asServerVoiceChannel().isPresent()) {
            createChannelPlus((event.getChannel().asServerVoiceChannel().get()));
        }
    }

    //Methode zum Erstellen eines neuen Channel+
    public void createChannelPlus(ServerVoiceChannel channel) {

        //prüfen, ob es ein Channel+ ist
        if (channel.getName().endsWith("+")) {
            if (channel.asServerVoiceChannel().isPresent()) {
                //Channel konfigurieren
                channel.updateUserLimit(1);
                ListenerManager<ServerVoiceChannelMemberJoinListener> listener =
                        channel.addServerVoiceChannelMemberJoinListener(event -> {
                            ServerVoiceChannel newChannel = new ServerVoiceChannelBuilder(event.getServer())
                                    .setName(event.getChannel().getName().replace("+", ""))
                                    .setCategory(event.getChannel().getCategory().get())
                                    .create()
                                    .join();
                            //Setzt den neuen Channel unter den Channel+, der ihn erzeugt hat
                            newChannel.updateRawPosition(event.getChannel().getRawPosition()).join();
                            //Setzt die Bitrate eines Servervoice Chanels abhängig von der Tier Stufe des Servers
                            switch (event.getServer().getBoostLevel()) {
                                case NONE -> newChannel.updateBitrate(96000);
                                case TIER_1 -> newChannel.updateBitrate(128000);
                                case TIER_2 -> newChannel.updateBitrate(256000);
                                case TIER_3 -> newChannel.updateBitrate(384000);
                            }
                            event.getUser().move(newChannel);

                            //Channeluser < 1, dann Channel entfernen
                            newChannel.addServerVoiceChannelMemberLeaveListener(leaveEvent -> {
                                if (leaveEvent.getChannel().getConnectedUserIds().isEmpty()) {
                                    newChannel.delete();
                                }
                            });
                        });

                //Wird das "+" aus dem Namen entfernt, wird die Channel+ Konfiguration entfernt
                channel.addServerChannelChangeNameListener(changeNameEvent -> {
                    if (!changeNameEvent.getChannel().getName().endsWith("+")) {
                        channel.updateUserLimit(0);
                        listener.remove();
                    }
                });
            }
        }
    }
}
