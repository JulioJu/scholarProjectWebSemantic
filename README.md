
<!-- vim-markdown-toc GFM -->

* [How to](#how-to)
    * [Teacher example](#teacher-example)
    * [scholarProjectWebSemantic](#scholarprojectwebsemantic)
        * [Fuseki embedded](#fuseki-embedded)
        * [Fuseki non embedded](#fuseki-non-embedded)
        * [API implemented](#api-implemented)
            * [The Vim Plugin roast.vim](#the-vim-plugin-roastvim)
            * [Fuseki Administration](#fuseki-administration)
            * [Wireshark](#wireshark)
        * [Teacher's instructions](#teachers-instructions)
* [scholarProjectWebSemantic details](#scholarprojectwebsemantic-details)
    * [Where is my code](#where-is-my-code)
    * [Why JHipster](#why-jhipster)
* [Jena](#jena)
    * [Jena doc](#jena-doc)
    * [sempic.ttl](#sempicttl)
    * [How to install Jena](#how-to-install-jena)
    * [Construct a Jena Query](#construct-a-jena-query)
        * [Sparql syntax](#sparql-syntax)
        * [Java API 1) syntax form of the query](#java-api-1-syntax-form-of-the-query)
        * [Java API 2) Algebra form of the query](#java-api-2-algebra-form-of-the-query)
        * [Java API, Query](#java-api-query)
        * [See also](#see-also)
        * [Jena DELETE](#jena-delete)
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
    * [StackTrace « Iterator used inside a different transaction »](#stacktrace-iterator-used-inside-a-different-transaction)
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

Note that the current release of Protege software should use Java 8,
see https://github.com/protegeproject/protege/issues/822 .

## scholarProjectWebSemantic

### Fuseki embedded
***Section outdated, use Fuseki non embedded. See why in a section below.***

**Note that it's still implemented. But do not use it.**.

`$ mvn -P \!webpack -Dspring-boot.run.arguments="fusekiServerEmbedded"`

### Fuseki non embedded

***Section outdated. Actually Fuseki is managed completly by my App, but
launch in an independant process.***

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


### API implemented

Server API are tested thanks ./rest_request_with_vim.roast
All API implemented are described in this file.

***All API are protected against SQL injection.***

#### The Vim Plugin roast.vim

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

#### Fuseki Administration

* If you don't want use the app, you could use Fuseki administration at
    `localhost:3000`.

####  Wireshark

Some API send only HTTP code.

To trace call, use Wireshark.

Start Wireshark with `gksudo wireshark & ; disown %1`

1.  ...using this filter `tcp port 3030`

2. Use interface `Loopback:lo`

3. `menu -> capture -> start`

4. In « Apply a display filter », type `http`

Note: verify the tcpdump is ordered by `N°`.

### Teacher's instructions

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


# Jena

## Jena doc

* Note that Fuseki documentation has several break links. Use Google to
    search pages pointed by dead links.

* For Java documentation, to see in wish project of Jena is
    (e.g. ARQ, Fuseki, Core, etc.) check the start of URL.

* All JavaDoc for all projects are at https://jena.apache.org/documentation/javadoc/

## sempic.ttl
* To understand the file `sempic.ttl` give by the teacher, read

> The TDB2 database can be in a configuration file, either a complete server
> configuration (see below) or as an entry in the FUSEKI_BASE/configuration/
> area of the full server.
https://jena.apache.org/documentation/tdb2/tdb2_fuseki.html

## How to install Jena

See ./teacherExample/HowToConfigureJenaByJeromeDavid.pdf


## Construct a Jena Query

I've shown three ways to construct a Query in
./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/ReadPhoto.java
under function `readPhoto(long id)`. It exists more solutions.
See also `subClassOf(String classUri)`, more simple example. See also all this
file. This file is not factorize, my goal is keep as an example.

The most important doc is the ***official doc***
    https://jena.apache.org/documentation/query/manipulating_sparql_using_arq.html

### Sparql syntax

The Sparql syntax is very known and very documented. But it's better
to not in a Java Program

> “But what about [little Bobby Tables](https://xkcd.com/327/)? And, even if you
> sanitise your inputs, string manipulation is a fraught process and syntax
> errors await you. Although it might seem harder than string munging, the ARQ
> API is your friend in the long run.”
> (source:
> https://jena.apache.org/documentation/query/manipulating_sparql_using_arq.html )

See also https://en.wikipedia.org/wiki/SQL_injection

* But as it's not a standard, if we change of implementation it
  could be a little more bit hard.

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
    * https://www.fun-mooc.fr/c4x/inria/41002S02/asset/C013FG-W5.pdf
    * ***The MOOC from INRIA*** (in french)
        https://www.fun-mooc.fr/courses/inria/41002S02/session02/about

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

### Jena DELETE

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

# Fuseki serious troubleshooting

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

## Solution: restart Fuseki Server

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

## StackTrace « Iterator used inside a different transaction »
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

* The current version of Java EE, Jakarto EE is not referenced by
    StackOverflow

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

## But FusekiServer is totally buggy

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

# Credits

The First version of ./teacherExample/ was created by Jérôme David.
Copied from http://imss-www.upmf-grenoble.fr/~davidjer/javaee/CoursJena/SempicRDF.zip

Note: the files under ./teacherExample was modified a little bit by me, but not
a lot.
