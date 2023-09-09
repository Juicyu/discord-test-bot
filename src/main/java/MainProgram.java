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
import properties.PropertiesReader;

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
         .addIntents(Intent.MESSAGE_CONTENT)//vorher MESSAGE_CONTENT genau wie im Tutorial, wurde aber als Fehler angezeigt
         .login()
         .join();
      //All permissions: 1099511627775
      System.out.println("Invite the Bot using following link: " + api.createBotInvite());

      //Erstelle Instanzen der Hilfsklassen
      ChannelManager channelManager = new ChannelManager();
      PollBuilder pollBuilder = new PollBuilder();
      General general = new General();
      BotPlayer botPlayer = new BotPlayer();

      //Erstellung der Befehlsliste für den Bot und Aufruf der entsprechenden Methoden
      api.addMessageCreateListener(event -> {
         String message = event.getMessageContent();
         String command = message.split("\s")[0].toLowerCase();
         String content = "0";

         if (message.startsWith("!del")
            || message.startsWith("!play")
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
                  SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "Privatsphäre", "Soll die Abstimmung anonym oder öffentlich erfolgen?", true,
                     Arrays.asList(
                        SlashCommandOptionChoice.create("öffentlich", "öffentlich"),
                        SlashCommandOptionChoice.create("anonym", "anonym")
                     )
                  ),
                  SlashCommandOption.createWithChoices(SlashCommandOptionType.BOOLEAN, "Mehrfachauswahl", "Soll ein User mehr als eine Antwort wählen dürfen?", true,
                     Arrays.asList(
                        SlashCommandOptionChoice.create("ja", "true"),
                        SlashCommandOptionChoice.create("nein", "false")
                     )
                  ),
                  SlashCommandOption.create(SlashCommandOptionType.STRING, "Titel", "Welchen Titel möchtest Du Deiner Umfrage geben?", true),
                  SlashCommandOption.create(SlashCommandOptionType.STRING, "Frage", "Welche Frage möchtest Du stellen?", true),
                  SlashCommandOption.create(SlashCommandOptionType.STRING, "Antwort1", "Geben Sie die erste Antwortmöglichkeit ein", true),
                  SlashCommandOption.create(SlashCommandOptionType.STRING, "Antwort2", "Geben Sie die zweite Antwortmöglichkeit ein"),
                  SlashCommandOption.create(SlashCommandOptionType.STRING, "Antwort3", "Geben Sie die dritte Antwortmöglichkeit ein"),
                  SlashCommandOption.create(SlashCommandOptionType.STRING, "Antwort4", "Geben Sie die vierte Antwortmöglichkeit ein"),
                  SlashCommandOption.create(SlashCommandOptionType.STRING, "Antwort5", "Geben Sie die fünfte Antwortmöglichkeit ein"),
                  SlashCommandOption.createLongOption("Stunden", "Wie viele Stunden soll die Umfrage aktiv sein?", false, 0, (24 * 7)),
                  SlashCommandOption.createLongOption("Minuten", "Wie viele Minuten soll die Umfrage aktiv sein?", false, 0, 59),
                  SlashCommandOption.createLongOption("Sekunden", "Wie viele Sekunden soll die Umfrage aktiv sein?", false, 0, 59)
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
            ArrayList<String> numberEmojis = new ArrayList<String>();
            ArrayList<String> numberReactions = new ArrayList<String>();
            List<User> answereList1 = new ArrayList<User>();
            List<User> answereList2 = new ArrayList<User>();
            List<User> answereList3 = new ArrayList<User>();
            List<User> answereList4 = new ArrayList<User>();
            List<User> answereList5 = new ArrayList<User>();
            final String progressBarElementFilled = "█";
            final String progressBarElementUnfilled = "░";

            // Reactions/Emojis Listen befüllen
            numberEmojis.add(":zero: ");
            numberEmojis.add(":one: ");
            numberEmojis.add(":two: ");
            numberEmojis.add(":three: ");
            numberEmojis.add(":four: ");
            numberEmojis.add(":five: ");
            numberEmojis.add(":six: ");
            numberEmojis.add(":seven: ");
            numberEmojis.add(":eight: ");
            numberEmojis.add(":nine: ");
            numberEmojis.add("\uD83D\uDD1F ");

            numberReactions.add("0️⃣");
            numberReactions.add("1️⃣");
            numberReactions.add("2️⃣");
            numberReactions.add("3️⃣");
            numberReactions.add("4️⃣");
            numberReactions.add("5️⃣");
            numberReactions.add("6️⃣");
            numberReactions.add("7️⃣");
            numberReactions.add("8️⃣");
            numberReactions.add("9️⃣");
            numberReactions.add("\uD83D\uDD1F");

            //Sicheres Abfragen aller Eingaben
            if (interaction.getOptionByName("Channel").isPresent() &&
               interaction.getOptionByName("Channel").get().getStringValue().isPresent()) {
               channel = interaction.getOptionByName("Channel").get().getChannelValue().get();
            }
            //--rufe den Channel ab und prüfe, ob es sich um einen Textchannel handelt!
            try {
               textChannel = (ServerTextChannel) channel;
            } catch (Exception e) {
               interaction.getUser().sendMessage("Bei der Erstellung Ihrer Umfrage gab es ein Problem!");
            }
            //--
            if (interaction.getOptionByName("Privatsphäre").isPresent() &&
               interaction.getOptionByName("Privatsphäre").get().getStringValue().isPresent()) {
               privacy = interaction.getOptionByName("Privatsphäre").get().getStringValue().get();
            }
            if (interaction.getOptionByName("Mehrfachauswahl").isPresent() &&
               interaction.getOptionByName("Mehrfachauswahl").get().getStringValue().isPresent()) {
               multipleChoice = Boolean.parseBoolean(interaction.getOptionByName("Mehrfachauswahl").get().getStringValue().get());
            }
            if (interaction.getOptionByName("Titel").isPresent() &&
               interaction.getOptionByName("Titel").get().getStringValue().isPresent()) {
               titel = interaction.getOptionByName("Titel").get().getStringValue().get();
            }
            if (interaction.getOptionByName("Frage").isPresent() &&
               interaction.getOptionByName("Frage").get().getStringValue().isPresent()) {
               question = interaction.getOptionByName("Frage").get().getStringValue().get();
            }
            if (interaction.getOptionByName("Antwort1").isPresent() &&
               interaction.getOptionByName("Antwort1").get().getStringValue().isPresent()) {
               String answere1 = interaction.getOptionByName("Antwort1").get().getStringValue().get();
               answereList.add(answere1);
            }
            if (interaction.getOptionByName("Antwort2").isPresent() &&
               interaction.getOptionByName("Antwort2").get().getStringValue().isPresent()) {
               String answere2 = interaction.getOptionByName("Antwort2").get().getStringValue().get();
               answereList.add(answere2);
            }
            if (interaction.getOptionByName("Antwort3").isPresent() &&
               interaction.getOptionByName("Antwort3").get().getStringValue().isPresent()) {
               String answere3 = interaction.getOptionByName("Antwort3").get().getStringValue().get();
               answereList.add(answere3);
            }
            if (interaction.getOptionByName("Antwort4").isPresent() &&
               interaction.getOptionByName("Antwort4").get().getStringValue().isPresent()) {
               String answere4 = interaction.getOptionByName("Antwort4").get().getStringValue().get();
               answereList.add(answere4);
            }
            if (interaction.getOptionByName("Antwort5").isPresent() &&
               interaction.getOptionByName("Antwort5").get().getStringValue().isPresent()) {
               String answere5 = interaction.getOptionByName("Antwort5").get().getStringValue().get();
               answereList.add(answere5);
            }
            if (privacy.equals("öffentlich")) {
               PollBuilder.createPublicPoll(event, multipleChoice, question, answereList, timeList);
            }
            if (privacy.equals("anonym")) {
               PollBuilder.createAnonymPoll(event, multipleChoice, question, answereList, timeList);
            }
            if (interaction.getOptionByName("Stunden").isPresent() &&
               interaction.getOptionByName("Stunden").get().getStringValue().isPresent()) {
               String stunden = interaction.getOptionByName("Stunden").get().getStringValue().get();
               timeList.add(Integer.parseInt(stunden));
            }
            if (interaction.getOptionByName("Minuten").isPresent() &&
               interaction.getOptionByName("Minuten").get().getStringValue().isPresent()) {
               String minuten = interaction.getOptionByName("Minuten").get().getStringValue().get();
               timeList.add(Integer.parseInt(minuten));
            }
            if (interaction.getOptionByName("Sekunden").isPresent() &&
               interaction.getOptionByName("Sekunden").get().getStringValue().isPresent()) {
               String sekunden = interaction.getOptionByName("Sekunden").get().getStringValue().get();
               timeList.add(Integer.parseInt(sekunden));
            }
         }
      };

      api.addSlashCommandCreateListener(pollCommandListener);
   }
}
