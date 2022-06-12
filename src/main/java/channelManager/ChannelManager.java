package channelManager;

import org.javacord.api.entity.user.User;
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
}
