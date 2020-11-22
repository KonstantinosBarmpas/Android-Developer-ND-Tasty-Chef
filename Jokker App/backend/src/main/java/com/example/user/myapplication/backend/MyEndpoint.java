/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.example.user.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.ApiReference;


@Api(
        name = "myApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.user.example.com",
                ownerName = "backend.myapplication.user.example.com",
                packagePath = ""
        )
)
public class MyEndpoint {

    public String joke;

    @ApiMethod(name = "create")
    public MyBean create() {
        MyBean joke = new MyBean();
        return joke;
    }

    @ApiMethod(name="get")
    private void get(MyBean joke1){
        joke=joke1.getJoke();
    }

}
