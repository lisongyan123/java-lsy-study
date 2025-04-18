package lsy.work.core.jdk11.network;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class Demo {
    public static void main(String[] args) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://www.baidu.com")).GET().build();
        HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString();
        CompletableFuture<HttpResponse<String>> httpResponseCompletableFuture = httpClient.sendAsync(request, bodyHandler);
        // 做别的操作
        HttpResponse<String> response = httpResponseCompletableFuture.join();
        String body = response.body();
        System.out.println(body);
    }


}
