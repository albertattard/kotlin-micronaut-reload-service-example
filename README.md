# An Example of Micronaut Management Endpoints in Kotlin 

Based on:
1. [14 Management & Monitoring](https://docs.micronaut.io/latest/guide/management.html)
2. [Micronaut Security]( https://micronaut-projects.github.io/micronaut-security/latest/guide/)

## Pending Issues

1. `/refresh` endpoint fails with a `Read Timeout` when accessed from `@MicronautTest`

    The test, [RefreshableTest](./src/test/kotlin/com/albertattard/example/micronaut/RefreshableTest.kt), that makes sure that the [WeatherService](./src/main/kotlin/com/albertattard/example/micronaut/WeatherService.kt) is refreshed in a black-box manner, is not working as expected.  The call to trigger the refresh, shown next, times out.
    
    ```kotlin
    client.toBlocking().exchange(HttpRequest.POST("/refresh", mapOf("force" to "true")), String::class.java)
    ```
    
    This should work, but it is failing with a `Read Timeout`.
    
    ```
    Read Timeout
    io.micronaut.http.client.exceptions.ReadTimeoutException: Read Timeout
        at io.micronaut.http.client.exceptions.ReadTimeoutException.<clinit>(ReadTimeoutException.java:26)
        at io.micronaut.http.client.DefaultHttpClient.lambda$null$29(DefaultHttpClient.java:1091)
        at io.reactivex.internal.operators.flowable.FlowableOnErrorNext$OnErrorNextSubscriber.onError(FlowableOnErrorNext.java:103)
        at io.reactivex.internal.operators.flowable.FlowableTimeoutTimed$TimeoutSubscriber.onTimeout(FlowableTimeoutTimed.java:139)
        at io.reactivex.internal.operators.flowable.FlowableTimeoutTimed$TimeoutTask.run(FlowableTimeoutTimed.java:170)
        at io.reactivex.internal.schedulers.ScheduledRunnable.run(ScheduledRunnable.java:66)
        at io.reactivex.internal.schedulers.ScheduledRunnable.call(ScheduledRunnable.java:57)
        at java.util.concurrent.FutureTask.run(FutureTask.java:266)
        at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$201(ScheduledThreadPoolExecutor.java:180)
        at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:293)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
        at java.lang.Thread.run(Thread.java:748)
    ```
   
   This test is ignored for the time being until this issue is sorted out.

1. Test dependency `kotlintest-runner-junit5` [version `3.4.2`](https://mvnrepository.com/artifact/io.kotlintest/kotlintest-runner-junit5/3.4.2) fails with an `initializationError`

    ```kotlin
    dependencies {
        testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")
    }
    ```
   
   When using the `kotlintest-runner-junit5` with versions between [version `3.4.1`](https://mvnrepository.com/artifact/io.kotlintest/kotlintest-runner-junit5/3.4.1) and version `3.4.2`, the tests fail to run due to the following error.
   
   ```bash
   java.lang.ArrayStoreException: sun.reflect.annotation.TypeNotPresentExceptionProxy
   	at sun.reflect.annotation.AnnotationParser.parseClassArray(AnnotationParser.java:724)
   	at sun.reflect.annotation.AnnotationParser.parseArray(AnnotationParser.java:531)
   	at sun.reflect.annotation.AnnotationParser.parseMemberValue(AnnotationParser.java:355)
   	at sun.reflect.annotation.AnnotationParser.parseAnnotation2(AnnotationParser.java:286)
   	at sun.reflect.annotation.AnnotationParser.parseAnnotations2(AnnotationParser.java:120)
   	at sun.reflect.annotation.AnnotationParser.parseAnnotations(AnnotationParser.java:72)
   	at java.lang.Class.createAnnotationData(Class.java:3521)
   	at java.lang.Class.annotationData(Class.java:3510)
   	at java.lang.Class.getAnnotation(Class.java:3415)
   	at java.lang.reflect.AnnotatedElement.isAnnotationPresent(AnnotatedElement.java:258)
   	at java.lang.Class.isAnnotationPresent(Class.java:3425)
   	at org.junit.platform.commons.util.AnnotationUtils.findAnnotation(AnnotationUtils.java:114)
   	at org.junit.platform.commons.support.AnnotationSupport.findAnnotation(AnnotationSupport.java:126)
   	at io.micronaut.test.extensions.kotlintest.MicronautKotlinTestExtension.instantiate(MicronautKotlinTestExtension.kt:72)
   	at io.kotlintest.runner.jvm.JvmKt.instantiateSpec(jvm.kt:15)
   	at io.kotlintest.runner.jvm.TestEngine.createSpec(TestEngine.kt:122)
   	at io.kotlintest.runner.jvm.TestEngine.access$createSpec(TestEngine.kt:19)
   	at io.kotlintest.runner.jvm.TestEngine$submitSpec$1.run(TestEngine.kt:105)
   	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
   	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
   	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
   	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
   	at java.lang.Thread.run(Thread.java:748)
   ```

    Had to revert to a previous version, [version `3.4.0`](https://mvnrepository.com/artifact/io.kotlintest/kotlintest-runner-junit5/3.4.0) until this is sorted.  The issue seems to be related to JUnit 5 and some swallowed exception.  It seems that some types are missing from the classpath.  Further investigation is required.

## Insecure Example

1. Disable security by marking the refresh endpoint as not sensitive and disabling security in the [application.yml](./src/main/resources/application.yml) file.

    ```yaml
    micronaut:
      security:
        enabled: false
    
    endpoints:
      refresh:
        enabled: true
        sensitive: false
    ```

    This permits unauthenticated access to our endpoints.  Note that some of the tests may fails as these are expecting the endpoints to be secure. 
    
1. Access the Weather Forecast

    ```bash
    $ curl http://localhost:8080/weather/forecast
    ```

    This should return a message similar to the following

    ```json
    {"caption":"Scattered Clouds","dateTime":"2020-02-14T14:14:00.000"}
    ```

    The same message will be returned every time the request is made, including the `dateTime` field.  

    The `dateTime` field, by default, is shown as an array as shown next.
    
    ```json
    {"caption":"Scattered Clouds","dateTime":[2020,2,18,20,41,1,43000000]}
    ```  
      
    The can be changed by setting the `writeDatesAsTimestamps` to `false` in the [application.yml](./src/main/resources/application.yml) file, as shown next.
    
    ```yaml
    jackson:
      serialization:
        writeDatesAsTimestamps: false
    ```

    This ensures that the `dateTime` is displayed in ISO format.
    
1. Refresh the refreshable beans
    
    ```bash
    $ curl -v \
        http://localhost:8080/refresh \
        -H 'Content-Type: application/json' \
        -d '{"force": true}' 
    ```

    This should return a successful message 
    
    ```bash
    *   Trying ::1...
    * TCP_NODELAY set
    * Connected to localhost (::1) port 8080 (#0)
    > POST /refresh HTTP/1.1
    > Host: localhost:8080
    > User-Agent: curl/7.64.1
    > Accept: */*
    > Content-Type: application/json
    > Content-Length: 15
    >
    * upload completely sent off: 15 out of 15 bytes
    < HTTP/1.1 200 OK
    < Date: Mon, 14 Feb 2020 14:15:00 GMT
    < content-type: application/json
    < content-length: 2
    < connection: keep-alive
    <
    * Connection #0 to host localhost left intact
    []* Closing connection 0
    ```
   
   Note that [`-v` option](https://curl.haxx.se/docs/manpage.html#-v) has to be added to the `curl` command so that we can see the headers returned and see the `HTTP/1.1 200 OK` message.

1. Access the refreshable service again

    ```bash
    $ curl http://localhost:8080/weather/forecast
    ```

    This time the timestamp should be updated to when the bean was refreshed.

    ```json
    {"caption":"Scattered Clouds","dateTime":"2020-02-14T14:16:00.000"}
    ```

## Secure Example

1. Enable security by marking the refresh endpoint as sensitive and enabling security in the [application.yml](./src/main/resources/application.yml) file.

    ```yaml
    micronaut:
      security:
        enabled: true
    
    endpoints:
      refresh:
        enabled: true
        sensitive: true
    ```

1. Access the Weather Forecast including the `-v` option

    ```bash
    $ curl -v http://localhost:8080/weather/forecast
    ```

    This should reply with the message following error

    ```bash
    *   Trying ::1...
    * TCP_NODELAY set
    * Connected to localhost (::1) port 8080 (#0)
    > GET /weather/forecast HTTP/1.1
    > Host: localhost:8080
    > User-Agent: curl/7.64.1
    > Accept: */*
    >
    < HTTP/1.1 401 Unauthorized
    < Date: Tue, 14 Feb 2020 14:17:00 GMT
    < transfer-encoding: chunked
    < connection: close
    <
    * Closing connection 0
    ```
    
    Include the credentials to the request

    ```bash
    $ curl http://localhost:8080/weather/forecast \
        --user micronaut:framework
    ```

    This time, we should get a response

    ```json
    {"caption":"Scattered Clouds","dateTime":"2020-02-14T14:18:00.000"} 
    ```

1. Refresh the refreshable beans

    ```bash
    $ curl -v \
        http://localhost:8080/refresh \
        --user micronaut:framework \
        -H 'Content-Type: application/json' \
        -d '{"force": true}' 
    ```

    This should return a successful message 

    ```bash
    *   Trying ::1...
    * TCP_NODELAY set
    * Connected to localhost (::1) port 8080 (#0)
    > POST /refresh HTTP/1.1
    > Host: localhost:8080
    > User-Agent: curl/7.64.1
    > Accept: */*
    > Content-Type: application/json
    > Content-Length: 15
    >
    * upload completely sent off: 15 out of 15 bytes
    < HTTP/1.1 200 OK
    < Date: Mon, 14 Feb 2020 14:19:00 GMT
    < content-type: application/json
    < content-length: 2
    < connection: keep-alive
    <
    * Connection #0 to host localhost left intact
    []* Closing connection 0
    ```

1. Access the refreshable service again

    ```bash
    $ curl http://localhost:8080/weather/forecast \
        --user micronaut:framework
    ```

    This time the timestamp should be updated to when the bean was refreshed.

    ```jsons
    {"caption":"Scattered Clouds","dateTime":"2020-02-14T14:20:00.000"}
    ```
