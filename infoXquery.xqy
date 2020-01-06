xquery version "1.0" encoding "utf-8";
declare namespace an="http://schemas.assemblee-nationale.fr/referentiel";
declare namespace output = "http://www.w3.org/2010/xslt-xquery-serialization";
declare option output:item-separator "&#xa;";
<information>{
        for $act in doc("assemblee1920.xml")/assemblée/liste-acteurs/an:acteur[./an:uid/text() =
    /assemblée/liste-scrutins/an:scrutin[contains(./an:titre/text(), 'l''information')]/an:ventilationVotes/an:organe/an:groupes/an:groupe/an:vote/an:decompteNominatif/an:pours/an:votant/an:acteurRef/text()]
    return 
        <act> {$act/an:uid/text() }</act>
}</information>