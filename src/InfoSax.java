import org.xml.sax.*;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.*;
import java.util.Hashtable;
import java.util.ArrayList;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;


public class InfoSax extends DefaultHandler {
    //Initiation des variables
    public String noeudCourant = null;
    //Flags
    public Boolean isOrg = false;
    public Boolean isAct = false;
    public Boolean isSc = false;
    public Boolean isPrenom = false;
    public Boolean isNom = false;
    public Boolean isMandat = false;
    public Boolean isLibQualite = false;
    public Boolean isOrgRef = false;
    public Boolean isLib = false;
    public Boolean isTitre = false;
    public Boolean isDate = false;
    public Boolean isSort = false;
    public Boolean isGrp = false;
    public Boolean isPours = false;
    public Boolean isVotant = false;
    public Boolean isMandatRef = false;
    public Boolean isDeleg = false;
    public Boolean isUid = false;
    public Boolean isActeurRef = false;

    public ArrayList<Acteur>tableauActeurs = new ArrayList<Acteur>();
    public Acteur acteur;
    public Mandat mandat;
    public Scrutin scrutin;
    public String uidAct, uidMandat, uidOrg;
    Map<String, Acteur> acteurs;
    Map<String, Organe> orgs;


    File file = new File ("../Resultats/SAX_sortie.xml");
    PrintWriter printWriter;
    {
        try {
            printWriter = new PrintWriter ("../Resultats/SAX_sortie.xml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Début du parsing
    public void startDocument() throws SAXException{
        printWriter.println("<?xml encoding=\"UTF-8\"?>");
        printWriter.println("<!DOCTYPE information SYSTEM \"info.dtd\">");
    }

    //Fin du parsing
    public void endDocument() throws SAXException{
        ArrayList<Acteur> acts = new ArrayList<>(acteurs.values());
        Collections.sort(acts, Comparator.comparing(Acteur::getNom).thenComparing(Acteur::getPrenom));
        for (int i = 0; i< acts.size(); i++){
            if (acts.get(i).isProcessable()){
                printWriter.println("<act nom = \"" + acts.get(i).getNom() + acts.get(i).getPrenom()+ ">")
                for (int j = 0; j < acts.get(i).scrutins.size(); j++){
                    Scrutin sc = acts.get(i).scrutins.get(j);
                    printWriter.println("<sc nom =\"" + sc.getTitre());
                    printWriter.println("sort =\"" + sc.getSort());
                    printWriter.println("date =\"" + sc.getDate());
                    printWriter.println("mandat=\"" + sc.getMandat());
                    printWriter.println("grp =\"" + sc.getGrp());
                    printWriter.println("present =\"" + sc.getPresent() + "/>");
            }

            printWriter.println("</act>")
            }     
        }
        printWriter.println("</information>")
        printWriter.close();
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException {
        
        noeudCourant = localName;
        switch (localName) {
            case "acteur":
            isAct= true;
                break;
            case "organe":
            isOrg= true;
                break;
            case "scrutin":
            isSc= true;
                break;
            case "prenom":
            isPrenom= true;
                break;
            case "nom":
            isNom= true;
                break;
            case "mandat":
            isMandat= true;
                break;
            case "libQualiteSex":
            isLibQualite= true;
                break;
            case "organeRef":
            isOrgRef= true;
                break;
            case "libelle":
            isLib= true;
                break;
            case "titre":
            isTitre= true;
                break;
            case "date":
            isDate= true;
                break;
            case "sort":
            isSort= true;
                break;
            case "groupe":
            isGrp= true;
                break;
            case "pours":
            isPours= true;
                break;
            case "votant":
            isVotant= true;
                break;
            case "acteurRef":
            isActeurRef= true;
                break;
            case "parDelegation":
            isDeleg= true;
                break;
            case "mandatRef":
            isMandatRef= true;
                break;
            default:
                break;
        }                      
    }

    public void endElement(String uri, String localName, String qName) throws SAXException{
       
        switch (localName) {
            case "acteur":
            isAct= false;
            acteurs.put(uidAct, acteur);
                break;
            case "organe":
            isOrg= false;
            orgs.put(uidOrg, org);
                break;
            case "scrutin":
            isSc= true;
                break;
            case "prenom":
            isPrenom= true;
                break;
            case "nom":
            isNom= true;
                break;
            case "mandat":
            isMandat= false;
            acteur.addMandat(uidMandat, mandat);
                break;
            case "libQualiteSex":
            isLibQualite= true;
                break;
            case "organeRef":
            isOrgRef= true;
                break;
            case "libelle":
            isLib= true;
                break;
            case "titre":
            isTitre= true;
                break;
            case "date":
            isDate= true;
                break;
            case "sort":
            isSort= true;
                break;
            case "groupe":
            isGrp= true;
                break;
            case "pours":
            isPours= true;
                break;
            case "votant":
            isVotant= false;
            acteurs.get(uidAct).scrutins.addSc(scrutin);
                break;
            case "acteurRef":
            isActeurRef= true;
                break;
            case "parDelegation":
            isDeleg= true;
                break;
            case "mandatRef":
            isMandatRef= true;
                break;
            default:
                break;
    }

    public void characters(char[] data, int start, int end){

        String noeud = new String(data, start, end);

        if (isAct) {
            if(!isMandat){
                if (noeudCourant.equals("uid")){
                    acteur = new Acteur (noeud);
                    uidAct = noeud;
                }else if(noeudCourant.equals("prenom")){
                    acteur.setPrenom(noeud);
                }else if (noeudCourant.equals("nom")  ){
                    acteur.setNom(noeud);
                }
            }else {

                if (noeudCourant.equals("uid") ){
                    uidMandat = noeud;
                    mandat = new Mandat (noeud);
                }else if (noeudCourant.equals("libQualiteSex") ){
                    mandat.setLib(noeud);
                }else if (noeudCourant.equals("organeRef") ){
                    mandat.setOrgRef(noeud);
                }
            }

         }
         
         if (isOrg) {
                 if (noeudCourant.equals("uid")){
                     org = new Organe (noeud);
                     uidOrg = noeud;
                 }else if (noeudCourant.equals("libelle")){
                     org.setLibelle(noeud);
                 }
         }

         if (isSc){
             if (noeudCourant.equals("dateScrutin")){
                scrutin = new Scrutin();
                scrutin.setDate(noeud);
             }else if (noeudCourant.equals ("titre")){
                 if (noeud.contains("l'information")){
                     scrutin.setTitre(noeud);
                 }else {
                    isSc = false;
                 }
             }else if (noeudCourant.equals ("code")){
                 if (isSort){
                     scrutin.setSort(noeud);
                 }
             }else if (noeudCourant.equals ("organeRef")){
                 if (isGrp){
                     scrutin.setGrp(orgs.get(noeud).getLibelle());
                 }
             }
             if (isPours){
                 if (noeudCourant.equals("acteurRef")){
                     uidAct = noeud;
                 }else if (noeudCourant.equals("mandatRef")){
                    Mandat m = acteurs.get(uidAct).getMandat(noeud);
                    String mand = m.getlibQualite() + ' ' + orgs.get(m.getOrgane()).getLibelle();
                    scrutin.setMandat(mandat);
                 }else if (noeudCourant.equals("parDelegation")){
                     if (noeud == "false") {
                         scrutin.setPst("Non");
                     }else {scrutin.setPst("Oui");}
                 }
             }
         }
   }

}



