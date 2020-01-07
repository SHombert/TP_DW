import org.xml.sax.*;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class InfoSax extends DefaultHandler {

    public String noeudCourant; // Nom du noeud qu'on a sauvegardé dans le startElement

    // Flags
    public Boolean isOrg = false;
    public Boolean isAct = false;
    public Boolean isSc = false;
    public Boolean isMandat = false;
    public Boolean isSort = false;
    public Boolean isGrp = false;
    public Boolean isPours = false;
    public Boolean isAdr = false;

    public Acteur acteur;
    public Mandat mandat;
    public Scrutin scrutin;
    public Organe org;
    public String uidAct, uidMandat, uidOrg; // uid à retenir pour les traitements

    Map<String, Acteur> acteurs;
    Map<String, Organe> orgs;

    File file;
    PrintWriter printWriter;

    public InfoSax() {
        super();
        acteurs = new HashMap<>();
        orgs = new HashMap<>();
        file = new File("../sortieSax.xml");
        try {
            printWriter = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

  
    public void startDocument() throws SAXException {
        printWriter.println("<?xml version = \"1.0\" encoding=\"utf-8\" ?>");
        printWriter.println("<!DOCTYPE information SYSTEM \"info.dtd\">");
        printWriter.println("<information>");
    }


    /**
     * Construction du document xml résultat 
     */
    public void endDocument() throws SAXException{
        ArrayList<Acteur> acts = new ArrayList<>(acteurs.values());
        Collections.sort(acts, Comparator.comparing(Acteur::getNom).thenComparing(Acteur::getPrenom));
        for (int i = 0; i< acts.size(); i++){
            if (acts.get(i).isProcessable()){
                printWriter.println("   <act nom = \"" + acts.get(i).getNom() + " " + acts.get(i).getPrenom()+ "\">");
                for (Iterator<Scrutin> it = acts.get(i).scrutins.iterator(); it.hasNext(); ) {
                 
                    Scrutin sc = it.next();
                    printWriter.println("       <sc nom =\"" + sc.getTitre()+ "\"");
                    printWriter.println("           sort =\"" + sc.getSort()+ "\"");
                    printWriter.println("           date =\"" + sc.getDate()+ "\"");
                    printWriter.println("           mandat=\"" + sc.getMandat()+ "\"");
                    printWriter.println("           grp =\"" + sc.getGrp()+ "\"");
                    printWriter.println("           present =\"" + sc.getPresent() + "\"/>");
                }

            printWriter.println("   </act>");
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
                // on ajoute l'acteur qui vient d'être complété à la map d'acteurs
                acteurs.put(uidAct, acteur);
                break;

            case "organe":
                isOrg = false;
                // on ajoute l'organe qui vient d'être complété à la map d'organes
                orgs.put(uidOrg, org);
                break;

            case "scrutin":
                isSc = false;
                break;

            case "mandat":
                isMandat = false;
                // on ajoute à l'acteur courant le mandat qui vient d'etre créé
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
                // On ajoute à l'acteur correspondant le scrutins qui a été créé à condition que le flag sc n'ai pas été repassé à "faux" (ie le titre contient bien l'information)
                if(isSc){
                    acteurs.get(uidAct).addSc(scrutin);

                }
                break;

            default:
                break;
        }
    }

    public void characters(char[] data, int start, int end) {

        String contenu = new String(data, start, end);

        if (isAct) {
            if (!isMandat) { // on doit tester si on n'est pas dans un mandat car on peut trouver le noeud "uid" également
                if(!isAdr){ // idem pour adresse
                    if (noeudCourant.equals("uid")) {
                        acteur = new Acteur(contenu);
                        uidAct = contenu;
                    } else if (noeudCourant.equals("prenom")) {
                        acteur.setPrenom(contenu);
                    } else if (noeudCourant.equals("nom")) {
                        acteur.setNom(contenu);
                    }
                }
                
            }else{

                if (noeudCourant.equals("uid")) {
                    uidMandat = contenu;
                    mandat = new Mandat(contenu);
                } else if (noeudCourant.equals("libQualiteSex")) {
                    mandat.setLib(contenu);
                } else if (noeudCourant.equals("organeRef")) {
                    mandat.setOrgRef(contenu);
                }
            }

        }

        if (isOrg) {
            if (noeudCourant.equals("uid")) {
                org = new Organe(contenu);
                uidOrg = contenu;
            } else if (noeudCourant.equals("libelle")) {
                org.setLibelle(contenu);
            }
        }

        if (isSc) {
            if (noeudCourant.equals("dateScrutin")) {
                scrutin = new Scrutin();
                scrutin.setDate(contenu);
            } else if (noeudCourant.equals("titre")) {
                if (contenu.contains("l'information")) {
                    scrutin.setTitre(contenu);
                } else {
                    isSc = false;
                }
            } else if (noeudCourant.equals("code")) {
                if (isSort) {
                    scrutin.setSort(contenu);
                }
            }
            if (isGrp) {
                if (noeudCourant.equals("organeRef")){
                    scrutin.setGrp(orgs.get(contenu).getLibelle());

                }
            }
            if (isPours) {
                if (noeudCourant.equals("acteurRef")) {
                    uidAct = contenu; // on retient l'acteur pour pouvoir lui ajouter le scrutin une fois la balise fermante </votant> atteinte
                } else if (noeudCourant.equals("mandatRef")) {
                    Mandat m = acteurs.get(uidAct).getMandat(contenu);
                    String mand = m.getlibQualite() + ' ' + orgs.get(m.getOrgane()).getLibelle();
                    scrutin.setMandat(mand);
                } else if (noeudCourant.equals("parDelegation")) {
                    if (contenu == "false") {
                        scrutin.setPst("Non");
                    } else {
                        scrutin.setPst("Oui");
                    }
                }
            }
        }
    }

}


