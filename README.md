## UniprotKB Bio4j module

### About UniprotKB

> The UniProt Knowledgebase (UniProtKB) is the central hub for the collection of functional information on proteins, with accurate, consistent and rich annotation. In addition to capturing the core data mandatory for each UniProtKB entry (mainly, the amino acid sequence, protein name or description, taxonomic data and citation information), as much annotation information as possible is added. This includes widely accepted biological ontologies, classifications and cross-references, and clear indications of the quality of annotation in the form of evidence attribution of experimental and computational data.
>
> The UniProt Knowledgebase consists of two sections: a section containing manually-annotated records with information extracted from literature and curator-evaluated computational analysis, and a section with computationally analyzed records that await full manual annotation. For the sake of continuity and name recognition, the two sections are referred to as "UniProtKB/Swiss-Prot" (reviewed, manually annotated) and "UniProtKB/TrEMBL" (unreviewed, automatically annotated), respectively.

(from the [official website](http://www.uniprot.org/help/uniprotkb))

#### What are the differences between UniProtKB/Swiss-Prot and UniProtKB/TrEMBL?

> UniProtKB/TrEMBL (unreviewed) contains protein sequences associated with computationally generated annotation and large-scale functional characterization. UniProtKB/Swiss-Prot (reviewed) is a high quality manually annotated and non-redundant protein sequence database, which brings together experimental results, computed features and scientific conclusions.


### Modules

So we represent these parts as three modules:

- `UniprotSwissprot` (reviewed)
- `UniprotTrEMBL` (unreviewed)
- `UniprotKB` which just combines the other two

Find more information about modules in [bio4j/modules](https://github.com/bio4j/modules).


## Usage

To use it in you sbt-project, add this to you `build.sbt`:

```scala
resolvers += "Era7 maven releases" at "http://releases.era7.com.s3.amazonaws.com"

libraryDependencies += "bio4j" %% "uniprotkb-module" % "0.1.0"
```
