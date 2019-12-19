import java.util.Map;
import java.util.HashMap;

public class Acteur {
    String uid;
    String nom;
    String prenom;
    Map <String,Mandat> mandats;

    public Acteur(){
        Map <String,Mandat> mandats = new HashMap <>();

    }
    public Acteur(String uid){
        this.uid = uid;
        Map <String,Mandat> mandats = new HashMap <>();
    }
    public Acteur (String uid, String nom, String prenom){
        this.uid = uid;
        this.nom = nom;
        this.prenom = prenom;
        Map <String,Mandat> mandats = new HashMap <>();

    }

    public void setNom(String nom){
        this.nom = nom;
    }

    public void setPrenom(String pren){
        this.prenom = pren;
    }

    
    // get / set / addMandat ?

}