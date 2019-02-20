/******************************************************************************
 *  Dependencies: java.util.HashMap
 *
 *  Simple Investor Class. Packages together cash and share holdings in a single class.
 *  Will update SOON(TM) to include bonds and forex currencies.
 *
 ******************************************************************************/

import java.util.HashMap;

class Investor {
    // Global variables storing cash and shares (shares in a hashmap TICKER --> AMOUNT)
    private double cash_reserves;
    private HashMap<String, Integer> shares = new HashMap<>();

    // Create a new investor with pure funds
    Investor(double cash) {
        cash_reserves = cash;
    }
    
    // Create a new investor with funds and stock holdings
    Investor(double cash, HashMap<String, Integer> stocks) {
        cash_reserves = cash;
        shares = stocks;
    }

    // Get investor's cash reserves
    double getReserves() {
        return cash_reserves;
    }

    // Boost investor's reserves: Note that this is not intended to be used
    void addReserves() {
        cash_reserves += 90000;
    }

    // Get investor's share holdings in a HashMap TICKER --> AMOUNT style
    HashMap<String, Integer> getHoldings() {
        return shares;
    }
    
    // Buy shares, if there is enough cash
    boolean buyOrder(String ticker, int amount, double price) {
        // Don't buy if not enough money
        if(price*amount > cash_reserves) {
            return false;
        }
        else {
            // May be bugged, will check back later
            shares.put(ticker, amount);
            cash_reserves -= (double) amount*price;
            cash_reserves = Math.round(cash_reserves*100)/100.0;
            return true;
        }
    }

}
