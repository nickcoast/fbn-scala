package net.wiringbits.repositories

import net.wiringbits.common.models.{BabyName}
import net.wiringbits.config.UserTokensConfig
import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.repositories.daos.{BabyDAO}
import net.wiringbits.repositories.models._
import play.api.db.Database

import java.time.Clock
import javax.inject.Inject
import scala.concurrent.Future

class BabiesRepository @Inject()(
    database: Database,
    userTokensConfig: UserTokensConfig
)(implicit
    ec: DatabaseExecutionContext,
    clock: Clock
) {

  def create(request: Baby.CreateBaby): Future[Unit] = Future {
    database.withTransaction { implicit conn =>
      BabyDAO.create(request)
    }
  }

  def all(): Future[List[Baby]] = Future {
    database.withConnection { implicit conn =>
      BabyDAO.all()
    }
  }

  def find(babyName: BabyName): Future[Option[Baby]] = Future {
    database.withConnection { implicit conn =>
      BabyDAO.find(babyName)
    }
  }

  def find(id: Int): Future[Option[Baby]] = Future {
    database.withConnection { implicit conn =>
      BabyDAO.find(id)
    }
  }

  def update(babyName: BabyName, newBabyName: BabyName): Future[Unit] = Future {
    database.withTransaction { implicit conn =>
      BabyDAO.updateName(babyName, newBabyName)
    }
  }
}
