package shared;

import java.util.HashMap;
import java.util.Map;

public enum Currency {
    SGD ("SGD", 1.00),
    USD ("USD", 1.31),
    EUR ("EUR", 1.61),
    BGP ("GBP", 1.84);

    private final String abbrv;
    private final double rate;
    private static Map<String, Currency> map = new HashMap<String, Currency>();

    static {
        for (Currency currencyEnum : Currency.values()) {
            map.put(currencyEnum.abbrv, currencyEnum);
        }
    }

    Currency (String abbrv, double rate) {
        this.abbrv = abbrv;
        this.rate = rate;
    }

    public String getAbbrv() {
        return this.abbrv;
    }

    public double getRate() {
        return this.rate;
    }

    public static Currency valueFromString (String abbrv) {
        return map.get(abbrv);
    }
}
