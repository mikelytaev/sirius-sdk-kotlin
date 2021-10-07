/*
package com.examples.covid

import com.sirius.library.agent.model.Entity
import com.sirius.library.agent.pairwise.Pairwise
import com.sirius.library.encryption.P2PConnection
import com.sirius.library.hub.CloudContext
import com.sirius.library.hub.CloudHub
import kotlin.jvm.JvmStatic

object Main {
    const val DKMS_NAME = "test_network"
    const val COVID_MICROLEDGER_NAME = "covid_ledger_test3"
    var steward: CloudHub.Config = CloudHub.Config()
    var labConfig: CloudHub.Config = CloudHub.Config()
    var airCompanyConfig: CloudHub.Config = CloudHub.Config()
    var airportConfig: CloudHub.Config = CloudHub.Config()
    const val LAB_DID = "X1YdguoHBaY1udFQMbbKKG"
    const val AIRCOMPANY_DID = "XwVCkzM6sMxk87M2GKtya6"
    const val AIRPORT_DID = "Ap29nQ3Kf2bGJdWEV3m4AG"
    val labEntity: Entity = Entity(
        "Laboratory",
        "",
        "HMf57wiWK1FhtzLbm76o37tEMJvaCbWfGsaUzCZVZwnT",
        "X1YdguoHBaY1udFQMbbKKG"
    )
    val aircompanyEntity: Entity = Entity(
        "AirCompany",
        "",
        "Hs4FPfB1d7nFUcqbMZqofFg4qoeGxGThmSbunJYpVAM6",
        "XwVCkzM6sMxk87M2GKtya6"
    )
    val airportEntity: Entity = Entity(
        "Airport",
        "",
        "6M8qgMdkqGzQ2yhryV3F9Kvk785qAFny5JuLp1CJCcHW",
        "Ap29nQ3Kf2bGJdWEV3m4AG"
    )

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        var medCredInfo: CredInfo?
        CloudContext(labConfig).also { c ->
            medCredInfo = Laboratory.createMedCreds(c, LAB_DID, DKMS_NAME)
            if (medCredInfo != null) {
                println("Covid test credentials registered successfully")
            } else {
                println("Covid test credentials was not registered")
                return
            }
        }
        var boardingPassCredInfo: CredInfo?
        CloudContext(airCompanyConfig).also { c ->
            boardingPassCredInfo = AirCompany.createBoardingPassCreds(c, AIRCOMPANY_DID, DKMS_NAME)
            if (boardingPassCredInfo != null) {
                println("Boarding pass credentials registered successfully")
            } else {
                println("Boarding pass credentials was not registered")
                return
            }
        }
        val lab2aircompany: Pairwise =
            Helpers.establishConnection(labConfig, labEntity, airCompanyConfig, aircompanyEntity)
        val aircompany2lab: Pairwise =
            Helpers.establishConnection(airCompanyConfig, aircompanyEntity, labConfig, labEntity)
        val lab = Laboratory(
            labConfig, listOf(lab2aircompany), COVID_MICROLEDGER_NAME, lab2aircompany.me,
            medCredInfo!!
        )
        val airCompany = AirCompany(
            airCompanyConfig, listOf(aircompany2lab), COVID_MICROLEDGER_NAME, aircompany2lab.me,
            boardingPassCredInfo!!
        )
        val airport = Airport(
            airportConfig,
            medCredInfo!!, LAB_DID, boardingPassCredInfo!!, AIRCOMPANY_DID, DKMS_NAME
        )
        airCompany.start()
        airport.start()
        lab.start()
        val `in`: java.util.Scanner = java.util.Scanner(java.lang.System.`in`)
        println("Enter your Name")
        val fullName: String = `in`.nextLine()
        var loop = true
        while (loop) {
            println("Enter your option:")
            println("1 - Get Covid test")
            println("2 - Get boarding pass")
            println("3 - Enter to the terminal")
            println("4 - Exit")
            val option: Int = `in`.nextInt()
            when (option) {
                1 -> {
                    println("Do you have Covid? (true/false)")
                    val hasCovid: Boolean = `in`.nextBoolean()
                    val df: java.text.DateFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    val timestamp: String = df.format(java.util.Date(java.lang.System.currentTimeMillis()))
                    val testRes = CovidTest().setFullName(fullName).setCovid(hasCovid).setLocation("Nur-Sultan")
                        .setBioLocation("Nur-Sultan").setApproved("House M.D.").setTimestamp(timestamp)
                    val qr = lab.issueTestResults(testRes)!!.first
                    println("Scan this QR by Sirius App for receiving the Covid test result $qr")
                }
                2 -> {
                    val df: java.text.DateFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    val timestamp: String = df.format(java.util.Date(java.lang.System.currentTimeMillis()))
                    val boardingPass =
                        BoardingPass().setFullName(fullName).setArrival("Nur-Sultan").setDeparture("New York JFK")
                            .setClass("first").setDate(timestamp).setFlight("KC 1234").setSeat("1A")
                    val qr = airCompany.register(boardingPass)!!.first
                    println("Scan this QR by Sirius App for receiving the boarding pass $qr")
                }
                3 -> {
                    val qr = airport.enterToTerminal()!!.first
                    println("Scan this QR by Sirius App to enter to the terminal $qr")
                }
                4 -> {
                    loop = false
                }
            }
        }
        airCompany.stop()
        airport.stop()
        lab.stop()
    }

    init {
        steward.serverUri = "https://demo.socialsirius.com"
        steward.credentials =
            "ez8ucxfrTiV1hPX99MHt/C/MUJCo8OmN4AMVmddE/sew8gBzsOg040FWBSXzHd9hDoj5B5KN4aaLiyzTqkrbD3uaeSwmvxVsqkC0xl5dtIc=".encodeToByteArray()
        steward.p2p = P2PConnection(
            "6QvQ3Y5pPMGNgzvs86N3AQo98pF5WrzM1h6WkKH3dL7f",
            "28Au6YoU7oPt6YLpbWkzFryhaQbfAcca9KxZEmz22jJaZoKqABc4UJ9vDjNTtmKSn2Axfu8sT52f5Stmt7JD4zzh",
            "6oczQNLU7bSBzVojkGsfAv3CbXagx7QLUL7Yj1Nba9iw"
        )
        labConfig.serverUri = "https://demo.socialsirius.com"
        labConfig.credentials =
            "BXXwMmUlw7MTtVWhcVvbSVWbC1GopGXDuo+oY3jHkP/4jN3eTlPDwSwJATJbzwuPAAaULe6HFEP5V57H6HWNqYL4YtzWCkW2w+H7fLgrfTLaBtnD7/P6c5TDbBvGucOV".encodeToByteArray()
        labConfig.p2p = P2PConnection(
            "EzJKT2Q6Cw8pwy34xPa9m2qPCSvrMmCutaq1pPGBQNCn",
            "273BEpAM8chzfMBDSZXKhRMPPoaPRWRDtdMmNoKLmJUU6jvm8Nu8caa7dEdcsvKpCTHmipieSsatR4aMb1E8hQAa",
            "342Bm3Eq9ruYfvHVtLxiBLLFj54Tq6p8Msggt7HiWxBt"
        )
        airCompanyConfig.serverUri = "https://demo.socialsirius.com"
        airCompanyConfig.credentials =
            "/MYok4BSllG8scfwXVVRK8V47I1PC44mktwiJKKduf38Yb7UgIsq8n4SXVBrRwIzHMQA/6sdiKgrB20Kbw9ieHbOGlxx3UVlWNM0Xfc9Rgk85cCLSHWM2vqlNQSGwHAM+udXpuPwAkfKjiUtzyPBcA==".encodeToByteArray()

        airCompanyConfig.p2p = P2PConnection(
            "BhDMxfvhc2PZ4BpGTExyWHYkJDFPhmXpaRvUoCoNJ8rL",
            "2wwakvFwBRWbFeLyDbsH6cYVve6FBH6DL133sPNN87jWYbc6rHXj7Q3dnAsbB6EuNwquucsDzSBhNcpxgyVLCCYg",
            "8VNHw79eMTZJBasgjzdwyKyCYA88ajm9gvP98KGcjaBt"
        )
        airportConfig.serverUri = "https://demo.socialsirius.com"
        airportConfig.credentials =
            "/MYok4BSllG8scfwXVVRK3NATRRtESRnhUHOU3nJxxZ+gg81/srwEPNWfZ+3+6GaEHcqghOJvRoV7taA/vCd2+q2hIEpDO/yCPfMr4x2K0vC/pom1gFRJwJAKI3LpMy3".encodeToByteArray()

        airportConfig.p2p = P2PConnection(
            "HBEe9KkPCK4D1zs6UBzLqWp6j2Gj88zy3miqybvYx42p",
            "23jutNJBbgn8bbX53Qr36JSeS2VtZHvY4DMqazXHq6mDEPNkuA3FkKVGAMJdjPznfizLg9nh448DXZ7e1724qk1a",
            "BNxpmTgs9B3yMURa1ta7avKuBA5wcBp5ZmXfqPFPYGAP"
        )
    }
}
*/
