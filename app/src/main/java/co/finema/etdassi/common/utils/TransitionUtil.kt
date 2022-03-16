package co.finema.etdassi.common.utils

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import co.finema.etdassi.R

object TransitionUtil {
    var sDisableFragmentAnimations = false
    var loadingBackPressedEnable = false
    var onBackPressedEnable = false
}
fun FragmentManager.animateTransition(): FragmentTransaction {
    return this.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
}

fun FragmentManager.animateOnlyInTransition(): FragmentTransaction {
    return this.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_out_left, R.anim.slide_in_right)
}

fun FragmentManager.animateOutIn(): FragmentTransaction {
    return this.beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
}

fun FragmentManager.animateOnBack(): FragmentTransaction {
    return this.beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_out_right, R.anim.slide_in_left)
}

enum class BackStack() {
    REGISTER_APP, REGISTER_EWALLET, SET_PASSCODE, SET_BIOMETRIC, SET_NEW_PINCODE
}