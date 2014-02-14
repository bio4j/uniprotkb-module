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
case object UniprotSwissProt
  extends UniprotRawData("ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/complete/uniprot_sprot.xml.gz")
case object UniprotTrEMBL 
  extends UniprotRawData("ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/complete/uniprot_trembl.xml.gz")

  
case object UniprotKBRawData 
  extends RawDataBundle(???)

case object UniprotKBAPI extends APIBundle(){}

case class UniprotKBProgram(
  ???
) extends ImporterProgram(new ImportUniprotKBTitan(), Seq(
  ???
))

case object UniprotKBImportedData extends ImportedDataBundle(
    rawData = UniprotKBRawData :~: ∅,
    initDB = InitialBio4j,
    importDeps = ∅
  ) {
  override def install[D <: AnyDistribution](d: D): InstallResults = {
    UniprotKBProgram(
      ???
    ).execute ->-
    success("Data " + name + " is imported to" + dbLocation)
  }
}

case object UniprotKBModule extends ModuleBundle(UniprotKBAPI, UniprotKBImportedData)

case object UniprotKBMetadata extends generated.metadata.UniprotkbModule()

case object UniprotKBRelease extends ReleaseBundle(
  ObjectAddress("bio4j.releases", 
                "uniprotkb/v" + UniprotKBMetadata.version.stripSuffix("-SNAPSHOT")), 
  UniprotKBModule
)

case object UniprotKBDistribution extends DistributionBundle(
  UniprotKBRelease,
  destPrefix = new File("/media/ephemeral0/")
)

