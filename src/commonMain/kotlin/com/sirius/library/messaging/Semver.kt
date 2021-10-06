package com.sirius.library.messaging

/**
 * Wrapper around the more complete VersionInfo class from semver package.
 *
 *
 * This wrapper enables abbreviated versions in message types
 * (i.e. 1.0 not 1.0.0).
 */
class Semver {

    val SEMVER_RE: Regex =
        Regex("^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:\\.(0|[1-9]\\d*))?$")

    companion object {
        fun fromStr(version: String?): Semver {
            // Matcher matcher =  Matcher.
            return Semver()
        }
    }

}
