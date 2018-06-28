package android.support.v4.os;

import android.os.AsyncTask;

import java.util.concurrent.Executor;

/**
 * Helper for accessing features in {@link android.os.AsyncTask}.
 *
 * @deprecated Use {@link android.os.AsyncTask} directly.
 */
@Deprecated
public final class AsyncTaskCompat {

    /**
     * Executes the task with the specified parameters, allowing multiple tasks to run in parallel
     * on a pool of threads managed by {@link android.os.AsyncTask}.
     *
     * @param task The {@link android.os.AsyncTask} to execute.
     * @param params The parameters of the task.
     * @return the instance of AsyncTask.
     *
     * @deprecated Use {@link android.os.AsyncTask#executeOnExecutor(Executor, Object[])} directly.
     */
    @Deprecated
    public static <Params, Progress, Result> AsyncTask<Params, Progress, Result> executeParallel(
            AsyncTask<Params, Progress, Result> task,
            Params... params) {
        if (task == null) {
            throw new IllegalArgumentException("task can not be null");
        }
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);

        return task;
    }

    private AsyncTaskCompat() {}

}