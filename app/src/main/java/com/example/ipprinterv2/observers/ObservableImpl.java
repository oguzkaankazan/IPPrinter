package com.example.ipprinterv2.observers;

import com.example.ipprinterv2.observers.Observable;
import com.example.ipprinterv2.observers.Observer;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Anil on 8/13/2014.
 */
public class ObservableImpl implements Observable {
    private List<Observer> mObservers;

    public ObservableImpl() {
        mObservers = new ArrayList<Observer>();
    }

    @Override
    public void notifyObserver(boolean bool) {
        if (bool) {
            for (int i = mObservers.size()-1; i >= 0; i--) {
                mObservers.get(i).update();
            }
        }
    }

    @Override
    public void attach(java.util.Observer observer) {
        if (observer != null) {
            mObservers.add((Observer) observer);
        }
    }

    @Override
    public void detach(java.util.Observer observer) {
        try {
            mObservers.remove(observer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateProgress(int percentage) {
        for (int i = mObservers.size()-1; i >= 0; i--) {
            mObservers.get(i).updateObserverProgress(percentage);
        }
    }


    @Override
    public void notify(Object param) {
        try {
            for (int i = mObservers.size()-1; i >= 0; i--) {
                mObservers.get(i).update();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
