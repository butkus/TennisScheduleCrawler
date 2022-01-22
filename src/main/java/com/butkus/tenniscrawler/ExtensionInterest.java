package com.butkus.tenniscrawler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExtensionInterest {
    EARLIER("‹"),
    LATER("›"),
    ANY(" "),
    NONE(" ");

    private final String sign;
}
