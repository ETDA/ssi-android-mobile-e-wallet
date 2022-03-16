package co.finema.etdassi.feature.fcm

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture

class SSIWork(appContext: Context, params: WorkerParameters): Worker(appContext, params){
//    override fun doWork(): Result {
//        Log.e(TAG, "Performing long running task in scheduled job")
//        return Result.success()
//    }


    override fun onStopped() {
        super.onStopped()
    }

    override fun doWork(): Result {
        return Result.success()
    }

}