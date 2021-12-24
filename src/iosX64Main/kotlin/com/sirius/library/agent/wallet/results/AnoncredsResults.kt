package com.sirius.library.agent.wallet.results

/**
 * anoncreds.rs results
 */
/**
 * Result classes related to anonymous credentials calls.
 */
class AnoncredsResults private constructor() {
    /**
     * Result from calling issuerCreateSchema.
     */
    class IssuerCreateSchemaResult internal constructor(/**
         * Gets the schema Id.
         *
         * @return Schema Id.
         */
        val schemaId: String,
        /**
         * Gets the schema JSON.
         *
         * @return The schema JSON.
         */
        val schemaJson: String
    )   : Result(){


    }

    /**
     * Result from calling IssuerCreateAndStoreCredentialDefResult.
     */
    /**
     * Gets the credential def Id.
     *
     * @return credential def Id.
     */
    /**
     * Gets the credential definition JSON.
     *
     * @return The credential definition JSON.
     */
    class IssuerCreateAndStoreCredentialDefResult internal constructor(val credDefId: String, val credDefJson: String) :
       Result() {


    }

    /**
     * Result from calling issuerCreateAndStoreRevocReg.
     */
    /**
     * Gets the revocation registry Id.
     *
     * @return revocation registry Id.
     */

    /**
     * Gets the revocation registry definition JSON.
     *
     * @return The revocation registry definition JSON.
     */

    /**
     * Gets the revocation registry entry JSON.
     *
     * @return The revocation registry entry JSON.
     */
    class IssuerCreateAndStoreRevocRegResult internal constructor(
        val revRegid: String,
       val revRegDefJson: String,
       val revRegEntryJson: String
    ) :
        Result() {





    }

    /**
     * Result from calling issuerCreateCredential.
     */
    class IssuerCreateCredentialResult internal constructor(
        val credentialJson: String,
       val revocId: String,
        val revocRegUpdateJson: String
    ) :
        Result() {
        /**
         * Gets the credential JSON.
         *
         * @return The credential JSON.
         */


        /**
         * Gets the credential revocation Id.
         *
         * @return The credential revocation Id.
         */


        /**
         * Gets the revocation registration delta JSON.
         *
         * @return The revocation registration delta JSON.
         */



    }

    /**
     * Result from calling proverCreateCredentialReq.
     */
    class ProverCreateCredentialRequestResult internal constructor(
        val credentialRequestJson: String,
       val  credentialRequestMetadataJson: String
    ) :
        Result() {
        /**
         * Gets the credential request JSON.
         *
         * @return The credential request JSON.
         */


        /**
         * Gets the credential request metadata JSON.
         *
         * @return The credential request metadata JSON.
         */

    }
}