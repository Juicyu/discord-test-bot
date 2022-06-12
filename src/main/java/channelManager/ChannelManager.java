package channelManager;

import org.javacord.api.entity.channel.VoiceChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

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
                try {
                    user.mute(event.getServer().get());
                } catch (NoSuchElementException ignored){

                }
            }
            event.getMessage().delete();
        }
    }

/*    public void muteAll(MessageComponentInteraction interaction, Server server) {
        if (interaction.getUser().getConnectedVoiceChannel(server).isPresent()) {
            Collection<User> connectedUser = interaction.getUser().getConnectedVoiceChannel(server).get().getConnectedUsers();
            for (User user :
                    connectedUser) {
                user.mute(server);
            }
        }
    }*/
}
