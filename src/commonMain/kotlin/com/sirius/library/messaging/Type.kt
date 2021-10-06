package com.sirius.library.messaging

import com.sirius.library.errors.sirius_exceptions.SiriusInvalidType

class Type {
    var docUri: String

    fun getVersionInfo(): Semver {
        return versionInfo
    }

    var protocol: String
    var version: String? = null
    var versionInfo: Semver
    var name: String
    var typeString: String? = null
    var normalizedString: String? = null

    constructor(docUri: String, protocol: String, versionInfo: Semver, name: String) {
        this.docUri = docUri
        this.protocol = protocol
        this.versionInfo = versionInfo
        this.name = name
        // version = versionInfo.
    }

    constructor(docUri: String, protocol: String, version: String?, name: String) {
        this.docUri = docUri
        this.protocol = protocol
        this.version = version
        this.name = name
        versionInfo = Semver.fromStr(version)

        typeString =  "$docUri$protocol/$version/$name" //String.format(FORMAT_PATTERN, docUri, protocol, version, name)
        normalizedString = "$docUri$protocol/$versionInfo/$name" //String.format(FORMAT_PATTERN, docUri, protocol, versionInfo, name)
    }

    override fun toString(): String {

        return  "$docUri$protocol/$version/$name" //String.format("%s%s/%s/%s", docUri, protocol, version, name)
    }

    companion object {
        val MTURI_RE: Regex =
            Regex("(.*?)([a-z0-9._-]+)/(\\d[^/]*)/([a-z0-9._-]+)$")
        val MTURI_PROBLEM_REPORT_RE: Regex =
            Regex("(.*?)([a-z0-9._-]+)/(\\d[^/]*)/([a-z0-9._-]+)/([a-z0-9._-]+)$")
        const val FORMAT_PATTERN = "%s%s/%s/%s"
        const val FORMAT_PATTERN_PROBLEM_REPORT = "%s%s/%s/%s/%s"

        /**
         * Parse type from string.
         *
         * @param type
         * @return
         */
        @Throws(SiriusInvalidType::class)
        fun fromStr(type: String): Type {
            val matcher: MatchResult? = MTURI_RE.matchEntire(type)
            val matcherMatches: Boolean = MTURI_RE.matches(type)
            val matcherProblemReport: MatchResult? = MTURI_PROBLEM_REPORT_RE.matchEntire(type)
            val matcherProblemReportMatches: Boolean = MTURI_PROBLEM_REPORT_RE.matches(type)
            if (!matcherMatches && !matcherProblemReportMatches) {
                throw SiriusInvalidType("Invalid message type")
            }
            if (matcherProblemReportMatches) {
                return if (matcherProblemReport!!.groupValues.size >= 5) {
                    Type(
                        matcherProblemReport.groupValues.get(1),
                        matcherProblemReport.groupValues.get(2),
                        matcherProblemReport.groupValues.get(3),
                        matcherProblemReport.groupValues.get(5)
                    )
                } else {
                    throw SiriusInvalidType("Invalid message type")
                }
            }
            if (matcherMatches) {
                return if (matcher!!.groupValues.size >= 4) {
                    Type(matcher.groupValues.get(1), matcher.groupValues.get(2), matcher.groupValues.get(3), matcher.groupValues.get(4))
                } else {
                    throw SiriusInvalidType("Invalid message type")
                }
            }
            throw SiriusInvalidType("Invalid message type")
        }
    }
}
