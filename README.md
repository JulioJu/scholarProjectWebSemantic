
<!-- vim-markdown-toc GFM -->

* [How to](#how-to)
    * [Teacher example](#teacher-example)
    * [scholarProjectWebSemantic](#scholarprojectwebsemantic)
* [Teacher's instructions](#teachers-instructions)
* [scholarProjectWebSemantic details](#scholarprojectwebsemantic-details)
    * [Jena](#jena)
    * [Hot swapping (watch mode)](#hot-swapping-watch-mode)
    * [Test API without front (resolve authentification problem)](#test-api-without-front-resolve-authentification-problem)
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

# Credits

The First version of ./teacherExample/ was created by Jérôme David.
Copied from http://imss-www.upmf-grenoble.fr/~davidjer/javaee/CoursJena/SempicRDF.zip
