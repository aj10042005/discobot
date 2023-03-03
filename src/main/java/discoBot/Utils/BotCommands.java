package discoBot.Utils;

import discoBot.Model.Calculator;
import discoBot.Model.RadioModule.RadioModule;
import discoBot.Model.SampleClass;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

public class BotCommands {

    public static Map<String, BotCommand> commands = new HashMap<String, BotCommand>();
    public static void initCommands() {
        //Main
        commands.put("chunk", new BotCommand(){
            @Override
            public Object call(MessageReceivedEvent evt, Object in) {
                return SampleClass.SampleClass();
            }
        });

        commands.put("calc", new BotCommand() {
            @Override
            public Object call(MessageReceivedEvent evt, Object in) {
                return Calculator.calc(in.toString());
            }
        });
        commands.put("polish", new BotCommand() {
            @Override
            public Object call(MessageReceivedEvent evt, Object in) {
                return Calculator.toReversePolishNotation(in.toString().toCharArray());
            }
        });
        commands.put("puts", new BotCommand() {
            @Override
            public Object call(MessageReceivedEvent evt, Object in) {
                return in.toString();
            }
        });
        commands.put("len", new BotCommand() {
            @Override
            public Object call(MessageReceivedEvent evt, Object in) {
                return in.toString().length();
            }
        });
        commands.put("help", new BotCommand() {
            @Override
            public Object call(MessageReceivedEvent evt, Object in) {
                StringBuffer helplist = new StringBuffer();
                commands.forEach((s, botCommand) -> {
                    helplist.append(s).append("\n");
                });
                return helplist.toString();
            }
        });

        //External
        RadioModule.init(commands);
    }
}
