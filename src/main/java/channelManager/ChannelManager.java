package channelManager;

import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.ServerVoiceChannelBuilder;
import org.javacord.api.entity.server.BoostLevel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.channel.server.ServerChannelChangeNameEvent;
import org.javacord.api.event.channel.server.ServerChannelCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.channel.server.voice.ServerVoiceChannelMemberJoinListener;
import org.javacord.api.util.event.ListenerManager;

import java.util.Collection;

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
    public void deleteMessages(MessageCreateEvent event, String content){
        int messageCount;
        try{
            messageCount = Integer.parseInt(content);
            event.getChannel().getMessages(++messageCount).get().deleteAll();
        } catch (Exception ignored){
        }
    }

    //Aufruf der Methode zur Erstellung eines Channel+, wenn ein Channel mit einem "+" am Ende des Namens erstellt wurde
    //Neuer Channel!
    public void channelPlusEvent(ServerChannelCreateEvent event){
        if(event.getChannel().asServerVoiceChannel().isPresent()){
            createChannelPlus((event.getChannel().asServerVoiceChannel().get()));
        }
    }

    //Aufruf der Methode zur Erstellung eines Channel+, wenn ein Channelname mit einem + am Ende versehen wurde
    //Rename!
    public void channelRenameEvent(ServerChannelChangeNameEvent event){
        if(event.getChannel().asServerVoiceChannel().isPresent()){
            createChannelPlus((event.getChannel().asServerVoiceChannel().get()));
        }
    }

    //Methode zum Erstellen eines neuen Channel+
    public void createChannelPlus(ServerVoiceChannel channel){

        //prüfen, ob es ein Channel+ ist
        if(channel.getName().endsWith("+")){
            if(channel.asServerVoiceChannel().isPresent()){
                //Channel konfigurieren
                channel.updateUserLimit(1);
                ListenerManager<ServerVoiceChannelMemberJoinListener> listener =
                channel.addServerVoiceChannelMemberJoinListener(event -> {
                    ServerVoiceChannel newChannel = new ServerVoiceChannelBuilder(event.getServer())
                            .setName(event.getChannel().getName().replace("+", ""))
                            .setCategory(event.getChannel().getCategory().get())
                            .create()
                            .join();

                    //Setzt die Bitrate eines Servervoice Chanels abhängig von der Tier Stufe des Servers
                    switch (event.getServer().getBoostLevel()){
                        case NONE -> newChannel.updateBitrate(96000);
                        case TIER_1 -> newChannel.updateBitrate(128000);
                        case TIER_2 -> newChannel.updateBitrate(256000);
                        case TIER_3 -> newChannel.updateBitrate(384000);
                    }

                    event.getUser().move(newChannel);

                    //Channeluser < 1, dann Channel entfernen
                    newChannel.addServerVoiceChannelMemberLeaveListener(leaveEvent -> {
                        if(leaveEvent.getChannel().getConnectedUserIds().size() < 1){
                            newChannel.delete();
                        }
                    });
                });

                //Wird das "+" aus dem Namen entfernt, wird die Channel+ Konfiguration entfernt
                channel.addServerChannelChangeNameListener(changeNameEvent -> {
                    if(!changeNameEvent.getChannel().getName().endsWith("+")){
                        channel.updateUserLimit(0);
                        listener.remove();
                    }
                });
            }
        }
    }
}
