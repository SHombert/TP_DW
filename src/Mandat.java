public class Mandat {
    
    String uid;
    String libQualite;
    String organeRef;

    public Mandat (String uid, String lib, String orgRef) {
        this.uid = uid;
        this.libQualite = lib;
        this.organeRef = orgRef;
    }
    public Mandat (String uid){
        this.uid = uid;
    }

    public void setLib(String lib){
        this.libQualite=lib;
    }

    public void setOrgRef(String orgRef){
        this.organeRef=orgRef;
    }

    public String getUid(){
        return this.uid;
    }
    

}