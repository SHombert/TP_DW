import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.transform.OutputKeys;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class InfoDomXpath {

  public Document docAS, res;
  Map<String, Acteur> acteurs;
  Map<String, Organe> orgs;
  public DocumentBuilder db;
  public Transformer transformer;

  public InfoDomXpath() {
    acteurs = new HashMap<>();
    orgs = new HashMap<>();
  }

  public void load(String fichier) {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setIgnoringComments(true);
      dbf.setIgnoringElementContentWhitespace(true);
      dbf.setNamespaceAware(true);
      dbf.setValidating(false);

      db = dbf.newDocumentBuilder();
      docAS = db.parse(fichier);

    } catch (Exception e) {
      System.out.println("Exception lors du chargement du fichier");
      System.exit(0);
    }
  }

  public void save(String fichier) {
    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "info.dtd");

      transformer.transform(new DOMSource(res), new StreamResult(new File(fichier)));

    } catch (Exception e) {
      System.out.println("Exception lors de la sauvegarde du fichier");
      System.exit(0);
    }
  }

  public void traiter() {
    DOMImplementation domImpl = db.getDOMImplementation();
    DocumentType docType = domImpl.createDocumentType("information", null, "info.dtd");

    String nameSpace = "http://schemas.assemblee-nationale.fr/referentiel";
    res = domImpl.createDocument(nameSpace, "information", docType);

    XPathFactory xpf = XPathFactory.newInstance();
    XPath path = xpf.newXPath();
    path.setNamespaceContext(new NamespaceContext() {
      public String getNamespaceURI(String s) {
        if ("an".equals(s))
          return "http://schemas.assemblee-nationale.fr/referentiel";
        return null;
      }
      public String getPrefix(String s) {
        return null;
      }
      public Iterator getPrefixes(String s) {
        return null;
      }
    });
    
    Element rootAS = docAS.getDocumentElement();
    String expression = "liste-organes/an:organe";
    try {
      NodeList orgsToLoad = (NodeList) path.evaluate(expression, rootAS, XPathConstants.NODESET);
     
      for (int i = 0; i < orgsToLoad.getLength(); ++i) {
        Element org = (Element) orgsToLoad.item(i);
        Organe newOrg = new Organe(org.getFirstChild().getTextContent());
      
        String test = (String) path.evaluate("an:libelle", org, XPathConstants.STRING);
        newOrg.setLibelle( test);
       
        orgs.put(newOrg.getUid(), newOrg);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  
    

    //Copie des acteurs
    try {
      NodeList actsToLoad = (NodeList) path.evaluate("liste-acteurs/an:acteur", rootAS, XPathConstants.NODESET);
      for (int i = 0; i < actsToLoad.getLength(); ++i) {
        Element act = (Element) actsToLoad.item(i);
        Acteur newAct = new Acteur(act.getFirstChild().getTextContent());
        NodeList params = act.getChildNodes();
  
        for (int j = 0; j < params.getLength(); ++j) {
          Element param = (Element) params.item(j);
          if (param.getTagName() == "etatCivil") { // Nom et prenom
            newAct.setNom(param.getFirstChild().getFirstChild().getNextSibling().getNextSibling().getTextContent());
            newAct.setPrenom(param.getFirstChild().getFirstChild().getNextSibling().getTextContent());
          }
          if (param.getTagName() == "mandats") { // recuperation de tous les mandats de l'acteur
  
            NodeList mandats = param.getChildNodes();
            for (int k = 0; k < mandats.getLength(); ++k) {
              Element mand = (Element) mandats.item(k);
              Mandat newMandat = new Mandat(mand.getFirstChild().getTextContent());
  
              newMandat.setLib(mand.getFirstChild().getNextSibling().getNextSibling().getNextSibling().getNextSibling()
                  .getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getLastChild()
                  .getTextContent());// libQualiteSex
                  
              newMandat.setOrgRef(mand.getFirstChild().getNextSibling().getNextSibling().getNextSibling().getNextSibling()
                  .getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling()
                  .getFirstChild().getTextContent());
              newAct.addMandat(newMandat.getUid(), newMandat);
            }
          }
        }
        acteurs.put(newAct.getUid(), newAct);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    

    /**
     * Traitement des scrutins Pour tous les scrutins, si bon libelle, ajouter le sc
     * à chaque acteur ayant voté pour
     */

     try {
      NodeList scrutins = (NodeList) path.evaluate("liste-scrutins/an:scrutin", rootAS, XPathConstants.NODESET);

      for (int i = 0; i < scrutins.getLength(); ++i) {
        Element scrut = (Element) scrutins.item(i);
        String titre = scrut.getFirstChild().getNextSibling().getNextSibling().getNextSibling().getNextSibling()
            .getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling()
            .getTextContent();
  
        if (titre.contains("l'information")) {
          String date = scrut.getFirstChild().getNextSibling().getNextSibling().getNextSibling().getNextSibling()
              .getNextSibling().getNextSibling().getTextContent();
          String sort = scrut.getFirstChild().getNextSibling().getNextSibling().getNextSibling().getNextSibling()
              .getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getFirstChild()
              .getTextContent();
          String grp = scrut.getLastChild().getPreviousSibling().getFirstChild().getFirstChild().getNextSibling()
              .getFirstChild().getFirstChild().getTextContent();
          NodeList pours = scrut.getLastChild().getPreviousSibling().getFirstChild().getFirstChild().getNextSibling()
              .getFirstChild().getLastChild().getLastChild().getFirstChild().getNextSibling().getChildNodes();
  
          for (int j = 0; j < pours.getLength(); ++j) {
            Element votant = (Element) pours.item(j);
            Scrutin sc = new Scrutin();
            sc.setTitre(titre);
            sc.setDate(date);
            sc.setSort(sort);
            sc.setGrp(orgs.get(grp).getLibelle());
            Mandat m = acteurs.get(votant.getFirstChild().getTextContent())
                .getMandat(votant.getFirstChild().getNextSibling().getTextContent());
            String mandat = m.getlibQualite() + ' ' + orgs.get(m.getOrgane()).getLibelle();
            sc.setMandat(mandat);
            acteurs.get(votant.getFirstChild().getTextContent()).addSc(sc);
          }
        }
      }
     } catch (Exception e) {
       e.printStackTrace();
     }
   

    // construction du document
    Element racine = res.getDocumentElement();

    ArrayList<Acteur> acts = new ArrayList<>(acteurs.values());
    Collections.sort(acts, Comparator.comparing(Acteur::getNom).thenComparing(Acteur::getPrenom));
    for (Acteur entry : acts) {
      if (entry.scrutins.size() > 0) {
        final Element act = res.createElement("act");
        act.setAttribute("nom", entry.getNom() + " " + entry.getPrenom());

        for (Iterator<Scrutin> it = entry.scrutins.iterator(); it.hasNext();) {

          Scrutin sc = it.next();
          final Element scrutin = res.createElement("sc");
          scrutin.setAttribute("nom", sc.getTitre());
          scrutin.setAttribute("sort", sc.getSort());
          scrutin.setAttribute("date", sc.getDate());
          scrutin.setAttribute("mandat", sc.getMandat());
          scrutin.setAttribute("grp", sc.getGrp());
          if (sc.getPresent() == "false") {
            scrutin.setAttribute("present", "Non");
          } else {
            scrutin.setAttribute("present", "Oui");
          }
          act.appendChild(scrutin);
        }
        racine.appendChild(act);

      }

    }

  }

  public static void main(String argv[]) {
    InfoDomXpath id = new InfoDomXpath();
    id.load("../assemblee1920.xml");
    id.traiter();
    id.save("sortieDom2.xml");

  }
}
