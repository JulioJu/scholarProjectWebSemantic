
<!-- vim-markdown-toc GFM -->

* [How to](#how-to)
    * [Teacher example](#teacher-example)
    * [scholarProjectWebSemantic](#scholarprojectwebsemantic)
* [Teacher's instructions](#teachers-instructions)
* [scholarProjectWebSemantic details](#scholarprojectwebsemantic-details)
    * [Where is my code](#where-is-my-code)
    * [Jena](#jena)
    * [Hot swapping (watch mode)](#hot-swapping-watch-mode)
    * [Test API without front (resolve authentification problem)](#test-api-without-front-resolve-authentification-problem)
* [Construct a Jena Query](#construct-a-jena-query)
    * [Sparql syntax](#sparql-syntax)
    * [Java API 1) syntax build](#java-api-1-syntax-build)
    * [Java API 2) Algebra](#java-api-2-algebra)
    * [Java API, Query](#java-api-query)
    * [See also](#see-also)
* [Jena DELETE](#jena-delete)
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
(see https://www.jhipster.tech/2019/04/04/jhipster-release-6.0.0-beta.0.html ),
but I've tested without success. No search further.

I develop with OpenJDK 11 on Linux.

Note that the current release of Protege software should use Java 8,
see https://github.com/protegeproject/protege/issues/822 .

## scholarProjectWebSemantic

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
mvn
yarn start
```

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

## Jena

To install Jena maven plugin, see pom.xml. Code relative to Jena
has added between comment “`Added by JulioJu`”

Do not forget to run `mvn install`.

See also ./teacherExample/HowToConfigureJenaByJeromeDavid.pdf

## Hot swapping (watch mode)
* Trigger recompilation on change

* Useful to develop with Vim, without any IDE.

* Do not run simply `mvn` because it automatically trigger `npm install`
    Run instead: `mvn -P\!webpack`

* To trigger hot swapping, `pom.xml` was changed as explained at:
    https://blog.docker.com/2017/05/spring-boot-development-docker/
    * See also https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-running-your-application.html
    * TODO create a Pull Request on JHipster

## Test API without front (resolve authentification problem)

Very useful to test an API. Learnt in a [TupperVim](https://tuppervim.org).

See https://github.com/sharat87/roast.vim

Thanks ./rest_request_with_vim.roast we could test API without front, in Vim.
It manages authentifications tokens automatically.

See also my issue at https://github.com/sharat87/roast.vim/issues/4

# Construct a Jena Query

I've shown three ways to construct a Query in
./scholarProjectWebSemantic/src/main/java/fr/uga/julioju/sempic/RDFStore.java ,
under function `readPhoto(long id)`. It exists more solutions.

The most important doc is the ***official doc***
    https://jena.apache.org/documentation/query/manipulating_sparql_using_arq.html

## Sparql syntax

The Sparql syntax is very known and very documented. But it should not be
used in a Java Program as the say at
https://jena.apache.org/documentation/query/manipulating_sparql_using_arq.html

> “But what about [little Bobby Tables](https://xkcd.com/327/)? And, even if you
> sanitise your inputs, string manipulation is a fraught process and syntax
> errors await you. Although it might seem harder than string munging, the ARQ
> API is your friend in the long run.”

See also https://en.wikipedia.org/wiki/SQL_injection

## Java API 1) syntax build

See the official doc presented above and
also:
https://stackoverflow.com/questions/9979931/jena-updatefactory/30370545#30370545

It's a cool solution. And as they say in the official doc.

> “So far there is no obvious advantage in using the algebra.”

But we could see that it's very verbose. Maybe it's a little bit more
understandable, because contrary to Algebra form, we could define
`CONSTRUCT clause` before the `WHERE clause`.

## Java API 2) Algebra


I believe the best solution is to use directly Sparql Algebra.
To understand what is Spqral Algebra,
1. First see official doc presented above
2. See definition of Algebra in official doc at
    https://jena.apache.org/documentation/query/algebra.html
3. See the official example
    https://github.com/apache/jena/blob/master/jena-arq/src-examples/arq/examples/AlgebraExec.java
4. The Power Point
    https://afia.asso.fr/wp-content/uploads/2018/01/Corby_PDIA2017_RechercheSemantique.pdf
    (they explain also what is a BGP, a Basic Graph Pattern).
5. As they said at
    https://www.programcreek.com/java-api-examples/?api=org.apache.jena.sparql.syntax.ElementFilter (https://www.programcreek.com/java-api-examples/?code=xcurator/xcurator/xcurator-master/lib/new_libs/apache-jena-3.1.0/src-examples/arq/examples/propertyfunction/labelSearch.java)
    “The better design is to build the Op structure programmatically,”.
    Note they call `op = Algebra.optimize`, but I didn't explored that.
6. https://www.w3.org/2011/09/SparqlAlgebra/ARQalgebra , a very detailed
    explanation (maybe too much).

## Java API, Query

Even if we use directly Algebra, as:

> Notice that the query form (SELECT, CONSTRUCT, DESCRIBE, ASK) isn't
> part of the algebra, and we have to set this in the query (although
> SELECT is the default). FROM and FROM NAMED are similarly absent.
> https://jena.apache.org/documentation/query/manipulating_sparql_using_arq.html

You should see https://jena.apache.org/documentation/query/app_api.html

## See also

* As **I don't use all examples of the teacher**, you must study
    ./teacherExample/src/main/java/fr/uga/miashs/sempic/rdf/RDFStore.java

* To understand how it works, don't forget that
    1. `FrontsNode <-- RDFNode <-- Resource <-- Property` (inheritance).
    2. `FrontsNode` has a `Node` Property (`protected`), and therefore
        has a public method `Node.asNode()`

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

# Jena DELETE

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


# Credits

The First version of ./teacherExample/ was created by Jérôme David.
Copied from http://imss-www.upmf-grenoble.fr/~davidjer/javaee/CoursJena/SempicRDF.zip

Note: the files under ./teacherExample was modified a little bit by me, but not
a lot.
