package shared;

import java.util.HashMap;
import java.util.Map;

public enum Currency {
    SGD ("SGD", 1.00),
    USD ("USD", 1.31),
    EUR ("EUR", 1.61),
    BGP ("GBP", 1.84);

    private final String id;
    private final double rate;
    private static Map<String, Currency> map = new HashMap<String, Currency>();

    static {
        for (Currency currencyEnum : Currency.values()) {
            map.put(currencyEnum.id, currencyEnum);
        }
    }

    Currency (String id, double rate) {
        this.id = id;
        this.rate = rate;
    }

    public String getAbbrv() {
        return this.id;
    }
    public static Currency valueOf (int String) {
        return map.get(String);
    }
}
