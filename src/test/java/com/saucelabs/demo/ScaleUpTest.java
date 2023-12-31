package com.saucelabs.demo;

import static java.util.concurrent.TimeUnit.MINUTES;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class ScaleUpTest {

  private final ExecutorService executor =
    Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 9);

  @Test
  public void chromeTest()
    throws MalformedURLException, InterruptedException, ExecutionException, TimeoutException {
    URL gridUrl = new URL("http://localhost:4444");
    int nTests = 20;
    CompletableFuture<?>[] futures = new CompletableFuture<?>[nTests];
    for (int i = 0; i < futures.length; i++) {
      CompletableFuture<Object> future = new CompletableFuture<>();
      futures[i] = future;
      executor.submit(() -> {
        try {
          WebDriver driver = new RemoteWebDriver(gridUrl, new ChromeOptions().setHeadless(true));
          long sleepLength = 20000;
          driver.get("https://www.selenium.dev/");
          // For demo purposes
          Thread.sleep(sleepLength);
          driver.get("https://opensource.saucelabs.com/");
          // For demo purposes
          Thread.sleep(sleepLength);
          // And now quit
          driver.quit();
          future.complete(true);
        } catch (Exception e) {
          future.completeExceptionally(e);
        }
      });
    }
    CompletableFuture.allOf(futures).get(15, MINUTES);
  }

}
