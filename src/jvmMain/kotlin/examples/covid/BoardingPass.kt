package examples.covid

import com.sirius.library.utils.JSONObject

class BoardingPass : JSONObject() {


    fun setFullName(name: String?): BoardingPass {
        put("full_name", name)
        return this
    }

    fun getFullName(): String? {
        return optString("full_name")
    }

    fun setFlight(flight: String?): BoardingPass {
        put("flight", flight)
        return this
    }

    fun setDeparture(departure: String?): BoardingPass {
        put("departure", departure)
        return this
    }

    fun setArrival(arrival: String?): BoardingPass {
        put("arrival", arrival)
        return this
    }

    fun setDate(date: String?): BoardingPass {
        put("date", date)
        return this
    }

    fun setClass(cls: String?): BoardingPass {
        put("class", cls)
        return this
    }

    fun setSeat(seat: String?): BoardingPass {
        put("seat", seat)
        return this
    }
}
