package poll;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.HighLevelComponent;
import org.javacord.api.entity.message.component.LowLevelComponent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.message.reaction.ReactionRemoveEvent;
import org.javacord.api.listener.interaction.ButtonClickListener;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.javacord.api.listener.message.reaction.ReactionRemoveListener;
import org.w3c.dom.Text;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class PollBuilder {

   private final ArrayList<String> numberEmojis = new ArrayList<>();
   private final ArrayList<String> numberReactions = new ArrayList<>();

   private final String progressBarElementFilled = "█";
   private final String progressBarElementUnfilled = "░";

   public PollBuilder() {
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
   }

   public void createPoll(MessageCreateEvent event) {

      //Frage von den Antworten trennen und speichern
      String poll = event.getMessageContent().split("!poll ")[1];
      String question = poll.split("(\\? )")[0];
      String[] answeres = (poll.split("(\\? )")[1]).split("(\\! )");

      //Eingebette Message erstellen
      EmbedBuilder pollEmbeded = new EmbedBuilder()
         .setAuthor(event.getMessageAuthor())
         .setTitle("**Gildenumfrage!**")
         .addField("Frage: ", question)
         .setColor(Color.red);

      for (int i = 1; i <= answeres.length; i++) {
         pollEmbeded.addField("Antwort: ", numberEmojis.get(i) + answeres[i - 1]);
      }

      Message finishedPoll = event.getChannel().sendMessage(pollEmbeded).join();

      for (int i = 1; i <= answeres.length; i++) {

         finishedPoll.addReaction(numberReactions.get(i)).join();
      }
      event.getMessage().delete();
   }

   private EmbedBuilder createPollMessage(PollValue pollValue){
      return new EmbedBuilder()
         .setAuthor(pollValue.getEvent().getInteraction().getUser())
         .setTitle(pollValue.getTitel())
         .addField("Frage: ", pollValue.getQuestion())
         .setColor(Color.red)
         .setFooter(
            "Sichtbarkeit: " + pollValue.getPrivacy() +
               "\nMutliple Choice: " + pollValue.isMultipleChoice()
         );
   }

   private void addAnsweres(EmbedBuilder pollEmbeded, PollValue pollValue){
      for (int i = 1; i <= pollValue.getAnswereList().size(); i++) {
         pollEmbeded.addField("Antwort: ", numberEmojis.get(i) + pollValue.getAnswereList().get(i - 1));
      }
   }

   public void createPublicPollSingle(PollValue pollValue) {
      ReactionAddListener reactionAddListener = null;
      ReactionRemoveListener reactionRemoveListener = null;
      //Eingebette Message erstellen
      EmbedBuilder pollEmbeded = createPollMessage(pollValue);

      this.addAnsweres(pollEmbeded, pollValue);

      Message finishedPoll = pollValue.getTextChannel().sendMessage(pollEmbeded).join();

      for (int i = 1; i <= pollValue.getAnswereList().size(); i++) {
         finishedPoll.addReaction(numberReactions.get(i)).join();
         reactionAddListener = new ReactionAddListener() {
            @Override
            public void onReactionAdd(ReactionAddEvent event) {
               User user = event.getUser().get();
               Reaction reaction = event.getReaction().get();
               // Wenn die Reaktion von einem Bot kommt, soll sie ignoriert werden!
               try{
                  if(user.isBot()){
                     return;
                  }
                  // Wenn der User noch nicht auf der Liste ist, entferne ihn
                  if(!pollValue.getUserList1().contains(user)){
                     pollValue.getUserList1().add(user);
                  } else {
                     reaction.removeUser(user);
                  }

               } catch (Exception e){
                  ServerTextChannel bottasLog = (ServerTextChannel) event.getServer().get().getChannelsByName("bottas-log").get(0);
                  System.out.println(e.getMessage());
                  bottasLog.sendMessage(e.getMessage());
                  bottasLog.sendMessage(Arrays.toString(e.getStackTrace()));
               }
               System.out.println(pollValue.getUserList1());
            }
         };

         reactionRemoveListener = new ReactionRemoveListener() {
            @Override
            public void onReactionRemove(ReactionRemoveEvent event) {
               User user = event.getUser().get();
               List<Reaction> reactions = event.getMessage().get().getReactions();
               Set<User> tempUserList = new HashSet<>();
               boolean containsUser = false;

               reactions.forEach(reaction -> {
                  try {
                     Set<User> users = reaction.getUsers().get();
                     tempUserList.addAll(users);
                  } catch (InterruptedException | ExecutionException e) {
                     throw new RuntimeException(e);
                  }
               });

               if(tempUserList.contains(user)){
                  return;
               } else {
                  pollValue.getUserList1().remove(user);
               }
               pollValue.getUserList1().remove(user);
               System.out.println(pollValue.getUserList1());
            }
         };
      }
      finishedPoll.addReactionAddListener(reactionAddListener);
      finishedPoll.addReactionRemoveListener(reactionRemoveListener);
   }

   public void createPublicPollMulti(PollValue pollValue){
      EmbedBuilder pollEmbeded = createPollMessage(pollValue);
      this.addAnsweres(pollEmbeded, pollValue);

      Message finishedPoll = pollValue.getTextChannel().sendMessage(pollEmbeded).join();
      for (int i = 1; i <= pollValue.getAnswereList().size(); i++) {
         finishedPoll.addReaction(numberReactions.get(i)).join();
      }
   }

   public void createAnonymPollSingle(PollValue pollValue){

   }
   public void createAnonymPollMulti(PollValue pollValue) {
      EmbedBuilder pollEmbeded = createPollMessage(pollValue);
      this.addAnsweres(pollEmbeded, pollValue);
      List<ArrayList<User>> userLists = new ArrayList<>();
      List<LowLevelComponent> buttons = new ArrayList<>();
      for(int i = 0; i < pollValue.getAnswereList().size(); i++) {
         //für jeden Button soll eine neue Userliste angehangen werden.
         userLists.add(new ArrayList<User>());
         Button button = Button.primary("button" + i+1, "Option " + (i+1));
         int finalI = i;
         ButtonClickListener listener = new ButtonClickListener() {
            @Override
            public void onButtonClick(ButtonClickEvent event) {
               //Hole Benutzer, der die Interaktion ausgelöst hat
               User user = event.getInteraction().getUser();
               //Wenn die Nutzerliste des entsprechenden Buttons den Nutzer bereits enthält, lösche ihn,
               //ansonsten füge ihn hinzu
               if(userLists.get(finalI).contains(user)){
                  userLists.get(finalI).remove(user);
               } else {
                  userLists.get(finalI).add(user);
               }
               userLists.forEach(System.out::println);
            }
         };
         buttons.add(button);
      }
      MessageBuilder finishedPoll = new MessageBuilder()
         .addEmbed(pollEmbeded)
         .addComponents(ActionRow.of(buttons));
      finishedPoll.send(pollValue.getTextChannel());
   }
}
