package com.netflix.spinnaker.cats.sql

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.spectator.api.NoopRegistry
import com.netflix.spinnaker.cats.cache.Cache
import com.netflix.spinnaker.cats.cache.WriteableCacheSpec
import com.netflix.spinnaker.cats.sql.cache.SpectatorSqlCacheMetrics
import com.netflix.spinnaker.cats.sql.cache.SqlCache
import com.netflix.spinnaker.kork.sql.config.SqlRetryProperties
import spock.lang.AutoCleanup
import spock.lang.Shared

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

import static com.netflix.spinnaker.kork.sql.test.SqlTestUtil.*

class SqlCacheSpec extends WriteableCacheSpec {

  @Shared
  @AutoCleanup("close")
  TestDatabase currentDatabase

  def setup() {
    (getSubject() as SqlCache).clearCreatedTables()
    return initDatabase("jdbc:h2:mem:test")
  }


  def cleanup() {
    currentDatabase.context.dropSchemaIfExists("test")
  }

  @Override
  Cache getSubject() {
    def mapper = new ObjectMapper()
    def clock = new Clock.FixedClock(Instant.EPOCH, ZoneId.of("UTC"))
    def sqlRetryProperties = new SqlRetryProperties()
    def sqlMetrics = new SpectatorSqlCacheMetrics(new NoopRegistry())
    currentDatabase = initDatabase()
    return new SqlCache("test", currentDatabase.context, mapper, clock, sqlRetryProperties, "test", sqlMetrics, 10, 10)
  }

}
