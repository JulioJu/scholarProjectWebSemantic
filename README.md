
<!-- vim-markdown-toc GFM -->

* [How to](#how-to)
    * [Teacher example](#teacher-example)
    * [scholarProjectWebSemantic](#scholarprojectwebsemantic)
        * [Fuseki embedded](#fuseki-embedded)
        * [Fuseki non embedded](#fuseki-non-embedded)
    * [Jena doc](#jena-doc)
* [Teacher's instructions](#teachers-instructions)
* [scholarProjectWebSemantic details](#scholarprojectwebsemantic-details)
    * [Where is my code](#where-is-my-code)
    * [Implementation notes](#implementation-notes)
    * [Test API without front (resolve authentification problem)](#test-api-without-front-resolve-authentification-problem)
    * [Jena](#jena)
    * [Spring Dev tools and Hot swapping (watch mode)](#spring-dev-tools-and-hot-swapping-watch-mode)
        * [Start quickly Spring boot](#start-quickly-spring-boot)
        * [Conflict with org.apache.jena.fuseki.main.FusekiServer.html](#conflict-with-orgapachejenafusekimainfusekiserverhtml)
            * [Solution](#solution)
                * [Reload instead of Restart](#reload-instead-of-restart)
                    * [DCEVM](#dcevm)
                * [kill the thread using port](#kill-the-thread-using-port)
                * [Use IntelliJ](#use-intellij)
                * [Use loop to restart mvn](#use-loop-to-restart-mvn)
            * [FusekiServerConn for every request](#fusekiserverconn-for-every-request)
    * [Construct a Jena Query](#construct-a-jena-query)
        * [Sparql syntax](#sparql-syntax)
        * [Java API 1) syntax form of the query](#java-api-1-syntax-form-of-the-query)
        * [Java API 2) Algebra form of the query](#java-api-2-algebra-form-of-the-query)
        * [Java API, Query](#java-api-query)
        * [See also](#see-also)
    * [Jena DELETE](#jena-delete)
* [Fuseki non solvable serious troubleshooting](#fuseki-non-solvable-serious-troubleshooting)
    * [How to activate log](#how-to-activate-log)
    * [See also](#see-also-1)
    * [Limitations](#limitations)
* [Eclipse problems](#eclipse-problems)
* [Notes about Spring and Java EE](#notes-about-spring-and-java-ee)
    * [Others bugs with Fuseki](#others-bugs-with-fuseki)
* [Others bugs and TODO](#others-bugs-and-todo)
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

I develop with OpenJDK 11 on Linux.

Note that the current release of Protege software should use Java 8,
see https://github.com/protegeproject/protege/issues/822 .

## scholarProjectWebSemantic

### Fuseki embedded

`$ mvn -P \!webpack -Dspring-boot.run.arguments="fusekiServerEmbedded"`

### Fuseki non embedded

Start the Fuseki server ***at path ./scholarProjectWebSemanticFusekiDatabase/***.

On Arch Linux, with the official https://aur.archlinux.org/packages/apache-jena-fuseki/
run simply:

```sh
cd ./scholarProjectWebSemanticFusekiDatabase/
fuseki-server
```

To run the app, run:
```sh
cd ./scholarProjectWebSemantic/
$ mvn -P \!webpack -Dspring-boot.run.arguments="fusekiServerNoEmbedded"
yarn start
```

To run server side in one line:
```sh
$ pushd ../scholarProjectWebSemanticFusekiDatabase && fuseki-server > /dev/null 2> /dev/null & ; popd &&  rm -Rf target && mvn -P \!webpack
```

* Important notes functions of:
    ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/FusekiServerConn.java
    are `return;` before any action.


## Jena doc


* Note that Fuseki documentation has several break links. Use Google to
    search pages pointed by dead links.

* For Java documentation, to see in wish project of Jena is
    (e.g. ARQ, Fuseki, Core, etc.) check the start of URL.

* All JavaDoc for all projects are at https://jena.apache.org/documentation/javadoc/

* To understand the file `sempic.ttl` give by the teacher, read

> The TDB2 database can be in a configuration file, either a complete server
> configuration (see below) or as an entry in the FUSEKI_BASE/configuration/
> area of the full server.
https://jena.apache.org/documentation/tdb2/tdb2_fuseki.html

# Teacher's instructions

See ./TeachersInstruction1.pdf

See ./TeachersInstruction2.pdf

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

## Implementation notes

Server API are tested thanks ./rest_request_with_vim.roast (see below).

***All API are protected against SQL injection.***

## Test API without front (resolve authentification problem)

Very useful to test an API. Learnt in a [TupperVim](https://tuppervim.org).

See https://github.com/sharat87/roast.vim

Thanks ./rest_request_with_vim.roast we could test API without front, in Vim.
It manages authentifications tokens automatically.

See also my issue at https://github.com/sharat87/roast.vim/issues/4

## Jena

To install Jena maven plugin, see pom.xml. Code relative to Jena
has added between comment “`Added by JulioJu`”

Do not forget to run `mvn install`.

See also ./teacherExample/HowToConfigureJenaByJeromeDavid.pdf

## Spring Dev tools and Hot swapping (watch mode)
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

### Start quickly Spring boot

* Add argument `-noverify -XX:TieredStopAtLevel=1` (not investigate more)

* Therefore I've added, but
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

### Conflict with org.apache.jena.fuseki.main.FusekiServer.html

* See also section below about FusekiServer

* On reload, when FusekiServer was started, HTTP port 3030 is not released.
    It's a very big problem.
    I've tested with Eclipse, I have the problem. Furthermore, the object
    link to the FusekiServer is garbaged (it's normal, see below).

* There is no this problem with IntelliJ as IntelliJ kill properly the app
    between each rebuild and no use spring-boot dev tools.

* With Spring-boot dev tools, it seems not have solutions. `@PreDestroy`
    is never called. I've tested several solutions,
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

#### Solution

##### Reload instead of Restart
* See https://github.com/jhipster/generator-jhipster/issues/6573
    and https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-devtools.html#using-boot-devtools-restart

###### DCEVM

* Use dcevm, originally developped by France Telecom and ***INRIA***

* See https://github.com/TravaOpenJDK/trava-jdk-11-dcevm

* Read also https://phauer.com/2017/increase-jvm-development-productivity/

* Original DCEVM is at https://github.com/dcevm/dcevm/ (without the JDK embedded).

* TODO not works, see https://github.com/jhipster/generator-jhipster/issues/6573

##### kill the thread using port

* See ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/FusekiServerConn.java , the commented method `killThreadOnPort3030()`. It works, port is
released, but Fuseki is not started correctly. Don't know why.

In this case the TDB2 database
(./scholarProjectWebSemanticFusekiDatabase/run/configuration/sempic.ttl)
is partially loaded.

Following is not loaded.
```
:sempicdata a tdb2:GraphTDB2 ;
	tdb2:location "./run/databases/sempic-data" .
```

Following is loaded
```
:sempiconto
	a ja:MemoryModel ;
ja:content [
		ja:externalContent "../scholarProjectWebSemantic/src/main/resources/sempiconto.owl"
	].
```

##### Use IntelliJ

* Works perfectly with IntelliJ Ultimate Edition. Port is released.
    To reset IntelliJ Ultimate Edition trial use
    https://gist.github.com/denis111/c3e08bd7c60febc1de8219930a97c2f6 .
    Actually I'm student, therefore no need… But in the Future.

* Actually, IntelliJ community doesn't support Spring boot. Anyway, maybe
    could work, but no tested.

##### Use loop to restart mvn

* Actually, when port 3030 is not free, the app is killed with error code.
In ./MAKEFILE.sh is use an infinite loop to restart automatically
`mvn \!webpack` .

I've also tested with `inotify` to trigger automatically `$ kill $(pgrep -P $$)`
(kill all child the bash shell) then restart mvn, but inotify complains. I've
watched only `src/main/java/sempic` `src/main/java/jhipster/SempicRest` folders,
but it complains inotify is very slow when Maven start. Don't know why, no
search further. Maybe there are others watchers, with Node for instance, but
not tested further and no investigated.

In any case, restart totally `mvn`
take a long time. Therefore solutions discussed in this section are not good.

#### FusekiServerConn for every request

Any solution is to change scope of `FusekiServerConn`. Change it to have a life
not more than a request, and add a `@PreDestroy` hook to stop Fusek or
instantiate and stop manually FusekiServerConn in each method of
./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/jhipster/SempicRest

As it we could use spring-boot devtools without have an outdated
FusekiServer java thread already alive.

But I think it's not the best. As I say in section below
(Fuseki non solvable serious troubleshooting), it takes time
to start and stop Fuseki. And we could not send severals requests in
some short lapse of time. For only development considerations, it's not a viable
solution.

But in other hand, it could be cool because as it we reduce problems of
`InterruptedException` if two incompatibles SPARQL query are sent.

## Construct a Jena Query

I've shown three ways to construct a Query in
./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/RDFStore.java ,
under function `readPhoto(long id)`. It exists more solutions.
See also `subClassOf(String classUri)`, more simple example. See also all this
file. This file is not factorize, my goal is keep as an example.

The most important doc is the ***official doc***
    https://jena.apache.org/documentation/query/manipulating_sparql_using_arq.html

### Sparql syntax

The Sparql syntax is very known and very documented. But it should not be
used in a Java Program

> “But what about [little Bobby Tables](https://xkcd.com/327/)? And, even if you
> sanitise your inputs, string manipulation is a fraught process and syntax
> errors await you. Although it might seem harder than string munging, the ARQ
> API is your friend in the long run.”
> (source:
> https://jena.apache.org/documentation/query/manipulating_sparql_using_arq.html )

See also https://en.wikipedia.org/wiki/SQL_injection

### Java API 1) syntax form of the query

See the official doc presented above and
also:
https://stackoverflow.com/questions/9979931/jena-updatefactory/30370545#30370545

It's a cool solution. And as they say in the official doc.

> “So far there is no obvious advantage in using the algebra.”

But we could see that it's very verbose. Maybe it's a little bit more
understandable, because contrary to Algebra form, we could define
`CONSTRUCT clause` before the `WHERE clause`.

### Java API 2) Algebra form of the query


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

### Java API, Query

Even if we use directly Algebra, as:

> Notice that the query form (SELECT, CONSTRUCT, DESCRIBE, ASK) isn't
> part of the algebra, and we have to set this in the query (although
> SELECT is the default). FROM and FROM NAMED are similarly absent.
> https://jena.apache.org/documentation/query/manipulating_sparql_using_arq.html

You should see https://jena.apache.org/documentation/query/app_api.html

### See also

* As **I don't use all examples of the teacher**, you must study
    ./teacherExample/src/main/java/fr/uga/miashs/sempic/rdf/RDFStore.java

* To understand how it works, don't forget that
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


* As the official doc is a little bit poor of examples, don't forget
    to use https://www.programcreek.com/java-api-examples/
    Maybe use Google, in the search bar type something like:
    `site:https://www.programcreek.com/java-api-examples/ ElementTriplesBlock`


* During my search, I've explored how to build an `OntModel` and `OntResource`
    To understand what it is, see official API Jena and
    https://jena.apache.org/documentation/ontology/
    There is a function `OntResource.remove()`, but as the resource isn't link
    to a `RdfConnection`, if we use `OntResource.commit()` it can' work.

* See also
    https://stackoverflow.com/questions/7250189/how-to-build-sparql-queries-in-java
    They show the “StringBuilder style API for building query/update strings and
    parameterizing them if desired”.

* The Course of M.  Atencias: http://imss-www.upmf-grenoble.fr/~atenciam/WS/
    * Two well understand http://imss-www.upmf-grenoble.fr/~atenciam/WS/5-owl.pdf
        see the Protege software.
    * What is a statement: https://stackoverflow.com/questions/21391135/what-is-the-owlnothing-class-designed-to-do/21391737
    * Nodes in hierarchy:
        * http://soft.vub.ac.be/svn-pub/PlatformKit/platformkit-kb-owlapi3-doc/doc/owlapi3/javadoc/org/semanticweb/owlapi/reasoner/Node.html
        * https://stackoverflow.com/questions/21391135/what-is-the-owlnothing-class-designed-to-do/21391737
        * https://en.wikipedia.org/wiki/Semantic_Web

* See also
    * https://en.wikipedia.org/wiki/Template:Semantic_Web
    * https://www.w3.org/wiki/Good_Ontologies

* Maybe a good model to make a new Ontology is
    https://www.w3.org/Submission/vcard-rdf/
    It's a [member submission](https://www.w3.org/wiki/Good_Ontologies),
    therefore less than a draft!
    But it's a very simple Ontology
    See an example non official of an Ontology :
    http://content.scottstreit.com/Semantic_Web/Assignments/Resources/Protege/protegeLab/original_vCard/vCard.owl
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

* Why the original idea of Tim Beners-Lee is dead.
    * https://hackernoon.com/semantic-web-is-dead-long-live-the-ai-2a5ea0cf6423
    * https://twobithistory.org/2018/05/27/semantic-web.html
    * https://www.forbes.com/sites/cognitiveworld/2018/08/03/the-importance-of-schema-org/

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
    ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/RDFStore.java ,
    the method `deleteResource (Resource r)`.

    As the method `readPhoto(long id)` presented above, there are the SPARQL
    syntax and the Jena Java API.
    To write the method `deleteResource(Rsource r)` with Java API, I was
    inspired by the method `deleteModel(Model m)` of the teacher.

# Fuseki non solvable serious troubleshooting

Use FusekiServer embedded is a workaround to the bug
 bug described at https://mail-archives.us.apache.org/mod_mbox/jena-users/201810.mbox/<51361cde-332c-ffb7-4ba4-b73d43bd4cf5@apache.org>
 (see the full clear mail thread at https://mail-archives.us.apache.org/mod_mbox/jena-users/201810.mbox/thread)
***Iterator used inside a different transaction"***
 > The stack trace looks like you have configured a dataset with some inference.
 > It is possible that the inference layer is not using transactions on the
 > underlying dataset properly, and/or caching some data that is tied to a
 > specific transaction.

* When we we start the app with a Fuseki not embedded
    , the Fuseki server
    show a StackTrace similar to those described
    in the link above.
    (I've disabled the logs of the embedded Fuseki because there are
    too much logs).

As they say, it seams there is a bug under Fuseki Server. If we use an external
Fuseki Server, we can't stop the server before the bug appears. My workaround
is to restart Fuseki server before the bug appears. The seams appears each
time we use `ASK WHERE { <my-uri-one> ?p ?o }` before
`DELETE WHERE { <my-uri-two> ?p ?o } ; DELETE WHERE { ?s ?p <my-uri> }`.
If we use in the other sens, the bug doesn't appears.

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

My workaround is simply restart the embedded Fuseki Server before the bug appears.

See
./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/jhipster/SempicRest/PhotoRDFResource.java

* To start an embedded Fuseki see
    * https://jena.apache.org/documentation/fuseki2/fuseki-main
    * https://jena.apache.org/documentation/fuseki2/fuseki-run.html
    * https://github.com/apache/jena/blob/master/jena-rdfconnection/src/main/java/org/apache/jena/rdfconnection/examples/RDFConnectionExample6.java
    * My example into ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/FusekiServerConn.java and API Doc.

* **I've tested from with Fuseki 3.6, 3.7, 1.9, 1.10, 1.11 without success**.
    Fuseki 1.5 doesn't work with the current Sempic.ttl file, therefore
    can't test with it.

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

* Actually, it seems we cant restart fuseki simply thanks REST API:
    https://jena.apache.org/documentation/fuseki2/fuseki-server-protocol.html

##  How to activate log

Simply uncomment:
```java
        // FusekiLogging.setLogging();
        // (…)
            // .verbose(true)
```

## See also
See also section « Conflict with org.apache.jena.fuseki.main.FusekiServer.html »

## Limitations

Anyway, there is no best solution for this. If there are several incoming
requests, stop then restart Fuseki manually could be break the requests. If
a new request is sent during time of Restart of Request could be problematic.

In this case, we must add a queue to delay request.

Maybe we could add a boolean in ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/RDFConn.java
to say if `FusekiServer` (defined in FusekiServerConn.java)
is on or or off. As it, we not trigger a request
when `FusekiServer` is during a boot.

Before create a new `RDFConnection` in  RDFConn.java, we could test is the
port is taken or free. But don't know if it's cool, because maybe the port
could be taken by Fuseki but in a middle of a reboot process.

In ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/FusekiServerConn.java in the method `serverRestart()`, as FusekiServer has no callback to say when
it is ready, I've added a delay between `FusekiServer.stop()` and
`FusekiServer.start()`. Actually it is of 10 secondes, probably too much.



# Eclipse problems

* Problem with `exec-maven-plugin` on startup?
    See https://www.eclipse.org/m2e/documentation/m2e-execution-not-covered.html
    On the current project, I've changed the pom.xml accordingly.

* Do not forget to install the `Spring Tool Suite` plugin !!!

# Notes about Spring and Java EE

* `@ApplicationScoped` component are note instantiated at the startup of the
    application. They are instatiated the first time one of its method is
    called. I've instantied in
    the main method of
    ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/jhipster/ScholarProjectWebSemanticApp.java

* Constructors are called before `static void main(String[] args)`
    in ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/jhipster/ScholarProjectWebSemanticApp.java

* `@ApplicationScoped` is not very useful. Use simply `static` class properties
    and method.

## Others bugs with Fuseki

* I've noticed a time than my code stop to work, even if I `kill -9` all java
then restart server. Only restart the computer solve my issue.

# Others bugs and TODO

* Factorize
    ./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/RDFStore.java ,
    (creation of Query could be factorize), but actually I not factorize to
    keep an example.


* Actually use JHipster is not very useful. I use it only to manage authetifications
    because I know well how JHipster works. JHIpster should be removed and
    authentification used in an other maneer.

* Actually there are lot of stack trace bugs, especially when we run into
    Eclipse. Not know why, but probably note du to my code.

* Test if in an Normal application, without spring-boot, therefore
    without spring-boot devtools, with Eclipse
    when code is automatically rebuild on change, the FusekiServer thread stay.

* Maybe RDFConnect could become global to all application as the teacher as done
    in its example, but contrary to the tutorials (see above, I've added
    explanations into parenthesis)


# Credits

The First version of ./teacherExample/ was created by Jérôme David.
Copied from http://imss-www.upmf-grenoble.fr/~davidjer/javaee/CoursJena/SempicRDF.zip

Note: the files under ./teacherExample was modified a little bit by me, but not
a lot.
