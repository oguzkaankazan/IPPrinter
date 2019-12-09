package com.example.ipprinterv2;

/**
 * Created by OKK on 06-11-2019.
 */
public interface PrintFragmentCommunicator {
    public void respondOnPrintComplete();
    public void respondOnPrinterSelect();
    public void respondOnPrinterSelectCancelled();
}
