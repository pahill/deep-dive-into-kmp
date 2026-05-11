package com.jetbrains.whereiskodee.whereiskodee

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

private fun currentTimestamp() = Clock.System.now().epochSeconds

class KodeeRepositoryImpl() : KodeeRepository {

    private val sampleLocations = listOf(
        KodeeLocation("Munich", "Germany", "Marienplatz",         "kodee_waving",          0L),
        KodeeLocation("Munich", "Germany", "Englischer Garten",   "kodee_sleeping",        0L),
        KodeeLocation("Munich", "Germany", "Nymphenburg Palace",  "kodee_reading",         0L),
        KodeeLocation("Munich", "Germany", "Deutsches Museum",    "kodee_tinkering",       0L),
        KodeeLocation("Munich", "Germany", "Hofbräuhaus",         "kodee_eating",          0L),
        KodeeLocation("Munich", "Germany", "Viktualienmarkt",     "kodee_shopping",        0L),
        KodeeLocation("Munich", "Germany", "BMW Museum",          "kodee_driving",         0L),
        KodeeLocation("Munich", "Germany", "Olympiapark",         "kodee_running",         0L),
        KodeeLocation("Munich", "Germany", "Munich Residenz",     "kodee_painting",        0L),
        KodeeLocation("Munich", "Germany", "Alte Pinakothek",     "kodee_admiring",        0L),
        KodeeLocation("Munich", "Germany", "Frauenkirche",        "kodee_sightseeing",     0L),
        KodeeLocation("Munich", "Germany", "Tierpark Hellabrunn", "kodee_feeding_animals", 0L),
        KodeeLocation("Munich", "Germany", "Allianz Arena",       "kodee_cheering",        0L),
        KodeeLocation("Munich", "Germany", "Schloss Schleissheim","kodee_cycling",         0L),
    )

    override suspend fun locateKodee(): KodeeLocation {
        delay(1500L.milliseconds)
        return sampleLocations[Random.nextInt(sampleLocations.size)]
            .copy(timestamp = currentTimestamp())
    }

    override fun locationHistory(): Flow<KodeeLocation> = flow {
        for (sample in sampleLocations) {
            delay(Random.nextLong(1_000L, 3_001L).milliseconds)
            emit(sample.copy(timestamp = currentTimestamp()))
        }
    }
}
