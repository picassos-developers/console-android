package app.mynta.console.android.activities.store.powerups.policies;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;


import com.ixuea.android.downloader.DownloadService;
import com.ixuea.android.downloader.callback.DownloadListener;
import com.ixuea.android.downloader.callback.DownloadManager;
import com.ixuea.android.downloader.domain.DownloadInfo;
import com.ixuea.android.downloader.exception.DownloadException;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import app.mynta.console.android.R;

import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.utils.Helper;
import app.mynta.console.android.utils.Toasto;

import java.io.File;

public class FinishPoliciesSetupActivity extends AppCompatActivity {

    ConsolePreferences consolePreferences;
    DownloadManager downloadManager;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_finish_policies_setup);

        // initialize download manager
        downloadManager = DownloadService.getDownloadManager(this);

        // horizontal progress bar
        progressBar = findViewById(R.id.horizontal_progress_bar);

        // download privacy
        findViewById(R.id.download_privacy).setOnClickListener(v -> requestPermission(API.API_URL + "/policies/privacy/" + consolePreferences.loadSecretAPIKey() + ".json", "privacy.json", getString(R.string.downloading_your_privacy_policy)));

        // download terms
        findViewById(R.id.download_terms).setOnClickListener(v -> requestPermission(API.API_URL + "/policies/terms/" + consolePreferences.loadSecretAPIKey() + ".json", "terms.json", getString(R.string.downloading_your_terms_of_use)));

        // finish
        findViewById(R.id.my_power_ups).setOnClickListener(v -> finish());
    }

    private void downloadFile(String url, String filename, String success) {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            File targetFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
            final DownloadInfo downloadInfo = new DownloadInfo.Builder().setUrl(url)
                    .setPath(targetFile.getAbsolutePath())
                    .build();

            downloadInfo.setDownloadListener(new DownloadListener() {
                @Override
                public void onStart() {
                    progressBar.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
                    Toasto.show_toast(FinishPoliciesSetupActivity.this, success, 1, 0);
                }

                @Override
                public void onWaited() {

                }

                @Override
                public void onPaused() {

                }

                @Override
                public void onDownloading(long progress, long size) {
                    progressBar.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
                }

                @Override
                public void onRemoved() {
                }

                @Override
                public void onDownloadSuccess() {
                    progressBar.setProgress(0);
                    Toasto.show_toast(FinishPoliciesSetupActivity.this, getString(R.string.your_file_downloaded_successfully), 1, 0);
                }

                @Override
                public void onDownloadFailed(DownloadException e) {
                    progressBar.setProgress(0);
                    Toasto.show_toast(FinishPoliciesSetupActivity.this, getString(R.string.failed_to_download_file), 1, 1);
                }
            });

            downloadManager.download(downloadInfo);
        }
    }

    /**
     * request permission for camera
     */
    private void requestPermission(String url, String filename, String success) {
        Dexter.withActivity(this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                downloadFile(url, filename, success);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toasto.show_toast(getApplicationContext(), getString(R.string.write_external_storage_permission_required), 1, 1);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

}