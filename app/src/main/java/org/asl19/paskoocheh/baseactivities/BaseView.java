package org.asl19.paskoocheh.baseactivities;

public interface BaseView<T extends BasePresenter> {
    boolean isActive();

    void setPresenter(T presenter);
}
