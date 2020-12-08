// IPackageStatsObserver.aidl.aidl  清理缓存
package android.content.pm;

// Declare any non-default types here with import statements
import android.content.pm.PackageStats;
interface IPackageStatsObserver {
    oneway void onGetStatsCompleted(in PackageStats pStats, boolean succeeded);
}
