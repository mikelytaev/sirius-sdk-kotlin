package com.sirius.library.mobile.scenario


interface ScenarioListener{
    fun  onScenarioStart(id : String)
    fun onScenarioEnd(id : String,success: Boolean, error: String?)
}