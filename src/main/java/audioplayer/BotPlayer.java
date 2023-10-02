package audioplayer;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.event.message.MessageCreateEvent;


public class BotPlayer {

    public void play(DiscordApi api, MessageCreateEvent event, String content) {

        if (event.getMessageAuthor().getConnectedVoiceChannel().isPresent()) {
            event.getMessageAuthor().getConnectedVoiceChannel().get().connect().thenAccept(audioConnection -> {

                // Create a player manager
                AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
                playerManager.registerSourceManager(new YoutubeAudioSourceManager());
                playerManager.registerSourceManager(new LocalAudioSourceManager());
                AudioSourceManagers.registerLocalSource(playerManager);
                AudioPlayer player = playerManager.createPlayer();
                TrackScheduler trackScheduler = new TrackScheduler();
                player.addListener(trackScheduler);

                // Create an audio source and add it to the audio connection's queue
                AudioSource source = new LavaplayerAudioSource(api, player);
                audioConnection.setAudioSource(source);

                api.addMessageCreateListener(stopEvent -> {
                    if(stopEvent.getMessageContent().startsWith("!stop")){
                        audioConnection.close();
                    }
                });

                // You can now use the AudioPlayer like you would normally do with Lavaplayer, e.g.,
                playerManager.loadItem(content, new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack track) {
                        player.playTrack(track);
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {
                        for (AudioTrack track : playlist.getTracks()) {
                            player.playTrack(track);
                        }
                    }

                    @Override
                    public void noMatches() {
                        // Notify the user that we've got nothing
                    }

                    @Override
                    public void loadFailed(FriendlyException throwable) {
                        // Notify the user that everything exploded
                    }
                });
            }).exceptionally(e -> {
                System.out.println(e.getMessage());
                return null;
            });
        }
    };
}
