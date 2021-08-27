package com.lcy0x1.base.proxy.container;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WithinProxyContextConfig {
    final boolean block;
    final boolean proxy;

    public WithinProxyContextConfig() {
        this(false, false);
    }
}
