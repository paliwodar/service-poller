package eu.paliwoda.servicepoller.fixtures

import eu.paliwoda.servicepoller.domain.Pollee

class PolleeFixture {

    static long DEFAULT_POLLEE_ID = 42L

    static def defaultPollee(url = "http://example.zzz") {
        return Pollee.builder()
                     .name("some name")
                     .serviceUrl(url)
                     .build()
    }

    static def existingPollee() {
        return defaultPollee().toBuilder()
                              .id(DEFAULT_POLLEE_ID)
                              .build()
    }
}
