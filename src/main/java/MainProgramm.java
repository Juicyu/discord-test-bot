import channelManager.ChannelManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import poll.PollBuilder;
import properties.PropertiesReader;

public class MainProgramm {

    public static void main(String[] args) {

        //Erstellen der Verbindung
        String token = PropertiesReader.getProperty("token");
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
        System.out.println("Invite the Bot using following link: " + api.createBotInvite());

        api.addMessageCreateListener(event -> {
            String command = event.getMessageContent().split("\s")[0];
            String content = "0";

            if(event.getMessageContent().startsWith("!del")){
                content = event.getMessageContent().split("\s")[1];
            }

            switch (command){
                case ("!poll") -> PollBuilder.createPoll(event);
                case ("!Hallo") -> event.getChannel().sendMessage("Hi! " + event.getMessageAuthor().getDisplayName());
                case ("!muteAll") -> ChannelManager.muteAll(event);
                case ("!unmuteAll") -> ChannelManager.unmuteAll(event);
                case ("!del") -> ChannelManager.deleteMessages(event, content);
            }
        });
    }
}
