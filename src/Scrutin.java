

public class Scrutin {
        String titre;
        String sort;
        String date;
        String grp;
        String mandat;
        String present;

        public Scrutin (){}

        

        public Scrutin(String titre, String sort, String date, String grp, String mandat, String present) {
                this.titre = titre;
                this.sort = sort;
                this.date = date;
                this.grp = grp;
                this.mandat = mandat;
                this.present = present;
        }

        public void setTitre(String t){
                this.titre = t;
        }

        
        public void setSort(String s){
                this.sort = s;
        }
        
        public void setDate(String d){
                this.date = d;
        }
        
        public void setGrp(String g){
                this.grp = g;
        }
        
        public void setMandat(String m){
                this.mandat = m;
        }

        public void setPst(String p){
                this.present = p;
        }

        public String getTitre() {
                return this.titre;
        }


        public String getSort() {
                return this.sort;
        }


        public String getDate() {
                return this.date;
        }


        public String getGrp() {
                return this.grp;
        }


        public String getMandat() {
                return this.mandat;
        }


        public String getPresent() {
                return this.present;
        }

        @Override
        public String toString() {
                return "{" +
                        " titre='" + getTitre() + "'" +
                        ", sort='" + getSort() + "'" +
                        ", date='" + getDate() + "'" +
                        ", grp='" + getGrp() + "'" +
                        ", mandat='" + getMandat() + "'" +
                        ", present='" + getPresent() + "'" +
                        "}";
        }

}
