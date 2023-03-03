package discoBot.Utils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.Callable;
import java.util.function.Supplier;



public interface BotCommand<MessageRecievedEvent, String> {
    String call(MessageReceivedEvent evt,String in);
}
