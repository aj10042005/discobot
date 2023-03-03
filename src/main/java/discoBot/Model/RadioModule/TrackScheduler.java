package discoBot.Model.RadioModule;


import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.LinkedList;
import java.util.Queue;


public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final AudioPlayerManager audiMan;
    private final Queue<String> playlist;

    public TrackScheduler(AudioPlayer player, AudioPlayerManager man) {
        this.player = player;
        this.audiMan = man;
        this.playlist = new LinkedList<String>();
    }
    public void startTrack() {

        if(!playlist.isEmpty())audiMan.loadItem(playlist.poll(), new FunctionalResultHandler(null, playlist -> {
            player.playTrack(playlist.getTracks().get(0));
        }, null, null));
        else System.err.println("Playlist empty!");
    }

    public boolean setIsPaused() {
        player.setPaused(!player.isPaused());return player.isPaused();
    }
    public void stopTrack() {
        player.stopTrack();
    }

    public void addTrack(String trackName) {
        boolean flag=false;
        try { player.getPlayingTrack().getInfo();
        } catch(Exception e) {
            flag = true;
        }
        System.out.println(flag);
        playlist.add(trackName);
        if(flag)startTrack();
    }
    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {}
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) startTrack();
    }
    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        System.err.println("Track "+playlist.peek()+" stuck! Playing the next track");
        startTrack();
    }
    @Override
    public void onPlayerResume(AudioPlayer player) {}
    @Override
    public void onPlayerPause(AudioPlayer player) {}
}