package com.partcraft.back.service.PriceProvider;

import com.partcraft.back.service.PriceProvider.PriceProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public class AlternatePriceProvider implements PriceProvider {

    @Override
    public String getName() {
        return "alternate";
    }

    @Override
    public Double fetchPrice(String productUrl) {
        try {
            Document doc = Jsoup.connect(productUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(8000)
                    .get();

            // meta tag is the most stable
            Element priceMeta = doc.selectFirst("meta[itemprop=price]");

            if (priceMeta == null) {
                throw new RuntimeException("Price meta not found");
            }

            String rawValue = priceMeta.attr("content"); // "299.00"

            return Double.parseDouble(rawValue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch price from Alternate", e);
        }
    }
}
