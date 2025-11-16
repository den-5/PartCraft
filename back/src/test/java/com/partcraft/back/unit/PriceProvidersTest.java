package com.partcraft.back.unit;

import com.partcraft.back.service.PriceProvider.AlternatePriceProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class PriceProvidersTest {

    @Nested
    @DisplayName("AlternatePriceProvider Tests")
    class AlternatePriceProviderTests {

        @Test
        @DisplayName("Should return provider name")
        void testGetName() {
            AlternatePriceProvider provider = new AlternatePriceProvider();
            assertEquals("alternate", provider.getName());
        }

        @Test
        @DisplayName("Should fetch price successfully")
        void testFetchPriceSuccess() throws Exception {
            String html = """
                        <html><head>
                            <meta itemprop="price" content="299.00"/>
                        </head></html>
                    """;

            AlternatePriceProvider provider = new AlternatePriceProvider();

            try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
                org.jsoup.Connection connection = mock(org.jsoup.Connection.class);
                Document doc = Jsoup.parse(html);

                jsoupMock.when(() -> Jsoup.connect(anyString())).thenReturn(connection);
                when(connection.userAgent(anyString())).thenReturn(connection);
                when(connection.timeout(anyInt())).thenReturn(connection);
                when(connection.get()).thenReturn(doc);

                Double price = provider.fetchPrice("http://test.com/product");
                assertEquals(299.00, price);
            }
        }

        @Test
        @DisplayName("Should throw exception when price meta not found")
        void testFetchPriceMetaNotFound() throws Exception {
            String html = "<html><head></head></html>";

            AlternatePriceProvider provider = new AlternatePriceProvider();

            try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
                org.jsoup.Connection connection = mock(org.jsoup.Connection.class);
                Document doc = Jsoup.parse(html);

                jsoupMock.when(() -> Jsoup.connect(anyString())).thenReturn(connection);
                when(connection.userAgent(anyString())).thenReturn(connection);
                when(connection.timeout(anyInt())).thenReturn(connection);
                when(connection.get()).thenReturn(doc);

                RuntimeException ex = assertThrows(RuntimeException.class,
                        () -> provider.fetchPrice("http://test.com/product"));
                assertTrue(ex.getMessage().contains("Price meta not found"));
            }
        }

        @Test
        @DisplayName("Should throw exception when price value is invalid")
        void testFetchPriceInvalidValue() throws Exception {
            String html = """
                        <html><head>
                            <meta itemprop="price" content="invalid"/>
                        </head></html>
                    """;

            AlternatePriceProvider provider = new AlternatePriceProvider();

            try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
                org.jsoup.Connection connection = mock(org.jsoup.Connection.class);
                Document doc = Jsoup.parse(html);

                jsoupMock.when(() -> Jsoup.connect(anyString())).thenReturn(connection);
                when(connection.userAgent(anyString())).thenReturn(connection);
                when(connection.timeout(anyInt())).thenReturn(connection);
                when(connection.get()).thenReturn(doc);

                RuntimeException ex = assertThrows(RuntimeException.class,
                        () -> provider.fetchPrice("http://test.com/product"));
                assertTrue(ex.getMessage().contains("Failed to fetch price from Alternate"));
            }
        }

        @Test
        @DisplayName("Should parse decimal price correctly")
        void testParsePriceWithDecimals() throws Exception {
            String html = """
                        <html><head>
                            <meta itemprop="price" content="1299.99"/>
                        </head></html>
                    """;

            AlternatePriceProvider provider = new AlternatePriceProvider();

            try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
                org.jsoup.Connection connection = mock(org.jsoup.Connection.class);
                Document doc = Jsoup.parse(html);

                jsoupMock.when(() -> Jsoup.connect(anyString())).thenReturn(connection);
                when(connection.userAgent(anyString())).thenReturn(connection);
                when(connection.timeout(anyInt())).thenReturn(connection);
                when(connection.get()).thenReturn(doc);

                Double price = provider.fetchPrice("http://test.com/product");
                assertEquals(1299.99, price);
            }
        }

        @Test
        @DisplayName("Should handle connection errors")
        void testFetchPriceConnectionError() throws Exception {
            AlternatePriceProvider provider = new AlternatePriceProvider();

            try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
                org.jsoup.Connection connection = mock(org.jsoup.Connection.class);

                jsoupMock.when(() -> Jsoup.connect(anyString())).thenReturn(connection);
                when(connection.userAgent(anyString())).thenReturn(connection);
                when(connection.timeout(anyInt())).thenReturn(connection);
                when(connection.get()).thenThrow(new java.io.IOException("Connection timeout"));

                RuntimeException ex = assertThrows(RuntimeException.class,
                        () -> provider.fetchPrice("http://test.com/product"));
                assertTrue(ex.getMessage().contains("Failed to fetch price from Alternate"));
            }
        }
    }
}
