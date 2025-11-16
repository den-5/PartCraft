package com.partcraft.back.service.PriceProvider;

public interface PriceProvider {
    Double fetchPrice(String url);

    String getName();
}
