package co.finema.etdassi.common.utils

fun String?.subAsDIDAddress(): String {
    if (this == null) {
        return ""
    }
    return this.substring(0, 12) + "xxxxxxxxxxxxx" + this.substring(this.length - 2)
}