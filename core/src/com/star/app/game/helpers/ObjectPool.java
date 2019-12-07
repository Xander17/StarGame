package com.star.app.game.helpers;

import java.util.ArrayList;
import java.util.List;

public abstract class ObjectPool<T extends Poolable> {
    private List<T> freeList;
    protected List<T> activeList;

    public abstract T getNew();

    protected ObjectPool() {
        this.freeList = new ArrayList<>();
        this.activeList = new ArrayList<>();
    }

    public List<T> getActiveList() {
        return activeList;
    }

    protected T getActive() {
        if (freeList.size() == 0) freeList.add(getNew());
        T element = freeList.remove(freeList.size() - 1);
        activeList.add(element);
        return element;
    }

    private void free(int index) {
        freeList.add(activeList.remove(index));
    }

    protected void checkFreeObjects() {
        for (int i = activeList.size() - 1; i >= 0; i--) {
            if (!activeList.get(i).isActive()) free(i);
        }
    }
}