import org.xml.sax.*;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

public class InfoSax extends DefaultHandler {
    // Initiation des variables
    public String noeudCourant;
    // Flags
    public Boolean isOrg = false;
    public Boolean isAct = false;
    public Boolean isSc = false;
    public Boolean isMandat = false;
    public Boolean isSort = false;
    public Boolean isGrp = false;
    public Boolean isPours = false;
    public Boolean isAdr = false;

    public int compt;

    public Acteur acteur;
    public Mandat mandat;
    public Scrutin scrutin;
    public Organe org;
    public String uidAct, uidMandat, uidOrg;
    Map<String, Acteur> acteurs;
    Map<String, Organe> orgs;
    File file;
    PrintWriter printWriter;

    public InfoSax() {
        super();
        acteurs = new HashMap<>();
        orgs = new HashMap<>();
        file = new File("sortieSax.xml");
        try {
            printWriter = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Début du parsing
    public void startDocument() throws SAXException {
        printWriter.println("<?xml version = \"1.0\" standalone=\"no\"?>");
        printWriter.println("<!DOCTYPE information SYSTEM \"info.dtd\">");
        printWriter.println("<information>");
    }

    // Fin du parsing
    public void endDocument() throws SAXException{
        ArrayList<Acteur> acts = new ArrayList<>(acteurs.values());
        Collections.sort(acts, Comparator.comparing(Acteur::getNom).thenComparing(Acteur::getPrenom));
        for (int i = 0; i< acts.size(); i++){
            if (acts.get(i).isProcessable()){
                compt++;
                printWriter.println("<act nom = \"" + acts.get(i).getNom() + " " + acts.get(i).getPrenom()+ "\">");
                for (Iterator<Scrutin> it = acts.get(i).scrutins.iterator(); it.hasNext(); ) {
                 
                    Scrutin sc = it.next();
                    printWriter.println("   <sc nom =\"" + sc.getTitre()+ "\"");
                    printWriter.println("       sort =\"" + sc.getSort()+ "\"");
                    printWriter.println("       date =\"" + sc.getDate()+ "\"");
                    printWriter.println("       mandat=\"" + sc.getMandat()+ "\"");
                    printWriter.println("       grp =\"" + sc.getGrp()+ "\"");
                    printWriter.println("       present =\"" + sc.getPresent() + "\"/>");
                }

            printWriter.println("</act>");
            }     
        }
        printWriter.println("</information>");
        printWriter.close();
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs)
            throws SAXException {
            
        noeudCourant = localName;
        switch (localName) {

        case "acteur":
            isAct = true;
            break;

        case "organe":
            isOrg = true;
            break;

        case "scrutin":
            isSc = true;
            break;

        case "mandat":
            isMandat = true;
            break;

        case "sort":
            isSort = true;
            break;

        case "groupe":
            isGrp = true;
            break;

        case "pours":
            isPours = true;
            break;

            case "adresse":
            isAdr = true;
            break;

        default:
            break;
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {


        switch (localName) {
            case "acteur":
                isAct = false;
                acteurs.put(uidAct, acteur);
                break;

            case "organe":
                isOrg = false;
                orgs.put(uidOrg, org);
                break;

            case "scrutin":
                isSc = false;
                break;

            case "mandat":
                isMandat = false;
                acteur.addMandat(uidMandat, mandat);
                break;

            case "sort":
                isSort = false;
                break;

            case "groupe":
                isGrp = false;
                break;

            case "pours":
                isPours = false;
                break;
                case "adresse":
                isAdr = false;
                break;

            case "votant":
                if(isSc){
                    acteurs.get(uidAct).addSc(scrutin);

                }
                break;

            default:
                break;
        }
    }

    public void characters(char[] data, int start, int end) {

        String noeud = new String(data, start, end);

        if (isAct) {
            if (!isMandat) {
                if(!isAdr){
                    if (noeudCourant.equals("uid")) {
                        acteur = new Acteur(noeud);
                        uidAct = noeud;
                    } else if (noeudCourant.equals("prenom")) {
                        acteur.setPrenom(noeud);
                    } else if (noeudCourant.equals("nom")) {
                        acteur.setNom(noeud);
                    }
                }
                
            }else{

                if (noeudCourant.equals("uid")) {
                    uidMandat = noeud;
                    mandat = new Mandat(noeud);
                } else if (noeudCourant.equals("libQualiteSex")) {
                    mandat.setLib(noeud);
                } else if (noeudCourant.equals("organeRef")) {
                    mandat.setOrgRef(noeud);
                }
            }

        }

        if (isOrg) {
            if (noeudCourant.equals("uid")) {
                org = new Organe(noeud);
                uidOrg = noeud;
            } else if (noeudCourant.equals("libelle")) {
                org.setLibelle(noeud);
            }
        }

        if (isSc) {
            if (noeudCourant.equals("dateScrutin")) {
                scrutin = new Scrutin();
                scrutin.setDate(noeud);
            } else if (noeudCourant.equals("titre")) {
                if (noeud.contains("l'information")) {
                    scrutin.setTitre(noeud);
                } else {
                    isSc = false;
                }
            } else if (noeudCourant.equals("code")) {
                if (isSort) {
                    scrutin.setSort(noeud);
                }
            }
            if (isGrp) {
                if (noeudCourant.equals("organeRef")){
                    scrutin.setGrp(orgs.get(noeud).getLibelle());

                }
            }
            if (isPours) {
                if (noeudCourant.equals("acteurRef")) {
                    uidAct = noeud;
                } else if (noeudCourant.equals("mandatRef")) {
                   
                    Mandat m = acteurs.get(uidAct).getMandat(noeud);
                    String mand = m.getlibQualite() + ' ' + orgs.get(m.getOrgane()).getLibelle();
                    scrutin.setMandat(mand);
                } else if (noeudCourant.equals("parDelegation")) {
                    if (noeud == "false") {
                        scrutin.setPst("Non");
                    } else {
                        scrutin.setPst("Oui");
                    }
                }
            }
        }
    }

}


