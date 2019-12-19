import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



import java.io.FileOutputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;



public class InfoDom{

	public Document docAS, res;
    Map <String, Acteur> acteurs;
    Map <String, Organe> orgs;
    public DocumentBuilder db;

    public InfoDom (){
        Map <String, Acteur> acteurs = new HashMap<>();
        Map <String, Organe> orgs = new HashMap<>();
    }
    
	public void load(String fichier) {
	  try {
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setIgnoringComments(true);
      dbf.setIgnoringElementContentWhitespace(true);
      dbf.setValidating(false);

      db = dbf.newDocumentBuilder();

	    docAS = db.parse(fichier);
	  } catch(Exception e) {
      System.out.println("Exception !");System.exit(0);
    }
	}
	
	public void save(String fichier) {
        /**try{
          XMLSerializer ser = new XMLSerializer(new FileOutputStream(fichier), 
                                                new OutputFormat(doc));
          ser.serialize(doc);
        }catch(IOException e){
          System.out.println("Exception !");System.exit(0);
        }*/
      }

    public void traiter (){
        DOMImplementation domImpl = db.getDOMImplementation();
        DocumentType docType = domImpl.createDocumentType("information", 
                                                     null, "info.dtd");

        String nameSpace = "http://schemas.assemblee-nationale.fr/referentiel";                                      
        res = domImpl.createDocument(nameSpace, "information", docType);
        
        Element listOrg = (Element) docAS.getDocumentElement().getFirstChild().getNextSibling();
        NodeList orgsToLoad = listOrg.getChildNodes();

        for(int i=0;i<orgs.getLength();++i){
          Element org = (Element) orgsToLoad.item(i);
          Organe newOrg = new Organe(org.getFirstChild().getTextContent());
          

        }
        for(int i=0;i<acteurs.getLength();++i){
          Element act = (Element) acteurs.item(i);
          Acteur newAct = new Acteur(act.getFirstChild().getTextContent());

          NodeList params = act.getChildNodes();
          
          for(int j=0; j<params.getLength(); ++j){
            Element param = (Element) params.item(j);
            if(param.getTagName()== "etatCivil"){
              newAct.setNom(param.getFirstChild().getFirstChild().getNextSibling().getNextSibling().getTextContent());
              newAct.setPrenom(param.getFirstChild().getFirstChild().getNextSibling().getTextContent());
            }

          }
        }


    }

	public static void main(String argv[]) {
      InfoDom id= new InfoDom();
      id.load("../assemblee1920.xml");
      id.traiter();
	  

	}
}



