package general;

import org.javacord.api.event.message.MessageCreateEvent;

public class General {

    public void zeigeAnleitung(MessageCreateEvent event) {
        event.getChannel().sendMessage("""
                Hier findest Du eine Anleitung zu den atkuell integrierten Befehlen:
                - **!Hallo** (Bottas wird dich zurück grüßen!)
                - **!poll Frage? Antwort! Antwort! ...** So kann eine Umfrage gestartet werden. Es können bis zu 10 Antworten angegeben werden.
                        Beispiel: !poll Ist der Bot nicht geil? Ja! Juicy ist geiler! Ich mag Züge!
                - **!del x** (löscht die letzten x Nachrichten eines Channels. Für "x" kann eine beliebige Zahl eingesetzt werden.)
                - **!mute** (mutet alle Teilnehmer des Voicechannels, in dem sich der Autor des Befehls befindet)
                - **!unmute** (unmutet alle Teilnehmer des Voicechannels, in dem sich der Autor des Befehls befindet)
                - **!play x** (Bottas tritt dem Channel bei und spielt Musik ab. An der Stelle von x muss z.B. ein Youtubelink eingefügt werden.
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
                - **!stop** (stoppt die Musikwiedergabe von Bottas sofort und disconnected ihn vom Channel)"""
        );
    }

    public void sagHallo(MessageCreateEvent event){
        event.getChannel().sendMessage("Hallo " + event.getMessageAuthor().getDisplayName());
    }
}
