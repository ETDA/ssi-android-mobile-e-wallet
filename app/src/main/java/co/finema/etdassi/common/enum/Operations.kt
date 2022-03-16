package co.finema.etdassi.common.enum

enum class Operations(val action: String) {
    CREATE_WALLET("WALLET_CREATE"),
    DID_RECOVERER_ADD("DID_RECOVERER_ADD"),
    RECOVERY("RECOVERY"),
    REGISTER("REGISTER"),
    DID_KEY_RESET("DID_KEY_RESET")
}