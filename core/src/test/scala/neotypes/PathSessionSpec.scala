package neotypes

import neotypes.implicits.mappers.results._
import neotypes.implicits.syntax.string._
import org.neo4j.driver.v1.types.{Node, Relationship}
import shapeless._

import scala.concurrent.Future

class PathSessionSpec extends BaseIntegrationSpec[Future] {
  import PathSessionSpec.Person

  it should "map path to Seq" in execute { s =>
    for {
      path <- "match path=(:Person)-[*]->() return path".query[types.Path[Node, Relationship]].list(s)
      pathHList <- "match path=(p:Person)-[*]->() return p, path limit 1".query[Person :: types.Path[Node, Relationship] :: HNil].single(s)
    } yield {
      assert(path.size == 2)
      assert(pathHList.head == Person("Charlize Theron"))
      assert(pathHList.last.nodes.size == 2)
    }
  }

  override val initQuery: String = BaseIntegrationSpec.DEFAULT_INIT_QUERY
}

object PathSessionSpec {
  final case class Person(name: String)
}
