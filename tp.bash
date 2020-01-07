#!/bin/bash

javac src/*.java
java -jar saxon9he.jar -xsl:infoXSLT.xsl -s:assemblee1920.xml > sortieXSLT.xml
cd src
java InfoDom
java InfoDomXpath
java AppliSax

