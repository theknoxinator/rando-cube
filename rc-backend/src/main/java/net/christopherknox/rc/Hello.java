package net.christopherknox.rc;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Hello {
    private String content;

    public Hello() {
        content = "Hello!";
    }
}
