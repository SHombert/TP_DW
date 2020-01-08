xquery version "1.0" encoding "utf-8";
declare namespace an="http://schemas.assemblee-nationale.fr/referentiel";
declare namespace output = "http://www.w3.org/2010/xslt-xquery-serialization";
 declare option output:method "xml";
 declare option output:media-type "text/xml";
 declare option output:omit-xml-declaration "no";
 declare option output:indent "yes";
 declare option output:doctype-system "info.dtd";
declare option output:item-separator "&#xa;";

declare variable $org := doc("assemblee1920.xml")/assemblée/liste-organes/an:organe;
<information>{
        for $act in doc("assemblee1920.xml")/assemblée/liste-acteurs/an:acteur
        let $actUid := $act/an:uid/text()
        order by $act/an:etatCivil/an:ident/an:nom,$act/an:etatCivil/an:ident/an:prenom
        where $act/an:uid/text() = doc("assemblee1920.xml")/assemblée/liste-scrutins/an:scrutin[contains(./an:titre/text(), 'l''information')]/an:ventilationVotes/an:organe/an:groupes/an:groupe/an:vote/an:decompteNominatif/an:pours/an:votant/an:acteurRef/text()
        return 
        <act nom ="{$act/an:etatCivil/an:ident/an:prenom} {$act/an:etatCivil/an:ident/an:nom}"> {
            for $sc in doc("assemblee1920.xml")/assemblée/liste-scrutins/an:scrutin[an:ventilationVotes/an:organe/an:groupes/an:groupe/an:vote/an:decompteNominatif/an:pours/an:votant/an:acteurRef/text() = $actUid and contains(./an:titre/text(), "l'information")]
            let $acteur := $sc/an:ventilationVotes/an:organe/an:groupes/an:groupe/an:vote/an:decompteNominatif/an:pours/an:votant[an:acteurRef/text() = $actUid]
            return
                <sc nom = "{$sc/an:titre}" 
                    sort = "{$sc/an:sort/an:code}"
                    date = "{$sc/an:dateScrutin}"
                    mandat = "{$act/an:mandats/an:mandat[uid eq $acteur/an:mandatRef]/an:libQualiteSex} {$org[./uid eq $act/an:mandats/an:mandat[./uid eq $acteur/an:mandatRef]/an:organes/an:organeRef]/an:libelle}"
                    grp = "{$org[./an:uid eq $acteur/../../../../an:organeRef]/an:libelle}" 
                    present = "{
                        if($acteur/an:parDelegation ="true") then
                        ("Oui") else("Non")
                    }"
                    />
        }</act>
        
}</information>
