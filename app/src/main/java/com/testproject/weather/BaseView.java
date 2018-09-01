package com.testproject.weather;

public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);

}