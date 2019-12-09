package com.example.ipprinterv2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;

import androidx.fragment.app.Fragment;

import com.example.ipprinterv2.observers.Observable;
import com.example.ipprinterv2.observers.Observer;
import com.example.ipprinterv2.services.PrintCompleteService;

import java.io.*;

/**
 * Created by OKK on 09-12-2019.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class PrintServicesAdapter extends PrintDocumentAdapter implements Observer {
    private Activity mActivity;
    private int totalpages = 1;
    private File pdfFile;
    private PrintCompleteService mPrintCompleteService;
    private Fragment mFragment;

    private Observable mObservable;

    public PrintServicesAdapter(Activity mActivity, Fragment mFragment, File pdfFile) {
        this.mActivity = mActivity;
        this.mFragment = mFragment;
        this.pdfFile = pdfFile;
       // this.totalpages = Util.computePDFPageCount(pdfFile);

        if(mFragment!=null){
            this.mPrintCompleteService = (PrintCompleteService) mFragment;
        } else{
            this.mPrintCompleteService = (PrintCompleteService) mActivity;
        }

        mObservable = ObservableSingleton.getInstance();
        mObservable.attach((java.util.Observer) this);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onLayout(PrintAttributes oldAttributes,
                         PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback,
                         Bundle metadata) {

        try {

            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            if (totalpages > 0) {
                PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                        .Builder(pdfFile.getName())
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(totalpages);

                PrintDocumentInfo info = builder.build();
                callback.onLayoutFinished(info, true);
            } else {
                totalpages = 0;
                callback.onLayoutFailed("Page count is zero.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onWrite(final PageRange[] pageRanges,
                        final ParcelFileDescriptor destination,
                        final CancellationSignal cancellationSignal,
                        final WriteResultCallback callback) {
        InputStream input = null;
        OutputStream output = null;


        try {
            input = new FileInputStream(pdfFile);
            output = new FileOutputStream(destination.getFileDescriptor());
            byte[] buf = new byte[1024];
            int bytesRead;

            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }

            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

        } catch (FileNotFoundException ee) {
            //Catch exception
            mPrintCompleteService.onMessage(Constants.PRINTER_STATUS_CANCELLED);
        } catch (Exception e) {
            //Catch exception
            mPrintCompleteService.onMessage(Constants.PRINTER_STATUS_CANCELLED);
        } finally {
            try {
                input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
                mPrintCompleteService.onMessage(Constants.PRINTER_STATUS_CANCELLED);
            }
        }

        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                mPrintCompleteService.onMessage(Constants.PRINTER_STATUS_CANCELLED);
            }
        });
    }

    @Override
    public void onFinish() {
        mPrintCompleteService.onMessage(Constants.PRINTER_STATUS_COMPLETED);
    }

    @Override
    public void update() {
        mObservable.detach((java.util.Observer) this);
    }

    @Override
    public void updateObserver(boolean bool) {

    }

    @Override
    public void updateObserverProgress(int percentage) {

    }

}