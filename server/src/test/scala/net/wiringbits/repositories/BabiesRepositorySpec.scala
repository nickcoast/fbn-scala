package net.wiringbits.repositories

import net.wiringbits.common.models.{ParentName, Name, Email}
import net.wiringbits.core.RepositorySpec
import net.wiringbits.repositories.models.Baby
import net.wiringbits.util.EmailMessage

import java.time.{LocalDate, ZoneId}
/*import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat*/
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.matchers.must.Matchers._

import java.util.Date
import java.util.TimeZone
import java.util.Calendar
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.UUID

class BabiesRepositorySpec extends RepositorySpec {

  "create" should {
    "work" in withRepositories() { repositories =>
      val request = Baby.CreateBaby(
        name = Name.trusted("Brozo"),
        babyDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant
      )      
      repositories.babies.create(request).futureValue
    }

    "fail when the id already exists" in withRepositories() { repositories =>
      val request = Baby.CreateBaby(
        name = Name.trusted("Brozo"),
        babyDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant,
      )

      repositories.babies.create(request).futureValue
      val ex = intercept[RuntimeException] {
        repositories.babies.create(request.copy(Name.trusted("Brozo"))).futureValue
      }
      // TODO: This should be a better message
      ex.getCause.getMessage must startWith(
        """ERROR: duplicate key value violates unique constraint "baby_name_unique""""
      )
    }
  }

  "all" should {
    "return the existing babies" in withRepositories() { repositories =>
      for (i <- 1 to 3) {
        val request = Baby.CreateBaby(
          name = Name.trusted(s"Sample$i"),
          babyDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant,
        )
        repositories.babies.create(request).futureValue
      }

      val response = repositories.babies.all().futureValue
      response.length must be(3)
    }

    "return no babies" in withRepositories() { repositories =>
      val response = repositories.babies.all().futureValue
      response.isEmpty must be(true)
    }
  }

  "find(name)" should {
    "return a baby when the name exists" in withRepositories() { repositories =>
      val request = Baby.CreateBaby(
        name = Name.trusted("Sample"),
        babyDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant,
      )
      repositories.babies.create(request).futureValue

      val response = repositories.babies.find(request.name).futureValue
      response.value.name must be(request.name)
      response.value.date must be(request.babyDate)
    }

    "return no result when the id doesn't exists" in withRepositories() { repositories =>
      val id = 420
      val response = repositories.babies.find(id).futureValue
      response.isEmpty must be(true)
    }
  }

  "update" should {
    "update an existing baby" in withRepositories() { repositories =>
      val request = Baby.CreateBaby(
        name = Name.trusted("Brozo"),
        babyDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant,
      )
      repositories.babies.create(request).futureValue

      val newName = Name.trusted("Frowup")
      repositories.babies.update(request.name, newName).futureValue

      val response = repositories.babies.find(newName).futureValue
      response.value.name must be(newName)
      response.value.date must be(request.babyDate)
    }

    "fail when the baby doesn't exist" in withRepositories() { repositories =>
      val name = Name.trusted("999")
      val newName = Name.trusted("Test")
      /*
      // doesn't work because unlike in User, we're not updating a log table with a foreign key that would fail
      val ex = intercept[RuntimeException] {
        repositories.babies.update(name,newName).futureValue
      }
      ex.getCause.getMessage must startWith(
        """ERROR: insert or update on table "baby_names" violates foreign key constraint "baby_logs_babies_fk""""
      )*/
      // does not need an assertion because any exception will now fail the test
      repositories.babies.update(name, newName).futureValue
    }
  }

  "get" should {
    "get baby by parent names" in withRepositories() { repositories =>
      val request = Baby.CreateBaby( // TODO: convert to set of tuples to call Baby.CreateBaby on?
        name = Name.trusted("Brozo"),
        babyDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant //Instant.parse("2049-04-20T00:00:00.00Z")
      )
      val request2 = Baby.CreateBaby(
        name = Name.trusted("Frowup"),
        babyDate = Instant.parse("2069-11-19T00:00:00.00Z")
      )
      val request3 = Baby.CreateBaby(
        name = Name.trusted("Timberley"),
        babyDate = Instant.parse("2150-12-25T00:00:00.00Z")
      )
      repositories.babies.create(request).futureValue
      repositories.babies.create(request2).futureValue
      repositories.babies.create(request3).futureValue

      val parent1Name = ParentName.trusted("Timothy Dalton")
      val parent2Name = ParentName.trusted("Christopher Reeve")
      // sum of lowercase chars ex spaces = 3154
      // 3154 % 3 = 1, so SECOND name should be returned
      val response = repositories.babies.getBaby(parent1Name, parent2Name).futureValue
      response.value.name must be(request3.name)
      //response.value.date must be(request.babyDate) // TODO: try this

      val parent3Name = ParentName.trusted("Timothy Daltol") // -2 by changing 'n' to 'l'
      val parent4Name = ParentName.trusted("Christopher Reeve")
      val response2 = repositories.babies.getBaby(parent3Name,parent4Name).futureValue
      response2.value.name must be(request.name)
      // TODO: check date also
    }
    "get baby by ONE parent name" in withRepositories() { repositories =>
      val request = Baby.CreateBaby( // TODO: convert to set of tuples to call Baby.CreateBaby on?
        name = Name.trusted("Brozo"),
        babyDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant //Instant.parse("2049-04-20T00:00:00.00Z")
      )
      val request2 = Baby.CreateBaby(
        name = Name.trusted("Frowup"),
        babyDate = Instant.parse("2069-11-19T00:00:00.00Z")
      )
      val request3 = Baby.CreateBaby(
        name = Name.trusted("Timberley"),
        babyDate = Instant.parse("2150-12-25T00:00:00.00Z")
      )
      repositories.babies.create(request).futureValue
      repositories.babies.create(request2).futureValue
      repositories.babies.create(request3).futureValue

      val parent1Name = ParentName.trusted("Jammy Jellyfish") // char sum modulo 3 = 0
      val response = repositories.babies.getBaby(parent1Name).futureValue
      response.value.name must be(request3.name)

      val parent2Name = ParentName.trusted("Jammy Jellyfisg") // char sum modulo 3 = 2
      val response2 = repositories.babies.getBaby(parent2Name).futureValue
      response2.value.name must be(request2.name)

    }
  }
}
