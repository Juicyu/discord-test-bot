package poll;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.ArrayList;

public class PollBuilder {

    private static ArrayList<String> numberEmojis = new ArrayList<>();
    private static ArrayList<String> numberReactions = new ArrayList<>();

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

    public static void createPoll(MessageCreateEvent event) {

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
            pollEmbeded.addField("Antwort: ", numberEmojis.get(i) + answeres[i-1]);
        }

        Message finishedPoll = event.getChannel().sendMessage(pollEmbeded).join();

        for (int i = 1; i <= answeres.length; i++) {

            finishedPoll.addReaction(numberReactions.get(i)).join();
        }
        event.getMessage().delete();
    }
}
