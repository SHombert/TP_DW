
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

public class AppliSax {

    public static void main(String[] args) {
 
        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            SAXParser parser = factory.newSAXParser();
            InfoSax handler = new InfoSax();
            parser.parse("../assemblee1920.xml",handler );

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }
}