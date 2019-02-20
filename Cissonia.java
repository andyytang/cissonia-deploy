import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class Cissonia {
    private static HashMap<User, Investor> gamers = new HashMap<>();
    public static void main (String[] args) throws LoginException, InterruptedException, IOException {
        BufferedReader f = new BufferedReader(new FileReader("C:/[PLACEHOLDER PATH TO TXT FILE]"));


        JDA cissonia = new JDABuilder("[BOT TOKEN]").build();
        cissonia.awaitReady().getPresence().setActivity(Activity.listening("the forex rates"));

        String s;
        while((s = f.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(s);
            User u = cissonia.awaitReady().getUserById(st.nextToken());
            System.out.println(u);
            double i = Double.parseDouble(st.nextToken());
            int numnext = Integer.parseInt(st.nextToken());
            HashMap<String, Integer> stocks = new HashMap<>();
            for(int j = 0; j < numnext; j++) {
                String s2 = f.readLine();
                StringTokenizer st2 = new StringTokenizer(s2);
                String symbol = st2.nextToken();
                int number = Integer.parseInt(st2.nextToken());
                stocks.put(symbol, number);
            }
            gamers.put(u, new Investor(i, stocks));
        }

        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("C:/[PLACEHOLDER PATH TO TXT FILE]")));


        cissonia.addEventListener(new GameListener(gamers));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for(User u : gamers.keySet()) {
                Investor investor = gamers.get(u);
                System.out.println(u.getId() + " " + investor.getReserves()
                        + " " + investor.getHoldings().keySet().size());
                out.println(u.getId() + " " + investor.getReserves()
                        + " " + investor.getHoldings().keySet().size());
                for(String key : investor.getHoldings().keySet()) {
                    out.println(key + " " + investor.getHoldings().get(key));
                }
            }
            out.close();
        }));

        while(true) {
            List<Object> a = cissonia.getRegisteredListeners();
            for (Object x : a) {
                if (GameListener.class == x.getClass()) {
                    GameListener g = (GameListener) x;
                    if (System.currentTimeMillis() % 10000 == 0) {
                        gamers = g.getGamers();
                    }
                    if(g.sleep) {
                        cissonia.removeEventListener(cissonia.getRegisteredListeners().toArray());
                        cissonia.addEventListener(new SleepListener());
                        cissonia.getPresence().setStatus(OnlineStatus.INVISIBLE);
                    }
                    if(g.shut) {
                        System.exit(0);
                    }
                }
                if (SleepListener.class == x.getClass()) {
                    try {
                        SleepListener sleep = (SleepListener) x;
                        if (sleep.awakened) {
                            cissonia.removeEventListener(cissonia.getRegisteredListeners().toArray());
                            cissonia.addEventListener(new GameListener(gamers));
                            cissonia.getPresence().setStatus(OnlineStatus.ONLINE);
                        }
                    } catch(ClassCastException c) {
                        c.printStackTrace();
                    }
                }
            }
        }
    }
}
