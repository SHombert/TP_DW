

public class Organe {
    private String uid;
    private String libelle;


    public Organe (String uid, String lib){
        this.uid = uid;
        this.libelle = lib;
    }

    public Organe (String uid){
        this.uid = uid;
    }

    public void setLibelle(String lib){
        this.libelle=lib;
    }

    public String getLibelle(){
        return this.libelle;
    }

    public String getUid(){
        return this.uid;
    }


}