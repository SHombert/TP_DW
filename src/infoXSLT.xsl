<?xml version="1.0" encoding="utf-8"?>

<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
xmlns:an ="http://schemas.assemblee-nationale.fr/referentiel">
<xsl:output method="xml" indent="yes" doctype-system="info.dtd"/>

<xsl:strip-space elements="*"/>
<!-- variable pour selectionner les bon votants-->
<xsl:variable name="liste-act-pour" select="/assemblée/liste-acteurs/an:acteur[./an:uid/text() =
    /assemblée/liste-scrutins/an:scrutin[contains(./an:titre/text(), 'l'information')]/an:ventilationVotes/an:organe/an:groupes/an:groupe/an:vote/an:decompteNominatif/an:pours/an:votant/an:acteurRef/text()
]"/>

<!-- Regle pour match tous les noeuds et ajouter a la pile les votants correspondant au critere-->
    <xsl:template match="/">

        <information>
            <xsl:apply-templates select="$liste-act-pour">
                <xsl:sort select="./an:etatCivil/an:ident/an:nom"/>
                <xsl:sort select="./an:etatCivil/an:ident/an:prenom"/>
            </xsl:apply-templates>
        </information>

    </xsl:template>

    <!-- votants -->
    <xsl:template match="an:acteur">
        <xsl:variable name="liste-scrutins-acteur" select="/assemblée/liste-scrutins/an:scrutin[
        ./an:ventilationVotes/an:organe/an:groupes/an:groupe/an:vote/an:decompteNominatif/an:pours/an:votant/an:acteurRef = current()/an:uid and contains(./an:titre/text(), 'l''information')]"/>
        <act>
            <xsl:attribute name="nom" select="concat(./an:etatCivil/an:ident/an:prenom,' ',./an:etatCivil/an:ident/an:nom)" />
            <xsl:apply-templates select="$liste-scrutins-acteur">
                <xsl:with-param name="acteurID" select="./an:uid"/>
            </xsl:apply-templates>
        </act>
    </xsl:template>

    <!-- votes-->
    <xsl:template match="an:scrutin">
       <xsl:param name="acteurID"/>
       
        <xsl:variable name="acteur" select="./an:ventilationVotes/an:organe/an:groupes/an:groupe/an:vote/an:decompteNominatif/an:pours/an:votant[./an:acteurRef eq $acteurID]"/>
        <xsl:variable name="to-mandats" select="/assemblée/liste-acteurs/an:acteur/an:mandats/an:mandat[./an:uid eq current()/$acteur/an:mandatRef]"/>
        <xsl:variable name="to-organe" select="/assemblée/liste-organes/an:organe"/>
        <sc>

            <xsl:attribute name ="nom" select="./an:titre"/>
            <xsl:attribute name ="sort" select="./an:sort/an:code"/>
            <xsl:attribute name ="date" select="./an:dateScrutin"/>
            <xsl:attribute name ="mandat">  
                <xsl:value-of select="concat($to-mandats/an:infosQualite/an:libQualiteSex, ' ' ,$to-organe[./an:uid eq $to-mandats/an:organes/an:organeRef]/an:libelle)"/>
            </xsl:attribute>
            <xsl:attribute name ="grp" select="$to-organe[./an:uid eq current()/$acteur/../../../../an:organeRef]/an:libelle"/>
            <xsl:if test="./$acteur/an:parDelegation eq 'false'">
                <xsl:attribute name ="présent">
                    <xsl:value-of>Oui</xsl:value-of>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="./$acteur/an:parDelegation eq 'true'">
                <xsl:attribute name ="présent">
                    <xsl:value-of>Non</xsl:value-of>
                </xsl:attribute>
            </xsl:if>
        </sc>
    </xsl:template>

</xsl:transform>
