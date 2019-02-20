import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class GameListener extends ListenerAdapter {

    boolean shut = false;
    boolean sleep = false;
    private HashMap<User, Investor> gamers;
    GameListener(HashMap<User, Investor> g) {
        gamers = g;
    }

    HashMap<User, Investor> getGamers() {
        return gamers;
    }


    /*private boolean isLeap(int year) {
        if(year % 400 == 0) {
            return true;
        }
        return year % 4 == 0 && year % 100 != 0;
    }*/

    @Override
    public void onMessageReceived(MessageReceivedEvent m) {
        Message msg = m.getMessage();
        String content = msg.getContentRaw();
        if(Pattern.matches("\\$.+", content)){
            String tag = content.substring(1);
            //System.out.println(tag);
            if(tag.equals("sleep")) {
                sleep = true;
            }
            if(tag.equals("shutdown") && (msg.getAuthor().getId().equals("393798435788161025") || msg.getAuthor().getId().equals("428645727821103115"))) {
                shut = true;
            }

            if(tag.equals("extort") && (msg.getAuthor().getId().equals("393798435788161025") || msg.getAuthor().getId().equals("428645727821103115"))) {
                MessageChannel ch = msg.getChannel();
                if(gamers.get(msg.getAuthor()) == null) {
                    ch.sendMessage("Dude, I don't even know what bank account to put it into").queue();
                }
                else {
                    gamers.get(msg.getAuthor()).addReserves();
                    ch.sendMessage("What's money to a goddess? Here, have some cash.").queue();
                }
            }
            else if(tag.equals("extort")) {
                MessageChannel ch = msg.getChannel();
                ch.sendMessage("Get out before I smite you!").queue();
            }
            if(tag.equals("help")) {
                MessageEmbed.ImageInfo img = new MessageEmbed.ImageInfo("https://i.imgur.com/zpscFD1.jpg", null, 1200, 673);
                MessageEmbed.Footer call = new MessageEmbed.Footer("Cissonia v1.0", "https://cdn.discordapp.com/attachments/379852649358426112/510109234419990528/Logo.PNG", null);
                MessageEmbed.AuthorInfo a = new MessageEmbed.AuthorInfo("Investment Information", null, null, null);
                MessageEmbed embed = new MessageEmbed(null, "*Current Investment Commands*",
                        "**$help:** Display the help page for all investment game commands." +
                                "\n\n**$join**: Join the current global investment game (with $10,000)." +
                                "\n\n**$balance**: Display user currency reserves, if game joined." +
                                "\n\n**$holdings**: Display all current user stock holdings, as well " +
                                "as current currency reserves, if user has joined the game." +
                                "\n\n**$net-worth**: Displays user net worth if user is in game." +
                                "\n\n**$buy  [AMOUNT]  [TICKER]:** Purchase stock," +
                                " if market is open, user has enough money, and game joined." +
                                "\n\n**$quote  [TICKER]:** get a stock quote, including day change.", null,
                        msg.getTimeCreated().plusSeconds(5), 15893760,null,null,
                        a, null, call, img, null);
                MessageChannel ch = msg.getChannel();
                ch.sendMessage(embed).queue();
            }
            if(tag.equals("join")) {
                User author = msg.getAuthor();
                MessageChannel ch = msg.getChannel();
                if(gamers.get(author) != null) {
                    ch.sendMessage("You are already in the investment game!").queue();
                }
                else {
                    ch.sendMessage("Ok, " + author.getAsMention() + " is added to the investment game.").queue();
                    gamers.put(author, new Investor(100000));
                }
                //System.out.println(gamers.toString());
            }
            if(tag.equals("balance")) {
                MessageChannel ch = msg.getChannel();
                User author = msg.getAuthor();
                if(gamers.get(author) == null) {
                    ch.sendMessage("Please use \"$invest join\" to join the investment " +
                            "game before assessing your balance!").queue();
                }
                else {
                    ch.sendMessage(author.getAsMention() + ", your cash reserves total $" + gamers.get(author).getReserves()).queue();
                }
            }
            if(tag.equals("holdings")) {
                MessageChannel ch = msg.getChannel();
                User author = msg.getAuthor();
                if(gamers.get(author) == null) {
                    ch.sendMessage("Please use \"$invest join\" to join the investment " +
                            "game before assessing your stock holdings!").queue();
                }
                else {
                    HashMap<String, Integer> holdings = gamers.get(author).getHoldings();
                    StringBuilder s = new StringBuilder();
                    int total = 0;
                    s.append("**Cash reserves** totaling $").append(gamers.get(author).getReserves()).append("\n");
                    total += gamers.get(author).getReserves();
                    for (String ticker : holdings.keySet()) {
                        try {
                            Document doc = Jsoup.connect("https://finance.yahoo.com/quote/" + ticker).get();
                            //System.out.println(s);
                            Element e = doc.getElementById("quote-header-info");
                            Elements data = e.getElementsByTag("span");
                            double price = Double.parseDouble(data.get(3).text());
                            s.append(holdings.get(ticker)).append(" shares of **").append(ticker).append("** worth $").append(price*holdings.get(ticker)).append("\n");
                            total += price*holdings.get(ticker);
                        } catch(MalformedURLException e){
                            ch.sendMessage("Invalid Ticker Symbol, we are investigating").queue();
                            e.printStackTrace();
                        } catch(IOException e){
                            ch.sendMessage("Connection failed :(").queue();
                            e.printStackTrace();
                        }
                    }
                    s.append("----------\n**Total Funds: $**").append(total);
                    MessageEmbed.Footer call = new MessageEmbed.Footer("Portfolio Information", "https://cdn.discordapp.com/attachments/379852649358426112/510109234419990528/Logo.PNG", null);
                    MessageEmbed.AuthorInfo a = new MessageEmbed.AuthorInfo("Cissonia, goddess of commerce", null, null, null);
                    MessageEmbed embed = new MessageEmbed(null, author.getAsTag() + "'s holdings",
                            s.toString(), null,
                            msg.getTimeCreated().plusSeconds(5), 14480383, null, null,
                            a, null, call, null, null);
                    ch.sendMessage(embed).queue();
                }
            }

            if(tag.equals("net-worth")) {
                MessageChannel ch = msg.getChannel();
                User author = msg.getAuthor();
                if(gamers.get(author) == null) {
                    ch.sendMessage("Please use \"$invest join\" to join the investment " +
                            "game before getting net worth!").queue();
                }
                else {
                    double total_worth = 0.0;
                    boolean broke = false;
                    HashMap<String, Integer> holdings = gamers.get(author).getHoldings();
                    for (String ticker : holdings.keySet()) {
                        try {
                            Document doc = Jsoup.connect("https://finance.yahoo.com/quote/" + ticker).get();
                            //System.out.println(s);
                            Element e = doc.getElementById("quote-header-info");
                            Elements data = e.getElementsByTag("span");
                            double price = Double.parseDouble(data.get(3).text());
                            total_worth += price*holdings.get(ticker);
                        } catch(MalformedURLException e){
                            ch.sendMessage("Invalid Ticker Symbol, we are investigating").queue();
                            broke = true;
                            e.printStackTrace();
                        } catch(IOException e){
                            ch.sendMessage("Connection failed :(").queue();
                            broke = true;
                            e.printStackTrace();
                        }
                    }
                    total_worth += gamers.get(author).getReserves();
                    if(broke) {
                        ch.sendMessage("Something broke, we are investigating :thinking:").queue();
                    }
                    else {
                        ch.sendMessage(author.getAsMention() + ", your net worth is $" + Math.round(total_worth*100)/100 + ", representing" +
                                " a " + Math.round((total_worth/1000.0 - 100.0)*1000.0)/1000.0 + "% overall return on investment.").queue();
                    }
                }
            }

            if(Pattern.matches("buy .+", tag)) {
                MessageChannel ch = msg.getChannel();
                User author = msg.getAuthor();
                if(gamers.get(author) == null) {
                    ch.sendMessage("Please use \"$invest join\" to join the investment " +
                            "game before buying stocks!").queue();
                }
                else {
                    StringTokenizer st = new StringTokenizer(tag);
                    st.nextToken();
                    int amount = Integer.parseInt(st.nextToken());
                    String ticker = st.nextToken();
                    try {
                        Document doc = Jsoup.connect("https://finance.yahoo.com/quote/" + ticker).get();
                        //System.out.println(s);
                        if (doc.title().equals("")) {
                            ch.sendMessage("Not a valid ticker symbol :(").queue();
                        } else {
                            Element e = doc.getElementById("quote-header-info");
                            //Get price
                            //System.out.println(e.html());
                            Elements data = e.getElementsByTag("span");
                            double price = Double.parseDouble(data.get(3).text());

                            OffsetDateTime d = msg.getTimeCreated().minusHours(6);
                            //int dayOfYear = d.getDayOfYear();
                            int hour = d.getHour();
                            int minute = d.getMinute();
                            //boolean isLeap = isLeap(d.getYear());
                            if(hour > 9 && hour < 15 || (minute >= 30 && hour == 9)) {
                                boolean ret = gamers.get(author).buyOrder(ticker, amount, price);
                                if (ret) {
                                    ch.sendMessage(amount + " shares of " + ticker + " have been bought" +
                                            " for $" + (Math.round(amount * price * 100)/100.0)).queue();
                                } else {
                                    ch.sendMessage("Not enough cash reserves :(").queue();
                                }
                            }
                            else {
                                ch.sendMessage("Stock market is closed :(").queue();
                            }
                            /*Document checkMarket = Jsoup.connect("https://www.isthemarketopen.com/").get();
                            Element el2 = checkMarket.selectFirst("div.Bnr_main").selectFirst("h4");
                            if (el2.text().contains("Stock market is closed")) {
                                ch.sendMessage("Stock market is closed :(").queue();
                            } else {
                                if (el2.getElementById("object").text().contains("The US markets will open in:")) {
                                    ch.sendMessage("Stock market is closed :(").queue();
                                } else {
                                    boolean ret = gamers.get(author).buyOrder(ticker, amount, price);
                                    if (ret) {
                                        ch.sendMessage(amount + " shares of " + ticker + " have been bought" +
                                                " for $" + (Math.round(amount * price * 100)/100.0)).queue();
                                    } else {
                                        ch.sendMessage("Not enough cash reserves :(").queue();
                                    }
                                }
                            }*/
                        }
                    } catch (MalformedURLException e) {
                        ch.sendMessage("Not a valid ticker symbol :(").queue();
                        e.printStackTrace();
                    } catch (IOException e) {
                        ch.sendMessage("Connection failed :(").queue();
                        e.printStackTrace();
                    } catch (Exception e) {
                        ch.sendMessage("Something broke, we are investigating :thinking:").queue();
                        e.printStackTrace();
                    }
                }
            }
            if(Pattern.matches("quote .+", tag)) {
                StringTokenizer st = new StringTokenizer(tag);
                st.nextToken();
                //int amount = Integer.parseInt(st.nextToken());
                String ticker = st.nextToken();
                try {
                    Document doc = Jsoup.connect("https://finance.yahoo.com/quote/" + ticker).get();
                    //System.out.println(s);
                    MessageChannel ch = msg.getChannel();
                    if(doc.title().equals("")) {
                        ch.sendMessage("Not a valid ticker symbol :(").queue();
                    }
                    else {
                        Element e = doc.getElementById("quote-header-info");
                        //Get price, and day change
                        //System.out.println(e.html());
                        Elements data = e.getElementsByTag("span");
                        String price = data.get(3).text();
                        String dayChange = data.get(4).text();
                        MessageEmbed.ImageInfo img = new MessageEmbed.ImageInfo("https://i.imgur.com/NSHrjXf.jpg", null, 1200, 673);
                        MessageEmbed.Footer call = new MessageEmbed.Footer("Stock Information", "https://cdn.discordapp.com/attachments/379852649358426112/510109234419990528/Logo.PNG", null);
                        MessageEmbed.AuthorInfo a = new MessageEmbed.AuthorInfo("Cissonia, goddess of commerce", null, null, null);
                        MessageEmbed embed = new MessageEmbed(null, e.getElementsByTag("h1").first().text(),
                                "**Current price per share:** $" + price + "\n**Day Change:** " + dayChange.substring(0, 1) + "$" + dayChange.substring(1), null,
                                msg.getTimeCreated().plusSeconds(5), 8764261,null,null,
                                a, null, call, img, null);

                        ch.sendMessage(embed).queue();
                    }
                } catch (MalformedURLException e) {
                    MessageChannel ch = msg.getChannel();
                    ch.sendMessage("Not a valid ticker symbol :(").queue();
                    e.printStackTrace();
                } catch (IOException e) {
                    MessageChannel ch = msg.getChannel();
                    ch.sendMessage("Connection failed :(").queue();
                    e.printStackTrace();
                } catch (Exception e) {
                    MessageChannel ch = msg.getChannel();
                    ch.sendMessage("Something broke, we are investigating :thinking:").queue();
                }
            }
        }
    }
}
