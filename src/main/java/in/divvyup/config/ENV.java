package in.divvyup.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ENV {
    DEV("dev"),
    TEST("test");

    private final String name;
}
