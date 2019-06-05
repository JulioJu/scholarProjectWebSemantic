#!/bin/bash
# -*- coding: UTF8 -*-

set -euET
# Note: `set +E' doesn't work
# set -x

declare -r fileDepartments="Departments.xml"

declare -r fileCommunes="Communes.xml"

declare -g DIR_SOURCE=
DIR_SOURCE="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd -P)"
# declare -r outputFile="${DIR_SOURCE}/scholarProjectWebSemantic/src/main/resources/julioJuGeographicalZone.owl"
declare -r outputFile="${DIR_SOURCE}/julioJuGeographicalZone.owl"
echo "'${outputFile}' (outputFile)"

pushd "${DIR_SOURCE}"

cp ./DepartmentsWikidata.tsv "${fileDepartments}"
cp ./CommunesWikidata.tsv "${fileCommunes}"

sed -i 1d "${fileDepartments}"
sed -i 's/<//g' ${fileDepartments}
sed -i 's/>//g' ${fileDepartments}
sed -i 's/@fr//g' ${fileDepartments}
sed -i 's/.$//' ${fileDepartments} # assumes that all lines end with CR/LF
sed -i 's/	$/	http:\/\/julioJuGeographicalZone.owl\/ErrorInWikidataTODOCorrectItNotADepartment/' "${fileDepartments}"
# shellcheck disable=SC2016
nvim -n -i NONE -u NORC --cmd "set nostartofline runtimepath=~/.vim/plugged/tabular,~/.vim/plugged/tabular/after" "${fileDepartments}" --headless +'%s#http://www.wikidata.org#http://wikidataJulioJuEntity#g | %s#http://wikidataJulioJuEntity#http://yoyoyoyoyoyoyoyoyoyoyoyoyoyo.fr# | %s/	/ 	 /g | %s/$/ 	 toto/' +'Tabularize / 	' +'%s/toto$//' +'normal gg^WhGy$p' +'%s#http://yoyoyoyoyoyoyoyoyoyoyoyoyoyo.fr#http://wikidataJulioJuEntity# | %s#http://yoyoyoyoyoyoyoyoyoyoyoyoyoyo.fr#http://www.wikidata.org# | %s/ \+	 \+/	/g | %s/\s\+$//g' +x
sed -i 's/^/<owl:Class rdf:about="/' "${fileDepartments}"
sed -i 's/	/"> <rdfs:label xml:lang="fr">/' "${fileDepartments}"
sed -i 's/	/<\/rdfs:label> <wikidataProperty:P2586>/' "${fileDepartments}"
# Do not use '#' sepearator otherwise does not work
sed -i 's/	/<\/wikidataProperty:P2586> <rdfs:isDefinedBy rdf:datatype="http:\/\/www.w3.org\/2001\/XMLSchema#anyURI">/' "${fileDepartments}"
sed -i 's#$#</rdfs:isDefinedBy> <rdfs:subClassOf rdf:resource="http://julioJuGeographicalZone.owl/geographicalZoneFrance"/> </owl:Class>#' "${fileDepartments}"

sed -i 1d "${fileCommunes}"
sed -i 's/<//g' ${fileCommunes}
sed -i 's/>//g' ${fileCommunes}
sed -i 's/@fr//g' ${fileCommunes}
sed -i 's/.$//' ${fileCommunes} # assumes that all lines end with CR/LF
sed -i 's/	$/	http:\/\/julioJuGeographicalZone.owl\/CommuneWithoutDepartmentInWikiDataTODOCorrectIt/' "${fileCommunes}"
# shellcheck disable=SC2016
nvim -n -i NONE -u NORC --cmd "set nostartofline runtimepath=~/.vim/plugged/tabular,~/.vim/plugged/tabular/after" "${fileCommunes}" --headless +'%s#http://www.wikidata.org#http://wikidataJulioJuEntity#g | %s#http://wikidataJulioJuEntity#http://yoyoyoyoyoyoyoyoyoyoyoyoyoyo.fr# | %s/	/ 	 /g | %s/$/ 	 toto/' +'Tabularize / 	' +'%s/toto$//' +'normal gg^WhGy$p' +'%s#http://yoyoyoyoyoyoyoyoyoyoyoyoyoyo.fr#http://wikidataJulioJuEntity# | %s#http://yoyoyoyoyoyoyoyoyoyoyoyoyoyo.fr#http://www.wikidata.org# | %s/ \+	 \+/	/g | %s/\s\+$//g' +x
sed -i 's/^/<owl:Class rdf:about="/' "${fileCommunes}"
sed -i 's/	/"> <rdfs:label xml:lang="fr">/' "${fileCommunes}"
sed -i 's/	/<\/rdfs:label> <rdfs:subClassOf rdf:resource="/' "${fileCommunes}"
sed -i 's/	/"\/> <rdfs:isDefinedBy rdf:datatype="http:\/\/www.w3.org\/2001\/XMLSchema#anyURI">/' "${fileCommunes}"
sed -i 's#$#</rdfs:isDefinedBy>  </owl:Class>#' "${fileCommunes}"

cat << END > "${outputFile}"
<?xml version="1.0"?>
<rdf:RDF xmlns="http://wikiDataFrenchCity/julioJuGeographicalZone.owl#"
xml:base="http://wikiDataFrenchCity/julioJuGeographicalZone.owl"
xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
xmlns:owl="http://www.w3.org/2002/07/owl#"
xmlns:xml="http://www.w3.org/XML/1998/namespace"
xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
xmlns:wikidataProperty="http://wikidataJulioJuProp/"
>
    <owl:Ontology rdf:about="http://julioJuGeographicalZone.owl" />

    <owl:DatatypeProperty rdf:about="http://wikidataJulioJuProp/P2586">
        <rdfs:label xml:lang="en">INSEE department code</rdfs:label>
        <rdfs:isDefinedBy rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://www.wikidata.org/prop/P2586</rdfs:isDefinedBy>
    </owl:DatatypeProperty>

    <owl:Class rdf:about="http://julioJuGeographicalZone.owl/geographicalZoneFrance" >
        <rdfs:label xml:lang="en">Place under geographical zone France</rdfs:label>
    </owl:Class>

    <owl:Class rdf:about="http://julioJuGeographicalZone.owl/CommuneWithoutDepartmentInWikiDataTODOCorrectIt" >
        <rdfs:label xml:lang="en">Without Department.</rdfs:label>
        <rdfs:comment xml:lang="en">This Commune has no Department referenced in WikiData. TODO correct it.</rdfs:comment>
        <rdfs:subClassOf rdf:resource="http://julioJuGeographicalZone.owl/geographicalZoneFrance" />
    </owl:Class>
END
cat "${fileDepartments}" "${fileCommunes}" >> "${outputFile}"
echo "</rdf:RDF>" >> "${outputFile}"
# Tidy (used by Neoformat) add carriage inner a pair of tags. So bad for `<rdfs:label>toto toto \n\t\t toto</toto>`
# Set ft in html is better. Use html-beautify
nvim -n -i NONE -u NORC --cmd 'filetype plugin indent on | set runtimepath=~/.vim/plugged/neoformat/' "${outputFile}" --headless +'set ft=html' +'Neoformat' +x

rm "${fileDepartments}" "${fileCommunes}"
