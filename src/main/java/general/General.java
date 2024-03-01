package general;

import org.javacord.api.event.message.MessageCreateEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class General {

    public void zeigeAnleitung(MessageCreateEvent event) {
        event.getChannel().sendMessage("""
                Hier findest Du eine Anleitung zu den atkuell integrierten Befehlen:
                **Alle Befehle sind nicht Case-sensitiv, können also groß, klein oder wie auch immer geschrieben werden.**
                - **!Hallo** (Bottas wird dich zurück grüßen!)
                - **!Poll Frage? Antwort! Antwort! ...** So kann eine Umfrage gestartet werden. Es können bis zu 10 Antworten angegeben werden.
                        Beispiel: !poll Ist der Bot nicht geil? Ja! Juicy ist geiler! Ich mag Züge!
                - **!Del x** (löscht die letzten x Nachrichten eines Channels. Für "x" kann eine beliebige Zahl eingesetzt werden.)
                - **!MuteAll** (mutet alle Teilnehmer des Voicechannels, in dem sich der Autor des Befehls befindet)
                - **!UnmuteAll** (unmutet alle Teilnehmer des Voicechannels, in dem sich der Autor des Befehls befindet)
                - **!roll xdy** (x ist die Anzahl der Würfe, y die Augenzahl des Würfels)
                - **!rollDestiny xdy** (wie "!roll", nur kann jedes Ergebnis nur ein einziges Mal vorkommen)
                - **!rollDestinyBetween xdy-z** (wie !rollDestiny, mit dem Unterschied, dass ein Wertebereich angegeben werden kann!)
                - **!Play x** (Bottas tritt dem Channel bei und spielt Musik ab. An der Stelle von x muss z.B. ein Youtubelink eingefügt werden.
                        Erlaubte Formate sind:
                        YouTube
                        SoundCloud
                        Bandcamp
                        Vimeo
                        Twitch streams
                        Local files
                        HTTP URLs
                        (Playlists funktionieren nicht)
                        (Eine Track Queue ist in Arbeit)
                - **!Stop** (stoppt die Musikwiedergabe von Bottas sofort und disconnected ihn vom Channel)
                
                **Viel Spaß mit Bottas!**
                """
        );
    }

    public void sagHallo(MessageCreateEvent event){
        event.getChannel().sendMessage("Hallo " + event.getMessageAuthor().getDisplayName());
    }

    public void shutdown(MessageCreateEvent event){
        long creatorID = event.getMessageAuthor().getId();
        if(creatorID == 394445942478209024L /*DEEMO*/ || creatorID == 225647075897901068L /*JUICY*/){
            System.exit(0);
        }
    }

    public void rollDice(MessageCreateEvent event, String content) {
        String[] numbers = content.split("d");
        StringBuilder message = new StringBuilder();
        int count = Integer.parseInt(numbers[0]);
        int dice = Integer.parseInt(numbers[1]);
        if (count > 5){
            event.getChannel().sendMessage("Würfe können nur bis 5 Wiederholungen gerollt werden!");
            return;
        }
        for(int i = 0; i < count; i++){
            double result = Math.floor(Math.random()*dice)+1;
            message.append("Das Ergebnis ist: ").append((int) result).append("\n");
        }
        event.getChannel().sendMessage(message.toString());
    }

    public void rollDestiny(MessageCreateEvent event, String content) {
        String[] numbers = content.split("d");
        int count = Integer.parseInt(numbers[0]);
        int dice = Integer.parseInt(numbers[1]);
        int result = 0;
        StringBuilder message = new StringBuilder();
        List<Integer> numbersList = new ArrayList<>();
        if (count > 5){
            event.getChannel().sendMessage("Schicksalswürfe können nur bis 5 Wiederholungen gerollt werden!");
            return;
        }
        if(dice < count) {
            event.getChannel().sendMessage("Die Anzahl der Würfe ist größer als die Anzahl der Möglichkeiten! Überprüfe deine Eingabe!");
            return;
        }
        for(int i = 0; i < count; i++){
            do {
                result = (int)Math.floor(Math.random()*dice)+1;
            }
            while (numbersList.contains(result));
            numbersList.add(result);
        }
        numbersList.forEach(number -> {
            message.append("Das Ergebnis ist: ").append(number).append("\n");
        });
        event.getChannel().sendMessage(message.toString());
    }

    public void rollDestinyBetween(MessageCreateEvent event, String content){
        String[] numbers = content.split("d");
        int count = Integer.parseInt(numbers[0]);
        String[] range = numbers[1].split("-");
        int lowerRange = Integer.parseInt(range[0]);
        int higherRange = Integer.parseInt(range[1].replace("-", ""));
        List<Integer> numbersList = new ArrayList<>();
        StringBuilder message = new StringBuilder();
        int result = 0;

        if (count > 5){
            event.getChannel().sendMessage("Schicksalswürfe können nur bis 5 Wiederholungen gerollt werden!");
            return;
        }
        if(higherRange < lowerRange) {
            event.getChannel().sendMessage("Der Wertebereich ist ungültig! Überprüfe deine Eingabe!");
            return;
        }
        if(higherRange-(lowerRange-1) < count){
            event.getChannel().sendMessage("Die Anzahl der Würfe ist größer als die Anzahl der Möglichkeiten! Überprüfe deine Eingabe!");
            return;
        }
        for(int i = 0; i < count; i++){
            do {
                result = (int)Math.floor(Math.random()*((higherRange)-(lowerRange-1)))+lowerRange;
            }
            while (numbersList.contains(result));
            numbersList.add(result);
        }
        numbersList.forEach(number -> {
            message.append("Das Ergebnis ist: ").append(number).append("\n");
        });
        event.getChannel().sendMessage(message.toString());
    }
}
