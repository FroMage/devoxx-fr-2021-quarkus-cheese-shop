package fr.fromage.cheeseshop;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Singleton;

/**
 * This implementation doesn't make any sense, but we want to use
 */
@Singleton
public class PriceService {

    private final BitcoinPrice bitcoinPrice;

    public PriceService(@RestClient BitcoinPrice bitcoinPrice) {
        this.bitcoinPrice = bitcoinPrice;
    }

    public double priceInBitcoin(Cheese type) {
        return bitcoinPrice.get("USD", type.getDollarPrice());
    }

}
