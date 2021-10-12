package examples.covid

import com.sirius.library.utils.JSONObject

class CovidTest : JSONObject() {

    fun setFullName(name: String?): CovidTest {
        put("full_name", name)
        return this
    }

    val fullName: String?
        get() = optString("full_name")

    fun setLocation(location: String?): CovidTest {
        put("location", location)
        return this
    }

    fun setBioLocation(bioLocation: String?): CovidTest {
        put("bio_location", bioLocation)
        return this
    }

    fun setTimestamp(timestamp: String?): CovidTest {
        put("timestamp", timestamp)
        return this
    }

    fun setApproved(approved: String?): CovidTest {
        put("approved", approved)
        return this
    }

    fun setCovid(has: Boolean): CovidTest {
        put("has_covid", has.toString())
        return this
    }

    fun hasCovid(): Boolean {
        return optString("has_covid") == "true"
    }
}
