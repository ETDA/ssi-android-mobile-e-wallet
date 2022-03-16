package co.finema.etdassi.common.helper

class KeyManager(
    private val userHelper: UserHelper
) {
    private var idx = 0

    init {
        if(userHelper.getCountKey() > 0 ){
            idx = userHelper.getCountKey()
        }else{
            idx = 1
            userHelper.setCountKey(idx)
        }
    }

    var currentKey: KeyHelper
        get() = KeyHelper()
        set(value) {}

    fun createNewKey(): Boolean {
        idx += 1
        userHelper.setCountKey(idx)

        return true
    }
}