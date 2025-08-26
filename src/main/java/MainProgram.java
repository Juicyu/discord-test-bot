import MacroBuilder.MacroBuilder;
import audioplayer.BotPlayer;
import channelManager.ChannelManager;
import general.General;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.*;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import poll.PollBuilder;
import poll.PollValue;
import properties.PropertiesReader;
import weeklyReminder.WeeklyReminder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MainProgram {

   public static void main(String[] args) {

      //Erstellen der Verbindung
      String token = PropertiesReader.getProperty("token");
      DiscordApi api = new DiscordApiBuilder()
         .setToken(token)
         .addIntents(Intent.MESSAGE_CONTENT, Intent.GUILD_MEMBERS)//vorher MESSAGE_CONTENT genau wie im Tutorial, wurde aber als Fehler angezeigt
         .login()
         .join();
      //All permissions: 1099511627775
      System.out.println("Invite the Bot using following link: " + api.createBotInvite());

      //Erstelle Instanzen der Hilfsklassen
      ChannelManager channelManager = new ChannelManager();
      PollBuilder pollBuilder = new PollBuilder();
      General general = new General();
      BotPlayer botPlayer = new BotPlayer();
      MacroBuilder macroBuilder = new MacroBuilder();

      //Sende eine Willkommens-Nachricht
      api.addUserRoleAddListener(event -> {
         String name = event.getUser().getName();
         name = name.substring(0,1).toUpperCase() + name.substring(1);
         String nachricht = "";
         switch(event.getRole().getName()){
            case "Anwärter":
               nachricht = "Du hast es geschafft, " + name + ", du hast den ersten Schritt gemeistert, um ein Teil von New Haven zu sein. \n" +
                    "\n" +
                    "Um richtig durchstarten zu können, möchten wir dich gerne an die Hand nehmen. Du findest eine Übersicht zu den wichtigsten Punkten im Kanal #faq. Lies diesen genau durch und die meisten Fragen sollten danach geklärt sein. \n" +
                    "\n" +
                    "Zudem startet heute, wie hoffentlich im Bewerbungsgespräch erwähnt, deine Probezeit bei uns. Nutze diese Zeit so gut es geht, um uns kennenzulernen und gib uns durch deine Aktivität auch die Möglichkeit, dies bei dir zu tun.\n" +
                    "\n" +
                    "In diesem Sinne nun genug der langen Worte und viel Spaß bei uns!";
               event.getUser().sendMessage(nachricht);
               break;
            case "WoW":
               nachricht = "Du hast es geschafft, " + name + ", du bist nun ein Teil von New Haven :heart:, Gratulation und Willkommen!";
               event.getUser().sendMessage(nachricht);
               break;
            case "Botaniker":
               nachricht = "Hallo liebe Botaniker,\n" +
                       "hiermit wollen wir auf folgendes hinweisen:\n" +
                       "- Zeigt nur Inhalte, die für euch an eurem Wohnort legal sind (siehe zb. CanG bei Wohnort in Deutschland)\n" +
                       "- Keine Absprachen oder Angebote zur Weitergabe, Tausch- oder Verkauf von Cannabis, Pflanzenteilen oder Vermehrungsmaterial\n" +
                       "- Aus Datenschutzgründen ist es zu empfehlen, Metadaten aus Bildern zu entfernen\n" +
                       "- Sollten Bilder den Anschein erwecken, dass es sich nicht eindeutig um legale Aktivitäten handelt, raten wir jedem, auf das Hochladen solcher Bilder zu verzichten\n" +
                       "Wir bitten euch dies im Hinterkopf zu haben, wenn ihr Bilder postet \uD83D\uDC9A\n" +
                       "Vielen Dank euch \uD83D\uDE4F";
               event.getUser().sendMessage(nachricht);
               break;
         }
      });

      //Erstellung der Befehlsliste für den Bot und Aufruf der entsprechenden Methoden
      api.addMessageCreateListener(event -> {
         String message = event.getMessageContent();
         String command = message.split("\s")[0].toLowerCase();
         String content = "0";

         if (message.startsWith("!del")
            || message.startsWith("!play")
            || message.startsWith("!roll")
            || message.startsWith("!rollDestiny")
            || message.startsWith("!rollDestinyBetween")
            || message.startsWith("!autodeleteon")) {
            content = message.split("\s")[1];
         }

         try {
            switch (command) {
               case ("!poll") -> pollBuilder.createPoll(event);
               case ("!hallo") -> general.sagHallo(event);
               case ("!muteall") -> channelManager.muteAll(event);
               case ("!unmuteall") -> channelManager.unmuteAll(event);
               case ("!del") -> channelManager.deleteMessages(event, content);
               //case ("!autodeleteon") -> channelManager.autoDeleteOn(event, content);
               case ("!anleitung") -> general.zeigeAnleitung(event);
               case ("!play") -> botPlayer.play(api, event, content);
               case ("!roll") -> general.rollDice(event, content);
               case ("!rolldestiny") -> general.rollDestiny(event, content);
               case ("!rolldestinybetween") -> general.rollDestinyBetween(event, content);
               case ("!macrobuilder") -> macroBuilder.sendMacroMessage(event);
               case ("!shutdown") -> general.shutdown(event);
            }
         } catch (Error e) {
            System.out.println(e.getMessage());
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

      SlashCommand pollCommand =
         SlashCommand.with("umfrage", "startet eine Umfrage", Arrays.asList(
                  SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "Channel", "test", true),
                  SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "privatsphäre", "Soll die Abstimmung anonym oder öffentlich erfolgen?", true,
                     Arrays.asList(
                        SlashCommandOptionChoice.create("öffentlich", "öffentlich"),
                        SlashCommandOptionChoice.create("anonym", "anonym")
                     )
                  ),
                  SlashCommandOption.createWithChoices(SlashCommandOptionType.BOOLEAN, "mehrfachauswahl", "Soll ein User mehr als eine Antwort wählen dürfen?", true,
                     Arrays.asList(
                        SlashCommandOptionChoice.create("ja", "true"),
                        SlashCommandOptionChoice.create("nein", "false")
                     )
                  ),
                  SlashCommandOption.create(SlashCommandOptionType.STRING, "titel", "Welchen titel möchtest Du Deiner Umfrage geben?", true),
                  SlashCommandOption.create(SlashCommandOptionType.STRING, "frage", "Welche frage möchtest Du stellen?", true),
                  SlashCommandOption.create(SlashCommandOptionType.STRING, "antwort1", "Geben Sie die erste Antwortmöglichkeit ein", true),
                  SlashCommandOption.create(SlashCommandOptionType.STRING, "antwort2", "Geben Sie die zweite Antwortmöglichkeit ein"),
                  SlashCommandOption.create(SlashCommandOptionType.STRING, "antwort3", "Geben Sie die dritte Antwortmöglichkeit ein"),
                  SlashCommandOption.create(SlashCommandOptionType.STRING, "antwort4", "Geben Sie die vierte Antwortmöglichkeit ein"),
                  SlashCommandOption.create(SlashCommandOptionType.STRING, "antwort5", "Geben Sie die fünfte Antwortmöglichkeit ein"),
                  SlashCommandOption.createLongOption("stunden", "Wie viele stunden soll die Umfrage aktiv sein?", false, 0, (24 * 7)),
                  SlashCommandOption.createLongOption("minuten", "Wie viele minuten soll die Umfrage aktiv sein?", false, 0, 59),
                  SlashCommandOption.createLongOption("sekunden", "Wie viele sekunden soll die Umfrage aktiv sein?", false, 0, 59)
               )
            )
            .createGlobal(api)
            .join();

      SlashCommandCreateListener pollCommandListener = new SlashCommandCreateListener() {
         @Override
         public void onSlashCommandCreate(SlashCommandCreateEvent event) {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            ServerChannel channel = null;
            ServerTextChannel textChannel = null;
            String privacy = "";
            boolean multipleChoice = false;
            String titel = "";
            String question = "";
            ArrayList<String> answereList = new ArrayList<String>();
            ArrayList<Integer> timeList = new ArrayList<Integer>();
            List<User> userList1 = new ArrayList<>();
            Interaction userInteraction = event.getInteraction();
            PollValue pollValue;

            //Sicheres Abfragen aller Eingabe
            if (interaction.getOptionByName("channel").isPresent() &&
               interaction.getOptionByName("channel").get().getChannelValue().isPresent()) {
               channel = interaction.getOptionByName("channel").get().getChannelValue().get();
            }
            //--rufe den Channel ab und prüfe, ob es sich um einen Textchannel handelt!
            try {
               textChannel = (ServerTextChannel) channel;
            } catch (Exception e) {
               interaction.getUser().sendMessage("Bei der Erstellung Ihrer Umfrage gab es ein Problem!");
               assert channel != null;
               ServerTextChannel bottasLog = (ServerTextChannel) channel.getServer().getChannelsByName("bottas-log").get(0);
               System.out.println(e.getMessage());
               bottasLog.sendMessage(e.getMessage());
               bottasLog.sendMessage(Arrays.toString(e.getStackTrace()));
            }
            //--

            if (interaction.getOptionByName("privatsphäre").isPresent() &&
               interaction.getOptionByName("privatsphäre").get().getStringValue().isPresent()) {
               privacy = interaction.getOptionByName("privatsphäre").get().getStringValue().get();
            }
            // Mehrfachauswahl
            multipleChoice = interaction.getOptionByName("mehrfachauswahl").get().getBooleanValue().get();
            if (interaction.getOptionByName("titel").isPresent() &&
               interaction.getOptionByName("titel").get().getStringValue().isPresent()) {
               titel = interaction.getOptionByName("titel").get().getStringValue().get();
            }
            // Frage
            if (interaction.getOptionByName("frage").isPresent() &&
               interaction.getOptionByName("frage").get().getStringValue().isPresent()) {
               question = interaction.getOptionByName("frage").get().getStringValue().get();
            }
            // Antwort 1
            if (interaction.getOptionByName("antwort1").isPresent() &&
               interaction.getOptionByName("antwort1").get().getStringValue().isPresent()) {
               String answere1 = interaction.getOptionByName("antwort1").get().getStringValue().get();
               answereList.add(answere1);
            }
            // Antwort 2
            if (interaction.getOptionByName("antwort2").isPresent() &&
               interaction.getOptionByName("antwort2").get().getStringValue().isPresent()) {
               String answere2 = interaction.getOptionByName("antwort2").get().getStringValue().get();
               answereList.add(answere2);
            }
            // Antwort 3
            if (interaction.getOptionByName("antwort3").isPresent() &&
               interaction.getOptionByName("antwort3").get().getStringValue().isPresent()) {
               String answere3 = interaction.getOptionByName("antwort3").get().getStringValue().get();
               answereList.add(answere3);
            }
            // Antwort 4
            if (interaction.getOptionByName("antwort4").isPresent() &&
               interaction.getOptionByName("antwort4").get().getStringValue().isPresent()) {
               String answere4 = interaction.getOptionByName("antwort4").get().getStringValue().get();
               answereList.add(answere4);
            }
            // Antwort 5
            if (interaction.getOptionByName("antwort5").isPresent() &&
               interaction.getOptionByName("antwort5").get().getStringValue().isPresent()) {
               String answere5 = interaction.getOptionByName("antwort5").get().getStringValue().get();
               answereList.add(answere5);
            }
            if (interaction.getOptionByName("stunden").isPresent() &&
               interaction.getOptionByName("stunden").get().getStringValue().isPresent()) {
               String stunden = interaction.getOptionByName("stunden").get().getStringValue().get();
               timeList.add(Integer.parseInt(stunden));
            }
            if (interaction.getOptionByName("minuten").isPresent() &&
               interaction.getOptionByName("minuten").get().getStringValue().isPresent()) {
               String minuten = interaction.getOptionByName("minuten").get().getStringValue().get();
               timeList.add(Integer.parseInt(minuten));
            }
            if (interaction.getOptionByName("sekunden").isPresent() &&
               interaction.getOptionByName("sekunden").get().getStringValue().isPresent()) {
               String sekunden = interaction.getOptionByName("sekunden").get().getStringValue().get();
               timeList.add(Integer.parseInt(sekunden));
            }
            pollValue = new PollValue(event,
                                      multipleChoice,
                                      titel,
                                      question,
                                      channel,
                                      textChannel,
                                      privacy,
                                      answereList,
                                      timeList,
                                      userList1
               );
            //Aufruf der entsprechenden Methode zur Umfragenerstellung
            if (privacy.equals("öffentlich") && !multipleChoice) {
               pollBuilder.createPublicPollSingle(pollValue);
            }
            if (privacy.equals("öffentlich") && multipleChoice) {
               pollBuilder.createPublicPollMulti(pollValue);
            }
            if (privacy.equals("anonym") && !multipleChoice) {
               pollBuilder.createAnonymPollSingle(pollValue);
            }
            if (privacy.equals("anonym") && multipleChoice) {
               pollBuilder.createAnonymPollMulti(pollValue);
            }
         }
      };

      api.addSlashCommandCreateListener(pollCommandListener);

      WeeklyReminder.startWeeklyReminder(api, "1106635906737262652");
   }
}
