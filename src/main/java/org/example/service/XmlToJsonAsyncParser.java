package org.example.service;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.example.model.Violation;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlToJsonAsyncParser {

    private static Map<String, Double> violationsMap = new HashMap<>();
    private static File violationsPackage = new File("src/main/resources/violations");
    private static File[] violations = violationsPackage.listFiles();

    public static void main(String[] args) {
        int threadsN = 8;
        long start = System.currentTimeMillis();
        startParseWithUsingThreads(threadsN);
        System.out.println(System.currentTimeMillis() - start);
    }

    public static void startParseWithUsingThreads(int threadsN) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadsN);

        for (File file : violations) {
            CompletableFuture.supplyAsync(() -> file, executorService)
                    .thenAccept(e -> {
                        try {
                            getSummaryViolatesFromXmlToJson(e);
                            System.out.println(Thread.currentThread().getName()
                                    + " " + file.getName());
                        } catch (ParserConfigurationException | SAXException | IOException ex) {
                            ex.printStackTrace();
                        }
                    });
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            parseMapToJson();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void parseMapToJson() throws IOException {
        List<Entry<String, Double>> returned = violationsMap.entrySet().stream()
                .sorted((e1, e2) -> -e1.getValue().compareTo(e2.getValue())).toList();

        ObjectMapper jsonMapper = new ObjectMapper();
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(
                new File("src/main/resources/outputFiles/statistic.json"), JsonEncoding.UTF8);
        jsonGenerator.setCodec(jsonMapper);

        for (Entry<String, Double> pair : returned) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("title", pair.getKey());
            jsonGenerator.writeObjectField("amount", pair.getValue());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.close();
    }

    private static void getSummaryViolatesFromXmlToJson(File file)
            throws ParserConfigurationException, SAXException, IOException {

        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser parser = parserFactory.newSAXParser();
        SaxHandler handler = new SaxHandler();
        parser.parse(violationsPackage + "/" + file.getName(), handler);

        for (Violation violate : handler.violates) {
            putInMap(violate);
        }
    }

    private synchronized static void putInMap(Violation violate) {
        if (!violationsMap.containsKey(violate.getType())) {
            violationsMap.put(violate.getType(), violate.getFineAmount());
        } else {
            violationsMap.put(violate.getType(),
                    violationsMap.get(violate.getType()) + violate.getFineAmount());
        }
    }

    static class SaxHandler extends DefaultHandler {
        private List<Violation> violates = new ArrayList<>();
        private Violation violate = null;
        private String content = null;

        @Override
        public void startElement(String uri, String localName, String nameQ,
                                   Attributes attributes)
                  throws SAXException {
            switch (nameQ) {
                case "violation": {
                    violate = new Violation();
                    break;
                }
            }

        }

        @Override
      public void endElement(String uri, String localName, String nameQ) throws SAXException {
            switch (nameQ) {
                case "violation": {
                    violates.add(violate);
                    break;
                }
                case "date_time": {
                    violate.setDate(content);
                    break;
                }
                case "first_name": {
                    violate.setFirstName(content);
                    break;
                }
                case "last_name": {
                    violate.setLastName(content);
                    break;
                }
                case "type": {
                    violate.setType(content);
                    break;
                }
                case "fine_amount": {
                    violate.setFineAmount(Double.valueOf(content));
                    break;
                }
            }
        }

        @Override
      public void characters(char[] ch, int start, int length) throws SAXException {
            content = String.copyValueOf(ch, start, length).trim();
        }
    }
}
