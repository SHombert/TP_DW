import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;



public class Acteur {
    String uid;
    String nom;
    String prenom;
    Map <String,Mandat> mandats;
    ArrayList <Scrutin> scrutins; // Liste des scrutins dont le titre contient "l'information" pour lesquels l'acteur a voté pour

    public Acteur(){
        mandats = new HashMap <>();
        scrutins = new ArrayList<>();

    }
    public Acteur(String uid){
        this.uid = uid;
        mandats = new HashMap <>();
        scrutins = new ArrayList<>();

    }
    public Acteur (String uid, String nom, String prenom){
        this.uid = uid;
        this.nom = nom;
        this.prenom = prenom;
        mandats = new HashMap <>();
        scrutins = new ArrayList<>();


    }

    public void setNom(String nom){
        this.nom = nom;
    }

    public void setPrenom(String pren){
        this.prenom = pren;
    }

    public void addMandat(String uid, Mandat mandat){
        mandats.put(uid,mandat);
    }

    public void addSc (Scrutin sc){
        scrutins.add(sc);
    }
    
    public Mandat getMandat(String uid){
        return mandats.get(uid);
    }

    public String getUid(){
        return uid;
    }
    /**
     * Indique si l'acteur fait partie des acteurs a ajouter au document xml ie si sa liste de scrutins n'est pas vide
     * @return 
     */
    public boolean isProcessable(){
        return !scrutins.isEmpty();
    }
	public String getNom() {
		return nom;
    }
    
    public String getPrenom(){
        return prenom;
    }


}