package ohnosequences.bio4j.bundles

import shapeless._
import shapeless.ops.hlist._
import ohnosequences.typesets._
import ohnosequences.statika._
import ohnosequences.statika.aws._
import ohnosequences.statika.ami._
import ohnosequences.bio4j.statika._
import ohnosequences.awstools.s3._
import ohnosequences.awstools.regions._
import com.ohnosequences.bio4j.titan.programs._
import java.io._
import sys.process._

// Universal uniprot raw data bundle, which adds config and depends only on url
abstract class UniprotRawData(url: String) extends RawDataBundle(url) {
  override val dataFolder: File = new File("uniprot")
  // We need to add an xml config file
  override def install[D <: AnyDistribution](d: D): InstallResults = 
    super.install(d) -&- {
    val config = """
      |<uniprot_data>
      |  <keywords>true</keywords>
      |  <interpro>true</interpro>
      |  <pfam>true</pfam>
      |  <citations>true</citations>
      |  <articles>true</articles>
      |  <online_articles>true</online_articles>
      |  <thesis>true</thesis>
      |  <books>true</books>
      |  <submissions>true</submissions>
      |  <patents>true</patents>
      |  <unpublished_observations>true</unpublished_observations>
      |  <comments>true</comments>
      |  <features>true</features>
      |  <reactome>true</reactome>
      |  <isoforms>true</isoforms>
      |  <subcellular_locations>true</subcellular_locations>
      |</uniprot_data>
      |""".stripMargin

    val file = new File(dataFolder, "uniprotData.xml")

    (Seq("echo", config) #> file) ->-
    success(s"UniprotKB configuration was saved to ${file}")
  }
}

// Two particular instances of the raw data for UniprotKB
case object UniprotSwissProtRawData
  extends UniprotRawData("ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/complete/uniprot_sprot.xml.gz")
case object UniprotTrEMBLRawData
  extends UniprotRawData("ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/complete/uniprot_trembl.xml.gz")


case object UniprotSwissProtAPI extends APIBundle(){}
case object UniprotTrEMBLAPI extends APIBundle(){}
case object UniprotKBAPI extends APIBundle(){}

// Universal program
case class UniprotProgram(
  data   : File, // 1. UniprotKB data xml file 
  db     : File, // 2. Bio4j DB folder
  config : File  // 3. Config XML file
) extends ImporterProgram(new ImportUniprotTitan(), Seq(
  data.getAbsolutePath, 
  db.getAbsolutePath, 
  config.getAbsolutePath
))


case object UniprotSwissProtImportedData extends ImportedDataBundle(
  rawData = UniprotSwissProtRawData :~: ∅,
  initDB = EnzymeDBImportedData,
  importDeps = GeneOntologyImportedData :~: ∅
) {
  override def install[D <: AnyDistribution](d: D): InstallResults = {
    UniprotProgram(
      data   = UniprotSwissProtRawData.inDataFolder("uniprot_sprot.xml"),
      db     = dbLocation,
      config = UniprotSwissProtRawData.inDataFolder("uniprotData.xml")
    ).execute ->-
    success(s"Data ${name} is imported to ${dbLocation}")
  }
}

case object UniprotTrEMBLImportedData extends ImportedDataBundle(
  rawData = UniprotTrEMBLRawData :~: ∅,
  initDB = EnzymeDBImportedData,
  importDeps = GeneOntologyImportedData :~: ∅
) {
  override def install[D <: AnyDistribution](d: D): InstallResults = {
    UniprotProgram(
      data   = UniprotTrEMBLRawData.inDataFolder("uniprot_trembl.xml"),
      db     = dbLocation,
      config = UniprotTrEMBLRawData.inDataFolder("uniprotData.xml")
    ).execute ->-
    success(s"Data ${name} is imported to ${dbLocation}")
  }
}

/* Both things together: */
case object UniprotKBImportedData extends ImportedDataBundle(
  initDB = UniprotSwissProtImportedData,
  importDeps = UniprotTrEMBLImportedData :~: ∅
)


case object UniprotSwissProtModule extends ModuleBundle(UniprotSwissProtAPI, UniprotSwissProtImportedData)
case object UniprotTrEMBLModule    extends ModuleBundle(UniprotTrEMBLAPI,    UniprotTrEMBLImportedData)
case object UniprotKBModule        extends ModuleBundle(UniprotKBAPI,        UniprotKBImportedData)


case object UniprotKBMetadata extends generated.metadata.UniprotkbModule()

case object UniprotSwissProtRelease extends ReleaseBundle(
  ObjectAddress("bio4j.releases", 
                "uniprot_swissprot/v" + UniprotKBMetadata.version.stripSuffix("-SNAPSHOT")), 
  UniprotSwissProtModule
)
case object UniprotTrEMBLRelease extends ReleaseBundle(
  ObjectAddress("bio4j.releases", 
                "uniprot_trembl/v" + UniprotKBMetadata.version.stripSuffix("-SNAPSHOT")), 
  UniprotTrEMBLModule
)
case object UniprotKBRelease extends ReleaseBundle(
  ObjectAddress("bio4j.releases", 
                "uniprotkb/v" + UniprotKBMetadata.version.stripSuffix("-SNAPSHOT")), 
  UniprotKBModule
)

case object UniprotSwissProtDistribution extends DistributionBundle(
  UniprotSwissProtRelease,
  destPrefix = new File("/media/ephemeral0/")
)
case object UniprotTrEMBLDistribution extends DistributionBundle(
  UniprotTrEMBLRelease,
  destPrefix = new File("/media/ephemeral0/")
)
case object UniprotKBDistribution extends DistributionBundle(
  UniprotKBRelease,
  destPrefix = new File("/media/ephemeral0/")
)

