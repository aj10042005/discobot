package discoBot.Model.RadioModule;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import discoBot.Utils.BotCommand;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public abstract class RadioModule  {
    private static final float[] BASS_BOOST = { 0.2f, 0.15f, 0.1f, 0.05f, 0.0f, -0.05f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f,
            -0.1f, -0.1f, -0.1f, -0.1f };
    private static AudioPlayerManager playerManager;
    private static AudioPlayer player;
    private static TrackScheduler trackScheduler;
    private static EqualizerFactory equalizer;

    public static void init(Map<String, BotCommand> commands) {
        //OBJECTS
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        player=playerManager.createPlayer();

        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);

        trackScheduler=new TrackScheduler(player, playerManager);
        player.addListener(trackScheduler);

        equalizer = new EqualizerFactory();
        player.setFilterFactory(equalizer);
        setBassBoost(-0.5f);

        //INDIAN CODE

        playerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.LOW);
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new BeamAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        playerManager.registerSourceManager(new LocalAudioSourceManager());
        //FUNCTIONAL
        commands.put("addtrack", new BotCommand() {
            @Override
            public Object call(MessageReceivedEvent evt, Object in) {
                trackScheduler.addTrack(in.toString());
                //trackScheduler.addToQueue(player.getPlayingTrack());

                return in+" added to the bot playlist";
            }
        });
        commands.put("nexttrack", new BotCommand() {
            @Override
            public Object call(MessageReceivedEvent evt, Object in) {
                trackScheduler.startTrack();
                return "Skipped to next track";
            }
        });
        commands.put("setbassboost", new BotCommand() {
            @Override
            public Object call(MessageReceivedEvent evt, Object in) {
                float off = Float.parseFloat(in.toString());
                boolean isCacaphony = off>0.15||(off<0&&off>=-0.25f);
                if(player.getPlayingTrack()!=null&&isCacaphony)return "No way you do that, smartass";
                setBassBoost(off);
                return (isCacaphony)?"I'm not responsible for you having your ears off. Turning this shit up to "+off:"Bass boosted by "+off;
            }
        });
        commands.put("gettrack", new BotCommand() {
            @Override
            public Object call(MessageReceivedEvent evt, Object in) {
                String retVal;

                try { retVal = player.getPlayingTrack().getInfo().title;
                } catch(Exception e) {
                    retVal="none";}

                return "Track: "+retVal;
            }
        });
        commands.put("stoptrack", new BotCommand() {
            @Override
            public Object call(MessageReceivedEvent evt, Object in) {
                String retVal;

                try { retVal = player.getPlayingTrack().getInfo().title;
                } catch(Exception e) {
                    retVal="none";}

                return "Track: "+retVal;
            }
        });
        commands.put("setpaused", new BotCommand() {
            @Override
            public Object call(MessageReceivedEvent evt, Object in) {
                return "Track paused: "+ trackScheduler.setIsPaused();
            }
        });
        commands.put("connectradio", new BotCommand() {
            @Override
            public Object call(MessageReceivedEvent evt, Object in) {
                try {
                    evt.getGuild().getAudioManager().openAudioConnection(evt.getGuild().getVoiceChannelsByName(in.toString(), true).get(0));
                    evt.getGuild().getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
                }catch(Exception e){
                    return e.getMessage();
                }
                return "Successfully connected to the voice channel "+in;
            }
        });
    }

    private static void setBassBoost(float off) {
        for (int i = 0; i < BASS_BOOST.length; i++) {
            equalizer.setGain(i, (off<0?-1:1)*BASS_BOOST[i]+off);
        }

    }

}