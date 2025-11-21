package com.partcraft.back.service.PriceProvider;

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

            Element priceElement = doc.selectFirst("span.price");

            if (priceElement == null) {
                throw new RuntimeException("Price element not found");
            }

            String rawValue = priceElement.text();

            // Clean the price: remove "€" and spaces, replace comma with dot
            String cleanedPrice = rawValue
                    .replace("€", "")
                    .replace(" ", "")
                    .replace(",", ".")
                    .trim();

            return Double.parseDouble(cleanedPrice);

        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid price format", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch price from Alternate", e);
        }
    }

}
