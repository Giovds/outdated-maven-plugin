package com.giovds;

import com.giovds.dto.PomResponse;
import com.giovds.dto.Scm;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class PomClient implements PomClientInterface {

    private final String basePath;
    private final String pomPathTemplate;

    private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    private final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    private final Log log;

    public PomClient(Log log) {
        this("https://repo1.maven.org", "/maven2/%s/%s/%s/%s-%s.pom", log);
    }

    public PomClient(String basePath, String pomPathTemplate, Log log) {
        this.basePath = basePath;
        this.pomPathTemplate = pomPathTemplate;
        this.log = log;
    }

    public PomResponse getPom(String group, String artifact, String version) throws IOException, InterruptedException {
        final String path = String.format(pomPathTemplate, group.replace(".", "/"), artifact, version, artifact, version);
        final HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(basePath + path))
                .build();

        return client.send(request, new PomResponseBodyHandler()).body();
    }

    private class PomResponseBodyHandler implements HttpResponse.BodyHandler<PomResponse> {

        @Override
        public HttpResponse.BodySubscriber<PomResponse> apply(final HttpResponse.ResponseInfo responseInfo) {
            int statusCode = responseInfo.statusCode();

            if (statusCode < 200 || statusCode >= 300) {
                return HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8), s -> {
                    throw new RuntimeException("Search failed: status: %d body: %s".formatted(responseInfo.statusCode(), s));
                });
            }

            HttpResponse.BodySubscriber<InputStream> stream = HttpResponse.BodySubscribers.ofInputStream();

            return HttpResponse.BodySubscribers.mapping(stream, this::toPomResponse);
        }

        private PomResponse toPomResponse(final InputStream inputStream) {
            try (final InputStream input = inputStream) {
                DocumentBuilder documentBuilder = PomClient.this.documentBuilderFactory.newDocumentBuilder();
                Document doc = documentBuilder.parse(input);

                doc.getDocumentElement().normalize();

                Element root = doc.getDocumentElement();
                NodeList urlNodes = root.getElementsByTagName("url");

                if (urlNodes.getLength() == 0) {
                    return PomResponse.empty();
                }
                String url = urlNodes.item(0).getTextContent();

                Scm scm = Scm.empty();
                NodeList scmNodes = root.getElementsByTagName("scm");
                if (scmNodes.getLength() > 0) {
                    Element scmElement = (Element) scmNodes.item(0);
                    NodeList scmUrlNodes = scmElement.getElementsByTagName("url");
                    if (scmUrlNodes.getLength() > 0) {
                        String scmUrl = scmUrlNodes.item(0).getTextContent();
                        scm = new Scm(scmUrl);
                    }
                }

                return new PomResponse(url, scm);
            } catch (IOException | ParserConfigurationException | SAXException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
