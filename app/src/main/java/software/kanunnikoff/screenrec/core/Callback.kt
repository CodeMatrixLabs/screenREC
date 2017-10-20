package software.kanunnikoff.screenrec.core

/**
 * Created by dmitry on 17/10/2017.
 */
interface Callback<T> {
    fun onResult(records: ArrayList<T>)
}