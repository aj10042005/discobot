package discoBot;

import discoBot.Utils.BotCommand;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import org.json.simple.*;

import javax.security.auth.login.LoginException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import discoBot.Utils.BotCommands;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main extends ListenerAdapter {
    private static final String settingsFilePath = "settings.json";
    private static String cmdPrefix;
    public static String defaultBroadcastChannel = "default_broadcast_channel";

    public static void main(String[] args) throws LoginException, IOException {

        JSONParser jsp = new JSONParser();

        try {
            Object obj = jsp.parse(new FileReader(settingsFilePath));

            JSONObject jsonObject = (JSONObject) obj;

            JDABuilder builder = JDABuilder.createLight( (String)jsonObject.get("token"), GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES);

            builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
            builder.setBulkDeleteSplittingEnabled(false);
            builder.setActivity(Activity.playing( (String)jsonObject.get("activity")) );
            builder.addEventListeners(new Main());

            BotCommands.initCommands();

            builder.build();

            cmdPrefix=(String)jsonObject.get("command_prefix");

        } catch(IOException e) {
            System.err.println("No JSON found");

            File settingsFileIns = new File(settingsFilePath);
            settingsFileIns.createNewFile();

            return;
        } catch (ParseException e) {
            System.err.println("Bad JSON-configuration given");
            return;
        }

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent evt) {
        if(!evt.getAuthor().isBot()) {

            String usrInput = evt.getMessage().getContentRaw();

            if(!usrInput.startsWith(cmdPrefix)) return;

            int cmdlen, prefixLen = cmdPrefix.length();
            boolean noArgCmd, cmdWithArgs;

            StringBuffer com = new StringBuffer().append(cmdPrefix);

            for(Map.Entry<String, BotCommand> command: BotCommands.commands.entrySet()) {

                noArgCmd = usrInput.contains( com.append( command.getKey()) ) && usrInput.length()==prefixLen+command.getKey().length();
                cmdWithArgs = usrInput.contains(com.append('\u0020'));

                com.setLength(prefixLen);

                if ( noArgCmd || cmdWithArgs ) {
                    try {
                        cmdlen = command.getKey().length();
                        String feed = usrInput.substring(prefixLen+cmdlen+((usrInput.length()>cmdlen+prefixLen)?1:0));
                        evt.getChannel().sendMessage(command.getValue().call(evt, feed).toString()).queue();
                    }
                    catch (Exception e) {
                        System.err.println("Failed to run " + usrInput + "\nGenerated exception: " + e.getMessage());
                        evt.getChannel().sendMessage("Failed to run " + usrInput + "\nGenerated exception: " + e.getMessage()).queue();
                    }

                    break;
                }

            }
        }
    }

}