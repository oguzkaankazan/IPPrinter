package com.example.ipprinterv2.observers;


import java.util.Observer;

public interface Observable {
    public void notifyObserver(boolean bool);
    public void attach(Observer observer);
    public void detach(Observer observer);
    public void notify(Object param);
    public void updateProgress(int percentage);
}
