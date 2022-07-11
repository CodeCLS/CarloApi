package carlo.api;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum BillingType {
    SUBSCRIPTION_A, SUBSCRIPTION_B, SUBSCRIPTION_C, INVALID;
    private static final Map<String, BillingType> ENUM_MAP = Stream.of(BillingType.values())
            .collect(Collectors.toMap(Enum::name, Function.identity()));

    public static BillingType of(final String name) {
        return ENUM_MAP.getOrDefault(name, INVALID);
    }
}
