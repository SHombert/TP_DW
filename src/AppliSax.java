import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

public class AppliSax {

    public static void main(String[] args) {
        // Parser
        // instanciation classe handler
        DefaultHandler handler = new InfoSax();
        try {
            XMLReader saxParser = XMLReaderFactory.createXMLReader();

            // check ignore whitespace
            saxParser.setContentHandler(handler);
            saxParser.setErrorHandler(handler);
            saxParser.parse("assemblee1920.xml");
        } catch (Throwable t){
            t.printStackTrace();
        }

    }
}