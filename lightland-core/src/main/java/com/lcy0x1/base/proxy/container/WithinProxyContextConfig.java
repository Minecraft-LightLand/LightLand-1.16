package com.lcy0x1.base.proxy.container;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WithinProxyContextConfig {
    final boolean block;
    final boolean proxy;
    final boolean pre;
    final boolean preSuper;

    public WithinProxyContextConfig() {
        this(false, false, false, false);
    }
}
