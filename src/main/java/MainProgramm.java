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

        //Erstellen der Module
        PollBuilder pollBuilder = new PollBuilder();

        api.addMessageCreateListener(event -> {
            String command = event.getMessageContent().split("\s")[0];
            System.out.println(command);


            switch (command){
                case ("!poll") -> pollBuilder.createPoll(event);
                case ("!Hallo") -> event.getChannel().sendMessage("Hi! ");
            }
        });
    }
}
