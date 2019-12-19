import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
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
  public Transformer transformer;

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
      System.out.println("Exception lors du chargement du fichier");
      System.exit(0);
    }
	}
	
	public void save(String fichier) {
    try{
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      
      transformer.transform(new DOMSource(res),new StreamResult(new File("sortieDOM1.xml")));

    }catch(Exception e){
      System.out.println("Exception lors de la sauvegarde du fichier");
      System.exit(0);
    }
  }

    public void traiter (){
        DOMImplementation domImpl = db.getDOMImplementation();
        DocumentType docType = domImpl.createDocumentType("information", 
                                                     null, "info.dtd");

        String nameSpace = "http://schemas.assemblee-nationale.fr/referentiel";                                      
        res = domImpl.createDocument(nameSpace, "information", docType);
        
        // Copie de tous les organes

        Element listOrg = (Element) docAS.getDocumentElement().getFirstChild().getNextSibling();
        NodeList orgsToLoad = listOrg.getChildNodes();

        for(int i=0;i<orgsToLoad.getLength();++i){
          Element org = (Element) orgsToLoad.item(i);
          Organe newOrg = new Organe(org.getFirstChild().getTextContent());
          newOrg.setLibelle(org.getFirstChild().getNextSibling().getNextSibling().getTextContent());
          orgs.put(newOrg.getUid(),newOrg);
        }

        // Copie de tous les acteurs
        Element listAct = (Element) docAS.getDocumentElement().getFirstChild();
        NodeList actsToLoad = listAct.getChildNodes();

        for(int i=0;i<actsToLoad.getLength();++i){
          Element act = (Element) acteurs.item(i);
          Acteur newAct = new Acteur(act.getFirstChild().getTextContent());

          NodeList params = act.getChildNodes();
          
          for(int j=0; j<params.getLength(); ++j){
            Element param = (Element) params.item(j);
            if(param.getTagName()== "etatCivil"){ // Nom et prenom
              newAct.setNom(param.getFirstChild().getFirstChild().getNextSibling().getNextSibling().getTextContent());
              newAct.setPrenom(param.getFirstChild().getFirstChild().getNextSibling().getTextContent());
            }
            if(param.getTagName()== "mandats"){ // recuperation de tous les mandats de l'acteur
              NodeList mandats = param.getChildNodes();
              for(int k=0; k<mandats.getLength();++k){
                Element mand = (Element) mandats.item(k);
                Mandat newMandat = new Mandat(mand.getFirstChild());
                newMandat.setLib(mand.getLastChild().getPreviousSibling().getLastChild()); // libQualiteSex
                newMandat.setOrgRef(mand.getLastChild().getFirstChild().getTextContent());
                newAct.addMandat(newMandat.getUid(),newMandat);
              }
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



