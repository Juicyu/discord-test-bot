import channelManager.ChannelManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.channel.server.ServerChannelCreateEvent;
import poll.PollBuilder;
import properties.PropertiesReader;

import java.util.Collection;

public class MainProgramm {

    public static void main(String[] args) {

        //Erstellen der Verbindung
        String token = PropertiesReader.getProperty("token");
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
        System.out.println("Invite the Bot using following link: " + api.createBotInvite());

        //Erstellung der Befehlsliste für den Bot und Aufruf der entsprechenden Methoden
        api.addMessageCreateListener(event -> {
            String command = event.getMessageContent().split("\s")[0];
            String content = "0";

            if (event.getMessageContent().startsWith("!del")) {
                content = event.getMessageContent().split("\s")[1];
            }

            switch (command) {
                case ("!poll") -> PollBuilder.createPoll(event);
                case ("!Hallo") -> event.getChannel().sendMessage("Hi! " + event.getMessageAuthor().getDisplayName());
                case ("!muteAll") -> ChannelManager.muteAll(event);
                case ("!unmuteAll") -> ChannelManager.unmuteAll(event);
                case ("!del") -> ChannelManager.deleteMessages(event, content);
            }
        });

        //Fragt beim Bot-Start alle Channel ab, ob Sie Channel+ sind und fügt Channel+ - Funktionalität hinzu
        Collection<ServerChannel> channels = api.getServerChannels();
        for (ServerChannel channel : channels) {
            if (channel.getName().endsWith("+")) {
                ChannelManager.createChannelPlus((ServerVoiceChannel) channel);
            }
        }

        //automatisierte Erstellung von Channel+ durch Channelerstellung und Channel Umbenennung
        api.addServerChannelCreateListener(ChannelManager::channelPlusEvent);
        api.addServerChannelChangeNameListener(ChannelManager::channelRenameEvent);
    }
}
