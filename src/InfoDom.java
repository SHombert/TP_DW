import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
import java.util.Map;

public class InfoDom {

  public Document docAS, res;
  Map<String, Acteur> acteurs;
  Map<String, Organe> orgs;
  public DocumentBuilder db;
  public Transformer transformer;

  public InfoDom() {
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

    // Copie de tous les organes

    Element listOrg = (Element) docAS.getDocumentElement().getFirstChild().getNextSibling();
    System.out.println(listOrg.getTagName());
    NodeList orgsToLoad = listOrg.getChildNodes();

    for (int i = 0; i < orgsToLoad.getLength(); ++i) {
      Element org = (Element) orgsToLoad.item(i);
      Organe newOrg = new Organe(org.getFirstChild().getTextContent());
      newOrg.setLibelle(org.getFirstChild().getNextSibling().getNextSibling().getTextContent());
      orgs.put(newOrg.getUid(), newOrg);
    }

    // Copie de tous les acteurs

    Element listAct = (Element) docAS.getDocumentElement().getFirstChild();
    System.out.println(listAct.getTagName());
    NodeList actsToLoad = listAct.getChildNodes();

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

    /**
     * Traitement des scrutins Pour tous les scrutins, si bon libelle, ajouter le sc
     * à chaque acteur ayant voté pour
     */

    NodeList scrutins = docAS.getDocumentElement().getLastChild().getChildNodes();

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

    // construction du document
    Element racine = res.getDocumentElement();

    ArrayList<Acteur> acts = new ArrayList<>(acteurs.values());
    Collections.sort(acts, Comparator.comparing(Acteur::getNom).thenComparing(Acteur::getPrenom));
    for (Acteur entry : acts) {
      if (entry.scrutins.size() > 0) {
        final Element act = res.createElement("act");
        act.setAttribute("nom", entry.getNom() + " " + entry.getPrenom());

        for (int i = 0; i < entry.scrutins.size(); ++i) {
          Scrutin sc = entry.scrutins.get(i);
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
    InfoDom id = new InfoDom();
    id.load("../assemblee1920.xml");
    id.traiter();
    id.save("sortieDom1.xml");

  }
}
