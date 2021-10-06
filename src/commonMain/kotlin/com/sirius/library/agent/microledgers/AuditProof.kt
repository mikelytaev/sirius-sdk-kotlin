package com.sirius.library.agent.microledgers

class AuditProof(rootHash: String, auditPath: List<String>, var ledgerSize: Int) :
    MerkleInfo(rootHash, auditPath)
