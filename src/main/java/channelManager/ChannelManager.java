package channelManager;

import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.channel.server.ServerChannelChangeNameEvent;
import org.javacord.api.event.channel.server.ServerChannelCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Collection;

public class ChannelManager {

    public static void unmuteAll(MessageCreateEvent event) {
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

    public static void muteAll(MessageCreateEvent event) {
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

    public static void deleteMessages(MessageCreateEvent event, String content){
        int messageCount;
        try{
            messageCount = Integer.parseInt(content);
            event.getChannel().getMessages(++messageCount).get().deleteAll();
        } catch (Exception ignored){
        }
    }

    public static void channelPlusEvent(ServerChannelCreateEvent event){
        if(event.getChannel().asServerVoiceChannel().isPresent()){
            createChannelPlus((event.getChannel().asServerVoiceChannel().get()));
        }
    }

    public static void channelRenameEvent(ServerChannelChangeNameEvent event){
        if(event.getChannel().asServerVoiceChannel().isPresent()){
            createChannelPlus((event.getChannel().asServerVoiceChannel().get()));
        }
    }

    private static void createChannelPlus(ServerVoiceChannel channel){

        if(channel.getName().endsWith("+")){
            if(channel.asServerVoiceChannel().isPresent()){
                channel.updateUserLimit(1);
            }
        }
    }
}
