package general;

import org.javacord.api.event.message.MessageCreateEvent;

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
}
