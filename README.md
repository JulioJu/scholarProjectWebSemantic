
r!-- vim-markdown-toc GFM -->

* [How to](#how-to)
    * [Teacher example](#teacher-example)
    * [scholarProjectWebSemantic](#scholarprojectwebsemantic)
        * [Fuseki embedded](#fuseki-embedded)
        * [Fuseki non embedded](#fuseki-non-embedded)
        * [API implemented](#api-implemented)
        * [HTTP codes returned](#http-codes-returned)
            * [The Vim Plugin roast.vim](#the-vim-plugin-roastvim)
            * [Fuseki Administration](#fuseki-administration)
            * [Wireshark](#wireshark)
        * [Teacher's instructions](#teachers-instructions)
* [scholarProjectWebSemantic details](#scholarprojectwebsemantic-details)
    * [Where is my code](#where-is-my-code)
    * [Why JHipster](#why-jhipster)
    * [Why JWT for dev and limitations](#why-jwt-for-dev-and-limitations)
    * [Notes about Jackson](#notes-about-jackson)
* [Documentations about Web Semantic](#documentations-about-web-semantic)
    * [Courses and MOOC and generalities](#courses-and-mooc-and-generalities)
    * [Linked Data](#linked-data)
        * [LinkedGeoData.org](#linkedgeodataorg)
        * [GeoNames](#geonames)
    * [DBpedia](#dbpedia)
    * [REST API](#rest-api)
    * [Why DBpedia is not so cool](#why-dbpedia-is-not-so-cool)
        * [WikiData](#wikidata)
                * [WikiData Sparql, my experimentations](#wikidata-sparql-my-experimentations)
            * [How to retrieve french city from WikiData](#how-to-retrieve-french-city-from-wikidata)
        * [Conclusion](#conclusion)
    * [Ontologies](#ontologies)
    * [Resource vs Individual vs Class](#resource-vs-individual-vs-class)
    * [Jena Documentation](#jena-documentation)
    * [Jena TDB2 vs Jena Fuseki2 vs OpenLink Virtuoso and Linked Data Platform](#jena-tdb2-vs-jena-fuseki2-vs-openlink-virtuoso-and-linked-data-platform)
        * [Why Fuseki is completely not a Linked Data Platform](#why-fuseki-is-completely-not-a-linked-data-platform)
    * [Fuseki and TDB, sempic.ttl](#fuseki-and-tdb-sempicttl)
    * [Protégé](#protege)
    * [How to install Jena](#how-to-install-jena)
    * [Construct a Jena Query](#construct-a-jena-query)
        * [Sparql syntax](#sparql-syntax)
        * [Java API 1) syntax form of the query](#java-api-1-syntax-form-of-the-query)
        * [Java API 2) Algebra form of the query](#java-api-2-algebra-form-of-the-query)
        * [Java API, Query](#java-api-query)
        * [Jena Source code organization](#jena-source-code-organization)
        * [See also](#see-also)
    * [Jena DELETE](#jena-delete)
        * [Cascading delete](#cascading-delete)
        * [Delete before update](#delete-before-update)
    * [Limitations of Fuseki](#limitations-of-fuseki)
        * [No informations about success](#no-informations-about-success)
* [Fuseki serious troubleshooting](#fuseki-serious-troubleshooting)
    * [Description](#description)
        * [Succession of REQUESTs](#succession-of-requests)
            * [Example 1](#example-1)
            * [Example 2](#example-2)
    * [Solution: restart Fuseki Server](#solution-restart-fuseki-server)
        * [Limitations of resarting Fuseki Server](#limitations-of-resarting-fuseki-server)
        * [Fuseki embedded](#fuseki-embedded-1)
        * [Manage Fuseki standalone with REST API?](#manage-fuseki-standalone-with-rest-api)
        * [The solution: Fuseki standalone managed by the App](#the-solution-fuseki-standalone-managed-by-the-app)
        * [Other solutions studied](#other-solutions-studied)
    * [StackTrace « Iterator used inside a different transaction » and links](#stacktrace-iterator-used-inside-a-different-transaction-and-links)
* [Notes on Spring](#notes-on-spring)
    * [Spring boot arguments](#spring-boot-arguments)
    * [Spring Devtools / hot swapping (watch mode)](#spring-devtools--hot-swapping-watch-mode)
    * [Known issues with Spring devtools](#known-issues-with-spring-devtools)
        * [Why? Threads and Spring devtools](#why-threads-and-spring-devtools)
* [Notes about IDE with Fuseki and Spring](#notes-about-ide-with-fuseki-and-spring)
    * [Eclipse problems](#eclipse-problems)
* [Conflict between Spring devtools and Fuseki embedded](#conflict-between-spring-devtools-and-fuseki-embedded)
    * [Why I've passed so long time on this bug](#why-ive-passed-so-long-time-on-this-bug)
    * [How to Fuseki embedded](#how-to-fuseki-embedded)
    * [@PreDestroy](#predestroy)
    * [Reload instead of Restart](#reload-instead-of-restart)
        * [DCEVM](#dcevm)
    * [kill the thread using port](#kill-the-thread-using-port)
    * [Use loop to restart mvn](#use-loop-to-restart-mvn)
    * [Reflexion about FusekiServer embedded for each request (never used)](#reflexion-about-fusekiserver-embedded-for-each-request-never-used)
    * [A solution: ../MAKEFILE.sh](#a-solution-makefilesh)
    * [But FusekiServer is totally buggy](#but-fusekiserver-is-totally-buggy)
* [Others bugs and TODO](#others-bugs-and-todo)
    * [TODO very important for teacher](#todo-very-important-for-teacher)
* [Credits](#credits)

<!-- vim-markdown-toc -->

# How to

## Teacher example

Start the Fuseki server ***at path ./teacherExample***.

On Arch Linux, with the official https://aur.archlinux.org/packages/apache-jena-fuseki/
run simply:
```sh
cd ./teacherExample
fuseki-server
```

To run the app, run:
```sh
mvn exec:java
```

Note: this example was a little bit modified. Original version is at the first
commit of this repository.
1. I've deleted ejb dependencies (not useful for the example)
2. I've added a new maven goal: `mvn exec:java`. As it, this sample could
    be run without IDE dependency.

See also ./teacherExample/HowToConfigureJenaByJeromeDavid.pdf

The current project should work with Java 8
(see https://www.jhipster.tech/2019/04/04/jhipster-release-6.0.0-beta.0.html ).

I develop with OpenJDK 11 on Linux. I've set java.version to 11 in `pom.xml`.

Note that the current release of Protégé software should use Java 8,
see https://github.com/protegeproject/protege/issues/822 .

## scholarProjectWebSemantic

### Generate ./julioJuGeographicalZone.owl

```sh
wget \
    --header "Accept: text/tab-separated-values" \
    -O DepartmentsWikidata.tsv \
    https://query.wikidata.org/sparql \
    --post-data='query=SELECT%20DISTINCT%20?department%20?departmentLabel%20?inseedepartmentCode%20WHERE%20{%0A%20%20?department%20(wdt:P31/(wdt:P279*))%20wd:Q6465.%0A%20%20OPTIONAL%20{%20?department%20wdt:P2586%20?inseedepartmentCode.%20}%0A%20%20OPTIONAL%20{%20?department%20wdt:P576%20?dissolution.%20}%0A%20%20FILTER(NOT%20EXISTS%20{%20?department%20wdt:P576%20?dissolution.%20})%0A%20%20SERVICE%20wikibase:label%20{%20bd:serviceParam%20wikibase:language%20"fr".%20}%0A}%0AORDER%20BY%20(?inseedepartmentCode)' \
    || exit 2

wget \
    --header "Accept: text/tab-separated-values" \
    -O CommunesWikidata.tsv \
    https://query.wikidata.org/sparql \
    --post-data='query=SELECT%20DISTINCT%20?commune%20?communeLabel%20?departmentURI%20WHERE%20{%0A%20%20?commune%20p:P31%20?communeStatement.%0A%20%20?communeStatement%20ps:P31%20?classCommuneOfFrance.%0A%20%20?classCommuneOfFrance%20(wdt:P279*)%20wd:Q484170.%0A%20%20OPTIONAL%20{%0A%20%20%20%20?commune%20p:P131%20?departmentStatement.%0A%20%20%20%20?departmentStatement%20ps:P131%20?departmentURI.%0A%20%20%20%20?departmentURI%20(wdt:P31/(wdt:P279*))%20wd:Q6465.%0A%20%20%20%20FILTER(NOT%20EXISTS%20{%20?departmentStatement%20pq:P582%20?endtime.%20})%0A%20%20%20%20FILTER(NOT%20EXISTS%20{%20?departmentURI%20wdt:P576%20?dissolution.%20})%0A%20%20}%0A%20%20OPTIONAL%20{%20?communeStatement%20pq:P580%20?startTime.%20}%0A%20%20FILTER((!(BOUND(?startTime)))%20||%20(?startTime%20<%20(NOW())))%0A%20%20OPTIONAL%20{%20?communeStatement%20pq:P582%20?endTime.%20}%0A%20%20FILTER((!(BOUND(?endTime)))%20||%20(?endTime%20>=%20(NOW())))%0A%20%20OPTIONAL%20{%20?commune%20wdt:P571%20?inception.%20}%0A%20%20FILTER((!(BOUND(?inception)))%20||%20(?inception%20<%20(NOW())))%0A%20%20OPTIONAL%20{%20?commune%20wdt:P576%20?dissolved.%20}%0A%20%20FILTER((!(BOUND(?dissolved)))%20||%20(?dissolved%20>=%20(NOW())))%0A%20%20SERVICE%20wikibase:label%20{%20bd:serviceParam%20wikibase:language%20"fr".%20}%0A}%0AORDER%20BY%20(?communeLabel)%20(?commune)' \
    || exit 2

./createWikidataFrenchCommunesOwl.sh
```

* See also (not mandatory) the section « Retrieve departments and communes »

* Preceding files are overwriting without warning.

* Note: do not use `curl` because it displays the error:
    `curl: (3) nested brace in URL position [number]`

### How to set up Fuseki and Openllet

1. At ./fusekiStandaloneWithOpenllet
    * To compile it, simply run
        ```sh
        $ rm -rf target && mvn package
        ```

    * Run it simpy use
        ```sh
        java -jar target/fuseki-1.0-SNAPSHOT.jar
        ```

2. The at ./scholarProjectWebSemantic/ run:
    ```sh
    $ rm -Rf target && mvn  -Dspring-boot.run.arguments="fusekiServerNoManaged"
    ```

* Note: **the database used is ./fusekiStandaloneWithOpenllet/run/***

* Note: ***NO GUI at localhost:3030***. To see it, don't forget
    to clean the Firefox cache ;-). (I've forgotten).
    To have GUI, use solution presented
    at section « Fuseki non embedded » below

### Fuseki managed

In this section fuseki could be managed (stop, start, restart)
by API
* http://localhost:8080/api/startFusekiProcess
* http://localhost:8080/api/stopFusekiProcess
* http://localhost:8080/api/restartFusekiProcess

#### Fuseki embedded
***Section outdated, use Fuseki non embedded. See why in a section below.***

**Note that it's still implemented. But do not use it with Spring.**.

***Could not work in any way actually***
It doesn't has Openllet

`$ mvn -P \!webpack -Dspring-boot.run.arguments="fusekiServerEmbedded"`


* Note: **the database used is ./scholarProjectWebSemantic/run**
    **The config file used is in another folder:
    ./scholarProjectWebSemanticFusekiDatabase/run/configuration/sempic.ttl**

#### Fuseki non embedded

To run the app, run:

```sh
$ mvn -Dspring-boot.run.arguments="fusekiServerNoEmbedded"
```

fuseki-server should be available in the Linux PATH

It should be compiled with Openllet

Wee section `jena-fuseki-fulljar`

***The advantage is that we could have the GUI administration tool!***

* Note: **the database used is ./scholarProjectWebSemanticFusekiDatabase/run**

### API implemented

Server API are tested thanks ./rest_request_with_vim.roast
All API implemented are described in this file.

***All API are protected against SQL injection.***

***THE BEST API IS FOR GET ALBUM***
* READ ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/jhipster/SempicRest/AlbumRDFResourceGet.java
* READ ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/ReadAlbum.java
* This API demonstrates why we should use Jena API and no SPARQL
    Very clear and very easy to factorized!!!
* ==> Very factorized and commented

### HTTP codes returned

* See ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/jhipster/SempicRest/
    to check what each REST API return.

#### The Vim Plugin roast.vim

This plugin was advised in an https://tuppervim.org/ of Paris.

Thanks ./rest_request_with_vim.roast we could test API without front, in Vim.
It manages authentifications tokens automatically.
See https://github.com/sharat87/roast.vim

There are alternatives, non vimesque solutions.

* robot.framwork (at the TupperVim they say me that it's the better)
    Generic test automation framework for acceptance testing and ATDD.
    https://en.wikipedia.org/wiki/Robot_Framework

* cucumber
    https://en.wikipedia.org/wiki/Cucumber_(software)

* postman
    https://www.getpostman.com/

* Note: To copy request in Wireshark to use it see notice below
        * see also https://github.com/sharat87/roast.vim/issues/14

* I've posted lot of issues at
    https://github.com/sharat87/roast.vim/issues?utf8=✓&q=author:JulioJu+

* TODO I've two new ideas of issues to post:
    1. Add type for rescue Wireshark headers in the documentation
    2. Add a template to retrieve authentification header (as it was
        shown at https://github.com/baverman/vial-http but this plugin was buggy
        when it was presented during a TupperVim conf)
    3. Automate tests
    5. Comparaison with vim-rest-console.

* Note vial-http is not compatible with Python 3
    https://github.com/baverman/vial-http/issues/13

* Probably the best alternative is https://github.com/diepm/vim-rest-console/
    * To remove header appends automatically simply use something like that:
        `Accept:`
    * Explications: `curl http://google.com` appends some headers automatically
        like `Accept: */*`. To remove it simply use
        `curl http://google.com -H 'Accept:`. In Wireshark we see that the
        header Accept is not append.
    * Note that the problem with this plugin is as there isn't
        bash heredoc to limit the Post request as with the roast plugin,
        if there are carriage return in a non JSON POST request there are
        some troubleshootings.
        ```
        POST http://google.fr
        toto
        titi
        ```
        send a `tototiti` request. Add "" between both lines cause this
        plugin to crash.
    * But as I said at https://github.com/sharat87/roast.vim/issues/9
        (check quickly in the doc if there is an option to display only the
        result, but not found)
        > Vim doesn't manage well several filetypes in a same buffer. See for
        > instance neovim/neovim#7866 . When tree sitter will be implemented on
        > NeoVim, IMHO implement a custom renderer could be a solution, but not
        > actually. Currently I believe it's better to keep the buffer
        > __roast_headers__ independent. I don't like
        > https://github.com/diepm/vim-rest-console because of this, all is meld
        > in one buffer and the render is not very good.


#### Fuseki Administration

* If you don't want use the app, you could use Fuseki administration at
    `localhost:3000`.

* But it seams we could only make `QUERY`

####  Wireshark

Some API send only HTTP code.

To trace call, use Wireshark.

Start Wireshark with `gksudo wireshark & ; disown %1`

1.  ...using this filter `tcp port 3030`

2. Use interface `Loopback:lo`

3. `menu -> capture -> start`

4. In « Apply a display filter », type `http`

Note: verify the tcpdump is ordered by `N°`.

* Note: To copy request in Wireshark to use it in vim roast or other tool
    simply
    1. double click on the row of the green pan
    2. A new window appears with two pans
    3. On the top pan, unfold « Hyptertext Transfer Protocol »
    4. Select the blue link « `[Full request URI:
    http://localhost…]` »
    (as it we are sure
    that we are on the second tab named « Reassembled TCP »).
    5. On its bottom pan righ click anywhere
    6. contextual menu appears
    7. copy
    8. as Printable text
    * Note: on Wireshark, Unicode and not printable characters are represented by
        the symbole ` . `.  It's not a problem, when you copy it in Vim the correct
        corresponding symbols are represented.
        https://github.com/sharat87/roast.vim/issues/22

### Teacher's instructions

See ./TeachersInstruction1.pdf

See ./TeachersInstruction2.pdf

### Install Protégé

* See https://bugs.archlinux.org/task/63080
    (issue posted by me)
    for ArchLinux
    Contrary to the build of ArchLinux, JRE is packaged with the app and
    default third party plugins are installed with it.

* See all versions of Protégé at
    https://protegewiki.stanford.edu/wiki/Protege_Desktop_Old_Versions
    * Or https://github.com/protegeproject/protege-distribution/releases/

* Do not build with
    https://github.com/protegeproject/protege/wiki/Building-from-Source
    because of
    https://github.com/protegeproject/protege/issues/903 (issue posted by me)
    (Window "Inconsistent ontology explanation" not displayed )

* As I say in « Protégé, generalities » section, « Regressions on Protégé 5.5 »
    , ***use Protégé 5.2 or 5.6, not Protégé 5.5***.
    Note that Protégé 5.5 add some useful functionalities, like a button
    to copy and past URI of an entity

* Note, on the official doc « Building from source » of
    https://github.com/protegeproject/protege/wiki/Building-from-Source
    I have done the following change:
    https://github.com/protegeproject/protege/wiki/Building-from-Source/_compare/708ed56e6932047f71dfac09c9e9a3c1caf71bbe...43706233fc63d3cf24013b4f1db1aeffcee54540

#### Protégé 4.x

Protégé seems to need Java 6. I've tested with Oracle-jre 7 and 8 without success
As JRE 7 and 8 are not provided by Oracle without
register, downladed thanks https://github.com/frekele/oracle-java/releases
see also https://aur.archlinux.org/packages/jdk8/ )

To test with Protégé 4.x, simply download Protégé with JRE embedded.
See https://protege.stanford.edu/download/protege/4.3/installanywhere/Web_Installers/

##### JDK6 and mvn

To build jdk6, use https://aur.archlinux.org/packages/jdk6/
Change the PKGBUILD with the source
http://www.atteya.net/site/en/downloads/java-jdk?download=48:java-jdk-6u45-linux-x64

> According to the Maven release history page, the last Maven version that works with JDK1.6 is 3.2.5.
> https://stackoverflow.com/questions/36141186/what-version-of-maven-is-compatible-with-java-6

Download it at https://www-us.apache.org/dist/maven/maven-3/3.2.5/binaries/apache-maven-3.2.5-bin.tar.gz

#### Pellet Protégé Plugin

As I show in the following doc, Pellet reasoner seems to be the best.

To download it, use the official git repo https://github.com/stardog-union/pellet
Should be compiled with oracle-jdk8 or openjdk8
(jdk12 or openjdk12)
> Removal of javac Support for 6/1.6 source, target, and release Values
>
> Consistent with the policy outlined in JEP 182: Policy for Retiring javac -source and -target Options, support for the 6/1.6 argument value for javac's -source, -target, and --release flags has been removed.
> https://www.oracle.com/technetwork/java/javase/12-relnote-issues-5211422.html

then put the pellet Jar into the plugins folder of Protege

```
$ sudo cp protege/plugin/com.clarkparsia.protege.plugin.pellet.jar /usr/share/java/protege/plugins
```

Difference with the last released version of Pellet 2.3 seems to be minor
(according to the commit history).

Do not forget to not use Protege build with
    https://github.com/protegeproject/protege/wiki/Building-from-Source
    because  it lack some Plugins dependencies.

See also https://github.com/protegeproject/protege-distribution/issues/21

##### oracle-jdk8

https://aur.archlinux.org/packages/jdk8/#comment-691071


# How to use fuseki and Openllet Reasoner

## Why simply add Openllet in classpath doesn't work:

* I have the following error if I try to manually add all dependencies
    of Openllet in the classpath (hard to understand this error)

* Simply comment the exclusion under ./fusekiStandaloneWithOpenllet/pom.xml
    and you will see the following error

```
Exception in thread "main" java.lang.NoClassDefFoundError: org/apache/jena/sys/JenaSystem
        at org.apache.jena.fuseki.Fuseki.init(Fuseki.java:269)
        at org.apache.jena.fuseki.Fuseki.<clinit>(Fuseki.java:291)
        at org.apache.jena.fuseki.servlets.ActionService.<init>(ActionService.java:51)
        at org.apache.jena.fuseki.servlets.SPARQL_Protocol.<init>(SPARQL_Protocol.java:46)
        at org.apache.jena.fuseki.servlets.SPARQL_Query.<init>(SPARQL_Query.java:68)
        at org.apache.jena.fuseki.servlets.SPARQL_QueryDataset.<init>(SPARQL_QueryDataset.java:29)
        at org.apache.jena.fuseki.servlets.ServiceDispatchRegistry.<clinit>(ServiceDispatchRegistry.java:45)
        at org.apache.jena.fuseki.main.FusekiServer$Builder.<init>(FusekiServer.java:275)
        at org.apache.jena.fuseki.main.FusekiServer.create(FusekiServer.java:120)
        at fr.julioju.fusekiEmbedded.Main.serverStartEmbeddedFuseki(Main.java:20)
        at fr.julioju.fusekiEmbedded.Main.main(Main.java:45)
Caused by: java.lang.ClassNotFoundException: org.apache.jena.sys.JenaSystem
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:583)
        at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:178)
        at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:521)
        ... 11 more
```

TODO add an issue on https://github.com/Galigator/openllet

## Introduction to the following solutions

* ***Fuseki could be started in different way***
    See https://jena.apache.org/documentation/fuseki2/

* Following it tested and work for d4beb7d99d48e98c981d434c980f83784b519ebd

* (version https://github.com/apache/jena/tree/d4beb7d99d48e98c981d434c980f83784b519ebd )

* Tagged version 3.12.0 of Fuseki

* Compile thanks the command `mvn package`

## jena-fuseki-war (Fuseki as a Web Application)

* Under https://github.com/apache/jena/tree/master/jena-fuseki2/jena-fuseki-war

* For file https://github.com/apache/jena/blob/d4beb7d99d48e98c981d434c980f83784b519ebd/jena-fuseki2/jena-fuseki-war/pom.xml

* Add following:
    ```xml
    <!-- Added by JulioJu -->
    <!-- ——————————————— -->
        <!--  https://github.com/Galigator/openllet -->
        <dependency>
            <groupId>com.github.galigator.openllet</groupId>
            <artifactId>openllet-owlapi</artifactId>
            <version>2.6.4</version>
            <exclusions>
                <!-- FOLLOWING  SHOULD BE EXCLUDE OTHERWISE
                THE MAVEN SHADE PLUGIN DETECT UNRESOLVABLE VERSION CONFLICT-->
                <!-- On « jena-fuseki-fulljar » package, no need to  exclude following -->
                <exclusion>
                    <!-- The shade pulgin print the following error if you remove this exclusion: -->
                    <!-- ===== --->
                    <!-- Dependency convergence error for com.fasterxml.jackson.core:jackson&#45;annotations:2.9.0 paths to -->
                    <!-- ============================= -->
                        <!-- dependency are: -->
                    <groupId>com.fasterxml.jackson.core</groupId>
                    <artifactId>jackson-annotations</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>com.github.galigator.openllet</groupId>
            <artifactId>openllet-jena</artifactId>
            <version>2.6.4</version>
            <!-- FOLLOWING  SHOULD BE EXCLUDE OTHERWISE
            THE MAVEN SHADE PLUGIN DETECT UNRESOLVABLE VERSION CONFLICT-->
            <exclusions>
                <exclusion>
                    <!-- The shade pulgin print the following error if you remove this exclusion: -->
                    <!-- ===== --->
                    <!-- Dependency convergence error for org.apache.jena:jena&#45;arq:3.7.0 paths to dependency  -->
                    <!-- ============================= -->
                    <groupId>org.apache.jena</groupId>
                    <artifactId>jena-arq</artifactId>
                </exclusion>
                <exclusion>
                    <!-- The shade pulgin print the following error if you remove this exclusion: -->
                    <!-- ===== --->
                    <!-- Dependency convergence error for org.apache.jena:jena&#45;shaded&#45;guava -->
                    <!-- ============================= -->
                    <groupId>org.apache.jena</groupId>
                    <artifactId>jena-core</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    <!-- End added by JulioJu -->
    ```

* I've tested with tomcat 8.0.

* See also https://jena.apache.org/documentation/fuseki2/fuseki-run.html#fuseki-server

* To deploy tomcat under ArchLinux:
    1. Before deploy it, don't forget to create `/etc/fuseki`
        with correct write permission!!
    2. copy
    ```sh
    $ cp target/jena-fuseki-war-3.12.0 ~tomcat8/webapps -R
    ```
    3. Run tomcat
    ```
    $ /usr/share/tomcat8/bin/startup.sh
    ```
    * Note 1:
    To stop tomcat:
    ```sh
    $ /usr/share/tomcat8/bin/shutdown.sh
    ```
    * Note 2
    Before run tomcat don't forget to add correct write permissions to
    `/var/log/tomcat8/` otherwise tomcat will fail to run!
    The error is about `usr/share/tomcat8/logs` but this is only a symlink.
    * Note 3
        I've tested to deploy with http://localhost:8080/manager/html/
        without success
        I've set correct permissions under /etc/tomcat8/tomcat-users.xml
        don't know why it fail (permission denied, even if `~tomcat8/webapp` has 777 permission), but not important
        ```xml
  <role rolename="tomcat"/>
  <role rolename="manager-gui"/>
  <user username="admin" password="admin" roles="manager-gui,tomcat"/>
        ```


### How to use Tomcat in a package without install it

* Check https://stackoverflow.com/questions/30406479/how-can-i-package-a-tomcat-server-into-a-standalone-double-clickable-package

* JHipster use Undertow to have an embedded web server

## jena-fuseki-fulljar (Fuseki as a Standalone Server¶)

* For https://github.com/apache/jena/tree/d4beb7d99d48e98c981d434c980f83784b519ebd/jena-fuseki2/jena-fuseki-fulljar

* Under https://github.com/apache/jena/blob/d4beb7d99d48e98c981d434c980f83784b519ebd/jena-fuseki2/jena-fuseki-fulljar/pom.xml
    Add following

    ```xml
    <!-- Added by JulioJu -->
    <!-- ——————————————— -->
        <!--  https://github.com/Galigator/openllet -->
        <dependency>
            <groupId>com.github.galigator.openllet</groupId>
            <artifactId>openllet-owlapi</artifactId>
            <version>2.6.4</version>
            <exclusions>

                <!-- In the ../jena-fuseki-war/pom.xml -->
                <!-- Following is not needed -->
                <exclusion>
                    <!-- The shade pulgin print the following error if you remove this exclusion: -->
                    <!-- ===== -->
                    <!-- [WARNING] owlapi&#45;api&#45;5.1.11.jar, owlapi&#45;distribution&#45;5.1.11.jar define 917 overlapping classes: -->
                    <!--  -->
                    <!-- [WARNING]   &#45; org.semanticweb.owlapi.model.OWLPropertyAssertionObject -->
                    <!-- [WARNING]   &#45; org.semanticweb.owlapi.model.OWLAnonymousClassExpression -->
                    <!-- [WARNING]   &#45; org.semanticweb.owlapi.model.HasApplyChange -->
                    <!-- [NOTE OF JULIOJU, I'VE NOT PAST LOT OF MESSAGES LIKE
                            THE [WARNING] ABOVE-->

                    <!-- [WARNING] maven&#45;shade&#45;plugin has detected that some class files are -->
                    <!-- [WARNING] present in two or more JARs. When this happens, only one -->
                    <!-- [WARNING] single version of the class is copied to the uber jar. -->
                    <!-- [WARNING] Usually this is not harmful and you can skip these warnings, -->
                    <!-- [WARNING] otherwise try to manually exclude artifacts based on -->
                    <!-- [WARNING] mvn dependency:tree &#45;Ddetail=true and the above output. -->
                    <!-- [WARNING] See http://maven.apache.org/plugins/maven&#45;shade&#45;plugin/ -->
                    <!-- ============================= -->
                    <groupId>net.sourceforge.owlapi</groupId>
                    <artifactId>owlapi-distribution</artifactId>
                </exclusion>

            </exclusions>
        </dependency>
        <dependency>
            <groupId>com.github.galigator.openllet</groupId>
            <artifactId>openllet-jena</artifactId>
            <version>2.6.4</version>
            <!-- FOLLOWING  SHOULD BE EXCLUDE OTHERWISE
            THE MAVEN SHADE PLUGIN DETECT UNRESOLVABLE VERSION CONFLICT-->
            <!-- Already a dependency of fuseki-core -->
            <exclusions>
                <exclusion>
                    <!-- The shade pulgin print the following error if you remove this exclusion: -->
                    <!-- ===== --->
                    <!-- Dependency convergence error for org.apache.jena:jena&#45;arq:3.7.0 paths to dependency  -->
                    <!-- ============================= -->
                    <groupId>org.apache.jena</groupId>
                    <artifactId>jena-arq</artifactId>
                </exclusion>
                <exclusion>
                    <!-- The shade pulgin print the following error if you remove this exclusion: -->
                    <!-- ===== --->
                    <!-- Dependency convergence error for org.apache.jena:jena&#45;shaded&#45;guava -->
                    <!-- ============================= -->
                    <groupId>org.apache.jena</groupId>
                    <artifactId>jena-core</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    <!-- End added by JulioJu -->
    ```

* To deploy, use
    1. Add the folder `webapp`
    (retrieve it under
    https://www-eu.apache.org/dist/jena/binaries/apache-jena-fuseki-3.12.0.tar.gz)
        * In short you must have a folder containing two elements at the root of
            this folder:
        1. `jena-fuseki-server-3.12.0.jar` (eventually renamed `jena-fuseki-server.jar`)
        2. the  `webapp` folder
    2. Trigger `$ java -jar ./jena-fuseki-server.jar`

## Fuseki embedded (Fuseki as a Standalone Server) (without GUI)

* See https://jena.apache.org/documentation/fuseki2/fuseki-main.html

* My code is at ./fusekiStandaloneWithOpenllet

* To compile it, simply run
    ```sh
    $ rm -rf target && mvn package
    ```

* Run it simpy use
    ```sh
    java -jar target/fuseki-1.0-SNAPSHOT.jar
    ```

* The file ./fusekiStandaloneWithOpenllet/src/main/resources/logback.xml
    is interesting

* Remember than sl4j is not an implementation and that logback is
    currently the better implem (used by Spring), and read on other pages
    on the net.

* Check ./fusekiStandaloneWithOpenllet/pom.xml
    to know how to configure fuseki embedded.

### How to build a jar

* Check this pom.xml, under the tag `<build>`

* Check https://www.baeldung.com/executable-jar-with-maven
    interesting link!

* I don't understand why `maven-shade-plugin` actually doesn't work with
    fuseki-embedded
    TODO why??


# My ontologies

* Read it in Protégé

## sempiconto.owl

### Dublin Core

I use Dublin Core in annotations of Ontology
https://en.wikipedia.org/wiki/Dublin_Core

> The Dublin Core Metadata Initiative supports innovation in metadata design and
> best practices.
> http://dublincore.org/

### Simple Knowledge Organization System

> The Simple Knowledge Organization System (SKOS) is a common data model for
> sharing and linking knowledge organization systems via the Semantic Web
> https://www.w3.org/2009/08/skos-reference/skos.html

I use `http://www.w3.org/2004/02/skos/core#` especially
`http://www.w3.org/2004/02/skos/core#definition`

For instance

```xml
    <owl:Class rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#DepictionMammalExceptHuman">
        <rdfs:subClassOf rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#DepictionWhatAnimal"/>
        <rdfs:comment xml:lang="fr">Mammifère à l&apos;exception des humains</rdfs:comment>
        <skos:definition xml:lang="fr">Mammifère à l&apos;exception des humains</skos:definition>
    </owl:Class>
```

See also https://en.wikipedia.org/wiki/Simple_Knowledge_Organization_System

### Shapes Constraint Language (SHACL)

Used to know what is the shape of `http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumSharedWith`

In Protégé, under the tab `Entities -> Indivudal` see
http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#ShapeSharedWithUserCollection

It is defined as:
```xml
        <rdfs:seeAlso>
            <rdf:Description>
                <rdfs:comment xml:lang="en">The example</rdfs:comment>
                <rdfs:seeAlso rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#ShapeSharedWithUserCollection</rdfs:seeAlso>
            </rdf:Description>
        </rdfs:seeAlso>
        <rdfs:seeAlso>
            <rdf:Description>
                <rdfs:comment xml:lang="en">On what the example is based</rdfs:comment>
                <rdfs:seeAlso rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">https://www.topquadrant.com/2017/06/13/constraints-on-rdflists-using-shacl/</rdfs:seeAlso>
            </rdf:Description>
        </rdfs:seeAlso>
        <rdfs:seeAlso>
            <rdf:Description>
                <rdfs:comment xml:lang="en">Official example to understand how SHACL works</rdfs:comment>
                <rdfs:seeAlso rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">https://www.w3.org/TR/shacl/#shacl-example</rdfs:seeAlso>
            </rdf:Description>
        </rdfs:seeAlso>
```

It renders this thing as annotations. But I don't understand why.

See also
* the Official example to understand how SHACL works https://www.w3.org/TR/shacl/#shacl-example
* On what the example is based https://www.topquadrant.com/2017/06/13/constraints-on-rdflists-using-shacl/

### « Who » question

The property could be ObjectProperty or DatatypeProperty

### « What » question

The property could be ObjectProperty or DatatypeProperty

### « When » question


#### Datatypes imported
1. date

```xml
    <rdfs:Datatype rdf:about="http://www.w3.org/2001/XMLSchema#date">
        <rdfs:comment xml:lang="en">Imported by me</rdfs:comment>
        <rdfs:isDefinedBy xml:lang="en">https://www.w3.org/TR/xmlschema11-2/#date</rdfs:isDefinedBy>
        <owl:versionInfo xml:lang="en">(…)

The dateLexicalRep production is equivalent to this regular expression:
    -?([1-9][0-9]{3,}|0[0-9]{3})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])(Z|(\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00))?
Note that neither the dateLexicalRep production nor this regular expression
alone enforce the constraint on dateLexicalRep given above.

(…)
The ·lexical mapping· for date is ·dateLexicalMap·. The ·canonical mapping· is
·dateCanonicalMap·.
(…)</owl:versionInfo>
    </rdfs:Datatype>
```
1. xsd:gYear

```xml
    <rdfs:Datatype rdf:about="http://www.w3.org/2001/XMLSchema#gYear">
        <rdfs:comment xml:lang="en">Imported by me xsd:date</rdfs:comment>
        <rdfs:isDefinedBy xml:lang="en">https://www.w3.org/TR/xmlschema11-2/#gYear</rdfs:isDefinedBy>
    </rdfs:Datatype>
```
2. xsd:gYearMonth
```xml
    <rdfs:Datatype rdf:about="http://www.w3.org/2001/XMLSchema#gYearMonth">
        <rdfs:comment xml:lang="en">Imported by me xsd:date</rdfs:comment>
        <rdfs:isDefinedBy xml:lang="en">https://www.w3.org/TR/xmlschema11-2/#gYearMonth</rdfs:isDefinedBy>
    </rdfs:Datatype>
```
##### OWL-Time

This ontology is defined at https://www.w3.org/TR/owl-time/#time:DateTimeInterval

The following DatatypeProperty of `http://www.w3.org/2006/time` can't be used
because their domains are

```xml
    <owl:Class rdf:about="http://www.w3.org/2006/time#Instant">
        <!-- […] -->
        <rdfs:comment xml:lang="en">A temporal entity with zero extent or duration</rdfs:comment>
        <rdfs:label xml:lang="en">Time instant</rdfs:label>
        <skos:definition xml:lang="en">A temporal entity with zero extent or duration</skos:definition>
    </owl:Class>
```

1. `http://www.w3.org/2006/time#inXSDDate`
2. `http://www.w3.org/2006/time#inXSDgYear`
3. `http://www.w3.org/2006/time#inXSDgYearMonth`


#### Winter, Spring, Summer, Autumn

Two solutions to define year and month.
The second, with Individuals is probably best solution

##### Datatypes created

* gYearSeason
    ```xml

    <rdfs:Datatype rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#gYearSeason">
        <rdfs:comment xml:lang="en">Is equivalent to the regular expression

-?([1-9][0-9]{3,}|0[0-9]{3})-(summer)|(autumn)|(winter)|(spring)</rdfs:comment>
        <rdfs:seeAlso xml:lang="en">https://www.w3.org/TR/xmlschema11-2/#date</rdfs:seeAlso>
    </rdfs:Datatype>
    ```

##### Solution studied (outdated): define thanks an interval of time

> in the individuals tab for example, by clicking the plus sign besides the
> "Types" field.  Then you put in the expression "hasCalories some
> integer[>=200,<=250]".
> https://mailman.stanford.edu/pipermail/protege-user/2014-August/001178.html
I've tested te preceding solution without success, even if hasCalories is correct
Tested also with `DateInterval some xsd:date[>=0000-09-21, <0000-12-22]`
without more success. (existential restriction)

> If you really need to assert that an intervals of integer as a property value
> (e.g., specify a month as an interval of days in a year, I think you’d have to
> define Interval as a class and specify max and min as data properties.
> http://protege-project.136.n4.nabble.com/Data-property-assertions-for-individuals-in-Protege-5-0-beta-td4661131.html

###### OWL-Time

* Or use:
    https://www.w3.org/TR/owl-time/#time:DateTimeInterval

* ***To understand how define interval of time***
    download https://www.w3.org/TR/owl-time/ and open it in Protégé. Study
    the tab « Individuals ».
    * Second, minutes, hours are defined as an instance of « Temporal Unit »
        and with « Data property assertion » of days, years, weeks, etc.
    * Data properties « Days », « Years », etc has simply a domain
        « Generalized duration description »

##### Define simply as an Individual

* Contrary as I have thought, winter, spring, summer, autumn can't be
    defined tanks an interval of times in a year. In fact, in France, the
    beginning date and the ending date of each change. They are not the same
    all years.
    See the following section of the Wikipedia article:
     https://en.wikipedia.org/wiki/Season#Astronomical

    Furthermore, there are two types of seasons. Astronomical seasons (used
    in France) and Meteorological seasons (used in some countries).
    See the following section of the Wikipedia article
    https://en.wikipedia.org/wiki/Season#Four-season_calendar_reckoning

##### Solution studied: use existing ontologies

* RDF Calendar
    https://www.w3.org/TR/rdfcal/
    But too much to answer at question « When photo was taken », user
    simply https://www.rubydoc.info/github/ruby-rdf/rdf/RDF/Literal/Date

* There is also https://www.w3.org/TR/owl-time/
    Very interesting and should be study to learn how they define time.
    But not needed. Simply I my own properties.


## « Where » question : ./julioJuGeographicalZone.owl

To understand how it works, simply see in sempiconto.owl

```xml
    <owl:ObjectProperty rdf:about="http://www.co-ode.org/ontologies/ont.owl#photoTakenInFrancePlace">
        <rdf:type rdf:resource="http://www.w3.org/2002/07/owl#FunctionalProperty"/>
        <rdfs:domain rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Photo"/>
        <rdfs:range rdf:resource="http://julioJuGeographicalZone.owl/geographicalZoneFrance"/>
        <rdfs:label xml:lang="en">photo taken in France Place</rdfs:label>
    </owl:ObjectProperty>
```

* I don't import julioJuGeographicalZone.owl into sempiconto.owl because
    julioJuGeographicalZone.owl is too big (near 36000 entities) and
    too much individuals. Anyway, those ontologies work together in
    Fuseki thanks declarations into `sempic.ttl` (see section above
    « Generate ./julioJuGeographicalZone.owl »)

    ```xml
    <owl:Ontology rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl">
        <owl:imports rdf:resource="……julioJuGeographicalZone.owl"/>
    </owl:Ontology>
    ```

### Utilisation of rdfs:isDefinedBy is not good

I used this to say
```xml
<owl:Class rdf:about="http://wikidataJulioJuEntity/entity/Q3083">
    <rdfs:isDefinedBy rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://www.wikidata.org/entity/Q3083</rdfs:isDefinedBy>
</owl:Class>
```

* Thanks content negotiation
    `wget http://www.wikidata.org/entity/Q3083`
    retrieve a json file.
    In `firefox http://www.wikidata.org/entity/Q3083` display an HTML file.

https://terms.tdwg.org/wiki/rdfs:isDefinedBy
> isDefinedBy: is an instance of rdf:Property that is used to indicate a resource
> defining the subject resource. This property may be used to indicate an RDF
> vocabulary in which a resource is described.
>
> Notes: The definition of the subject resource. It points to the authoritative
> information about the resource (which are not necessarily RDF, often html, and
> in some cases PDF).

==> at https://www.ics.forth.gr/isl/ontology/content-MTLO/html/owl_Thing.html
we have
`owl:thing rdfs:isDefinedBy http://www.w3.org/2002/07/owl#`

==> Not know if my utilisation of `rdfs:isDefinedBy` is very canonical.
    Probably, `rdfs:seeAlso` should be better. In fact, this page
    is not a whole resource (HTML, PDF) with a definition of
    http://wikidataJulioJuEntity/entity/Q3083 !!!

https://www.w3.org/TR/rdf-schema/#ch_isdefinedby
>      S rdfs:isDefinedBy O
>
> states that the resource O defines S. It may be possible to retrieve
> representations of O from the Web, but this is not required. When such
> representations may be retrieved, no constraints are placed on the format of
> those representations. rdfs:isDefinedBy is a subproperty of rdfs:seeAlso.

* In constrast, use `rdfs:isDefinedBy` for datatypes is a good option!


### How to generate

* See section above « Generate ./julioJuGeographicalZone.owl »

## The author of the picture

* As often the owner of the album has taken the photo, I have

    ```xml
    <owl:DatatypeProperty rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#photoTakenByOwner">
        <rdfs:subPropertyOf rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#photoTakenBy"/>
        <rdf:type rdf:resource="http://www.w3.org/2002/07/owl#FunctionalProperty"/>
        <rdfs:domain rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#User"/>
        <rdfs:range rdf:resource="http://www.w3.org/2001/XMLSchema#boolean"/>
        <rdfs:comment xml:lang="en">WARNING: the java app should deduce that the photo is taken by the owner&apos;s album, not by somebody named &quot;true&quot; or &quot;false&quot; ;-)</rdfs:comment>
        <rdfs:label xml:lang="en">photo taken by owner</rdfs:label>
    </owl:DatatypeProperty>
    ```

# scholarProjectWebSemantic details
This application was generated using JHipster 6.0.0-beta.0, you can find documentation and help at https://www.jhipster.tech/documentation-archive/v6.0.0-beta.0 .

See also ./scholarProjectWebSemantic/README.md

Entities generated thanks

```sh
cd ./scholarProjectWebSemantic
jhipster import-jdl ../generator_of_entities.jh --force
```

(see ./generator_of_entities.jh for further details)

## Where is my code

All my code is under
1. ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic
2. ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/jhipster/SempicRest
    * In fact the file SempicRest.java should be under the Spring boot project
    defined by
    ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/jhipster/ScholarProjectWebSemanticApp.java
    . With the current config, Spring could only automatically load REST routes
    defined under its root folder.

* There are one add under
    ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/jhipster/ScholarProjectWebSemanticApp.java

## Why JHipster

***I've removed lot of classes generated by JHipster, all front-end generated by
JHipster, the JHipster dependency, lot of not useful dependencies in pom.xml,
all JPA, all tests generated by JHipster,
Spring prod profil (keep dev profil)***


* I used JHipster because I know it very very well (see my Pull Requests and issues
    on https://github.com/jhipster/generator-jhipster ). I've followed
    last year severals conferences about it Grenoble and Paris.
    Mainly, I've follow the JHipster conf 2018 during one day in Paris

* Actually JHipster is not very used in this project. Used only to manage
    authentifications with users. Probably I could easy use a fake
    authentification process with fakes [JWT](https://jwt.io/) tokens (TODO).

    The front-end is not used at all. Only Back-end is used currently.

* Probably it's a cool solution as Proof of Concept, even if for the current
    project. In fact, I don't know hot to deploy a Spring boot application
    and configure well Jackson, Zalando, Spring REST, and maybe others.
    Furthermore, I as it I didn't implemented a fake authentification server,
    or even more so a real authentification server. Furthermore we could
    trust the authentification server of JHipster.

* Conversely, we have lot of dependencies and a fat app, even
    if we consider only Server Side, and I don't like that. We have Hibernate,
    Zalando, a SQL database, and lot of others stuffs not really needed.
    I believe it could be cool to implement something 100% Sparql without
    SQL.

* Note, JWT is stateless. Therefore it could be very easy to implement. No
    cookies, no token saved in database. JWT use simply a pair public key /
    private key and hash the token containing some informations about
    authentification (name of the user, date, etc.). Seems very cool for us
    if I have time to eject JHipster! Take model on what they have done.
    But implement a cool authentification system is not asked by the teacher
    for the current project. If I have one, it's cool, but no more. The
    others students have a small one gave by the teacher.

* For the FrontEnd, I believe I could use my work about
    https://github.com/JulioJu/medicalCentre . To make forms it could be very
    cool. My forms are discribed in JSON
    (thanks https://angular.io/guide/dynamic-form). It is very factorised,
    with good functionalities,
    and the teacher was happy with it. But make a front-end is not asked
    for the current scholar project.

## Why JWT for dev and limitations

* I continue to use JWT, even if when JHipster was deleted because
    as it's stateless, this solution is very cool. When I delete
    the database for each modification of the file sempiconto.owl, I keep
    the credentials (as it's stateless). Very interesting to test API.

* But a limitation of JWT is that it doesn't check than the user exists
    in the database. A user deleted continue to have access to pages
    reserved of authenticated users.
    * Therefore for all API we must test existence in Database
        (TODO test if JHipster
        make always this check)

* An other limitation is that it doesn\'t check the current role of the
    user in the database.
    If the user has a Spring Security Role
    `UserRDF.UserGroup.ADMIN_GROUP.toString()`, then it's updated
    to an other Spring Security Role
    (e.g `UserRDF.UserGroup.NORMAL_USER_GROUP.toString()`),
    filers in ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/jhipster/config/SecurityConfiguration.java
    continue to give access to this user of pages with an audience limited
    to `UserRDF.UserGroup.ADMIN_GROUP.toString()`.
    * Therefore I test all permissions by retrieve Spring Security Role in Database
    * TODO probably make a PR to JHipster to check page audience always without
        SecurityConfiguration.java

* Note: the hash is saved in
     ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/jhipster/security/jwt/TokenProvider.java

* Security management is kept fron the initial JHipster application generated thanks 6 beta 0
    with some littles simplifications.

* For the following
    ```
    PUT {root}/api/register << END                                                                                     |~
    {{"login":"admin2","password":"admin2","userGroup":"ADMIN_GROUP"}}                                                 |~
    END                                                                                                                |~
    ```
    For each PUT, PasswordEncoder.encode encode the field                                                            |~
    `password` in a different way. Probably, it is stateless.                                                        |~

* If the token is out of date with the Database State, send a HTTP 409 Conflict
    (see ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/Exceptions/TokenOutOfDateException.java).
    I test Token and Database)
    * In case of the user logged is not saved in Database
    * For API that needs
       `UserRDF.UserGroup.ADMIN_GROUP.toString()`
       if roles saved in the Token are out of date (see above in which case)
    * Should be used for all CRUD API except
        `GET {root}/api/userRDF/:login (in production, this API should also be protected)`
        `GET {root}/api/createInitialUser` (In production, this API should not be available)

 * Very important note: all logins are converted to lower case for
     security reasons
     See also https://cloud.google.com/blog/products/gcp/12-best-practices-for-user-account

* TODO password should be tested to not be too weak.

## Notes about Jackson

* Following send error 500 and not 404. Don't know why, no investigated
```java
    public FusekiSubjectNotFoundException(Node_URI node_URI) {
        super(ErrorConstantsSempic.FUSEKI_SUBJECT_NOT_FOUND,
                "'" + node_URI.getURI() + "' not found in database"
                + " (at least not a RDF subject)." ,
                Status.NOT_FOUND);
        this.node_URI = node_URI;
    }
```

* By default Zalando doesn't manage status code. Status code seems to be managed by
    ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/jhipster/web/rest/errors/ExceptionTranslator.java
    * See also
        ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/jhipster/config/JacksonConfiguration.java

## How to include include Jena in Spring boot

* See ./scholarProjectWebSemantic/pom.xml

* Note: to not have the following warning before Spring is started
    (on IntelliJ, Eclipse or in a Console)
    We must exclude sl4j dependencies of Jena.
    Under Jena dependencies, check my tag `<exclusions><exclusions>`
```
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/media/data/home/m2/repository/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/media/data/home/m2/repository/org/slf4j/slf4j-log4j12/1.7.26/slf4j-log4j12-1.7.26.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/media/data/home/m2/repository/org/slf4j/slf4j-nop/1.5.3/slf4j-nop-1.5.3.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/media/data/home/m2/repository/org/slf4j/slf4j-jdk14/1.5.6/slf4j-jdk14-1.5.6.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [ch.qos.logback.classic.util.ContextSelectorStaticBinder]
```

# Documentations about Web Semantic

## Courses and MOOC and generalities

* The Course of M.  Atencias: http://imss-www.upmf-grenoble.fr/~atenciam/WS/
    * ***Two well understand http://imss-www.upmf-grenoble.fr/~atenciam/WS/5-owl.pdf
        study with the Protégé software. Make experimentations. See the xml owl
        file generated. Check the doc online of the tag generated***.
    * What is a statement: https://stackoverflow.com/questions/21391135/what-is-the-owlnothing-class-designed-to-do/21391737
    * Nodes in hierarchy:
        * http://soft.vub.ac.be/svn-pub/PlatformKit/platformkit-kb-owlapi3-doc/doc/owlapi3/javadoc/org/semanticweb/owlapi/reasoner/Node.html
        * https://stackoverflow.com/questions/21391135/what-is-the-owlnothing-class-designed-to-do/21391737
        * https://en.wikipedia.org/wiki/Semantic_Web
    * The MOOC from INRIA (in french, but a little bit short)
        * About: https://www.fun-mooc.fr/courses/course-v1:inria+41002+self-paced/about
        1. Semaine 1 « Vers un web de données liées »
            https://www.fun-mooc.fr/c4x/inria/41002S02/asset/C013FG-W1.pdf
        2. Semaine 2 « Le modèle de données RDF »
            https://www.fun-mooc.fr/c4x/inria/41002S02/asset/C013FG-W2.pdf
        3. Semaine 3 « Le langage de requête SPARQL »
            https://www.fun-mooc.fr/c4x/inria/41002S02/asset/C013FG-W3.pdf
        4. Semaine 4 « Ontologies et schémas RDFS »
            https://www.fun-mooc.fr/c4x/inria/41002S02/asset/C013FG-W4.pdf
        5. Semaine 5 « Formalisation en OWL »
            https://www.fun-mooc.fr/c4x/inria/41002S02/asset/C013FG-W5.pdf
        6. Semaine 6 « Des schémas particuliers »
            https://www.fun-mooc.fr/c4x/inria/41002S02/asset/C013FG-W6.pdf
        7. Semaine 7 « Vers plus d’intégration de données »
            https://www.fun-mooc.fr/c4x/inria/41002S02/asset/C013FG-W7.pdf
    * « Common Errors In OWL »
        https://protege.stanford.edu/conference/2004/slides/6.1_Horridge_CommonErrorsInOWL.pdf
    * MOOC from OLEKSIY KHRIYENKO. ***VERY COOL AND VERY COMPLETE***
        * Semantic Web and Linked Data
            IHME course Spring 2015
            http://users.jyu.fi/~olkhriye/IHME/IHME_Course-SemanticWeb_LinkedData.pdf
        * TIES4520 Semantic Technologies for Developers
            Autumn 2018
            * Lecture 1: Semantic Web in a nutshel
                http://users.jyu.fi/~olkhriye/ties4520/lectures/Lecture01.pdf
            * Lecture 2: Storing and querying RDF data
                http://users.jyu.fi/~olkhriye/ties4520/lectures/Lecture02.pdf
            * Lecture 3: Ontologies
                http://users.jyu.fi/~olkhriye/ties4520/lectures/Lecture03.pdf
            * Lecture 4: Reasoning
                http://users.jyu.fi/~olkhriye/ties4520/lectures/Lecture04.pdf
            * Lecture 5: Programming with Semantic Web (RDF4J and Jena APIs)
                http://users.jyu.fi/~olkhriye/ties4520/lectures/Lecture05.pdf
                [note, some imprecisions, OntModel support only some part of OWL 1.1
                RDF4J supports also reasoning https://github.com/eclipse/rdf4j-doc]
            * Lecture 6: Data Exchange and Semantic Annotation
                http://users.jyu.fi/~olkhriye/ties4520/lectures/Lecture06.pdf
            * Lecture 7: Linked Data
                http://users.jyu.fi/~olkhriye/ties4520/lectures/Lecture07.pdf

* Why the original idea of Tim Beners-Lee is dead.
    * https://hackernoon.com/semantic-web-is-dead-long-live-the-ai-2a5ea0cf6423
    * https://twobithistory.org/2018/05/27/semantic-web.html
    * https://www.forbes.com/sites/cognitiveworld/2018/08/03/the-importance-of-schema-org/

## Ontologies

* See also
    * https://en.wikipedia.org/wiki/Template:Semantic_Web
    * https://www.w3.org/wiki/Good_Ontologies

* Maybe a good model to make a new Ontology is
    https://www.w3.org/Submission/vcard-rdf/
    It's a [member submission](https://www.w3.org/wiki/Good_Ontologies),
    therefore less than a draft!
    But it's a very simple Ontology
    See an example non official of an Ontology :
    http://content.scottstreit.com/Semantic_Web/Assignments/Resources/Protégé/protegeLab/original_vCard/vCard.owl
    (should be downloaded, or view-source in Firefox).

> The Semantic Web provides a common framework that allows data to be shared and
> reused across application, enterprise, and community boundaries
> (W3C)

* Ontology
     > Ontologies are a formal way to describe taxonomies and classification
     > networks, essentially defining the structure of knowledge for various
     > domains: the nouns representing classes of objects and the verbs
     > representing relations between the objects.
     > https://en.wikipedia.org/wiki/Web_Ontology_Language
     * See also https://en.wikipedia.org/wiki/Web_Ontology_Language#Ontologies
        https://en.wikipedia.org/wiki/Ontology_(information_science)#Components
    * RDF is an Ontology language
        https://en.wikipedia.org/wiki/Web_Ontology_Language#Ontologies

## RDFS/OWL/OWL DL 2 vs Object Oriented programming


* More complete article from W3C
    https://www.w3.org/2001/sw/BestPractices/SE/ODSD/
    See especially the tab under « 3.3 A Comparison of OWL/RDF and
    Object-Oriented Languages »
    ***Very important to read***. Synthetic table!
    https://www.w3.org/2001/sw/BestPractices/SE/ODSD/20060117/#comparison

* https://www.w3.org/TR/rdf-primer/
    * « 5.3 Interpreting RDF Schema Declarations »
        ***Very interesting***, comparing with Java. Explain with text,
        examples, etc.

* https://www.researchgate.net/publication/228577024_OWL_vs_object_oriented_programming
    (thesis)

* Complement not developed on link above. ***As I say below*** in section
    « Individual vs Class », contrary to Java, in OWL DL *version 2* (not 1)
    an instance of a class (e.g. an Individual) could also at the same time a
    `owl:class`.  I have an example in my ontology julioJuGeographicalZone.owl
    with departments.  In Java, a class `owl:Class` « Ain » could not be an
    instance of another Class!  ***I've blocked a lot on this problem***!!!


## RDFS

### Resources, classes, literals

* Read https://www.w3.org/TR/rdf-schema to understand, especially
    1. https://www.w3.org/TR/rdf-schema/#ch_resource
        * Simpler
            > « In RDF, anything with a subject URI is called a resource. »
            http://www.linkeddatatools.com/help/classes
    2. https://www.w3.org/TR/rdf-schema/#ch_class
    3. https://www.w3.org/TR/rdf-schema/#ch_literal

* ***BIG WARNING*** `rdf:resource !== rdfs:Resource`

* Read also https://stackoverflow.com/questions/25737584/subclassof-and-instance-of-rdf-rdfsclass
    about rdfs:Resource and rdfs:Class

### rdfs:subClassOf, rdf:type

* Even if this definition is outdated I like very much because I understand
    https://www.w3.org/TR/1998/WD-rdf-schema-19980409/
    * Note that `rdf:instanceOf -> rdf:type`
        https://bugzilla.mozilla.org/show_bug.cgi?id=90566
    * Citation ***very very interesting***:
    > From 2.3.1 RDF:instanceOf
    > This indicates that a resource is a member of a class, and thus has all the
    > characteristics that are to be expected of a member of that class. It is a
    > relation between a resource and another resource which must be an instance
    > of Class. A resource may be an instance of more than one class.
    >
    > 2.2.2 RDFS:subClassOf
    > This indicates the subset/superset relation between classes. RDFS:subClassOf
    > is transitive. If B is a sub-class of A and X is an instance of B, then X is
    > also an instance of A. Only instances of Class can have this property type
    > and the property value is always an instanceOf Class. A Class may be a
    > subClassOf more than one Class.

* Better definition https://www.w3.org/2001/sw/RDFCore/Schema/20010913/#s2.1
    > As described in the RDF Model and Syntax specification [RDFMS], resources
    > may be instances of one or more classes; this is indicated with the
    > rdf:type property. Classes themselves are often organized in a
    > hierarchical fashion, for example a class Dog might be considered a
    > subclass of Mammal which is a subclass of Animal, meaning that any
    > resource which is of rdf:type Dog is also considered to be of rdf:type
    > Animal. This specification describes a property, rdfs:subClassOf, to
    > denote such relationships between classes.

* Conclusion.
    * If Théo is an instanceOf (type) Human, and Human an subclassOf Primate
        Théo is an instanceOf (type) Human and Primate.
    * But as Human is only instanceOf (type) Species, Théo is no an instanceOf
    (type) Spacies

### rdfs:domain rdfs:range

* rdfs:domain and rdfs:range are constraints linked to rdf:type
    of the class
    `P rdfs:domain <http://example/Human>`
    means that the property P should be placed in a context of
    its subject is of type `<http://example/Human>`.
    * Above, conclusion by me because of
    1. https://www.w3.org/TR/rdf-schema/#ch_domain
    > rdfs:domain is an instance of rdf:Property that is used to state that any
    > resource that has a given property is an instance of one or more classes.
    2. https://www.w3.org/TR/rdf-schema/#ch_range
    > rdfs:range is an instance of rdf:Property that is used to state that the
    > values of a property are instances of one or more classes.

* More concept atout domain and range
 > The RDF Schema type system is similar to the type systems of object-oriented
 > programming languages such as Java. However, RDF differs from many such systems
 > in that instead of defining a class in terms of the properties its instances
 > may have, an RDF schema will define properties in terms of the classes of
 > resource to which they apply. This is the role of the rdfs:domain and
 > rdfs:range constraints described in Section 3. For example, we could define the
 > author property to have a domain of Book and a range of Literal, whereas a
 > classical OO system might typically define a class Book with an attribute
 > called author of type Literal. One benefit of the RDF property-centric approach
 > is that it is very easy for anyone to say anything they want about existing
 > resources, which is one of the architectural principles of the Web
 > [BERNERS-LEE98].
 https://www.w3.org/2001/sw/RDFCore/Schema/20010913/#s2.1

#### Range for datatype property

* Defining DataRange Expression in Protégé for a Data Type Property
  See  https://stackoverflow.com/questions/24531940/defining-datarange-expression-in-protege-for-a-data-type-property]
Use in range: `xsd:double[ >= 0, <= 0 ].`

## OWL2

### OWL2 essential resources


***OWL2 Document Overview***. When we want understand organization of the doc,
    use this one
    https://www.w3.org/TR/owl2-overview/

***You must read the figure 2 at this link:***
https://www.w3.org/TR/2012/REC-owl2-syntax-20121211/#Entities.2C_Literals.2C_and_Anonymous_Individuals

* To search something, use
    https://www.w3.org/TR/2012/REC-owl2-quick-reference-20121211/

* See also
    https://www.w3.org/2007/OWL/wiki/OWL_Working_Group

* Under https://www.w3.org/TR/owl2-overview/#Documentation_Roadmap
    not found something like « OWL Examples in XML Syntax ».
    Under OWL 1 specification, there was such appendix, ***very cool
    to understand the goal of each owl tag***
    https://www.w3.org/TR/owl-xmlsyntax/apd-example.html

### OWL2 described as UML and as turtle

* ***Very interesting to understand OWL2***.
    https://www.ics.forth.gr/isl/ontology/content-MTLO/html/
    * Contains also xsd datatypes described as owl ontology
        See for instance `xsd:gYear`

* ***We could also download the file and open it in Protégé***
    at https://www.w3.org/2002/07/owl#
    Is it described at turtle !!

For me, it's the reference to understand organization of OWL2.
As W3C use a syntax that I don't understand well
(See for instance https://www.w3.org/TR/owl2-new-features/)

### Entities (Protégé, tab « Entities »)

> Each entity in O MUST have an IRI satisfying the restrictions on the usage of
> the reserved vocabulary from Sections 5.1–5.6.
> https://www.w3.org/TR/2012/REC-owl2-syntax-20121211/#Ontologies

### Individual vs Class

* https://stackoverflow.com/questions/37186507/ontology-design-class-or-individuals
    > In OWL classes represent sets. So you can have a class :MicrostrategyManual
    > with members, which are URIs representing concrete files or paper copies,
    > which can be classified as a :MicrostrategyManual.
    > (…)
        > the only (except when going with option 3 below [pruning]) property that can link
        > individual with a class is rdf:type and the one between classes is
        > rdfs:subClassOf.

* Very interesting paper https://pdfs.semanticscholar.org/97bf/4ccca4c44b71c7e9ccf40ce57dce45fb7000.pdf
    * Classes versus Individuals: Fundamental Design Issues for Ontologies on the Biomedical Semantic Web*
    > a molecular biologist might instantiate the class Protein with the individual
    > ‘Serotonin_Receptor’ , which is a clear enough description in many cases. In
    > another context, a neuroscientist might instantiate the class Protein with the
    > individual ‘ Serotonin_Receptor_2A’ . Serotonin_Receptor_2A is in fact a
    > subclass of Serotonin_Receptor, but this information cannot be represented in
    > accordance to OWL DL semantics, since both have been defined as individuals.
    * The solution for this problem is https://www.w3.org/TR/owl2-new-features/#F12:_Punning
        When this paper was published maybe it was not published
        > OWL 1 DL required a strict separation between the names of, e.g.,
        > classes and individuals. OWL 2 DL relaxes this separation somewhat to
        > allow different uses of the same term, e.g., Eagle, to be used for
        > both a class, the class of all Eagles, and an individual, the
        > individual representing the species Eagle belonging to the (meta)class
        > of all plant and animal species. However, OWL 2 DL still imposes
        > certain restrictions: it requires that a name cannot be used for both
        > a class and a datatype and that a name can only be used for one kind
        > of property. The OWL 2 Direct Semantics treats the different uses of
        > the same name as completely separate, as is required in DL reasoners.
        ***PRECEDING PARAGRAPH IS VERY VERY VERY VERY IMPORTANT***

* Very cool explanation https://mailman.stanford.edu/pipermail/protege-owl/2007-February/001427.html
    Official Protégé Mailist

* See an other explanation. Very cool explanations
    about concepts http://www.linkeddatatools.com/help/classes

* Even if in OWL DL
    * ***An IRI I can be used as an individual in O even if I is not declared as an individual in O***
        > An IRI I can be used as an individual in O even if I is not declared as an individual in O
        https://www.w3.org/TR/2012/REC-owl2-syntax-20121211/#Typing_Constraints_of_OWL_2_DL

##### Class axioms vs Property Restriction vs Individual fact

* Axiom classes can't be applied to individuals (the tab « Individual »)
    See also  Protégé seems to say the same thing.

* Same note about « Property restrictions »
    (existencial, universal, value, cardinality)
    As Atencias says
    > In OWL2 we can declare that the instances of a class C must satisfy certain
    > conditions.
    * ***Property restrictions should be inserted in Protégé in the tab
        « Classes »***.

* « Individual facts » (see lecture of Atencias) (property assertions in Protégé)
    applied only to Individuals!

#### owl:NamedIndividual and Protégé tab `Entities -> Individuals`

* Definition
    > The class owl:NamedIndividual is new to OWL 2. It is used for declaring
    > named (in contrast to anonymous) individuals in OWL 2 DL. In OWL 2 Full,
    > named individual axioms simply provide an alternative way to state that a
    > given entity is an individual.
    > (…)
    > owl:NamedIndividual has the same class extension than owl:Thing, i.e. the
    > set of all individuals.
    > https://www.w3.org/2007/OWL/wiki/FullSemanticsNamedIndividuals
    * not that ***`owl:Thing` is the set of all individuals*** !!!!

* Example of a generated Named Individual thanks Protégé in
    `Entities -> Individuals`
    ```
    <owl:NamedIndividual rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Autumn">
        <rdf:type rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Season"/>
        <rdfs:label xml:lang="en">Autumn</rdfs:label>
    </owl:NamedIndividual>
    ```

 * Why when we open ./julioJuGeographicalZone.owl in Protégé
     Departments are Individuals and Classes.
     It's because we have

     ```xml
     <owl:Class rdf:about="http://wikidataJulioJuEntity/entity/Q3083">
        <rdfs:label xml:lang="fr">"Ain"</rdfs:label>
        <wikidataProperty:P2586>01</wikidataProperty:P2586>
        <rdfs:isDefinedBy rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://www.wikidata.org/entity/Q3083</rdfs:isDefinedBy>
        <rdfs:subClassOf rdf:resource="http://julioJuGeographicalZone.owl/geographicalZoneFrance" />
     </owl:Class>

     ```
     The Declaration `<wikidataProperty:P2586>01</wikidataProperty:P2586>`
     means for Protégé « Data property assertions ».
     And assertions on data are only for Individuals, not class!

* https://github.com/esnet/nml-mrml/issues/4
    > In my limited work with OWL about 5 years ago I learned the distinction
    > between class, declarations, and instance data.  OWL uses NamedIndividual as
    > a class definition to identify an named instance of an object.  It allows
    > the reasoning engine to determine the set of all instantiated objects, and
    > from out perspective, allows us to query for all NamedIndividuals in an
    > Ontology to get back all instance definitions within our model.  However, I
    > never used this in any practical application.

    > With all our objects defined as a specific class there was no need to use
    > NamedIndividual to identify an instance data.  When we do add both the
    > specific class and NamedIndividual to an instance we add some confusion to
    > the representation of the instance data.  For example, the following two
    > statements are equivalent:

    ```
    <owl:NamedIndividual rdf:about="urn:ogf:network:stp:uvalight.ets:ps-80">
        <rdf:type rdf:resource="http://schemas.ogf.org/nml/2013/03/base#Port"/>
    </owl:NamedIndividual>

    <nml:Port rdf:about="urn:ogf:network:stp:uvalight.ets:ps-80">
        <rdf:type rdf:resource="http://www.w3.org/2002/07/owl#NamedIndividual" />
    </nml:Port>
    ```
    > I also believe the following RDF statement is equivalent as well:
    ```
    <rdf:Description rdf:about="urn:ogf:network:stp:uvalight.ets:ps-80">
        <rdf:type rdf:resource="http://www.w3.org/2002/07/owl#NamedIndividual" />
        <rdf:type rdf:resource="http://schemas.ogf.org/nml/2013/03/base#Port"/>
    </rdf:Description>
    ```
    > Removing the use of NamedIndividual from the declaration means we only get
    > the <nml:Port> version of the definition.  I think this might provide
    > clarity.  Do you see anything we might break in OWL by removing it?

* See also
    https://stackoverflow.com/questions/37157883/member-of-an-owlclass-versus-owlnamedindividual

### Class expression
> In OWL 2, classes and property expressions are used to construct class
> expressions, sometimes also called descriptions, and, in the description logic
> literature, complex concepts. Class expressions represent sets of individuals by
> formally specifying conditions on the individuals' properties; individuals
> satisfying these conditions are said to be instances of the respective class
> expressions. In the structural specification of OWL 2, class expressions are
> represented by ClassExpression.
>
> A class expression can be used to represent the set of "people that have at
> least one child". If an ontology additionally contains statements that "Peter is
> a person" and that "Peter has child Chris", then Peter can be classified as an
> instance of the mentioned class expression.
>
> OWL 2 provides a rich set of primitives that can be used to construct class
> expressions. In particular, it provides the well known Boolean connectives and,
> or, and not; a restricted form of universal and existential quantification;
> number restrictions; enumeration of individuals; and a special self-restriction.
>
> As shown in Figure 2, classes are the simplest form of class expressions. The
> other, complex, class expressions, are described in the following sections.
> https://www.w3.org/TR/2012/REC-owl2-syntax-20121211/#Class_Expressions

#### Max(min)cardinialyty and existential / universal restrictions

> An existential class expression ObjectSomeValuesFrom( OPE CE ) consists of an
> object property expression OPE and a class expression CE, and it contains all
> those individuals that are connected by OPE to an individual that is an instance
> of CE. Provided that OPE is simple according to the definition in Section 11,
> such a class expression can be seen as a syntactic shortcut for the class
> expression ObjectMinCardinality( 1 OPE CE ).
https://www.w3.org/TR/2012/REC-owl2-syntax-20121211/#Existential_Quantification_2

> A universal class expression ObjectAllValuesFrom( OPE CE ) consists of an object
> property expression OPE and a class expression CE, and it contains all those
> individuals that are connected by OPE only to individuals that are instances of
> CE. Provided that OPE is simple according to the definition in Section 11, such
> a class expression can be seen as a syntactic shortcut for the class expression

https://www.w3.org/TR/2012/REC-owl2-syntax-20121211/#Maximum_Cardinality

### OWL DL

#### Essential resources

https://www.w3.org/TR/owl2-profiles

https://www.w3.org/TR/owl2-syntax/#Global_Restrictions_on_Axioms_in_OWL_2_DL

#### Data restriction

Pellet deduces that an Ontology is inconsistent
(it deduces: `:photo owl:equivalentClass owl:nothing`)
with the following declarations:

```xml
    <owl:DatatypeProperty rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#photoDepictsDatatypeProperty">
        <rdfs:domain rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Photo"/>
        <rdfs:range rdf:resource="http://www.w3.org/2001/XMLSchema#anyURI"/>
        <rdfs:range rdf:resource="http://www.w3.org/2001/XMLSchema#string"/>
    </owl:DatatypeProperty>
    <owl:Class rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Photo">
        <rdfs:subClassOf>
            <owl:Restriction>
                <owl:onProperty rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#photoDepictsDatatypeProperty"/>
                <owl:someValuesFrom rdf:resource="http://www.w3.org/2001/XMLSchema#string"/>
            </owl:Restriction>
        </rdfs:subClassOf>
        <rdfs:subClassOf>
            <owl:Restriction>
                <owl:onProperty rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#photoDepictsDatatypeProperty"/>
                <owl:minQualifiedCardinality rdf:datatype="http://www.w3.org/2001/XMLSchema#nonNegativeInteger">1</owl:minQualifiedCardinality>
                <owl:onDataRange rdf:resource="http://www.w3.org/2001/XMLSchema#string"/>
            </owl:Restriction>
        </rdfs:subClassOf>
        <rdfs:label>Photo</rdfs:label>
    </owl:Class>
```

##### Two (not cool) solutions

1. But if in the range of `:photoDepictsDatatypeProperty`
    we remove `<rdfs:range rdf:resource="http://www.w3.org/2001/XMLSchema#anyURI"/>`
    e.g. we let only
            `<rdfs:range rdf:resource="http://www.w3.org/2001/XMLSchema#string"/>`

    the ontology become consistent
    ```xml
    <owl:DatatypeProperty rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#photoDepictsDatatypeProperty">
        <rdfs:domain rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Photo"/>
        <rdfs:range rdf:resource="http://www.w3.org/2001/XMLSchema#string"/>
    </owl:DatatypeProperty>
    ```

2. If we delete data restriction in class expression, the ontology becomes
    consistent:
```xml
    <owl:DatatypeProperty rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#photoDepictsDatatypeProperty">
        <rdfs:domain rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Photo"/>
        <rdfs:range rdf:resource="http://www.w3.org/2001/XMLSchema#anyURI"/>
        <rdfs:range rdf:resource="http://www.w3.org/2001/XMLSchema#string"/>
    </owl:DatatypeProperty>
    <owl:Class rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Photo">
    </owl:Class>
```

##### The solution: correct Data ranges

See also:
https://www.w3.org/TR/2012/REC-owl2-quick-reference-20121211/#Data_Ranges

* Or if we create a new Datatype that is the union of `xsd:anyURI` `xsd:string`
    like following
    ```xml
        <rdfs:range>
            <rdfs:Datatype>
                <owl:unionOf rdf:parseType="Collection">
                    <rdf:Description rdf:about="http://www.w3.org/2001/XMLSchema#anyURI"/>
                    <rdf:Description rdf:about="http://www.w3.org/2001/XMLSchema#string"/>
                </owl:unionOf>
            </rdfs:Datatype>
        </rdfs:range>
    ```

#### OWL DL and composites property

C'est totalement logique qu'une propriété transitive n'est pas fonctionnelle. En
effet si on a `:e1 :p :e2` et `e2 :p :e3` on peut en déduire `:e1 :p e2, e3`.
C'est logique, même si les messages d'erreur n'étaient pas clair.

Au lien
http://protege-project.136.n4.nabble.com/Property-chains-and-irreflexive-properties-td4549882.html
il disent « The reason why the reasoner "crashes" is that the first axiom makes
the property hasSibling composite, and then it is not allowed to be used in an
Irreflexive(...) statement because of the global restrictions of OWL 2, which
ensure that reasoning is decidable »

Selon ce même lien, il semblerait que le raisonneur déduise qu'une propriété
définie par une chaîne de propriété est forcément réfléxive. Cela pause des
problèmes dans mon cas d'usage, car je cela n'aurait pas de sens.

Ils disent que pour résoudre ce type de problème, il faut utiliser « SWRL: A
Semantic Web Rule Language Combining OWL and RuleML ». D'après eux, ce langage
est correctement interprété par Pellet et HermiT.


> (…)
> An object property expression OPE is composite in the set of axioms Ax if
>
>     OPE is equal to owl:topObjectProperty or owl:bottomObjectProperty, or
>     Ax contains an axiom of the form
>         SubObjectPropertyOf( ObjectPropertyChain( OPE1 ... OPEn ) OPE ) with n > 1, or
>         SubObjectPropertyOf( ObjectPropertyChain( OPE1 ... OPEn ) INV(OPE) ) with n > 1, or
>         TransitiveObjectProperty( OPE ), or
>         TransitiveObjectProperty( INV(OPE) ).
>
> (…)
> An object property expression OPE is simple in Ax if, for each object property
> expression OPE' such that OPE' →* OPE holds, OPE' is not composite.
> (…)
> Restriction on Simple Roles. Each class expression and each axiom in Ax of
> type from the following two lists contains only simple object properties.
>
>     ObjectMinCardinality, ObjectMaxCardinality, ObjectExactCardinality, and ObjectHasSelf .
>     FunctionalObjectProperty, InverseFunctionalObjectProperty, IrreflexiveObjectProperty, AsymmetricObjectProperty, and DisjointObjectProperties.
>
> This restriction is necessary in order to guarantee decidability of the basic
> reasoning problems for OWL 2 DL [Description Logics].
> (…)
> https://www.w3.org/TR/owl2-syntax/#Global_Restrictions_on_Axioms_in_OWL_2_DL

## Protégé OWL 2 syntax

* Currently release version of Protégé is compatible with Java 8 and not Java 11
    See https://github.com/protegeproject/protege/issues/822
    Under /usr/bin/protege
    add:
    ```sh
    PATH="/usr/lib/jvm/java-8-openjdk/bin:$PATH"
    ```

* `rdfs:comment` become JavaDoc into the generated file
    ./scholarProjectWebSemantic/target/generated-sources/java/fr/uga/miashs/sempic/model/rdf/SempicOnto.java/

* To have `rdfs:label` in lang `fr` and not only lang `en`
    displayed in the pan `Entities`.
    Under its pan `Entity`, the left-pan `Classes`
    * `Menu View -> Custom Rendering` add « fr »

* Files catalog-v001.xml are generated by Protégé.

### Class axioms in Protégé

* ***See how to assert Property Restriction in Protégé***
    Use https://protegeproject.github.io/protege/class-expression-syntax/

#### owl:oneOf

In Protégé, under pan « Description: Season », we have
`{Autumn, Spring, Summer, Winter}.`
It means
`:Season owl:oneOf (:Autumn :Spring :Summer :Winter)`

In Protégé, under pan « Description: Administrator UserGroup », we have
`{admin_created_in_protege1, admin_created_in_protege2}.`
It means
`:UserGroupAdmin owl:oneOf (:admin_created_in_protege1 :admin_created_in_protege2)`

When we read the owl file, we see that it append tags `owl:equivalentClass` even
if we write manually this axiom in the file without.

#### owl:AllDifferent (individuals) owl:AllDisjointClasses (classes) owl:disjointWith (individuals and classes)

> new features are syntactic sugar (e.g., disjoint union of classes)
> https://www.w3.org/TR/owl2-overview/#Relationship_to_OWL_1

To edit `owl:AllDifferent`, in the
`Entities -> Individuals -> Description: TheIndividual` pan, use the button
« Different Individuals ».

When you create a new Individuals, don't create a new class, but append to
an existying anonymous class of type `owl:AllDiffent`.

Could also be append in the owl file. At the end of the file,
under the section « ***General axioms*** » you could see the collection.

Same remark for `owl:AllDisjointClasses` (use the button « Disjoint With »
in the pan `Entities -> Classes -> Description: TheClass`).

`owl:disjointWith` in the same way, but if the list contains
two and not more Classes or Individuals. Exemple for

```xml
    <owl:Class rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#AdministratorsCreatedByTheApp">
        <!-- declarations omited -->
        <owl:disjointWith rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#AdministratorsCreatedInProtege"/>
    </owl:Class>
```

#### owl:propertyChainAxiom

```xml
    <owl:ObjectProperty rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#photoSharedWith">
        <rdfs:label xml:lang="en">photo shared with</rdfs:label>
        <owl:propertyChainAxiom rdf:parseType="Collection">
            <rdf:Description rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#photoInAlbum"/>
            <rdf:Description rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumSharedWith"/>
        </owl:propertyChainAxiom>
    </owl:ObjectProperty>
```

In Protégé gives
in the pan `Entities -> Classes -> Description: TheClass`).
under the button `SubProperty of (Chain)`:
`'photo in album' o 'album shared with'`

#### owl:unionOf
***See*** https://www.w3.org/TR/2012/REC-owl2-primer-20121211/#Complex_Classes

```xml
    <owl:Class rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#TableAndChair">
        <rdfs:subClassOf rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#DepictionWhatObject"/>
        <owl:unionOf rdf:parseType="Collection">
            <rdf:Description rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Table"/>
            <rdf:Description rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Chair"/>
        </owl:unionOf>
    </owl:Class>
```

Give in Protégé
in the pan `Entities -> Classes -> Descrpition: TableAndChair`,
under the button « EquivalentTo »:
`Chair or Table`

The individual `CampingTableWithChair` is an instance of `Chair` and `Table`,
therefore can't be a `owl:disjointUnionOf`.

#### owl:intersectionOf

***See*** https://www.w3.org/TR/2012/REC-owl2-primer-20121211/#Complex_Classes

***See:*** https://www.w3.org/TR/owl-xmlsyntax/apd-example.html#subapd-eg41

```xml
    <owl:Class rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#FurnitureWithTableAndChair">
        <owl:equivalentClass>
            <owl:Class>
                <owl:intersectionOf rdf:parseType="Collection">
                    <rdf:Description rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Chair"/>
                    <rdf:Description rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Table"/>
                </owl:intersectionOf>
            </owl:Class>
        </owl:equivalentClass>
        <rdfs:subClassOf rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#TableAndChair"/>
    </owl:Class>
```

Give in Protégé
in the pan `Entities -> Classes -> Descrpition: FurnitureWithTableAndChair`,
under the button « EquivalentTo »:
`Chair and Table`

#### owl:hasValue

##### With a datatype (OWL full)

```xml
    <owl:Class rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Photo">
        <rdfs:subClassOf>
            <owl:Restriction>
                <owl:onProperty rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#isAPhoto"/>
                <owl:hasValue>true</owl:hasValue>
            </owl:Restriction>
        </rdfs:subClassOf>
    </owl:Class>
```
Give in Protégé
in the pan `Entities -> Classes -> Descrpition: Photo`,
under the button `Subclass of`:

`'is a photo' value "true"`

In « Class expression editor »

the value `"true"` is underlined in red, and an error message appears
`Encountered « true » at line 1 column 20. Expected one of: individual name`

Pellet and HermiT give the same type of error. In fact, in OWL DL
and object property could not be a datatype property.

Even if in OWL Full `owl:hasValue` could have a datatype property.
See https://www.w3.org/TR/2012/REC-owl2-syntax-20121211/#Literal_Value_Restriction
and https://www.w3.org/TR/2012/REC-owl2-quick-reference-20121211/#Class_Expressions

##### With a object (OWL DL)

```xml
        <rdfs:subClassOf>
            <owl:Restriction>
                <owl:onProperty rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#isAPhoto"/>
                <owl:hasValue rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#booleanTrue" />
            </owl:Restriction>
        </rdfs:subClassOf>
```
Give in Protégé
in the pan `Entities -> Classes -> Descrpition: Photo`,
under the button `Subclass of`:

`'is a photo' value booleanTrue`

#### owl:equivalentClass for Datatype (Datatype definitions)

> A datatype definition DatatypeDefinition( DT DR ) defines a new datatype DT as
> being semantically equivalent to the data range DR; the latter MUST be a unary
> data range.
> https://www.w3.org/TR/2012/REC-owl2-syntax-20121211/#Datatype_Definitions

Therefore the following can't work

```
    <rdf:Description rdf:about="http://www.w3.org/2001/XMLSchema#boolean">
        <owl:equivalentClass rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#false"/>
            <owl:Class>
                <owl:unionOf rdf:parseType="Collection">
                    <rdf:Description rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Chair"/>
                    <rdf:Description rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#FurnitureWithTableAndChair"/>
                    <rdf:Description rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Table"/>
                </owl:unionOf>
            </owl:Class>
    </rdf:Description>
```

But following could work:

```
    <rdf:Description rdf:about="http://www.w3.org/2001/XMLSchema#boolean">
        <owl:equivalentClass rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#false"/>
    </rdf:Description>
```

It's logical, because a datatype allow simply a range of string !
Not several class!

### owl:sameAs (for individuals)

> Export inferred axioms cannot handle same individuals axioms
> https://github.com/protegeproject/protege/issues/3

That's why when we open Protégé, under the tab `Individuals by class`
in the pan `Instances:`, the entity `:Hiver` is reported as `For: Nothing selected`.
That's why also in the pan `Class hierarchy`, when we select `owl: Thing`, under
the tab `Instances` we show also the entity `:Hiver`.

When we start `Pellet`, we see that hiver has inferred type `Hiver`.

### Property Range Restriction

#### Existential and universal restrictions in tab « Object Property »

EXISTENTIAL RESTRICTION COULD WORK ONLY FOR OBJECT PROPERTY, NO DATATYPE PROPERTY
An object property could have the same range of a datatype property
(tested in Protégé)
Under the tab « object property », click on « Ranges »

* As it we could have the following. If I understand well,
    the range are all individuals, such as
    ```
    ?domain :photoTakenDateObjectProperty ?individual .
    ?individual a :Season , xmln:date .
    ```

    * We deduce than ?individual is an individual of a *Class expression*
        who is the intersection of the Class `:Season` and `xlmn:date`
        *See also the section above about « Class expression ».

    * or as generated by Protégé:

    ```xml
    <owl:ObjectProperty rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#photoTakenDateObjectProperty">
        <rdf:type rdf:resource="http://www.w3.org/2002/07/owl#FunctionalProperty"/>
        <rdfs:range>
            <owl:Restriction>
                <owl:onProperty rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#photoTakenDateObjectProperty"/>
                <owl:allValuesFrom rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Season"/>
            </owl:Restriction>
        </rdfs:range>
        <rdfs:range>
            <owl:Restriction>
                <owl:onProperty rdf:resource="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#photoTakenYear"/>
                <owl:allValuesFrom rdf:resource="http://www.w3.org/2001/XMLSchema#date"/>
            </owl:Restriction>
        </rdfs:range>
        <rdfs:label xml:lang="en">photo taken at date</rdfs:label>
    </owl:ObjectProperty>
    ```

    * To have an other example of `owl:Restriction` to define a class
        see also https://www.w3.org/TR/owl-xmlsyntax/apd-example.html#subapd-eg41

#### Range restriction in table « Databype Property »

In Protégé, Range restriction generate the following xml text

```xml
    <owl:DatatypeProperty rdf:about="http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#photoTakenAtExactDate">
        <!-- properties not copied here -->
        <rdfs:range>
            <rdfs:Datatype>
                <owl:onDatatype rdf:resource="http://www.w3.org/2001/XMLSchema#date"/>
                <owl:withRestrictions rdf:parseType="Collection">
                    <rdf:Description>
                        <xsd:minInclusive rdf:datatype="http://www.w3.org/2001/XMLSchema#date">1826-01-01</xsd:minInclusive>
                    </rdf:Description>
                </owl:withRestrictions>
            </rdfs:Datatype>
        </rdfs:range>
        <!-- properties not copied here -->
    </owl:DatatypeProperty>
```

## Protégé, generalities

### Install Protégé

See section with the same name above


### Regressions on Protégé 5.5

« Inconsistent ontology makes the UI unresponsive »
https://github.com/protegeproject/protege/issues/877
Fixed in Protégé 5.6

See my issue
> Equivalent sign between classes and object properties not displayed in Protege 5.5 (regression)
> https://github.com/protegeproject/protege/issues/910

### Reset preferences UI issues

« Reset preferences does not update the preferences window »
https://github.com/protegeproject/protege/issues/168

### Java Preferences API

They use Java Preferences API
See https://stackoverflow.com/questions/1320709/preference-api-storage/34019194#34019194

Under Unix, it is saved in the global file (shared with all Java App)
~/.java/.userPrefs/

On Windows, saved in registry.

### Others preferences

Some preferences are also saved under
~/.Protege/

***This folder should be manually created otherwise an error
is displayed when we start Protégé in Console, or in the log files***

### Log file

Saved in ~/.Protege folder. If this folder is not created, saved under
~/.protege

File very interesting.

Log could be should under the menu `Window -> Show log`.

### Query

We could do some query directly in Protégé

Check the menu `Window -> tabs`.

## OWL 2 reasoners

### Generalities about reasoners and profiles

See https://www.w3.org/TR/owl2-profiles

From M. David:

> OWL (et OWL 2) est un langage basé sur les logiques de description qui
> sont elles même un ensemble de langages.
> Tu peux écrire une ontologie valide mais qui n'est pas décidable (i.e.
> en OWL Full).

> la plus base expressivité est OWL EL puis OWL DL puis OWL Full (non
> decidable, i.e. il n'existe pas de raisonneurs qui gèrent toute
> l'expressivité de l'ontologie)

> Plus un raisonneur gère des ontologies expressives, plus il va être
> potentiellement lent.

* See list of all reasoners at https://www.w3.org/2001/sw/wiki/OWL/Implementations

* A list probably more updated of reasoners. Very well commented.
    By owlapi doc.
    https://github.com/owlcs/owlapi/wiki

* To understand the interest of each reasoner see also what are OWL profiles
    https://www.w3.org/TR/owl2-profiles
    It seems that only OWL 2 DL are interesting.

### Pellet (Openllet) and HermiT, the solutions

* Could be used in command line.

#### OWL 2 DL compatibility

> « OWL 2 reasoner »
> https://github.com/stardog-union/pellet

> Since version 1.1, HermiT can handle DL Safe rules and the rules can directly be
> added to the input ontology in functional style or other OWL syntaxes supported
> by the OWL API (see
> [A Syntax for Rules in OWL 2](http://webont.org/owled/2009/papers/owled2009_submission_16.pdf).
> Note that reasoning with DL
> Safe rules is incomplete if the ontology contains property chains or
> transitivity axioms and complex properties are used in the rule bodies.
> http://www.hermit-reasoner.com/

#### HermiT limitations

##### OWL 2 DL limitations?

> Note that reasoning with DL
> Safe rules is incomplete if the ontology contains property chains or
> transitivity axioms and complex properties are used in the rule bodies.
> http://www.hermit-reasoner.com/

HermiT knows compliant OWL 2 DL transitivity and property chains, like
Pellet. Not tested `Property hierarchy`.

See also section above about `OWL DL and composites property`.

##### Datatypes

Contrary to Pellet, when we try to use datatypes not descripted in OWL 2
specifications, we have the error:

> org.semanticweb.HermiT.datatypes.UnsupportedDatatypeException: HermiT supports
> all and only the datatypes of the OWL 2 datatype map, see
> http://www.w3.org/TR/owl2-syntax/#Datatype_Maps.  The datatype
> 'http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#gYearSeason' is not
> part of the OWL 2 datatype map and no custom datatype definition is given;
> therefore, HermiT cannot handle this datatype.

See https://stackoverflow.com/questions/24540401/owl-2-reasoners-and-custom-datatypes-not-working

##### Justifications

~~Je crois que l'on peut en conclure qu'HermiiT met n'importe quoi quand il est
confronté à des erreurs. Quand je vous avais envoyé mon ontologie, vous me
disiez qu'il se plaignait des problèmes avec les propriétés déclarées comme
asymnétriques, ou irréfléxive. Hors, c'est le fait d'enlever les chaînes de
propriétés qui résoud le problème, non de supprimer les déclarations
assymétriques et irréfléxives.

Moi de mon côté j'avais vu l'erreur suivante « An error occurred during
reasoning: Non-simple property ' inverse (isSameUserAs)' or its inverse appears
in the cardinality restriction ' inverse (isSameUserAs) max 1 Thing'.. ». Or
isSameUserAs n'a pas de propriété inverse, et il n'apparaît pas dans une «
cardinality restriction ». De plus c'est le fait d'enlever le fait que
`isSameUserAs` est transitif qui permet de se débarrasser de cette erreur. Donc
le message d'erreur est erroné.~~
[Don't forget that a property functional is a cardinal property]

~~Je note sommes tout que Pellet donne de meilleurs indications que HermiT sur
l'origine des erreurs.~~

~~Cela va dans le sens de ce qu'à écrit un professeur des université dans
l'article
https://pdfs.semanticscholar.org/9091/e269a2cf7a44b46681b3de3ca489a36ad243.pdf

Dans cet article, à la case « Justiications  [feature of ontology reasoners
provide explanations for inconsistency that exist in the ontologies ] », pour
HermiT il mettent « no ». C'est compréhensible, si HermiT donne de mauvaises
justifications, c'est la même chose que s'ils n'en donnait pas ! Pourtant ce
papier date de 2012, et la version que nous utilisons d'HermiT date de 2013
(voir http://www.hermit-reasoner.com/download/current/ )~~
[Article outdated, HermiT show some justifications]

##### Scalability

> The project I'm working on deals with loading OWL files from the Financial
> Industry Business Ontology (FIBO) that contain numerous imports with many
> individuals and classes -- what I noticed was that Pellet performs quite well at
> doing an initial reasoning with generating the inferred hierarchy in contrast to
> HermiT (which hangs/takes too long) and FaCT++ (won't work due to custom
> datatypes in FIBO).
> https://github.com/bdionne/pellet/issues/47

##### Compatibility with Jena API

* HermiT is no compatible with Jena API. Pellet (Openllet)
    is the only one compatible with Jena API.
    (see section « THE SOLUTION, use another reasoner » and « Question about
    Owlapi and Jena API with Openllet »)

### ELK

* Elk ignores lot of Axioms. See the log file to see the warnings about
axioms ignored.

* It's an OWL EL reasoner.

* What is OWL EL
    > For example, OWL 2 EL provides class constructors that are sufficient to
    > express the very large biomedical ontology SNOMED CT
    > https://www.w3.org/TR/owl2-profiles/#OWL_2_EL

* Note: ELK doesn't check consistence of individuals and infers no lot of things
    Pellet and Hermit are better.

### FaCT++

Does not support custom datatypes

See https://stackoverflow.com/questions/24540401/owl-2-reasoners-and-custom-datatypes-not-working

J'ai essayé d'utiliser Fact++, mais il me force à supprimer les datatypes. De
plus, même si je supprime les datatypes, la transitivité, les chaînes de
propriété, quand je le lance il fait exploser Portégé sans messages d'erreurs
(core dumped).

De plus, Fact++ était hébergés sur le site Google Code, mais ce site a
fermé  (Google a fermé le site web Google Code).

Leur documentation est outdated. Elle renvoie à des spécifications de OWL 1
http://owl.man.ac.uk/factplusplus/ , or si on en croit els références citées
plus haut FaCT++ est owl 2 compatible.

Probably not compatible with Protégé 5.2+

### Ontop 1.18.0

***Not very useful as it is only a EL profil***

> Ontop is a platform to query relational databases as Virtual RDF Graphs using
> SPARQL. It's fast and is packed with features.
https://ontop.inf.unibz.it/

On Protégé 5.5 we have a menu `Ontop -> Check for inconsistancies`.

* OWL 2 Reasoners and custom datatypes not working
    https://stackoverflow.com/questions/24540401/owl-2-reasoners-and-custom-datatypes-not-working
    * But I have several custom datatypes!
    Actually When I start Ontop I have the error

* OWL 2 QL reasoner
    Therefore probably not very interesting, like ELK (see above).

* Maybe try to configure with thanks the outdated help
    https://github.com/ontop/ontop/wiki/Ontop-Preferences
    (now there is a menu in `File -> Preferences -> Ontop Reasoner`)
    * See also https://stackoverflow.com/questions/46297683/protege-ontop-reasoner-initialization-error
        * Note, as we could see at
            https://github.com/ontop/ontop/wiki/Ontop-Preferences/d619f9c34da99bef5fbaa6bb5bc8eb9f171a6187
            I've corrected the Markdown syntax a title. Not done by nobody,
            not edited since 2 years.

    * To learn how to use Protégé we could see
        https://github.com/ontop/ontop/wiki/Easy-Tutorial:-Using-Ontop-from-Protege
    * Information about ontopOBDAModel
        https://github.com/ontop/ontop/wiki/ontopOBDAModel
    * TODO investigate issues


### Reasoners in Protégé

***You must see Protégé logs at ~/.Protege/logs/protege.log !***
***When you start Protégé in Command line you have also infos of Reasoners***

*** Do not forget to check
    all the menu `Reasoner -> Configure -> Displayed inference`***

* Some Protégé reasoners could be downloaded at
    https://protegewiki.stanford.edu/wiki/Protege_Plugin_Library
* See also the article about reasoners in Protégé (a little bit old)
    https://protegewiki.stanford.edu/wiki/Using_Reasoners
* Not that HermiT is currently tagged to work with Protégé 4, but works with
    Protégé 5.5.
    Probably the case for others reasoners.

* See somme interesting post on Forums
    * http://protege-project.136.n4.nabble.com/Protege-4-3-How-to-check-the-consistency-td4660711.html
    * http://protege-project.136.n4.nabble.com/Reasoner-caused-owl-nothing-td4667552.html


## Owlapi and reasoners

owlapi uses rdf4j as dependency
    1. https://github.com/owlcs/owlapi/pull/551/commits/1dd4c38364483cf319e9d9f3784f432b22e459d4
    2. https://github.com/owlcs/owlapi/issues/539

Openllet is dependent of RDF4J, even if we use only Jena API

Protégé uses owlapi 4.5 under the hood.
See https://github.com/bdionne/pellet/issues/47 et https://github.com/protegeproject/protege/pull/633

Reasoners uses owlapi (Pellet, Openllet, Hermit, ELK, etc.)
See https://github.com/owlcs/owlapi/wiki

Actually the last version of owlapi is 5.x, no compatible with 4.x.

* No need to use directly owlapi:
    * ***Comparaison about owlapi, Jena API, Protege API***
https://stackoverflow.com/questions/17567771/owl-api-jena-api-protege-api-which-one-to-use
    * But we could use owlapi without a reasoner
        See https://github.com/phillord/owl-api/blob/master/contract/src/test/java/org/coode/owlapi/examples/Examples.java

### Question about Owlapi and Jena API with Openllet

Openllet is the only cool reasoner compatible with Jena API (see section
« THE SOLUTION, use another reasoner »).

I don't know if Jena API uses owlapi under the hoods. Feeling not.

When we use Openllet with Jena API, I don't know if there are some limitations
In fact, Jena API is compatible with a subset of OWL version 1

See also the section « Jena and OWL2 and OntModel » and all the doc
about Openllet in this current document.

## Linked Data (2006) / Linked Open Data (2010)

* What is Linked Data
    https://en.wikipedia.org/wiki/Linked_data
    Datasets are: DBpedia, Wikidata, GeoNames, etc.
    * But they no explain the system of five starts by Tim Berners Lee
        done in 2010
        The the course of Atencias or
        * http://users.jyu.fi/~olkhriye/IHME/IHME_Course-SemanticWeb_LinkedData.pdf
            > In 2006, Tim Berners-Lee set out four simple principles
            > for publishing data on the web.  (http://linkeddata.org)
            > (…)
            > In 2010, Tim Berners-Lee suggested a 5 star deployment scheme for
            > Open Data to encourage people (especially government data owners)
            > to improve linked data.  Linked Open Data (LOD) is Linked Data
            > which is released under an open license, which does not impede its
            > reuse for free.

* In Wikipedia (I was a Wikipedia Contributor),
    they use https://www.mediawiki.org/wiki/Extension:LinkedWiki
    to trigger SPARQL queries

* Very cool Thesis in French about Linked Data
    https://tel.archives-ouvertes.fr/tel-02003672/document (1 feb 2019).

### All sparql endpoints

* Check https://www.w3.org/wiki/SparqlEndpoints

### data.gouv.fr sparql


* How to use sparql on data.gouv.fr https://www.data.gouv.fr/fr/datasets/metadonnees-des-jeux-de-donnees-publies-sur-data-gouv-fr-rdf-web-semantique/
    * This page seems very old as they advise OpenRefine and its RDF plugin
        that is not maintened since lot of time.
    * The online sparql engine does not respond today
        http://www.data.maudry.com:3030/datagouvfr/sparql
    * Source code of the project https://github.com/ColinMaudry/datagouvfr-rdf
        * At this page (not updated since 3 day ago) he
        advises the tool tarql to ocnvert csv to RDF
        https://github.com/ColinMaudry/datagouvfr-rdf
    * The author is https://colin.maudry.com/

### LinkedGeoData.org

* For Geo there is LinkedGeoData.org, but the last update of the
    Database (nt format) is in 2015.

* There are lot of papers
    in web about aggregate LinkedGeoData and GeoNames.

### GeoNames

* For GeoNames, download RDF at
    http://www.geonames.org/export/geonames-search.html
    Warning, about 2Go. Not found how to download part of the earth
    (i.e. only France)
    * I've tested to query on GeoNames but doesn't seem work
    * API info at:
        http://www.geonames.org/export/geonames-search.html
    * I've tested lot of API without success. Especially,
        `name*` seams totally broken.
    * http://www.geonames.org/advanced-search.html?name_equals=Grenoble&country=FR
        returns lot of results, but no Grenoble
    * Only http://www.geonames.org/advanced-search.html?q=%22Grenoble%22&country=FR&featureClass=P&continentCode=EU&fuzzy=0.6
        returns interesting solutions, but also out of scope
        solutions.
    * To query on GeoNames they say
        > download the database dump and construct the url for the features using the pattern "http://sws.geonames.org/geonameId/"
        > https://stackoverflow.com/questions/19393908/retrieving-data-from-geonames-using-sparql
    * Tried to download it, but the file was too big! Furthermore
        this one is not OWL DL compliant
        https://stackoverflow.com/questions/21135179/owlreasonerruntimeexception-in-protégé-using-geonames-ontology

* For GeoNames, with a very cool web page, with a map and all.
    * An example at:
        http://www.geonames.org/2985244/provence-alpes-cote-d-azur.html
        With its corresponding rdf page
        http://sws.geonames.org/2985244/about.rdf

* Seams to be up to date.

* Use http://factforge.net/sparql to retrieve datas.

### DBpedia

* To query on DBpedia read https://wiki.dbpedia.org/OnlineAccess

* There is also DBpedia lookup
    1. https://wiki.dbpedia.org/lookup
    2. https://github.com/dbpedia/lookup
    > The DBpedia Lookup Service can be used to look up DBpedia URIs by related keywords.

* Found examples at https://stackoverflow.com/questions/44384005/get-all-cities-of-country-from-dbpedia-using-sparql
    For https://dbpedia.org/sparql (online service)

* To have a cool REST api,

* To search a city that match a pattern in a Country
    probably use http://mappings.dbpedia.org/server/ontology/classes/City
    (we could see that the doc is with a very old design)

* For REST API you could check
    https://wiki.dbpedia.org/rest-api
    > The BETA release of the DBpedia REST API tries to hide the complexity of
    > SPARQL and (partially) RDF in order to allow web developers querying of data
    > from a SPARQL endpoint.

#### REST API

1. Use http://dbpedia.org/sparql (change options as you want)

2. Write your SPARQL query like:

    ```
    select distinct ?subject where {
        ?subject <http://dbpedia.org/ontology/inseeCode> "38185" .
    }
    ```

    ```
    select distinct ?subject where {
        ?subject rdfs:label "Grenoble"@en .
    }
    ```

3. Click « Run Query »

4. Copy the URL of the result page

#### Why DBpedia is not so cool

* Outdated
    * DBpedia is completly outdated. Based on very old Wikipedia articles.
        For exemple, on http://fr.dbpedia.org/page/Bordeaux
        or http://dbpedia.org/page/Bordeaux
        they say that the mayor is still Alain Juppé!
    * On http://fr.dbpedia.org/resource/Grenoble
        We have:
        1. <http://fr.dbpedia.org/resource/Grenoble> <http://dbpedia.org/ontology/wikiPageRevisionID> 111084543^^xsd:integer
            ==> https://fr.wikipedia.org/w/index.php?title=Grenoble&oldid=111084543 give date of 2015 !!!
        2. <http://dbpedia.org/resource/Grenoble> <http://dbpedia.org/ontology/wikiPageRevisionID> 744326541^^xsd:integer
            ==> https://en.wikipedia.org/w/index.php?title=Grenoble&oldid=744326541 give date of 2016 !!!
        * ==> Therefore for
    * Therefore for datas that need to be very up to date (like communes)
        this tool is not very good.

* Very less accurate
    * On DBpedia fr or en
    * <http://fr.dbpedia.org/resource/Grenoble> rdf:type ?object .
    * ==> We have not something like « Commune of France » (very less good than WikiData).
    * There is interesting ` dcterms:subject:` but based on Catégorie of the
        corresponding Wikipedia page, not cool


* But DBpedia is not dead https://blog.dbpedia.org/
    Actually project about Agriculture.

### WikiData

* Made with the support of Google…
    All is open source !!!

* We could work on a Dump, in RDF if we want
    https://www.wikidata.org/wiki/Wikidata:Database_download

* Hot to obtaining data from Wikidata:
    https://www.wikidata.org/wiki/Wikidata:Data_access

* SPARQL tutorial https://www.wikidata.org/wiki/Wikidata:SPARQL_tutorial
    ***It is very very interesting***

*  https://query.wikidata.org/
    * User manual at https://www.mediawiki.org/wiki/Wikidata_query_service/User_Manual
        ***Very very very interesting***
    * Examples
        could be found in the page in a modal window
        under https://query.wikidata.org (click to the link Exemples) (so cooool)
        or at https://www.wikidata.org/wiki/Wikidata:SPARQL_query_service/queries/examples
    ***Test it, is so cool and so fancy,
    result of SPARQL is displayed in a map, so cool***
    * Under https://query.wikidat.org we could export the query into HTTP GET
        method (LINK -> SPARQL endpoint). As it we could have the result in
        JSON form in our program.

* Furthermore I was a contributor of Wikipedia, and WikiData is the *legal* child
    of Wikipedia therefore I prefer WikiData than DBpedia.
    Data of Wikidata are used in Wikipedia and could be improved by
    Wikipedia contributors. DBpedia aggregate data from Wikipedia and others data.

* This article is so cool
    to trigger SPARQL search on WikiData
    https://blog.ash.bzh/fr/a-la-recherche-des-communes-francaises-sur-wikidata/
    > la date de fin n’est manifestement pas correctement indiquée de manière
    > systématique. Il semble également y avoir quelques îles marquées comme
    > commune alors qu’un élément séparé existe pour celle-ci.
    * Don't know if this problem is corrected actually.
    * Problem in its request, does not use `(wdt:P279*)` (subclass of « commune
        of France »)
    * See my solution below
    * TODO contact it

* All tools to manipulate WikiData
    https://www.wikidata.org/wiki/Wikidata:Tools/For_programmers

* Very interesting paper
    https://iccl.inf.tu-dresden.de/w/images/5/5a/Malyshev-et-al-Wikidata-SPARQL-ISWC-2018.pdf

    > What distinguishes Wikidata from RDF, however, is that many components of
    > the knowledge graph carry more information than plain RDF would allow a
    > single property or value to carry.
    > (p. 2)
    > – Data values are often compound objects that do not correspond to literals
    > of any standard RDF type. For example, the value 100 km/h has a numeric
    > value of 100 and a unit of measurement km/h (Q180154).
    > [check for instance how « Rio de Janeiro light rail » (Q10388586))
    > speed limit is defined (click on "edit") ]
    > – Statements (i.e., subject-property-object connections) may themselves be
    > the subject of auxiliary property-value pairs, called qualifiers in
    > Wikidata. For example, the Wikidata statement for a speed limit of Germany
    > is actually as shown in Fig. 1, where the additional qualifier valid in
    > place (P3005) clarifies the context of this limit ( paved road outside of
    > settlements , Q23011975). Nesting of annotations is not possible, i.e.,
    > qualifiers cannot be qualified further.
    > (p.3)
    > [***See my example below***]

    > April 2018, theDBpedia Live endpoint reports 618,279,889 triples across
    > all graphs (less than 13% of the size of Wikidata in RDF).
    > (p 14)

    > Most of the Wikidata community, including developers, had no prior contact
    > with SPARQL. An impressive amount of SPARQL-literacy has developedvery
    > quickly. There is extensive documentation and support now, including a
    > community project Request a Query where experts design queries for novices
    > (…)
    > Slow take-up among semantic web researchers We received surprisingly
    > little input from this community so far.
    > (page 15)

* This Paper (payed) seems to be interesting
    « Wikidata and DBpedia: A Comparative Study »
    https://link.springer.com/chapter/10.1007/978-3-319-74497-1_14
    2017
    > (…) In this paper, a comparison of these two widely-used structured data
    > sources is presented.
    > This comparison considers the most relevant data quality dimensions in the
    > state of the art of the scientific research.
    > As fundamental differences between both projects, we can highlight that
    > Wikidata has an open centralised nature, whereas DBpedia is more popular
    > in the Semantic Web and the Linked Open Data communities and depends on
    > the different linguistic editions of Wikipedia.

* ***The French Government give data from Wikidata***
    See https://www.data.gouv.fr/fr/datasets/subdivisions-administratives-francaises-issues-de-wikidata/
    * This table is outdated.
    * Checked with https://fr.wikipedia.org/wiki/Liste_des_communes_nouvelles_créées_en_2016,
        seems to be correct
    * TODO contact the producer to ask to update is datas and share
        the URL to create its data.

* In Wikipedia page, to see the corresponding Wikidata article
    check the link `Wikidata item` on the left pan of the page.

##### Some experimentations WikiData Sparql

* The very useful doc is ***https://en.wikibooks.org/wiki/SPARQL***

* Qualifiers: explanations
    1. explanations on Wikibook https://en.wikibooks.org/wiki/SPARQL/WIKIDATA_Qualifiers,_References_and_Ranks
    2. definitions: https://www.wikidata.org/wiki/Help:Qualifiers
    3. prefix explanation https://en.wikibooks.org/wiki/SPARQL/Prefixes
    4. Official example
        1. https://www.wikidata.org/wiki/Wikidata:SPARQL_query_service/queries#US_presidents_and_their_spouses,_in_date_order
        2.  https://www.wikidata.org/wiki/Wikidata:SPARQL_query_service/queries#Working_with_qualifiers
    My example for La Réunion. This department has had several names
    Display it with their dates.
    ```
    SELECT DISTINCT ?departmentName ?starttime ?endtime WHERE {
        # La Réunion (wd:Q17070#) Official name (P1448)
        wd:Q17070 p:P1448 ?statement.
        ?statement ps:P1448 ?departmentName.
        # WARNING: Optional should be split, otherwise it
        # doesn't display when only one is absente (only ?startime or only ?endtime)
        OPTIONAL { ?statement pq:P580 ?starttime. }
        OPTIONAL { ?statement pq:P582 ?endtime. }
    }
    ORDER BY ?starttime
    ```

* ***DEPRECATED*** Following is not a good idea to retrieve a city
    `https://www.wikidata.org/wiki/Wikidata:Request_a_query/Archive/2019/02#Cities_names_ending_en_'ac'_==>_time_out`
    ```
    Adapted to Grenoble
    SELECT ?item ?coord WHERE {
        # instance of the object class (P31) or a sub-sub-*-class of this object class (P279*) (wd:P31/wdt:p279*) `human settlement` (wd:Q486972)
        # same has if we had notions of `rdf:type` and `owl:subPropertyOf``rdfs:subPropertyOf`. Use SPARQL property paths.
        ?item (wdt:P31/wdt:P279*) wd:Q486972.
        # ?item country(P17) France (wd:Q142)
        ?item wdt:P17 wd:Q142.
        ?item rdfs:label ?itemLabel.
        # coordinate location
        ?item wdt:P625 ?coord.
        FILTER((LANG(?itemLabel)) = "fr")
        FILTER(REGEX(?itemLabel, "^Grenoble$"))
    }
    ```
    * Could be cool to retrieve Uri of the city, but
    ==> two labels are retrieved
    (can't use DISTINCT as there is two rows to retrieve)
    and take long time, with a timeout reached.
    and need internet connection.

    * Note that the following reach always the timeout on my tests:
    ```
    SELECT ?item ?itemLabel WHERE {
        # instance of the object class (P31) or a sub-sub-*-class of this object class (P279*)  (wd:P31/wdt:p279*) `human settlement` (wd:Q486972)
        # same has if we had notions of `rdf:type` and `owl:subPropertyOf``rdfs:subPropertyOf`. Use SPARQL property paths.
        ?item (wdt:P31/wdt:P279*) wd:Q486972.
        # ?item country(P17) France (wd:Q142)
        ?item wdt:P17 wd:Q142.
        SERVICE wikibase:label { bd:serviceParam wikibase:language "fr". }
    }
    ```

* ***DEPRECATED*** Following can't work cause of cities that have homonyme
    (See https://www.ladepeche.fr/article/2010/08/12/887653-vos-communes-ont-parfois-des-homonymes.html)
    ```
    SELECT DISTINCT ?item ?itemLabel ?department WHERE {
    # instance of the object class (P31) or a sub-sub-*-class of this object class (P279*) (wd:P31/wdt:p279*) `commune of France` (wd:Q484170)
    # same has if we had notions of `rdf:type` and `owl:subPropertyOf``rdfs:subPropertyOf`. Use SPARQL property paths.
    ?item (wdt:P31/(wdt:P279*)) wd:Q484170;
        # Official name (P1448)
        wdt:P1448 ?itemLabel.
        wd:Q6465
    }
    ```

* ***DEPRECATED*** The following can't work because P1448 (Official name) is a container of
    several named (old one and the actual one). Therefore display several
    example. The problem is especially for département « La Réunion »
    that has had several name in the past (see following example).
    ```
    SELECT DISTINCT ?communeURI ?communeLabel ?departmentURI ?departmentLabel WHERE {
    # instance of the object class (P31) or a sub-sub-*-class of this object class (P279*) (wd:P31/wdt:p279*) `commune of France` (wd:Q484170)
    # same has if we had notions of `rdf:type` and `owl:subPropertyOf``rdfs:subPropertyOf`. Use SPARQL property paths.
    ?communeURI (wdt:P31/(wdt:P279*)) wd:Q484170;
        wdt:P1448 ?communeLabel;
        # located in the administrative territorial entity (P131)
        wdt:P131 ?departmentURI.
    # departments of France (Q6465)
    ?departmentURI (wdt:P31/(wdt:P279*)) wd:Q6465;
        wdt:P1448 ?departmentLabel.
    }
    ```

* ***DEPRECATED*** Display all departments of France. Departments with a Dissolution or
    that has several name in the past (i.e. « La Réunion »
    * ***WARNING: Optional should be split, otherwise it
        doesn't display when only one is absente (only ?startime or only
        ?endtime or only ?dissolution)***
    * WARNING: as lot of overseas departments (Guadeloupe, Mayotte)
        has not « Official name » (P1448) but only « native label ».
        It could have several « native label » for several languages.
    * In the solution, I don't use « native label » and « official name »,
        I use label of the page (SERVICE, etc)
    ```
    SELECT DISTINCT  ?departmentLabel ?nativeLabel ?nativeLabelLang ?inseedepartmentCode ?departmentURI ?dissolution ?starttime ?endtime WHERE {

        # « (wdt:P31/(wdt:P279*)) » is very important to display La Réunion
        # that is not directly an instance of Q6465 (Department of France)
        # instance of the object class (P31) or a sub-sub-*-class of this object class (P279*) (wd:P31/wdt:p279*) `Department of France` (wd:16465)
        # same has if we had notions of `rdf:type` and `owl:subPropertyOf``rdfs:subPropertyOf`. Use SPARQL property paths.
        ?departmentURI (wdt:P31/(wdt:P279*)) wd:Q6465.

        # « Insee Departemenmt Code »
        OPTIONAL { ?departmentURI wdt:P2586 ?inseedepartmentCode . }

        # Some department disappears in the past.
        # « dissolved, abolished or demolished » (wdt:P576)
        OPTIONAL { ?departmentURI wdt:P576 ?dissolution }.

        # « Native label » (p1448)
        OPTIONAL {
            ?departmentURI wdt:P1705 ?nativeLabel.
            # https://en.wikibooks.org/wiki/SPARQL/WIKIDATA_Precision,_Units_and_Coordinates#Monolingual_texts
            BIND( LANG(?nativeLabel) AS ?nativeLabelLang) .
        }

        OPTIONAL {
            # « Official name » (p1448)
            ?departmentURI p:P1448 ?departmentStatement.

            # A department could have several officials names in history.
            # « Official name » (p1448)
            ?departmentStatement ps:P1448 ?departmentLabel.
            OPTIONAL { ?departmentStatement pq:P580 ?starttime. }
            OPTIONAL { ?departmentStatement pq:P582 ?endtime. }
        }

    }
    ORDER BY ?inseedepartmentCode ?dissolution  ?departmentURI ?starttime
    ```
* ***DEPRECATED*** (Deprecated but interesting) Display department URI and department label
    * WARNING: as lot of overseas departments (Guadeloupe, Mayotte)
        has not « Official name » (P1448)
    * Warning, there are two  « native name » for Gironde
        (French and occident)
    * WARNING: Actually Mayotte has neither « native name » nor
        « official name ».
    ```
    SELECT DISTINCT ?departmentURI ?departmentLabel ?nativeLabel ?inseedepartmentCode WHERE {
        # « (wdt:P31/(wdt:P279*)) » is very important to display La Réunion
        # that is not directly an instance of Q6465 (Department of France)
        # instance of the object class (P31) or a sub-sub-*-class of this object class (P279*) (wd:P31/wdt:p279*) `Department of France` (wd:16465)
        # same has if we had notions of `rdf:type` and `owl:subPropertyOf``rdfs:subPropertyOf`. Use SPARQL property paths.
        ?departmentURI (wdt:P31/(wdt:P279*)) wd:Q6465 ;
            # « Insee Departemenmt Code »
            wdt:P2586 ?inseedepartmentCode ;

        # « Native label » (p1448)
        OPTIONAL {
            ?departmentURI wdt:P1705 ?nativeLabel.
            BIND( LANG(?nativeLabel) AS ?nativeLabelLang) .
            FILTER(REGEX(?nativeLabelLang, "^fr$"))
        }

        # « Official name » (p1448)
        OPTIONAL {
            ?departmentURI p:P1448 ?departmentStatement.
            ?departmentStatement ps:P1448 ?departmentLabel.
            # A department could have several officials names in history.
            # Take only the official name that has not «  end time »
            # « end time » (P582)
            FILTER(NOT EXISTS { ?departmentStatement pq:P582 ?endtime. })
        }

        # Some department disappears in the past.
        # Take only those who hasn't a property
        # « dissolved, abolished or demolished » (wdt:P576)
        FILTER(NOT EXISTS { ?departmentURI wdt:P576 ?dissolution. })
    }
    ORDER BY ?inseedepartmentCode
    ```

* ***DEPRECATED*** (Deprecated but interesting)
    display cities and department URI but with no filters
    ```
    SELECT DISTINCT ?communeURI ?communeLabel ?departmentURI WHERE {
    # instance of the object class (P31) or a sub-sub-*-class of this object class (P279*)  (wd:P31/wdt:p279*) of `commune of France` (wd:Q484170)
    # same has if we had notions of `rdf:type` and `owl:subPropertyOf``rdfs:subPropertyOf`. Use SPARQL property paths.
    ?communeURI (wdt:P31/(wdt:P279*)) wd:Q484170 ;
      # located in the administrative territorial entity (P131)
        wdt:P131 ?departmentURI.

        # Could have duplicate for communes that has changed its name
        # (See above resolution for Department)
        # Lot of communes has not « Official name »
        # « Official name » (p1448)
     OPTIONAL { ?communeURI   wdt:P1448 ?communeLabel }.

    # departments of France (Q6465)
    ?departmentURI (wdt:P31/(wdt:P279*)) wd:Q6465.
    }
    ORDER BY ?communeLabel
    ```


* ***DEPRECATED*** (Deprecated but interesting)
    displays communes not departments as it could have some departments
    that has disappeared (e.g. Seine) and replaced by others.
    If we try to display ?department we will have some duplicated
    `?commune`
```
    SELECT DISTINCT ?commune ?communeLabel WHERE {
      # instance of (P31)
      ?commune p:P31 ?statementCommune .

      # instance of (P31)
      ?statementCommune ps:P31
        # « subclass of » (P279) « commune of France » (wd:Q484170)
        [ (wdt:P279*) wd:Q484170 ] .

      OPTIONAL {
        # located in the administrative territorial entity (P131)
        ?commune  p:P131 ?departmentStatement .
        # instance of (P31)
        ?departmentStatement ps:P31
            # « subclass of » (P279) « Departments of France » (Q6465)
            [ (wdt:P279*) wd:Q6465 ] .
        # Take only « departmentStatement » that has not «  end time »
        # « end time » (P582)
        FILTER(NOT EXISTS { ?departmentStatement pq:P582 ?endtime. })
      } .

      # Take only « communeStatatenement » that has not «  end time »
      # « end time » (P582)
      FILTER(NOT EXISTS { ?statementCommune pq:P582 ?endtime. })

      SERVICE wikibase:label { bd:serviceParam wikibase:language "fr". }
    }
    ORDER BY ?communeLabel ?commune
```

#### Retrieve departments and communes

##### Retrieve Department infos
    * Warning it retrieve also https://www.wikidata.org/wiki/Q22247953
        that is probably not a Department.
        Error in Wikidata done by an English user on 27 march 2019
        TODO correct it.
    * Simpler, we could do not add `OPTIONAL` between
        `?department wdt:P2586 ?inseedepartmentCode`
        As it actually (2019) no need to filter with `?dissolution`.
    ```
    SELECT DISTINCT  ?department ?departmentLabel ?inseedepartmentCode WHERE {

        # « (wdt:P31/(wdt:P279*)) » is very important to display La Réunion
        # that is not directly an instance of Q6465 (Department of France)
        # instance of the object class (P31) or a sub-sub-*-class of this object class (P279*) (wd:P31/wdt:p279*) `Department of France` (wd:16465)
        # same has if we had notions of `rdf:type` and `owl:subPropertyOf``rdfs:subPropertyOf`. Use SPARQL property paths.
        ?department (wdt:P31/(wdt:P279*)) wd:Q6465.

        # « Insee Departemenmt Code »
        OPTIONAL { ?department wdt:P2586 ?inseedepartmentCode . }

        # Some department disappears in the past.
        # « dissolved, abolished or demolished » (wdt:P576)
        OPTIONAL { ?department wdt:P576 ?dissolution }.

        FILTER(NOT EXISTS { ?department wdt:P576 ?dissolution. })

        SERVICE wikibase:label { bd:serviceParam wikibase:language "fr". }
    }
    ORDER BY ?inseedepartmentCode
    ```


#####  Retrieve communes and its Department URI

* As:
    * « commune nouvelle » (Q2989454) is « subclass of » (P279) «commune of france » (wd:Q484170)
    *  « delagated commune » (Q21869758) is NOT a « subclass of » (P279) «commune of france » (wd:Q484170)
    * => we has « Val-de-Virieu (Q57695189) and not « Virieu » (Q1011549)
* 06/01/2018 (in June) we has 35 088 communes
* Some has not label and INSEE code like https://www.wikidata.org/wiki/Q55587701
    (i.e. label is « Q55587701 » on a SPARQL query and in the h1 tag of
    the page we have « No label defined ») instead of a title)
    It has no department too.
* TODO CORRECT WITH https://www.insee.fr/fr/information/2028028
    (34970 communes on 01/01/2019)
    TODO help with a SPARQL query in WikiData
* (Even if there is thee month since the last INSEE publication,
    probably WikiData is outdated, as Government
    wants less Communes and not more)
* At https://fr.wikipedia.org/wiki/Nombre_de_communes_en_France#cite_note-5
    it says « Au 1er mars 2019, la France métropolitaine et les départements
    d’outre-mer sont découpés en 34 968 communes »
* Actually https://www.wikidata.org/wiki/Q55587701 does not contain « inception » (P571) but not
    wdt:P31 [ instance of ] « commune nouvelle »  with a « start time » qualifier
* BIG WARNING
    * Following can't work as expected. It only remove ?startTime of the result.
    ```
    SELECT DISTINCT ?startTime WHERE {
        (…)
        OPTIONAL {
            ?communeStatement pq:P582 ?endTime.
            FILTER(?endTime >= NOW()).
        } .
        (…)
    }
    ```
    * As used in the example above use instead
    ```
    SELECT DISTINCT ?startTime WHERE {
        (…)
        OPTIONAL {
            ?communeStatement pq:P582 ?endTime.
        } .
        FILTER(!BOUND(?endTime) || ?endTime >= NOW()) .
        (…)
    }
    ```

* The solution

```
SELECT DISTINCT ?commune ?communeLabel ?departmentURI WHERE {
    # instance of (P31)
    ?commune p:P31 ?communeStatement .

    # instance of (P31)
    ?communeStatement ps:P31 ?classCommuneOfFrance .

    # « subclass of » (P279) « commune of France » (wd:Q484170)
    ?classCommuneOfFrance (wdt:P279*) wd:Q484170 .

    OPTIONAL {
        # located in the administrative territorial entity (P131)
        ?commune p:P131 ?departmentStatement .

        ?departmentStatement ps:P131 ?departmentURI .

        # instance of (P31) « departments of France » (Q6465)
        ?departmentURI (wdt:P31/(wdt:P279*)) wd:Q6465 .

        # Take only « departmentStatement » that has not «  end time »
        # « end time » (P582)
        FILTER(NOT EXISTS { ?departmentStatement pq:P582 ?endtime. }) .

        # Some departement disappears in the past.
        # « dissolved, abolished or demolished » (wdt:P576)
        FILTER(NOT EXISTS { ?departmentURI wdt:P576 ?dissolution. }) .

    } .

    # Take only « communeStatement » that has not «  end time »
    # « start time » (P580)
    OPTIONAL {
        ?communeStatement pq:P580 ?startTime .
    } .
    FILTER(!BOUND(?startTime) || ?startTime <  NOW()) .
    OPTIONAL {
        ?communeStatement pq:P582 ?endTime.
    } .
    FILTER(!BOUND(?endTime) || ?endTime >= NOW()) .
    # Take only « communeStatement » that has not «  end time »
    # « end time » (P582)
    OPTIONAL {
        ?commune wdt:P571 ?inception.
    } .
    FILTER(!BOUND(?inception) || ?inception < NOW()).
    OPTIONAL {
        ?commune wdt:P576 ?dissolved.
    } .
    FILTER(!BOUND(?dissolved) || ?dissolved >= NOW()).

    SERVICE wikibase:label { bd:serviceParam wikibase:language "fr". } .
}
ORDER BY ?communeLabel ?commune
```

##### Create the rdf file

* See in conclusion others solution to convert in RDV

* Note: do not remove trailer tabs on the file. The best is to not edit it.

1. In the Data retrieved, on the bottom pan go to `Download -> TSV file`
    (columns separated by tab)


2. Execute following bash
    ./createWikidataFrenchCommunesOwl.sh

3. At the beginning of the file, add xml informations needed
    (copy from another owl file).

4. Don't forget to change `owl:Ontology`, `xmlns` and `xmlns:base`

5. Append the new ontology at
 ./scholarProjectWebSemanticFusekiDatabase/run/configuration/sempic.ttl
 un `` ja:content []``

* Note:
    * In the file « Departments.xml »,
        actually there is « Antilles-Guyane »
    * In the file Communes.xml first lines has no label
    * But not important


### Conclusion about Linked Open Data

* WikiData is the best

* If you want up to date and reliable datas don't use Linked Data
    like DBpedia or WikiData, but user other data seeder (INSEE, etc.)

* Convert datas
    * with http://openrefine.org/download.html
        (https://github.com/fadmaa/grefine-rdf-extension/ was
        advised by the teacher for Google Refine, not updated for OpenRefine)
        * Google Refine. In October 2012, it was renamed OpenRefine
            as it transitioned to a community-supported product.
    * Or with the sed like explained above
    * Or with TARQL https://github.com/tarql/tarql
        (not tested)

### Linked Open Vocabulary

* *Linked Open Vocabularies, un écosystème encore fragile*
    2012
    (in French)
    https://projet.liris.cnrs.fr/qetr2012/site/wp-content/uploads/2012/02/qetr2012_1.pdf

* *Linked Open Vocabularies (LOV): a gateway to reusable semantic vocabularies on the Web*
    2014
    http://www.semantic-web-journal.net/system/files/swj1127.pdf (in English)

* With the search « Linked open vocabulary » on Google
    we have https://lov.linkeddata.es/dataset/lov/
    But I don't know if it is interesting.
    They say
    > LOV started in 2011, in the framework of a French research
    > projecthttp://datalift.org. Its main initial objective was to help
    > publishers and users of linked data and vocabularies to assess what was
    > available for their needs, to reuse it as far as possible, and to insert
    > their own vocabulary production seamlessly in the ecosystem.
    >
    > TheOpen Knowledge Foundationhas kindly provided technical hosting from
    > July 2012 until July 2018.
    >
    > Since July 2018, LOV is hosted by the Ontology Engineering Group at UPM
    > https://lov.linkeddata.es/dataset/lov/about

## Jena Documentation

* Note that Fuseki documentation has several break links. Use Google to
    search pages pointed by dead links.

* For Java documentation, to see in wish project of Jena is
    (e.g. ARQ, Fuseki, Core, etc.) check the start of URL.

* All JavaDoc for all projects are at https://jena.apache.org/documentation/javadoc/

### How to install Jena

See ./teacherExample/HowToConfigureJenaByJeromeDavid.pdf


### Jena and OWL2 and OntModel

> Jena does not provide OWL2 inference or OntModel support.
> https://jena.apache.org/documentation/javadoc/jena/org/apache/jena/vocabulary/OWL2.html

> Note: Although OWL version 1.1 is now a W3C recommendation, Jena's support for
> OWL 1.1 features is limited. We will be addressing this in future versions
> Jena.
> https://jena.apache.org/documentation/ontology/

* See also
    https://mail-archives.apache.org/mod_mbox/jena-users/201612.mbox/<A286D8DA9B2E3B4581355AFD9B0B39F302D38023@SMUC0224.bauhaus-luftfahrt.corp>

* During my search, I've explored how to build an `OntModel` and `OntResource`
    To understand what it is, see official API Jena and
    https://jena.apache.org/documentation/ontology/
    There is a function `OntResource.remove()`, but as the resource isn't link
    to a `RdfConnection`, if we use `OntResource.commit()` it can' work.

* See also https://jena.apache.org/documentation/inference/index.html
    Reasoners and rule engines: Jena inference support

### Fuseki and TDB, sempic.ttl
* To understand the file `sempic.ttl` give by the teacher, read

1. official doc
> The TDB2 database can be in a configuration file, either a complete server
> configuration (see below) or as an entry in the FUSEKI_BASE/configuration/
> area of the full server.
https://jena.apache.org/documentation/tdb2/tdb2_fuseki.html

2.  README in the official project
> Note that the Fuseki UI does not provide a way to create TDB2 databases; a
> configuration file must be used. Once setup, upload, query and graph editting
> will be routed to the TDB2 database.
https://github.com/apache/jena/blob/master/jena-db/use-fuseki-tdb2.md


## Jena TDB2 vs Jena Fuseki2 vs OpenLink Virtuoso and Linked Data Platform

* Jena TDB2 and OpenLink Virtuoso are databases, more precisely a « LargeTripleStores »
    * For comparaison see
        https://www.w3.org/wiki/LargeTripleStores#OpenLink_Virtuoso_v7.2B_.2839.8B.2B_explicit.3B_uncounted_virtual.2Finferred.29
    * See also https://www.w3.org/wiki/RdfStoreBenchmarking

* Linked Data Platform specification (REST specification)
    * Don't seem to be implemented by Fuseki (no found any reference about it)
    * (originally by IBM)
    * Virtuoso implements Linked Data Platform
        http://vos.openlinksw.com/owiki/wiki/VOS/VirtLDP
    * See also https://en.wikipedia.org/wiki/Linked_Data_Platform

* Apache Marmotta is build on top of RDF4J (also called Sesame)
    * https://en.wikipedia.org/wiki/Apache_Jena

* See also comparaison of triplesstores
    https://en.wikipedia.org/wiki/Comparison_of_triplestores

* The teacher cited OpenLink Virtuso
    (a proprietary software with open sources parts) and says
    that is more complicated to configure at startup.

* Virtuoso use an SQL Database under the hood
    https://stackoverflow.com/questions/17719341/difference-between-virtuoso-native-rdf-quad-store-and-virtuoso-sql-based-rdf-tri

### RDF4J, GraphDB, Neo4J

* RDF4J, contrary to TDB2 does not support OWL
    But Jena support for owl is bad
    (see section about « Question about Owlapi and Jena API with Openllet »
    and info about OntModel in this doc).

* RDF4J supports also reasoning https://github.com/eclipse/rdf4j-doc]

> I prefer rdf4j as Rdf-Api for clients and GraphDB from Ontotext as RDF store.
> I never liked programming with Jena.
https://news.ycombinator.com/item?id=19419025

GraphDB support some OWL inferences
http://graphdb.ontotext.com/documentation/standard/owl-compliance.html
See aslo http://graphdb.ontotext.com/documentation/enterprise/using-graphdb-with-the-rdf4j-api.html


But Neo4J(born in 2007 and open source AGPL with commercial support)
    seems to be better than GraphDB (born in 2000 and commercial)
    No SPARQL for Neo4j.
    https://db-engines.com/en/system/GraphDB;Neo4j
    https://en.wikipedia.org/wiki/Neo4j

I've seen than API of rdf4j seems to be easier than Jena
See https://rdf4j.eclipse.org/documentation/programming/model/
or https://rdf4j.eclipse.org/documentation/programming/repository/
or https://rdf4j.eclipse.org/documentation/getting-started/

### Classement of databases
https://db-engines.com/en/ranking
https://en.wikipedia.org/wiki/DB-Engines_ranking
Neo4J : rank 22
Virtuoso : rank 82
Apache Jena - TDB : rank 104
GraphDB : rank 137
RDF4J: rank 219


### Use Jena with Virtuoso

* Virtuoso could use Jena API
    http://vos.openlinksw.com/owiki/wiki/VOS/VirtJenaProvider

* See also
    http://docs.openlinksw.com/virtuoso/rdfsparqlprotocolendpoint/

* Jena Fuseki is a REST Server, it could serve a Jena TDB2 database or
    OpenLink Virtuoso database.
    * See https://stackoverflow.com/questions/27958212/is-it-possible-to-add-virtuoso-as-a-storage-provider-in-jena-fuseki

* In a future, we could use RDFConnection to a Virtuoso Database
    https://github.com/openlink/virtuoso-opensource/issues/792

* Other resources to use virtuoso as storage provider
    https://stackoverflow.com/questions/27958212/is-it-possible-to-add-virtuoso-as-a-storage-provider-in-jena-fuseki

* See also this Medium article
    https://medium.com/virtuoso-blog/maximizing-the-benefits-of-rdf-inference-between-client-side-frameworks-and-server-side-data-stores-46d0ce5e8fec

### Quad store and graph database

> Adding a name to the triple makes a "quad store" or named graph.
> A graph database has a more generalized structure than a triplestore
https://en.wikipedia.org/wiki/Triplestore

Virtuoso or SQL Server are famous Graph Store but neither TDB nor RDF4J
https://en.wikipedia.org/wiki/Graph_database

### Why Fuseki is has HTTP POST is tricky

* See my comments at ./rest_request_fuseki.roast
    * Shortly:
        POST request to update a resource seems contains non ASCII
        characters that could not be retrieved by Wireshark
        when we copy and past.
        When we copy and past a request from Wireshark, if it's an UNICODE
        char it is correctly copy and past even if it is symbolised
        by « . » on Wireshark.


## Construct a Jena Query

I've shown three ways to construct a Query in
./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/ReadPhoto.java
under function `readPhoto(long id)`. It exists more solutions.
See also `subClassOf(String classUri)`, more simple example. See also all this
file. This file is not factorize, my goal is keep as an example.

The most important doc is the ***official doc***
    https://jena.apache.org/documentation/query/manipulating_sparql_using_arq.html

### Why use Jena API and not Sparql W3C syntax

The Sparql syntax from W3c is very known and very documented. But it's better
to not in a Java Program

> “But what about [little Bobby Tables](https://xkcd.com/327/)? And, even if you
> sanitise your inputs, string manipulation is a fraught process and syntax
> errors await you. Although it might seem harder than string munging, the ARQ
> API is your friend in the long run.”
> (source:
> https://jena.apache.org/documentation/query/manipulating_sparql_using_arq.html )

See also https://en.wikipedia.org/wiki/SQL_injection

* Say that as it's not the best solution because it's not a standard
    is IHMO not an argument. In fact, we could very easy
    retrieve the SPARQL syntax in the output of the console! Then
    if we want to use Virtuoso, simply see the output of the Console!
    Furthermore we could use Jena API into Virtuoso or opposite
    * See also section above about « Virtuoso »

* Read
    * READ ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/jhipster/SempicRest/AlbumRDFResourceGet.java
    * READ ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/ReadAlbum.java
    * This API demonstrates why we should use Jena API and no SPARQL
        Very clear and very easy to factorized!!!

#### Java API 1) syntax form of the query

See the official doc presented above and
also:
https://stackoverflow.com/questions/9979931/jena-updatefactory/30370545#30370545

It's a cool solution. And as they say in the official doc.

> “So far there is no obvious advantage in using the algebra.”

But we could see that it's very verbose. Maybe it's a little bit more
understandable, because contrary to Algebra form, we could define
`CONSTRUCT clause` before the `WHERE clause`.

#### Java API 2) Algebra form of the query

I believe the best solution is to use directly Sparql Algebra.
To understand what is Spqral Algebra,
1. First see official doc presented above
2. Very important doc:
    https://jena.apache.org/documentation/query/arq-query-eval.html#opexecutor
3. See definition of Algebra in official doc at
    https://jena.apache.org/documentation/query/algebra.html
    * ***Important note*** : to print the algebra form of the query, use
        /opt/apache-jena/bin/qparse and not `arq.qparse` (it doesn't exist).
4. See the official example
    https://github.com/apache/jena/blob/master/jena-arq/src-examples/arq/examples/AlgebraExec.java
5. The Power Point
    https://afia.asso.fr/wp-content/uploads/2018/01/Corby_PDIA2017_RechercheSemantique.pdf
    (they explain also what is a BGP, a Basic Graph Pattern).
6. As they said at
    https://www.programcreek.com/java-api-examples/?api=org.apache.jena.sparql.syntax.ElementFilter (https://www.programcreek.com/java-api-examples/?code=xcurator/xcurator/xcurator-master/lib/new_libs/apache-jena-3.1.0/src-examples/arq/examples/propertyfunction/labelSearch.java)
    “The better design is to build the Op structure programmatically,”.
    Note they call `op = Algebra.optimize`, but I didn't explored that.
7. https://www.w3.org/2011/09/SparqlAlgebra/ARQalgebra , a very detailed
    explanation (maybe too much).

Note:
As explained in function `listSubClassesOf`, following works only
with SELECT clause, but bug with CONSTRUCT clause (no investigate further why,
and if I've forgotten something).

```java
        op = new OpProject(op,
                    Arrays.asList(
                        new Var[] {
                            Var.alloc("s"),
                            Var.alloc(RDFS.label.asNode()),
                            Var.alloc("o")
                        }
                    )
                );
```

Use instead:

```java
        BasicPattern basicPatternConstructClause = new BasicPattern();
        basicPatternConstructClause.add(tripleRDFType);
        queryAlgebraBuild.setConstructTemplate(
                new Template(basicPatternConstructClause));
```

### Result format

#### Result format isn't part of the Algebra

Even if we use directly Algebra, as:

> Notice that the query form (SELECT, CONSTRUCT, DESCRIBE, ASK) isn't
> part of the algebra, and we have to set this in the query (although
> SELECT is the default). FROM and FROM NAMED are similarly absent.
> https://jena.apache.org/documentation/query/manipulating_sparql_using_arq.html

You should see https://jena.apache.org/documentation/query/app_api.html

#### Difference between SELECT and CONSTRUCT

* Select could retrieve only result in Format XML, json, csv, tsv

* CONSTRUCT could only retrieve in format turtle, JSON-LD, N-Triples,
    XML (rdf xml).

#### LIMIT and OFFSET
* http://rdf.myexperiment.org/howtosparql?page=LIMIT


### SPARQL RDF collection

* General doc to retrieve, delete, insert members:
    http://www.snee.com/bobdc.blog/2014/04/rdf-lists-and-sparql.html

> RDF lists are hard to deal with because they are not first class objects
> in the RDF data model. Instead they are "encoded" in triples
>  https://seaborne.blogspot.com/2011/03/updating-rdf-lists-with-sparql.html

* BIG WARNING: SPARQL path needs SPARQL 1.1

#### Delete an element in a list / collection

* If we try to delete an URI like

    ```
<http://fr.uga.julioju.sempic/ResourcesCreated/album/2>
    <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumSharedWith>
        [ rdf:first  <http://fr.uga.julioju.sempic/ResourcesCreated/user/user> ; rdf:rest [ rdf:first <http://fr.uga.julioju.sempic/ResourcesCreated/user/admin> ; rdf:rest rdf:nil ] ]
    ```

    with the following request:
    ```
    DELETE WHERE { ?s rdf:first <http://fr.uga.julioju.sempic/ResourcesCreated/user/admin> }
    ```
    It breaks the rdf:list:
    ```
<http://fr.uga.julioju.sempic/ResourcesCreated/album/2>
    <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumSharedWith>
        [ rdf:rest [ rdf:first <http://fr.uga.julioju.sempic/ResourcesCreated/user/admin> ; rdf:rest rdf:nil ] ]
    ```

    * We want simply:
    ```
<http://fr.uga.julioju.sempic/ResourcesCreated/album/2>
    <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumSharedWith>
        [ rdf:first <http://fr.uga.julioju.sempic/ResourcesCreated/user/admin> ; rdf:rest rdf:nil ]
    ```

* In the resources above, they don't give trick to remove an element in a list

* My solution is simply request all lists that contains `<http://fr.uga.julioju.sempic/ResourcesCreated/user/admin>`
    and build a new list programmatically (in Java) without this resource, then
    ```
    DELETE WHERE {
<http://fr.uga.julioju.sempic/ResourcesCreated/album/2>
    <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumSharedWith>
    ?p
    }
    ```
    And append the list build programmatically.

    (***TODO use syntax DELETE / INSERT / WHERE***)
    See ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/jhipster/SempicRest/UserRDFResource.java
    TODO, improve performence by make only one request for all deletions.

#### SPARQL Query


##### With SELECT

* With select
    See https://stackoverflow.com/questions/10162052/rdfcollection-in-sparql
    (syntax )
    `rdf:rest*/rdf:first`

* For instance:

    ```
SELECT DISTINCT ?album ?albumSTitle ?listRest

WHERE
  { ?album  <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumOwnerLogin>  <http://fr.uga.julioju.sempic/ResourcesCreated/user/anotheruser> ;
            <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumSTitle>  ?albumSTitle ;
            <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumSharedWith>  ?albumSharedWithList
    OPTIONAL
      { ?albumSharedWithList (<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest>)*/rdf:first ?listRest .
      }
  }
  ```

##### WITH CONSTRUCT

* With construct, retrieve list
    ***IMPLEMENTED IN ALGEBRA FORM ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/ReadAlbum.java***

    ```
CONSTRUCT
  {
    <http://fr.uga.julioju.sempic/ResourcesCreated/album/2> <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumSharedWith> ?albumSharedWithList .
    <http://fr.uga.julioju.sempic/ResourcesCreated/album/2> <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumOwnerLogin> ?albumOwnerLogin .
    ?listRest <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> ?head .
    ?listRest <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> ?tail .
  }
WHERE
  { <http://fr.uga.julioju.sempic/ResourcesCreated/album/2>
              <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumSharedWith>  ?albumSharedWithList ;
              <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumOwnerLogin>  ?albumOwnerLogin
    OPTIONAL
      { ?albumSharedWithList (<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest>)* ?listRest .
        ?listRest  <http://www.w3.org/1999/02/22-rdf-syntax-ns#first>  ?head ;
                  <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest>  ?tail
      }
  }

  ```
* Syntax `rdf:rest*/rdf:first`
    is not allowed in `CONSTRUCT`.

* But following work (and more simple, but in the result we don't see it's a list,
    maybe teachers don't like it.
    ```
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
CONSTRUCT
  {
  <http://fr.uga.julioju.sempic/ResourcesCreated/album/2> <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumSharedWith>  ?listRest ;
    <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumOwnerLogin> ?o1 .

  }
WHERE
  {
  <http://fr.uga.julioju.sempic/ResourcesCreated/album/2> <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumSharedWith> ?list ;
    <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumOwnerLogin> ?o1 .
    ?list rdf:rest*/rdf:first ?listRest .
  }
  ```

* See also
    https://stackoverflow.com/questions/44221975/how-to-write-a-sparql-construct-query-that-returns-an-rdf-list

##### Jena Doc
* ARQ - Property Paths
    https://jena.apache.org/documentation/query/property_paths.html

##### Retrieve using Jena Property functions (no W3C compliant)
    https://jena.apache.org/documentation/query/extension.html#property-functions

    ```
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX list: <http://jena.hpl.hp.com/ARQ/list#>
CONSTRUCT
  {
  <http://fr.uga.julioju.sempic/ResourcesCreated/album/2> <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumSharedWith>  [ list:member ?member ] ;
    <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumOwnerLogin> ?o1 .
  }
WHERE
  {
  <http://fr.uga.julioju.sempic/ResourcesCreated/album/2> <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumSharedWith> [ list:member ?member ] ;
    <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumOwnerLogin> ?o1 .
  }
  ```

##### Retrieve albums shared with an user, when this album is shared by several users.
```
CONSTRUCT
  {
    ?album <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumOwnerLogin> ?albumOwnerLogin .
    ?album <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumSTitle> ?albumSTitle .
    ?album <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumSharedWith> ?albumSharedWithList .
    ?listRest <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> ?head .
    ?listRest <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> ?tail .
  }
WHERE
  { { ?album  <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumOwnerLogin>  ?albumOwnerLogin ;
              <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumSTitle>  ?albumSTitle ;
              <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumSharedWith>  ?albumSharedWithList ;
              <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumSharedWith>  ?albumShareFilter
      OPTIONAL
        { ?albumSharedWithList (<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest>)* ?listRest .
          ?listRest  <http://www.w3.org/1999/02/22-rdf-syntax-ns#first>  ?head ;
                    <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest>  ?tail
        }
      OPTIONAL
        { ?albumShareFilter (<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest>)*/<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> ?listRestFilter }
    }
    FILTER ( ?listRestFilter IN (<http://fr.uga.julioju.sempic/ResourcesCreated/user/admin>) )
  }
```

### Use UNION instead of FILTER if possible

* https://www.cray.com/blog/tuning-sparql-queries-performance/``
    Again, this will likely be much more performant because it creates a lot
    less work for the query engine than the FILTER form of the query.

* to retrieve all all albums owned by and shared with an user
    I thought at the beggining to use FILTER, but it's better to use UNION
*   See  ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/ReadAlbum.java

### org.apache.jena.rdf.model.Model

#### Important tuto

* ***You MUST read https://jena.apache.org/tutorials/rdf_api.html***

#### Querying a model (to retrieve all AlbumRDF on an user)

* To query all albumRDF of an user we use only one Query.

* As the code is very factorized, the Query and the parse of the result is
    the same as when we request only one AlbumRDF.

* See ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/ReadAlbum.java

* When we request all albumRDF of one user we have a succession
    of several albumRDF. We split the `org.apache.jena.rdf.model.Model`
    into several `Model` that contains only one AlbumRDF.
    Then each `Model` thant contains only one AlbumRDF is parsed
    and when we parse one AlbumRDF.

* See especially the doc
    ***https://jena.apache.org/tutorials/rdf_api.html#ch-Querying%20a%20Model***


### Jena Source code organization

Jena source code open source. Available at https://github.com/apache/jena/ .

To understand how it works, don't forget that

1. `FrontsNode <-- RDFNode <-- Resource <-- Property` (inheritance).
2. `FrontsNode` has a `Node` Property (`protected`), and therefore
    has a public method `FrontsNode.asNode()`
3. There are severals type of Node:
    `Node_Blank, Node_Anon, Node_URI, Node_Variable, and Node_ANY`
4. An RDF model is a set of Statements.
5. `StatementImpl` has three `protected` attributes: `Resource subject`,
    `Property predicate`
    `RDFNode object`, and its three corresponding getter.
    It has a function `createReifiedStatement()`. `ReifiedStatement`
    extends `Resource` and `ReifiedStatementImpl` has a `protected`
    attribute `Statement` and its public getter. `rdf:statement` is
    an rdf uri https://www.w3.org/TR/rdf-schema/#ch_statement .
6. `ResourceImpl.addLiteral()` add literal to the Model
    (`ModelCom` that implements `Model`) of the Resource
    (if exists, otherwise raise exception).
7. `RDFNode` doesn't have `ModelCom`.
8. See also https://www.w3.org/TR/rdf-schema/#ch_class

### See also

* As **I don't use all examples of the teacher**, you must study
    ./teacherExample/src/main/java/fr/uga/miashs/sempic/rdf/RDFStore.java

* As the official doc is a little bit poor of examples, don't forget
    to use https://www.programcreek.com/java-api-examples/
    Maybe use Google, in the search bar type something like:
    `site:https://www.programcreek.com/java-api-examples/ ElementTriplesBlock`

* See also
    https://stackoverflow.com/questions/7250189/how-to-build-sparql-queries-in-java
    They show the “StringBuilder style API for building query/update strings and
    parameterizing them if desired”.

## Jena DELETE

* For model already persisted in database,
    the method `deleteModel(Model m)` presented at
    ./teacherExample/src/main/java/fr/uga/miashs/sempic/rdf/RDFStore.java
    is not very useful
    * Maybe it's because often the model is not stored as it.
        The model build in the code is
        not the model we store, therefore there is never match.
        A subject could have more properties and objects that we has
        described in the code.

* To delete
    a Resource check
    ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/Delete.java ,
    the method `deleteResource (Resource r)`.

    As the method `readPhoto(long id)` presented above, there are the SPARQL
    syntax and the Jena Java API.
    To write the method `deleteResource(Rsource r)` with Java API, I was
    inspired by the method `deleteModel(Model m)` of the teacher.


* Maybe read https://stackoverflow.com/questions/1109228/removing-individuals-properties-from-rdf?rq=1

* DOCUMENTATION:
    https://www.w3.org/TR/2013/REC-sparql11-update-20130321/#deleteWhere


### Delete before update

* As I've explained in my code, we must delete a subject resource before update it
    otherwise it appends (insert) news object and don't delete old one.

* Therefore, Update can't be Atomic
    (see https://en.wikipedia.org/wiki/Atomicity_(database_systems) )

* FIXME Maybe there is a solution of that, but I don't know.
    Probably the answer is at https://www.fun-mooc.fr/c4x/inria/41002S02/asset/C013FG-W3.pdf
    page 65 with the pattern
    ```
    DELETE {

    }
    INSERT {

    }
    WHERE {

    }
    ```
### Delete RDF collections (list)

* See section about collections

### Cascading delete

* With owl

    ```
DELETE WHERE
{
  <http://fr.uga.julioju.sempic/ResourcesCreated/album/1> ?p1 ?o1 .
  ?s2 ?p3 ?o3 .
  ?s2 ?p2 <http://fr.uga.julioju.sempic/ResourcesCreated/album/1> .
}
    ```
    erase all the database.

    In fact, when we trigger
    ```
    CONSTRUCT { <http://fr.uga.julioju.sempic/ResourcesCreated/album/1> ?p ?o }
    WHERE { <http://fr.uga.julioju.sempic/ResourcesCreated/album/1> ?p ?o }
    ```
we see that `?anySubject owl:differentFrom <http://fr.uga.julioju.sempic/ResourcesCreated/album/1>`

* Same example with photos

## Limitations of Fuseki

### No informations about success

* Like for DELETE, for a POST it seems there is no way to know if the resource added
    already exist before.

* As we could test in ./rest_request_fuseki.roast
    there is no way to know if deletion is a success, if it deleted something.
    * Probably when it fail is throw an exception
    * TODO remove test after deletion at
        ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/RDFStore.java
        at function `RDFStore.deleteClassUriWithTests(node_URI);`

# Fuseki serious troubleshooting cause by reasoner

## Description

Use FusekiServer managed by Fuseki is a workaround to the bug
 bug described at https://mail-archives.us.apache.org/mod_mbox/jena-users/201810.mbox/<51361cde-332c-ffb7-4ba4-b73d43bd4cf5@apache.org>
 (see the full clear mail thread at https://mail-archives.us.apache.org/mod_mbox/jena-users/201810.mbox/thread)
***Iterator used inside a different transaction"***
 > The stack trace looks like you have configured a dataset with some inference.
 > It is possible that the inference layer is not using transactions on the
 > underlying dataset properly, and/or caching some data that is tied to a
 > specific transaction.

*See my StackTrace in section below.

* When we we start the app with a Fuseki not embedded
    , the Fuseki server
    show a StackTrace similar to those described
    in the link above.

I've passed lot of time on this bug. I'm pretty sure the bug is
on Fuseki Server side and not in my code. All my RDFConnection are closed
thanks `try-with-resources` as explained at https://jena.apache.org/documentation/txn/txn.html . (Note the teacher example (under `./teacherExample`), he doesn't
close properly connections opened, probably if there is only one
`RDFConnection` with `@ApplicationScope`, it's not a problem and it's better
as we have not the cost of several instantiations).
**Furthermore**. I've checked than HTTP GET, then HTTP POST sent by Spring Server to Fuseki Server thanks
Wireshark: I've discover there is ***no any*** reason for the bug *Iterator used inside
a different transaction*. On Client Side (therfore under my Spring code)
the way of RDFConnection are managed or any other stuff seems not impact `HTTP POST`
and request. Only RDF is sent :

### Succession of REQUESTs

#### Example 1

On the following ASK WHERE request, I notice than even if we restart
the Spring boot Server, following HTTP GET and HTTP POST are same (it's normal).
```
EoÕ@@²ÉRÖ*ÔÎÌÉÿc
&¹-9&¹-9GET /sempic/?query=ASK%0AWHERE%0A++%7B+%3Chttp%3A%2F%2Ffr.uga.julioju.sempic%2FResourcesCreated%2Falbum%2F1%3E%0A++++++++++++++%3Fp++%3Fo%0A++%7D%0A HTTP/1.1
Accept: application/sparql-results+json
User-Agent: Apache-Jena-ARQ/3.11.0
Host: localhost:3030
Connection: Keep-Alive
Accept-Encoding: gzip,deflate

```
```
E§n@@Ì³ÉTÖèméR¶ÿ
&¹TÌ&¹TÌPOST /sempic/ HTTP/1.1
User-Agent: Apache-Jena-ARQ/3.11.0
Content-Length: 170
Content-Type: application/sparql-update
Host: localhost:3030
Connection: Keep-Alive
Accept-Encoding: gzip,deflate

DELETE WHERE
{
  <http://fr.uga.julioju.sempic/ResourcesCreated/photo/1> ?p ?o .
} ;
DELETE WHERE
{
  ?s ?p <http://fr.uga.julioju.sempic/ResourcesCreated/photo/1> .
}
```

#### Example 2

The error come also with the succession of `ASK WHERE`, `ASK WHERE`
and `CONSTRUCT`

1)
```
EnW¯@@ãØÞlÖª ôrÎèÿb
¦VÓ¦VÒGET /sempic/?query=ASK%0AWHERE%0A++%7B+%3Chttp%3A%2F%2Ffr.uga.julioju.sempic%2FResourcesCreated%2Fuser%2F4%3E%0A++++++++++++++%3Fp++%3Fo%0A++%7D%0A HTTP/1.1
Accept: application/sparql-results+json
User-Agent: Apache-Jena-ARQ/3.11.0
Host: localhost:3030
Connection: Keep-Alive
Accept-Encoding: gzip,deflate

```
```sql
ASK
WHERE
  { <http://fr.uga.julioju.sempic/ResourcesCreated/user/4>
              ?p  ?o
  }

```

2)
```
EoW²@@ãÔÞlÖª õ¬Î5ÿc
¦bç¦WdGET /sempic/?query=ASK%0AWHERE%0A++%7B+%3Chttp%3A%2F%2Ffr.uga.julioju.sempic%2FResourcesCreated%2Fphoto%2F1%3E%0A++++++++++++++%3Fp++%3Fo%0A++%7D%0A HTTP/1.1
Accept: application/sparql-results+json
User-Agent: Apache-Jena-ARQ/3.11.0
Host: localhost:3030
Connection: Keep-Alive
Accept-Encoding: gzip,deflate

```
```sql
ASK
WHERE
  { <http://fr.uga.julioju.sempic/ResourcesCreated/photo/1>
              ?p  ?o
  }
```

3)

```
Eb`@@Ø
ÃHÖI¿Í¼6W
ê·êµGET /sempic/?query=CONSTRUCT+%0A++%7B+%0A++++%3Chttp%3A%2F%2Ffr.uga.julioju.sempic%2FResourcesCreated%2Fphoto%2F1%3E+%3Fp+%3Fo+.%0A++++%3Fo+%3Chttp%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23label%3E+%3Fo2+.%0A++++%3Fo+%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23type%3E+%3Fo3+.%0A++%7D%0AWHERE%0A++%7B+%7B+%3Chttp%3A%2F%2Ffr.uga.julioju.sempic%2FResourcesCreated%2Fphoto%2F1%3E%0A++++++++++++++++%3Fp++%3Fo%0A++++++OPTIONAL%0A++++++++%7B+%3Fo++%3Chttp%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23label%3E++%3Fo2+%3B%0A++++++++++++++a+++++++++++++++++++++%3Fo3%0A++++++++%7D%0A++++%7D%0A++++FILTER+%28+%3Fp+IN+%28%3Chttp%3A%2F%2Fmiashs.univ-grenoble-alpes.fr%2Fontologies%2Fsempic.owl%23depicts%3E%2C+%3Chttp%3A%2F%2Fmiashs.univ-grenoble-alpes.fr%2Fontologies%2Fsempic.owl%23albumId%3E%2C+%3Chttp%3A%2F%2Fmiashs.univ-grenoble-alpes.fr%2Fontologies%2Fsempic.owl%23ownerId%3E%29+%29%0A++%7D%0A HTTP/1.1
Accept: application/rdf+thrift
User-Agent: Apache-Jena-ARQ/3.11.0
Host: localhost:3030
Connection: Keep-Alive
Accept-Encoding: gzip,deflate

```
```sql
CONSTRUCT
  {
    <http://fr.uga.julioju.sempic/ResourcesCreated/photo/1> ?p ?o .
    ?o <http://www.w3.org/2000/01/rdf-schema#label> ?o2 .
    ?o <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?o3 .
  }
WHERE
  { <http://fr.uga.julioju.sempic/ResourcesCreated/photo/1>
              ?p  ?o
    OPTIONAL
      { ?o  <http://www.w3.org/2000/01/rdf-schema#label>  ?o2 ;
            a                     ?o3
      }
    FILTER ( ?p IN (<http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#depicts>, <http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#albumId>, <http://miashs.univ-grenoble-al
pes.fr/ontologies/sempic.owl#ownerId>) )
  }

```

### When too much classes are stored in the Database

* After several minutes the following request take a very long time
    when we load all comunes of France in the Database

* Therefore the following request doesn't work for the project
    at (2019-05-29)
    https://github.com/JulioJu/scholarProjectWebSemantic/commit/78d736fad56b781616dc90bf37fb85ce00989c37

```sh
wget 'http://localhost:3030/sempic/?query=CONSTRUCT+%0A++%7B+%0A++++%3Chttp%3A%2F%2Ffr.uga.julioju.sempic%2FResourcesCreated%2Fuser%2Fadmin%3E+%3Fp+%3Fo+.%0A++%7D%0AWHERE%0A++%7B+%3Chttp%3A%2F%2Ffr.uga.julioju.sempic%2FResourcesCreated%2Fuser%2Fadmin%3E%0A++++++++++++++%3Fp++%3Fo%0A++%7D%0A'
```

* The teacher confirms that this reasoner bug for scaffolding.

## A solution studied: restart Fuseki Server

We see that the problem is after the method GET in some precises contextes,
when we have first `ASK WHERE`
When we ask into `sempic-onto`, no problems
(TODO confirm it).
Maybe the problem could occurs in in an
other contexts.

~~My workaround is simply restart the Fuseki Server before the bug appears.
See
./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/FusekiServerConn.java
at the end of the method  `cnxQueryAsk (Query q)`
(after the HTTP request GET was done).
I call the method `FusekiServerConn.serverRestart()`~~ (Take too much time).

My workaround is simply restart the Fuseki Server when the bug appears
When the message of the error sent by the server contains `Iterator used inside
a different transaction` I restart the server, I call the method
`FusekiServerConn.serverRestart()`. See
./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/RDFConn.java

### Limitations of resarting Fuseki Server

Anyway, there is no best solution for this. If there are several incoming
requests, stop then restart Fuseki manually could maybe break the requests
(in my opinion, no tested). Indeed, If
a new request is sent during time of Restart of Request could be problematic.

In this case, we must add a queue to delay request.

Maybe we could add a boolean in ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/RDFConn.java
to say if `FusekiServer` (defined in FusekiServerConn.java)
is on or or off. As it, we not trigger a request
when `FusekiServer` is during a boot.

Before create a new `RDFConnection` in  RDFConn.java, we could test is the
port is taken or free. But don't know if it's cool, because maybe the port
could be taken by Fuseki but in a middle of a reboot process.


### Fuseki embedded

See section « Conflict with org.apache.jena.fuseki.main.FusekiServer.html »
(very important section)

### Manage Fuseki standalone with REST API?

* Actually, it seems we cant restart Fuseki Server simply thanks REST API:
    https://jena.apache.org/documentation/fuseki2/fuseki-server-protocol.html


### The solution: Fuseki standalone managed by the App

Manage an independent process into the app. See
./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/FusekiServerConn.java
with option `FusekiServerConn.isEmbeddedFuseki == false`.

Don't forget than the trace of the independant Fuseki is printed
by my app, in a independent Thread.

### Other solutions studied

* Transaction API
    * Actually I don't know how to retrieve Dataset from fuseki.
    See an example of utilisation into
    ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/RDFConn.java
    (actually my tests are commented)
    * https://jena.apache.org/documentation/txn/txn.html TODO not used.
    * https://jena.apache.org/documentation/txn/transactions_api.html
    * Maybe see also (outdated) https://stackoverflow.com/questions/46876859/org-apache-jena-atlas-web-httpexception-405-http-method-post-is-not-supported
    * At the beginning, I've tried also tu use Transaction API. But as I've discovered
        that HTTP GET then HTTP POST are strictly the same, I believe using
        Transaction API couldn't help. Furthermore, probably we can't access
        `TDB2` datasets. Transactions are managed by FusekiServer. Probably
        those examples are only for Dataset loaded in memory.
        * In the preceding examples, probably `FusekiServer.create().add(...)`
            are for in memory Dataset.
            Use `FusekiServer.create().add("/sempic")` complains that `\sempic`
            is already in use (loaded thanks `.parseConfigFile()`).

* **I've tested from with Fuseki 3.6, 3.7, 1.9, 1.10, 1.11 without success**.
    Fuseki 1.5 doesn't work with the current Sempic.ttl file, therefore
    can't test with it.

## THE SOLUTION, use another reasoner

* With Fuseki, only one other reasoner than (the reasoner of Jena) is
    available for free
    ```
         ja:reasoner  		[ ja:reasonerURL <http://jena.hpl.hp.com/2003/OWLFBRuleReasoner>].
    ```

* See the following doc very cool https://pdfs.semanticscholar.org/9091/e269a2cf7a44b46681b3de3ca489a36ad243.pdf
    * International Journal of Computer Applications (0975 – 8887) Volume 57 – No.17, November 2012 33
        A Survey on Ontology Reasoners and Comparison
        Sunitha Abburu , PhD.
        Professor &Director, Dept. of M.C.A Adhiyamaan College of Engineering,Tamilnadu.
    * See tab 3 page 37
        « Comparison of reasoners (Y represents supported feature, N represents non -
        supported feature, Y/N represents need further explanation) »
    * And page 38
        « The reasoners cannot be operated by Jena API except pellet. »

* For information about Pellet,
    read https://github.com/stardog-union/pellet/wiki/FAQ#how-can-i-use-pellet-with-jena

* But if you try to use Pellet there are error
    See https://stackoverflow.com/questions/36313972/fuseki-how-to-add-pellet-reasoner

* To configure Openllet, see also
    https://stackoverflow.com/questions/51764065/how-to-use-openllet-owl2-reasoner-or-any-other-with-jena-tdb

* Actually Pellet can't be used on Fuseki. Use Openllet
    https://github.com/Galigator/openllet
    open source (AGPL) or commercial license
    Historically developed and commercially supported by Complexible Inc; Maybe now https://www.stardog.com/

* List of all Reasoners referenced by w3.org
    https://www.w3.org/2001/sw/wiki/Category:OWL_Reasoner

## StackTrace « Iterator used inside a different transaction » and links

***It's caused by
https://github.com/apache/jena/blob/master/jena-db/jena-dboe-transaction/src/main/java/org/apache/jena/dboe/transaction/txn/IteratorTxnTracker.java***


```
$ cd ../scholarProjectWebSemanticFusekiDatabase && fuseki-server
[2019-05-10 18:15:47] Server     INFO  Apache Jena Fuseki 3.11.0
[2019-05-10 18:15:47] Config     INFO  FUSEKI_HOME=/opt/apache-jena-fuseki
[2019-05-10 18:15:47] Config     INFO  FUSEKI_BASE=/home/julioprayer/DCISS/webSemantique/scholarProjectWebSemantic/scholarProjectWebSemanticFusekiDatabase/run
[2019-05-10 18:15:47] Config     INFO  Shiro file: file:///home/julioprayer/DCISS/webSemantique/scholarProjectWebSemantic/scholarProjectWebSemanticFusekiDatabase/run/shiro.ini
[2019-05-10 18:15:47] Config     INFO  Configuration file: /home/julioprayer/DCISS/webSemantique/scholarProjectWebSemantic/scholarProjectWebSemanticFusekiDatabase/run/config.ttl
[2019-05-10 18:15:47] Config     INFO  Load configuration: file:///home/julioprayer/DCISS/webSemantique/scholarProjectWebSemantic/scholarProjectWebSemanticFusekiDatabase/run/configuration/sem
pic.ttl
[2019-05-10 18:15:48] Config     INFO  Register: /sempic
[2019-05-10 18:15:48] Config     INFO  Register: /sempic-onto
[2019-05-10 18:15:48] Config     INFO  Register: /sempic-data
[2019-05-10 18:15:48] Server     INFO  Started 2019/05/10 18:15:48 CEST on port 3030
[2019-05-10 18:17:36] Fuseki     INFO  [1] GET http://localhost:3030/sempic/?query=ASK%0AWHERE%0A++%7B+%3Chttp%3A%2F%2Ffr.uga.julioju.sempic%2FResourcesCreated%2Fuser%2F4%3E%0A++++++++++++++%
3Fp++%3Fo%0A++%7D%0A
[2019-05-10 18:17:36] Fuseki     INFO  [1] Query = ASK WHERE   { <http://fr.uga.julioju.sempic/ResourcesCreated/user/4>               ?p  ?o   }
[2019-05-10 18:17:36] Fuseki     INFO  [1] 200 OK (185 ms)
[2019-05-10 18:17:36] Fuseki     INFO  [2] POST http://localhost:3030/sempic/
org.apache.jena.dboe.transaction.txn.TransactionException: Iterator used inside a different transaction
        at org.apache.jena.tdb2.store.IteratorTxnTracker.check(IteratorTxnTracker.java:53)
        at org.apache.jena.tdb2.store.IteratorTxnTracker.hasNext(IteratorTxnTracker.java:41)
        at org.apache.jena.atlas.iterator.Iter$2.hasNext(Iter.java:265)
        at org.apache.jena.atlas.iterator.Iter.hasNext(Iter.java:903)
        at org.apache.jena.util.iterator.WrappedIterator.hasNext(WrappedIterator.java:90)
        at org.apache.jena.util.iterator.WrappedIterator.hasNext(WrappedIterator.java:90)
        at org.apache.jena.util.iterator.FilterIterator.hasNext(FilterIterator.java:55)
        at org.apache.jena.graph.compose.CompositionBase$1.hasNext(CompositionBase.java:94)
        at org.apache.jena.util.iterator.NiceIterator$1.hasNext(NiceIterator.java:105)
        at org.apache.jena.util.iterator.WrappedIterator.hasNext(WrappedIterator.java:90)
        at org.apache.jena.util.iterator.NiceIterator$1.hasNext(NiceIterator.java:105)
        at org.apache.jena.reasoner.rulesys.impl.TopLevelTripleMatchFrame.nextMatch(TopLevelTripleMatchFrame.java:55)
        at org.apache.jena.reasoner.rulesys.impl.LPInterpreter.run(LPInterpreter.java:328)
        at org.apache.jena.reasoner.rulesys.impl.LPInterpreter.next(LPInterpreter.java:190)
        at org.apache.jena.reasoner.rulesys.impl.Generator.pump(Generator.java:252)
        at org.apache.jena.reasoner.rulesys.impl.Generator.pump(Generator.java:239)
        at org.apache.jena.reasoner.rulesys.impl.LPBRuleEngine.pump(LPBRuleEngine.java:359)
        at org.apache.jena.reasoner.rulesys.impl.LPTopGoalIterator.moveForward(LPTopGoalIterator.java:107)
        at org.apache.jena.reasoner.rulesys.impl.LPTopGoalIterator.hasNext(LPTopGoalIterator.java:223)
        at org.apache.jena.util.iterator.WrappedIterator.hasNext(WrappedIterator.java:90)
        at org.apache.jena.util.iterator.WrappedIterator.hasNext(WrappedIterator.java:90)
        at org.apache.jena.util.iterator.FilterIterator.hasNext(FilterIterator.java:55)
        at org.apache.jena.util.iterator.WrappedIterator.hasNext(WrappedIterator.java:90)
        at org.apache.jena.util.iterator.FilterIterator.hasNext(FilterIterator.java:55)
        at org.apache.jena.sparql.engine.iterator.QueryIterTriplePattern$TripleMapper.hasNextBinding(QueryIterTriplePattern.java:135)
        at org.apache.jena.sparql.engine.iterator.QueryIteratorBase.hasNext(QueryIteratorBase.java:114)
        at org.apache.jena.sparql.engine.iterator.QueryIterRepeatApply.hasNextBinding(QueryIterRepeatApply.java:74)
        at org.apache.jena.sparql.engine.iterator.QueryIteratorBase.hasNext(QueryIteratorBase.java:114)
        at org.apache.jena.sparql.engine.iterator.QueryIterBlockTriples.hasNextBinding(QueryIterBlockTriples.java:63)
        at org.apache.jena.sparql.engine.iterator.QueryIteratorBase.hasNext(QueryIteratorBase.java:114)
        at org.apache.jena.sparql.engine.iterator.QueryIteratorWrapper.hasNextBinding(QueryIteratorWrapper.java:39)
        at org.apache.jena.sparql.engine.iterator.QueryIteratorBase.hasNext(QueryIteratorBase.java:114)
        at org.apache.jena.sparql.engine.iterator.QueryIteratorWrapper.hasNextBinding(QueryIteratorWrapper.java:39)
        at org.apache.jena.sparql.engine.iterator.QueryIteratorBase.hasNext(QueryIteratorBase.java:114)
        at java.base/java.util.Iterator.forEachRemaining(Iterator.java:132)
        at org.apache.jena.atlas.data.DataBag.addAll(DataBag.java:94)
        at org.apache.jena.sparql.modify.UpdateEngineWorker.visit(UpdateEngineWorker.java:351)
        at org.apache.jena.sparql.modify.request.UpdateDeleteWhere.visit(UpdateDeleteWhere.java:38)
        at org.apache.jena.sparql.modify.UpdateVisitorSink.send(UpdateVisitorSink.java:46)
        at org.apache.jena.sparql.modify.UpdateVisitorSink.send(UpdateVisitorSink.java:26)
        at org.apache.jena.atlas.iterator.Iter.sendToSink(Iter.java:575)
        at org.apache.jena.atlas.iterator.Iter.sendToSink(Iter.java:582)
        at org.apache.jena.sparql.modify.UpdateProcessorBase.execute(UpdateProcessorBase.java:59)
        at org.apache.jena.update.UpdateAction.execute$(UpdateAction.java:228)
        at org.apache.jena.update.UpdateAction.execute(UpdateAction.java:194)
        at org.apache.jena.fuseki.servlets.SPARQL_Update.execute(SPARQL_Update.java:235)
        at org.apache.jena.fuseki.servlets.SPARQL_Update.executeBody(SPARQL_Update.java:188)
        at org.apache.jena.fuseki.servlets.SPARQL_Update.perform(SPARQL_Update.java:105)
        at org.apache.jena.fuseki.servlets.ActionService.executeLifecycle(ActionService.java:266)
        at org.apache.jena.fuseki.servlets.ActionService.execCommonWorker(ActionService.java:155)
        at org.apache.jena.fuseki.servlets.ActionBase.doCommon(ActionBase.java:74)
        at org.apache.jena.fuseki.servlets.FusekiFilter.doFilter(FusekiFilter.java:73)
        at org.eclipse.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1642)
        at org.apache.shiro.web.servlet.ProxiedFilterChain.doFilter(ProxiedFilterChain.java:61)
        at org.apache.shiro.web.servlet.AdviceFilter.executeChain(AdviceFilter.java:108)
        at org.apache.shiro.web.servlet.AdviceFilter.doFilterInternal(AdviceFilter.java:137)
        at org.apache.shiro.web.servlet.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:125)
        at org.apache.shiro.web.servlet.ProxiedFilterChain.doFilter(ProxiedFilterChain.java:66)
        at org.apache.shiro.web.servlet.AbstractShiroFilter.executeChain(AbstractShiroFilter.java:449)
        at org.apache.shiro.web.servlet.AbstractShiroFilter$1.call(AbstractShiroFilter.java:365)
        at org.apache.shiro.subject.support.SubjectCallable.doCall(SubjectCallable.java:90)
        at org.apache.shiro.subject.support.SubjectCallable.call(SubjectCallable.java:83)
        at org.apache.shiro.subject.support.DelegatingSubject.execute(DelegatingSubject.java:383)
        at org.apache.shiro.web.servlet.AbstractShiroFilter.doFilterInternal(AbstractShiroFilter.java:362)
        at org.apache.shiro.web.servlet.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:125)
        at org.eclipse.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1642)
        at org.apache.jena.fuseki.servlets.CrossOriginFilter.handle(CrossOriginFilter.java:285)
        at org.apache.jena.fuseki.servlets.CrossOriginFilter.doFilter(CrossOriginFilter.java:248)
        at org.eclipse.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1634)
        at org.eclipse.jetty.servlet.ServletHandler.doHandle(ServletHandler.java:533)
        at org.eclipse.jetty.server.handler.ScopedHandler.handle(ScopedHandler.java:146)
        at org.eclipse.jetty.security.SecurityHandler.handle(SecurityHandler.java:548)
        at org.eclipse.jetty.server.handler.HandlerWrapper.handle(HandlerWrapper.java:132)
        at org.eclipse.jetty.server.handler.ScopedHandler.nextHandle(ScopedHandler.java:257)
        at org.eclipse.jetty.server.session.SessionHandler.doHandle(SessionHandler.java:1595)
        at org.eclipse.jetty.server.handler.ScopedHandler.nextHandle(ScopedHandler.java:255)
        at org.eclipse.jetty.server.handler.ContextHandler.doHandle(ContextHandler.java:1340)
        at org.eclipse.jetty.server.handler.ScopedHandler.nextScope(ScopedHandler.java:203)
        at org.eclipse.jetty.servlet.ServletHandler.doScope(ServletHandler.java:473)
        at org.eclipse.jetty.server.session.SessionHandler.doScope(SessionHandler.java:1564)
        at org.eclipse.jetty.server.handler.ScopedHandler.nextScope(ScopedHandler.java:201)
        at org.eclipse.jetty.server.handler.ContextHandler.doScope(ContextHandler.java:1242)
        at org.eclipse.jetty.server.handler.ScopedHandler.handle(ScopedHandler.java:144)
        at org.eclipse.jetty.server.handler.gzip.GzipHandler.handle(GzipHandler.java:690)
        at org.eclipse.jetty.server.handler.HandlerWrapper.handle(HandlerWrapper.java:132)
        at org.eclipse.jetty.server.Server.handle(Server.java:503)
        at org.eclipse.jetty.server.HttpChannel.handle(HttpChannel.java:364)
        at org.eclipse.jetty.server.HttpConnection.onFillable(HttpConnection.java:260)
        at org.eclipse.jetty.io.AbstractConnection$ReadCallback.succeeded(AbstractConnection.java:305)
        at org.eclipse.jetty.io.FillInterest.fillable(FillInterest.java:103)
        at org.eclipse.jetty.io.ChannelEndPoint$2.run(ChannelEndPoint.java:118)
        at org.eclipse.jetty.util.thread.strategy.EatWhatYouKill.runTask(EatWhatYouKill.java:333)
        at org.eclipse.jetty.util.thread.strategy.EatWhatYouKill.doProduce(EatWhatYouKill.java:310)
        at org.eclipse.jetty.util.thread.strategy.EatWhatYouKill.tryProduce(EatWhatYouKill.java:168)
        at org.eclipse.jetty.util.thread.strategy.EatWhatYouKill.produce(EatWhatYouKill.java:132)
        at org.eclipse.jetty.util.thread.QueuedThreadPool.runJob(QueuedThreadPool.java:765)
        at org.eclipse.jetty.util.thread.QueuedThreadPool$2.run(QueuedThreadPool.java:683)
        at java.base/java.lang.Thread.run(Thread.java:834)
[2019-05-10 18:17:36] Fuseki     INFO  [2] 500 Iterator used inside a different transaction (74 ms)
```

# Notes on Spring

* `@ApplicationScoped` component are note instantiated at the startup of the
    application. They are instatiated the first time one of its method is
    called. I've instantied in
    the main method of
    ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/jhipster/ScholarProjectWebSemanticApp.java

* Constructors are called before `static void main(String[] args)`
    in ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/jhipster/ScholarProjectWebSemanticApp.java

* `@ApplicationScoped` is not very useful. Use simply `static` class properties
    and method.

## Spring boot arguments

* I've added
```xml
<compilerArg>-noverify</compilerArg>
<compilerArg>-XX:TieredStopAtLevel=1</compilerArg>
```
See https://maven.apache.org/plugins/maven-compiler-plugin/examples/pass-compiler-arguments.html

* As in Eclipse Configuration under menu -> window -> preferences -> spring -> boot

* Don't know if it improve a lot compilation time. No tested but seems improve
    a little

* It seems recommended to une XX:TieredStopAtLevel , see https://stackoverflow.com/questions/38721235/what-exactly-does-xx-tieredcompilation-do

* For local, `-noverify` : « If this is a local application, there is usually no
    need to check the bytecode again. »

* TODO maybe propose a PR in JHipster

## Spring Devtools / hot swapping (watch mode)

* Before all, read https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-devtools.html#using-boot-devtools-livereload

* See also https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-running-your-application.html

* In Spring Boot Enable by default thanks
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>
```

* Trigger recompilation on change

* Do not run simply `mvn` because it automatically trigger `npm install`
    Run instead: `mvn -P\!webpack`

* ~~To trigger hot swapping, `pom.xml` was changed as explained at:
    https://blog.docker.com/2017/05/spring-boot-development-docker/~~

## Known issues with Spring devtools

* See section below « Convlict between Spring devtools and Fuseki Embedded »

* Note that Liquidebase crash too, but don't know if there is a link
    with Spring devtools.

### Why? Threads and Spring devtools

* Actually when we generate a new Thread, it is not destroyed when
    spring devtools restart the app.
    See my thread created in ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/FusekiServerConn.java
    (I print the name of the thread when it is created)
    TODO test with a minimal Spring boot with https://start.spring.io/
    and test if there is this issue. No answer found of StackOverflow .
    Investigate and maybe report to JHipster.

* « SpringBoot applications keeps rebooting all the time (restart loop) - spring.devtools »
    https://stackoverflow.com/questions/45812812/springboot-applications-keeps-rebooting-all-the-time-restart-loop-spring-dev
    TODO post a PR to JHipster

# Notes about IDE with Fuseki and Spring

* To reset IntelliJ Ultimate Edition trial use
    https://gist.github.com/denis111/c3e08bd7c60febc1de8219930a97c2f6 .
    Actually I'm student, therefore no need… But in the Future.

* Actually, IntelliJ community doesn't support Spring boot. Anyway, probably
    could work, as a simple Java Application (as in a Terminal) but no tested.

* IntelliJ and Eclipse does not use `mvn` goals. When you click `build`

* NetBeans is not a very cool IDE. In folder view, folders are fold by default,
    and fold again at each startup. We must unfold each folder of a package
    (e.g fr, then uga, then julioju, then sempic). Even with Vim and NerdTree
    we have not this problem! NetBeans is terrible.

* See my section « ../MAKEFILE.sh » below.

## Eclipse problems

* Problem with `exec-maven-plugin` on startup? (needed by Fuseki)
    See https://www.eclipse.org/m2e/documentation/m2e-execution-not-covered.html
    On the current project, I've changed the pom.xml accordingly.

* Do not forget to install the `Spring Tool Suite` plugin !!!

* When I run with Eclipse, the Console launch lot of errors problems
    with Spring dependencies. Don't know why.
    I've had lot of problems with Eclipse.

* If target is deleted we could have:
    ```
    Error: Could not find or load main class fr.uga.julioju.jhipster.ScholarProjectWebSemanticApp
    Caused by: java.lang.ClassNotFoundException: fr.uga.julioju.jhipster.ScholarProjectWebSemanticApp
    ```
    In this case, run again `./mvn -P \!webpack`

* I've the following stack trace

```
2019-05-10 17:52:45.335 DEBUG 1146 --- [on(1)-127.0.0.1] javax.management.mbeanserver             : Exception calling isInstanceOf

java.lang.ClassNotFoundException: org/springframework/boot/actuate/endpoint/jmx/DataEndpointMBean
	at java.base/java.lang.Class.forName0(Native Method)
	at java.base/java.lang.Class.forName(Class.java:398)
	at java.management/com.sun.jmx.interceptor.DefaultMBeanServerInterceptor.isInstanceOf(DefaultMBeanServerInterceptor.java:1394)
	at java.management/com.sun.jmx.mbeanserver.JmxMBeanServer.isInstanceOf(JmxMBeanServer.java:1091)
	at java.management/javax.management.InstanceOfQueryExp.apply(InstanceOfQueryExp.java:107)
	at java.management/javax.management.OrQueryExp.apply(OrQueryExp.java:97)
	at java.management/javax.management.OrQueryExp.apply(OrQueryExp.java:97)
	at java.management/com.sun.jmx.interceptor.DefaultMBeanServerInterceptor.objectNamesFromFilteredNamedObjects(DefaultMBeanServerInterceptor.java:1496)
	at java.management/com.sun.jmx.interceptor.DefaultMBeanServerInterceptor.queryNamesImpl(DefaultMBeanServerInterceptor.java:560)
	at java.management/com.sun.jmx.interceptor.DefaultMBeanServerInterceptor.queryNames(DefaultMBeanServerInterceptor.java:550)
	at java.management/com.sun.jmx.mbeanserver.JmxMBeanServer.queryNames(JmxMBeanServer.java:619)
	at java.management.rmi/javax.management.remote.rmi.RMIConnectionImpl.doOperation(RMIConnectionImpl.java:1485)
	at java.management.rmi/javax.management.remote.rmi.RMIConnectionImpl$PrivilegedOperation.run(RMIConnectionImpl.java:1307)
	at java.management.rmi/javax.management.remote.rmi.RMIConnectionImpl.doPrivilegedOperation(RMIConnectionImpl.java:1399)
	at java.management.rmi/javax.management.remote.rmi.RMIConnectionImpl.queryNames(RMIConnectionImpl.java:570)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at java.rmi/sun.rmi.server.UnicastServerRef.dispatch(UnicastServerRef.java:359)
	at java.rmi/sun.rmi.transport.Transport$1.run(Transport.java:200)
	at java.rmi/sun.rmi.transport.Transport$1.run(Transport.java:197)
	at java.base/java.security.AccessController.doPrivileged(Native Method)
	at java.rmi/sun.rmi.transport.Transport.serviceCall(Transport.java:196)
	at java.rmi/sun.rmi.transport.tcp.TCPTransport.handleMessages(TCPTransport.java:562)
	at java.rmi/sun.rmi.transport.tcp.TCPTransport$ConnectionHandler.run0(TCPTransport.java:796)
	at java.rmi/sun.rmi.transport.tcp.TCPTransport$ConnectionHandler.lambda$run$0(TCPTransport.java:677)
	at java.base/java.security.AccessController.doPrivileged(Native Method)
	at java.rmi/sun.rmi.transport.tcp.TCPTransport$ConnectionHandler.run(TCPTransport.java:676)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
	at java.base/java.lang.Thread.run(Thread.java:834)

2019-05-10 17:52:45.335 DEBUG 1146 --- [on(1)-127.0.0.1] javax.management.mbeanserver             : Exception calling isInstanceOf

java.lang.ClassNotFoundException: org/springframework/context/support/LiveBeansView
	at java.base/java.lang.Class.forName0(Native Method)
	at java.base/java.lang.Class.forName(Class.java:398)
	at java.management/com.sun.jmx.interceptor.DefaultMBeanServerInterceptor.isInstanceOf(DefaultMBeanServerInterceptor.java:1394)
	at java.management/com.sun.jmx.mbeanserver.JmxMBeanServer.isInstanceOf(JmxMBeanServer.java:1091)
	at java.management/javax.management.InstanceOfQueryExp.apply(InstanceOfQueryExp.java:107)
	at java.management/javax.management.OrQueryExp.apply(OrQueryExp.java:97)
	at java.management/com.sun.jmx.interceptor.DefaultMBeanServerInterceptor.objectNamesFromFilteredNamedObjects(DefaultMBeanServerInterceptor.java:1496)
	at java.management/com.sun.jmx.interceptor.DefaultMBeanServerInterceptor.queryNamesImpl(DefaultMBeanServerInterceptor.java:560)
	at java.management/com.sun.jmx.interceptor.DefaultMBeanServerInterceptor.queryNames(DefaultMBeanServerInterceptor.java:550)
	at java.management/com.sun.jmx.mbeanserver.JmxMBeanServer.queryNames(JmxMBeanServer.java:619)
	at java.management.rmi/javax.management.remote.rmi.RMIConnectionImpl.doOperation(RMIConnectionImpl.java:1485)
	at java.management.rmi/javax.management.remote.rmi.RMIConnectionImpl$PrivilegedOperation.run(RMIConnectionImpl.java:1307)
	at java.management.rmi/javax.management.remote.rmi.RMIConnectionImpl.doPrivilegedOperation(RMIConnectionImpl.java:1399)
	at java.management.rmi/javax.management.remote.rmi.RMIConnectionImpl.queryNames(RMIConnectionImpl.java:570)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at java.rmi/sun.rmi.server.UnicastServerRef.dispatch(UnicastServerRef.java:359)
	at java.rmi/sun.rmi.transport.Transport$1.run(Transport.java:200)
	at java.rmi/sun.rmi.transport.Transport$1.run(Transport.java:197)
	at java.base/java.security.AccessController.doPrivileged(Native Method)
	at java.rmi/sun.rmi.transport.Transport.serviceCall(Transport.java:196)
	at java.rmi/sun.rmi.transport.tcp.TCPTransport.handleMessages(TCPTransport.java:562)
	at java.rmi/sun.rmi.transport.tcp.TCPTransport$ConnectionHandler.run0(TCPTransport.java:796)
	at java.rmi/sun.rmi.transport.tcp.TCPTransport$ConnectionHandler.lambda$run$0(TCPTransport.java:677)
	at java.base/java.security.AccessController.doPrivileged(Native Method)
	at java.rmi/sun.rmi.transport.tcp.TCPTransport$ConnectionHandler.run(TCPTransport.java:676)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
	at java.base/java.lang.Thread.run(Thread.java:834)
```
# Conflict between Spring devtools and Fuseki embedded

* On reload, when FusekiServer was started, HTTP port 3030 is not released.
    It's a very big problem.

* Caused by the fact that Fuseki thread stay between spring devtool restart

* Furthermore, the object link to the FusekiServer is garbaged.

## Why I've passed so long time on this bug

* I used Spring for famous reasons (see above (why jhipster?))

* Spring / Spring-boot is very more  famous than Java EE and its successor Jakarta EE
    See https://insights.stackoverflow.com/trends?tags=spring,spring-boot,java-ee

* The current version of Java EE, Jakarta EE is not referenced by
    StackOverflow

* Oracle has deprecated Java EE.
    It a project in End of Life (still maintained
    some times).
    * Java EE 8 takes too much  times to be released and
        already when it was released it was not at "the state of the art"
        (lack lot of new famous tech like MicroServices, etc.).

* Its successor owned by Eclipse (Jakarta EE) is very new, and we
    don't know about its future. Could success or not, and we don't know
    actually what will be its future.
    * See https://dzone.com/articles/a-personal-opinion-on-the-future-of-jakarta-ee
    * https://www.eclipse.org/community/eclipse_newsletter/2018/may/noturningback.php
    * More precisions about Jakarta vs Java EE
        https://www.baeldung.com/java-enterprise-evolution

* To much time to switch to another solution

* Even IDE should works with `mvn`, they don't execute mvn goals needed by
    Fuseki (see section « Notes about IDE » above).

* Each time I thought the solution should be quick to implement.

* I've learnt lot of think about Spring (the only Java Framework used in
    Enterprise)and Java, it was so interesting…

* I've improved my level on JHipster (maybe so cool for a futur job).

* As bug come in the middle of a request, can't simply restart Fuseki.


## How to Fuseki embedded

* org.apache.jena.fuseki.main.FusekiServer.html

* To start an embedded Fuseki see
    * https://jena.apache.org/documentation/fuseki2/fuseki-main
    * https://jena.apache.org/documentation/fuseki2/fuseki-run.html
    * https://github.com/apache/jena/blob/master/jena-rdfconnection/src/main/java/org/apache/jena/rdfconnection/examples/RDFConnectionExample6.java
    * My example into ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/FusekiServerConn.java and API Doc.

## @PreDestroy

* `@PreDestroy` is never called. I've tested several solutions,
    * `Runtime.getRuntime().addShutdownHook(` https://stackoverflow.com/questions/16373276/predestroy-method-of-a-spring-singleton-bean-not-called
        But don't know how it could work.
    I I've tested to use also a `spring-config.xml` to manage bean thanks
    `appContext.registerShutdownHook()` without success
    I've never success to instantiate the Bean thanks the corresponding xml file.
    Anyway, I believe it's not a good solution.
    * See also https://www.concretepage.com/spring/registershutdownhook_spring
    * See more informations about bean cycle in Spring at
        https://www.baeldung.com/spring-shutdown-callbacks
    * Maybe, I should have use ApplicationWebXml.java to instantiate context
        (not tested)

## Reload instead of Restart

* See https://github.com/jhipster/generator-jhipster/issues/6573
    and https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-devtools.html#using-boot-devtools-restart

### DCEVM

* Use dcevm, originally developped by France Telecom and ***INRIA***

* See https://github.com/TravaOpenJDK/trava-jdk-11-dcevm

* Read also https://phauer.com/2017/increase-jvm-development-productivity/

* Original DCEVM is at https://github.com/dcevm/dcevm/ (without the JDK embedded).

* TODO not works, see https://github.com/jhipster/generator-jhipster/issues/6573

## kill the thread using port

* See ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/FusekiServerConn.java , the commented method `killThreadOnPort3030()`. It works, port is
released, but Fuseki is not started correctly. Maybe because there are
others thread own by FusekiServer.

In this case the TDB2 database
(./scholarProjectWebSemanticFusekiDatabase/run/configuration/sempic.ttl)
is not loaded. Don't know why. TODO why??

## Use loop to restart mvn

* Actually, when port 3030 is not free, the app is killed with error code.
I've tested to use use an infinite loop to restart automatically
`mvn \!webpack` .

I've also tested with `inotify` to trigger automatically `$ kill $(pgrep -P $$)`
(kill all child the bash shell) then restart mvn, but inotify complains. I've
watched only `src/main/java/sempic` `src/main/java/jhipster/SempicRest` folders,
but it complains inotify is very slow when Maven start. Don't know why, no
search further. Maybe there are others watchers, with Node for instance, but
not tested further and no investigated. It's not interesting.

In any case, restart totally `mvn`
take a long time. Therefore solutions discussed in this section are not good.

A major problem with Spring devtools it's that it restarts the app often
even if nothing is saved (see my TODO at the end of this doc).

## Reflexion about FusekiServer embedded for each request (never used)

Any solution is to change scope of `FusekiServerConn`. Change it to have a life
not more than a request, and add a `@PreDestroy` hook to stop Fusek or
instantiate and stop manually FusekiServerConn in each method of
./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/jhipster/SempicRest

As it we could use spring-boot devtools without have an outdated
FusekiServer java thread already alive.

But I think it's not the best. As I say in section above
(Fuseki serious troubleshooting), it takes time
to start and stop Fuseki. And we could not send severals requests in
some short lapse of time. For only development considerations, it's not a viable
solution.

But in other hand, it could be cool because as it we reduce problems of
`InterruptedException` if two incompatibles SPARQL query are sent.

## A solution: ../MAKEFILE.sh

***Section outdated (see below, anyway FusekiServer embedded is buggy)***

* Disable devtools (comment or remove it in pom.xml)

* Configure your IDE or Editor to build automatically one save.

* If your Editor has no good build process
    (like Eclipse, see my notes about Eclipse below) use ./MAKEFILE.sh

* ./MAKEFILE.sh is a result of copy and past from the Build's Console of IntelliJ.
    In IntelliJ, when you click to « Build », a new Console appears. Copy and
    past the first line.

* This MAKEFILE.sh is very more quick than `mvn`. Take around 8 secondes.
    A `mvn -P \-webpack` takes near 30 secondes!!!!!
    And `rm -Rf target && mvn` takes near 1 minute! So so so so long !

* Warning, with this solution, mvn goals are not used, therefore
    ./scholarProjectWebSemantic/target/generated-sources/java/fr/uga/miashs/sempic/model/rdf/SempicOnto.java is not generated.

* ***With NeoVim** use the following macro***:
    ```vim
nmap <F3> 1gt<C-w>j:bd!<CR>:sp enew<CR>:call termopen("bash ../MAKEFILE.sh")<CR>:sleep 4000ms<CR>a<C-\><C-n><C-w>ka | tnoremap <F3> <C-\><C-n>1gt<C-w>j:bd!<CR>:sp enew<CR>:call termopen("bash ../MAKEFILE.sh")<CR>:sleep 4000ms<CR>a<C-\><C-n><C-w>ka
    ```
* As explained below (in section Eclipse), do not delete
    `./scholarProjectWebSemantic/target`
    even only `./scholarProjectWebSemantic/target/classes` .
    In this case, run again `./mvn -P \!webpack`

## But FusekiServer doesn't seem to work with Spring / JHipster

In ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/FusekiServerConn.java in the method `serverRestart()`, as FusekiServer has no callback to say when
it is ready, I've added a delay between `FusekiServer.stop()` and
`FusekiServer.start()`. Actually it is of 10 secondes, probably too much.

* ***I've also noticed than the command line taken in IntelliJ***
    and past in MAKEFILE.sh restart the app when we save, but without compile
    again. So strange. Not investigated further.
    Probably because spring-devtools was append in the classpath by IntelliJ
    See also https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/devtools/restart/Restarter.html

* ***I've also noticed than in context of JHipster, the embedded server
    Fuseki print lot of error stack trace about it's own server Jetty***
    In this case, nothing could work and we must restart the server.

The stack trace is:
```
java.nio.channels.CancelledKeyException: null
        at java.base/sun.nio.ch.SelectionKeyImpl.ensureValid(SelectionKeyImpl.java:71)
        at java.base/sun.nio.ch.SelectionKeyImpl.readyOps(SelectionKeyImpl.java:130)
        at org.eclipse.jetty.io.ManagedSelector.safeReadyOps(ManagedSelector.java:294)
        at org.eclipse.jetty.io.ChannelEndPoint.toEndPointString(ChannelEndPoint.java:433)
        at org.eclipse.jetty.io.AbstractEndPoint.toString(AbstractEndPoint.java:447)
        at java.base/java.util.Formatter$FormatSpecifier.printString(Formatter.java:3031)
        at java.base/java.util.Formatter$FormatSpecifier.print(Formatter.java:2908)
        at java.base/java.util.Formatter.format(Formatter.java:2673)
        at java.base/java.util.Formatter.format(Formatter.java:2609)
        at java.base/java.lang.String.format(String.java:2897)
        at org.eclipse.jetty.io.AbstractConnection.toString(AbstractConnection.java:290)
        at org.slf4j.helpers.MessageFormatter.safeObjectAppend(MessageFormatter.java:299)
        at org.slf4j.helpers.MessageFormatter.deeplyAppendParameter(MessageFormatter.java:271)
        at org.slf4j.helpers.MessageFormatter.arrayFormat(MessageFormatter.java:233)
        at org.slf4j.helpers.MessageFormatter.arrayFormat(MessageFormatter.java:173)
        at org.eclipse.jetty.util.log.JettyAwareLogger.log(JettyAwareLogger.java:680)
        at org.eclipse.jetty.util.log.JettyAwareLogger.debug(JettyAwareLogger.java:224)
        at org.eclipse.jetty.util.log.Slf4jLog.debug(Slf4jLog.java:97)
        at org.eclipse.jetty.io.AbstractConnection.onClose(AbstractConnection.java:221)
        at org.eclipse.jetty.server.HttpConnection.onClose(HttpConnection.java:520)
        at org.eclipse.jetty.io.SelectorManager.connectionClosed(SelectorManager.java:345)
        at org.eclipse.jetty.io.ManagedSelector$DestroyEndPoint.run(ManagedSelector.java:958)
        at org.eclipse.jetty.util.thread.QueuedThreadPool.runJob(QueuedThreadPool.java:765)
        at org.eclipse.jetty.util.thread.QueuedThreadPool$2.run(QueuedThreadPool.java:683)
        at java.base/java.lang.Thread.run(Thread.java:834)
```

* **Furthermore** it doesn't support too much `FusekiServer.start()` and
    `FusekiSer.stop()`. Finish by doesn't work without
    stack trace. Doesn't investigated more.


# Others bugs and TODO

* I've noticed a time than my code stop to work, even if I `kill -9` all java
then restart server. Only restart the computer solve my issue.

* Factorize
    ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/RDFStore.java ,
    (creation of Query could be factorize), but actually I not factorize to
    keep an example.

* Maybe RDFConnect could become global to all application as the teacher as done
    in its example, but contrary to the tutorials (see above, I've added
    explanations into parenthesis)

* See all TODO

* Manage several albums photos thanks the Ressource in sempic.owl `AlbumPhoto`

* When we start the app and send the first POST request we have

```
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by com.fasterxml.jackson.module.afterburner.util.MyClassLoader (file:/media/data/home/m2/repository/com/fasterxml/jackson/module/jackson-module-afterburner/
2.9.8/jackson-module-afterburner-2.9.8.jar) to method java.lang.ClassLoader.defineClass(java.lang.String,byte[],int,int)
WARNING: Please consider reporting this to the maintainers of com.fasterxml.jackson.module.afterburner.util.MyClassLoader
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
```

## TODO very important for teacher

1. Under ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/jhipster/SempicRest/PhotoRDFResource.java
simplify in twice place
```java
    PhotoRDF photoRDF = ReadPhoto.getPhotoById(id);
    AlbumRDF albumRDF = ReadAlbum.readAlbum(photoRDF.getAlbumId());
    ReadAlbum.testUserLoggedPermissions(albumRDF);
```
It should be have only one SPARQL request

5. On the official doc https://jena.apache.org/tutorials/rdf_api.html#ch-Containers
    they speaks of « Collection »
    Contradiction with https://www.w3.org/2007/02/turtle/primer/#L2986
    TODO send an e-mail to ask to correct this mistake or create a PR

5. In the Atencias lesson, don't (see its PDF about RDF)
    don't understand the sentance
    > le comité dans l’ensemble a approuvé
    > la résolu1on : le
    > deuxième choix est meilleur
    (page 63)

6. For me following (page 35 of the course)
```
:FirstYearCourse rdf:type owl:Class ;
                 rdfs:subClassOf [ rdf:type owl:Restriction ;
                                   owl:onProperty :isTaughtBy ;
                                   owl:allValuesFrom :Professor . ] .
```
is the same as say that the domain of :isTaughtBy is :FirstYearCourse
and the range :Professor

7. See question under my section
    « Existential and universal restrictions in tab « Object Property » »

9. The question

diapositive number 72 of the course of Mister Atencias about RDF

On the second paragraph
```
exvoc:resolution1 exvoc:approvedBy exvoc:rulesCommittee.
exvoc:rulesCommittee a rdf:Bag;
 rdf:_1 exvoc:Fred;
 rdf:_2 exvoc:Wilma;
 rdf:_3 exvoc:Dino.
```

The lecture says that « le comité dans l'ensemble a approuvé la résolution ».
Mais pour exprimer des ensembles fermés, je croyais qu'il fallait utiliser des listes ?

L'exemple 2 ne voudrait-il pas plutôt dire : « La résolution a été approuvé par le comité en tant qu'entité. Ce comité est composé entre autre (rdf:Bag) de Fred, Wilma et Dino (tous les membres du comité n'ont peut-être pas approuvé) » ?

Et l'exemple 1
```
exvoc:resolution1 exvoc:approvedBy exvoc:Fred, exvoc:Wilma, exvoc:Dino.
```
signifirait simplement « la résolution 1 a été approuvé par Fred, Wilma et Dino » ?

Conclusion de la diapositive 72: l'exemple 2 est meilleur que l'exemple 1.
Mais si je comprends bien, l'exemple 1 et 2 ne signifient pas la même chose ?
L'un ne peut donc pas être meilleur que l'autre, vu qu'ils n'ont pas la même
signification ?

10. See my question in the section
    « Question about Owlapi and Jena API with Openllet »

11. Ask to the teacher if I can publish its explanations.

# Issues posted by me for this project

(Issues posted about roast.vim are not reported here)

1. Missing dependencies in org.sonatype
    https://github.com/bdionne/pellet/issues/48

2. Equivalent sign between classes and object properties not displayed in Protege 5.5 (regression)
    https://github.com/protegeproject/protege/issues/910

3. Add Pellet 2.4
    https://github.com/protegeproject/protege-distribution/issues/21

4. Progege build on ArchLinux
    https://bugs.archlinux.org/task/63080

# Credits

The First version of ./teacherExample/ was created by Jérôme David.
Copied from http://imss-www.upmf-grenoble.fr/~davidjer/javaee/CoursJena/SempicRDF.zip

Note: the files under ./teacherExample was modified a little bit by me, but not
a lot.

* Some files are generated by JHipster Generator. See section
    « Where is my code » above.


> This work was conducted using the Protégé resource, which is supported by
> grant GM10331601 from the National Institute of General Medical Sciences of
> the United States National Institutes of Health.”
> https://protege.stanford.edu/about.php#citing

