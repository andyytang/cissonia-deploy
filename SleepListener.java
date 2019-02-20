import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SleepListener extends ListenerAdapter {

    boolean awakened = false;

    @Override
    public void onMessageReceived(MessageReceivedEvent m) {
        if((m.getAuthor().getId().equals("428645727821103115") || m.getAuthor().getId().equals("393798435788161025")) &&
                m.getMessage().getContentRaw().equals("$awaken")) {
            m.getMessage().getChannel().sendMessage("I̺̜͛͒ͤ̌̃̕ ̰̈́̆̈́͒̽͂̄ä̹̜̦̥̩̆ͣ͜w̷͉̜̪̩ͮ̽ͧ͗̃͊a̩͍̺̤̘͐ͧ̓̀͛ͨ̿k͓̼̯͗̃̓e͍͝n͈͍ͧͪ̄͞ ̗͉̭͙͊͗ͩf̙̫͊͂̆͗r̸̼͙͕̭̳ͬͩͯ͆ͩo̤̙̟̦͉͉͍ͧ̉̾ͬm͖̩͖̓ͬ̓̌͐ͅ ̻͎̱͔̯̯ͫͬ͛ͮ̃m̲͕̃̉ͬ͐ͭ͐y͕̆̆̐͑ͩ͋ ̢̎̅̉ͣ̚ͅͅã̞̳̞̣͉e̥̻̯͚̰̱̠̓̆ͦ͛̾̌̈t̵̥̲͇̠̼͋̉͑̈́̏̂̅e͉̘̋̈r̹͓͓̈́ͤ̊̈́̓n̜͙̞̠͂͛ͅã̗l̴̾̆̒̎ͯ̿͋ ̣̬̗̦̘̯̒̒ͮ̓̈ͫͧͅs̢͕̝͈͌͗̌̍̏l̹̩̬̱̠̹̉̕u͇̩̿ͯͥ̃̎ͯ̅͘m̱̤̭͖̳̤͐̉̎̀̌b̷͖̩̯͚͍̭͌͌ĕ̩͎͔̞̬̻̆ͧ͗r͕̟͉̮̲̈́͒.͈͔̩͉͈̣̗͊̄͠.͓̘.̬̼̺̀").queue();
            awakened = true;
        }
    }
}
