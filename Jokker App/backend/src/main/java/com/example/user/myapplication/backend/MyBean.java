package com.example.user.myapplication.backend;
import com.example.JokeTell;

/**
 * The object model for the data we are sending through endpoints
 */
public class MyBean {

    private JokeTell joke_bean;

    public MyBean() {
        joke_bean = new JokeTell();
    }

    public String getJoke() {
        return joke_bean.getRandomJoke();
    }
}