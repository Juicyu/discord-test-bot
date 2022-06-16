import audioplayer.BotPlayer;
import channelManager.ChannelManager;
import general.General;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import poll.PollBuilder;
import properties.PropertiesReader;

import java.util.Collection;

public class MainProgramm {

    public static void main(String[] args) {

        //Erstellen der Verbindung
        String token = PropertiesReader.getProperty("token");
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
        System.out.println("Invite the Bot using following link: " + api.createBotInvite());

        //Erstelle Instanzen der Hilfsklassen
        ChannelManager channelManager = new ChannelManager();
        PollBuilder pollBuilder = new PollBuilder();
        General general = new General();
        BotPlayer botPlayer = new BotPlayer();

        //Erstellung der Befehlsliste für den Bot und Aufruf der entsprechenden Methoden
        api.addMessageCreateListener(event -> {
            String command = event.getMessageContent().split("\s")[0];
            String content = "0";

            if (event.getMessageContent().startsWith("!del") || event.getMessageContent().startsWith("!play")) {
                content = event.getMessageContent().split("\s")[1];
            }

            switch (command) {
                case ("!poll") -> pollBuilder.createPoll(event);
                case ("!Hallo") -> general.sagHallo(event);
                case ("!muteAll") -> channelManager.muteAll(event);
                case ("!unmuteAll") -> channelManager.unmuteAll(event);
                case ("!del") -> channelManager.deleteMessages(event, content);
                case ("!Anleitung") -> general.zeigeAnleitung(event);
                case ("!play") -> botPlayer.play(api, event, content);
            }
        });

        //Fragt beim Bot-Start alle Channel ab, ob Sie Channel+ sind und fügt Channel+ - Funktionalität hinzu
        Collection<ServerChannel> channels = api.getServerChannels();
        for (ServerChannel channel : channels) {
            if (channel.getName().endsWith("+")) {
                channelManager.createChannelPlus((ServerVoiceChannel) channel);
            }
        }

        //automatisierte Erstellung von Channel+ durch Channelerstellung und Channel Umbenennung
        api.addServerChannelCreateListener(channelManager::channelPlusEvent);
        api.addServerChannelChangeNameListener(channelManager::channelRenameEvent);
    }
}
