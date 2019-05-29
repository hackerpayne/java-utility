package com.lindonge.core.queue;


import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

class DelayedItem<T> implements Delayed {

    private T t;
    private long liveTime;
    private long removeTime;

    public DelayedItem(T t, long liveTime) {
        this.setT(t);
        this.liveTime = liveTime;
        this.removeTime = TimeUnit.MILLISECONDS.convert(liveTime, TimeUnit.MILLISECONDS) + System.currentTimeMillis();
    }

    @Override
    public int compareTo(Delayed o) {
        if (o == null)
            return 1;
        if (o == this)
            return 0;
        if (o instanceof DelayedItem) {
            DelayedItem<T> tmpDelayedItem = (DelayedItem<T>) o;
            if (liveTime > tmpDelayedItem.liveTime) {
                return 1;
            } else if (liveTime == tmpDelayedItem.liveTime) {
                return 0;
            } else {
                return -1;
            }
        }
        long diff = getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
        return diff > 0 ? 1 : diff == 0 ? 0 : -1;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(removeTime - System.currentTimeMillis(), unit);
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    @Override
    public int hashCode() {
        return t.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof DelayedItem) {
            return object.hashCode() == hashCode() ? true : false;
        }
        return false;
    }

}
