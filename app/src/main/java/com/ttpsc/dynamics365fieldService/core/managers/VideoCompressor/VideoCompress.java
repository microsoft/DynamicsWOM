package com.ttpsc.dynamics365fieldService.core.managers.VideoCompressor;

import android.os.AsyncTask;
import android.util.Log;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import kotlin.Unit;

public class VideoCompress {

    public static Observable<Boolean> compressVideoHigh(String srcPath, String destPath) {
        return Observable.create(emitter -> {
            VideoCompressTask task = new VideoCompressTask(emitter, VideoController.COMPRESS_QUALITY_HIGH);
            task.execute(srcPath, destPath);
        });
    }

    public static Observable<Boolean> compressVideoMedium(String srcPath, String destPath) {
        return Observable.create(emitter -> {
            VideoCompressTask task = new VideoCompressTask(emitter, VideoController.COMPRESS_QUALITY_MEDIUM);
            task.execute(srcPath, destPath);
        });
    }

    public static Observable<Boolean> compressVideoLow(String srcPath, String destPath) {
        return Observable.create(emitter -> {
            VideoCompressTask task = new VideoCompressTask(emitter, VideoController.COMPRESS_QUALITY_LOW);
            task.execute(srcPath, destPath);
        });
    }

    private static class VideoCompressTask extends AsyncTask<String, Float, Boolean> {
        private ObservableEmitter<Boolean> emitter;
        private int mQuality;

        public VideoCompressTask(ObservableEmitter<Boolean> listener, int quality) {
            emitter = listener;
            mQuality = quality;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... paths) {
            Log.i("VideoController", "Entered doInBackground");
            return VideoController.getInstance().convertVideo(paths[0], paths[1], mQuality, new VideoController.CompressProgressListener() {
                @Override
                public void onProgress(float percent) {
                    publishProgress(percent);
                }
            });
        }

        @Override
        protected void onProgressUpdate(Float... percent) {
            super.onProgressUpdate(percent);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (emitter != null) {
                emitter.onNext(result);
            }
        }
    }
}
