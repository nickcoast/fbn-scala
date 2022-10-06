package net.wiringbits.repositories

import net.wiringbits.common.models.EraName
import net.wiringbits.config.UserTokensConfig
import net.wiringbits.executors.DatabaseExecutionContext
import net.wiringbits.repositories.daos.EraDAO
import net.wiringbits.repositories.models._
import play.api.db.Database

import java.time.Clock
import javax.inject.Inject
import scala.concurrent.Future

class ErasRepository @Inject()(
            database: Database,
            userTokensConfig: UserTokensConfig
          )(implicit
            ec: DatabaseExecutionContext,
            clock: Clock
          ) {

  def create(request: Era.CreateEra): Future[Unit] = Future {
    database.withTransaction { implicit conn =>
      EraDAO.create(request)
    }
  }

  def all(): Future[List[Era]] = Future {
    database.withConnection { implicit conn =>
      EraDAO.all()
    }
  }

  def find(eraName: EraName): Future[Option[Era]] = Future {
    database.withConnection { implicit conn =>
      EraDAO.find(eraName)
    }
  }

  def find(id: Int): Future[Option[Era]] = Future {
    database.withConnection { implicit conn =>
      EraDAO.find(id)
    }
  }

  def update(id: Int, eraName: EraName): Future[Unit] = Future {
    database.withTransaction { implicit conn =>
      EraDAO.updateName(id, eraName)
    }
  }
}
