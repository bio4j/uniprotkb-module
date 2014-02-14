## UniprotKB Bio4j module

This is a Bio4j module consisting of three parts:

- `UniprotSwissprot`
- `UniprotTrEMBL`
- `UniprotKB` which just combines the other two

Find more information in [bio4j/modules](https://github.com/bio4j/modules).

## Usage

To use it in you sbt-project, add this to you `build.sbt`:

```scala
resolvers += "Era7 maven releases" at "http://releases.era7.com.s3.amazonaws.com"

libraryDependencies += "bio4j" %% "uniprotkb-module" % "0.1.0"
```
