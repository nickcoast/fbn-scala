package net.wiringbits.repositories

import net.wiringbits.common.models.{BabyName, Email}
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
        name = BabyName.trusted("Brozo"),
        //babyDate = Calendar.getInstance(TimeZone.getTimeZone("PST")).set(Calendar.MONTH, 0).set(Calendar.DATE, 30)
        //babyDate = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime("2027-04-20").toInstant
        //babyDate = LocalDate.parse("2027-04-20",DateTimeFormatter.ofPattern("yyyy-MM-dd")).toInstant
        babyDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant
      )      
      repositories.babies.create(request).futureValue
    }

    "fail when the id already exists" in withRepositories() { repositories =>
      val request = Baby.CreateBaby(
        name = BabyName.trusted("Brozo"),
        babyDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant,
      )

      repositories.babies.create(request).futureValue
      val ex = intercept[RuntimeException] {
        repositories.babies.create(request.copy(BabyName.trusted("Brozo"))).futureValue
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
          name = BabyName.trusted(s"Sample$i"),
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
        name = BabyName.trusted("Sample"),
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
        name = BabyName.trusted("Brozo"),
        babyDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant,
      )
      repositories.babies.create(request).futureValue

      val newName = BabyName.trusted("Frowup")
      repositories.babies.update(request.name, newName).futureValue

      val response = repositories.babies.find(newName).futureValue
      response.value.name must be(newName)
      response.value.date must be(request.babyDate)
    }

    "fail when the baby doesn't exist" in withRepositories() { repositories =>
      val name = BabyName.trusted("999")
      val newName = BabyName.trusted("Test")
      /*
      // doesn't work because unlike in User, we're not updating a log table with a foreign key that would fail
      val ex = intercept[RuntimeException] {
        repositories.babies.update(name,newName).futureValue
      }
      ex.getCause.getMessage must startWith(
        """ERROR: insert or update on table "baby_names" violates foreign key constraint "baby_logs_babies_fk""""
      )*/
      val response = repositories.babies.update(name, newName).futureValue
      response.isEmpty must be(true)
    }
  }
}
