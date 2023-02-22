package com.example.springboot;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import okhttp3.*;

// @ExtendWith(MockitoExtension.class)
// @PrepareForTest(Request.Builder.class)

public class CommonDefinitions {
  private final String tempSchemaPath = "schema.json";

  boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("win");
  }

  @Given("an API with the following specification")
  public void an_api_with_spec(String spec) throws IOException {
    generateApi(spec);
  }

  @Then("the requested URL should be {word}")
  public void requested_url(String url) {
    System.err.println(url);
    throw new io.cucumber.java.PendingException();
  }

  private void generateApi(String spec) throws IOException {
    writeToJsonFile(spec);
    forgeApi();
    getApiClientTypes();
  }

  private void writeToJsonFile(String spec) throws IOException {
    List<String> lines = Collections.singletonList(spec);
    Path file = Paths.get(tempSchemaPath);
    Files.write(file, lines, StandardCharsets.UTF_8);
  }

  private void forgeApi() throws IOException {
    Runtime runtime = Runtime.getRuntime();
    String npxCommand;
    if (isWindows()) {
      npxCommand = "npx.cmd";
    } else {
      npxCommand = "npx";
    }
    String[] openApiForgeCommand =
        new String[] {
          npxCommand,
          "openapi-forge",
          "forge",
          tempSchemaPath,
          "..",
          "--output",
          "../features",
          "--exclude",
          "pom.xml",
          // In current version of openapi-forge, can only exclude one item
          //            ".mvn/**",
          //            ".gitignore",
          //            "mvnw*",
          //            "*.md"
        };
    runtime.exec(openApiForgeCommand);
  }

  private void getApiClientTypes() {}

  protected static Object callMethod(String methodName, List<Object> parameters, String response) {
    return callMethod(methodName, parameters, response, 0);
  }

  protected static Object callMethod(
      String methodName, List<Object> parameters, String response, int serverIndex) {
    try {
      System.out.println("start callMethod");
      Response mockResponse = mock(Response.class);
      ResponseBody mockResponseBody = mock(ResponseBody.class);
      when(mockResponse.body()).thenReturn(mockResponseBody);
      when(mockResponseBody.string()).thenReturn(response);
      Call mockCall = mock(Call.class);
      OkHttpClient mockHttp = mock(OkHttpClient.class);
      when(mockHttp.newCall(any())).thenReturn(mockCall);
      when(mockCall.execute()).thenReturn(mockResponse);
      //      Request request = new Request.Builder()
      //              .url(requestUri)
      //
      //              .build();
      //      Request.Builder mockBuilder = mock(Request.Builder.class);
      //      try (MockedConstruction<Request.Builder> ignored =
      // mockConstruction(Request.Builder.class)) {
      //        Request.Builder mockBuilder = new Request.Builder();
      //        System.err.println(mockBuilder);
      //        //        mockRequestBuilder.when(Request.Builder::new).thenReturn(mockBuilder); //
      // TODO: Do
      //        // we need PowerMockito here???
      //        when(mockBuilder.url(anyString())).thenReturn(mockBuilder);
      //        when(mockBuilder.build()).thenReturn(mock(Request.class));
      //        //      try (MockedStatic<Request.Builder> mockRequestBuilder =
      //        // mockStatic(Request.Builder.class)) {
      //        //
      //        //        mockRequestBuilder.when(Request.Builder::new).thenReturn(mockBuilder); //
      // TODO: Do
      //        // we need PowerMockito here???
      //        //        when(mockBuilder.url(anyString())).thenReturn(mockBuilder);
      //        //        when(mockBuilder.build()).thenReturn(mock(Request.class));
      //        //      } catch (Exception e) {
      //        //        e.printStackTrace();
      //        //      }
      //
      //        //      Configuration configuration = new Configuration();
      //        //      ApiClient httpClient = new ApiClient(mockHttp, configuration);
      //
      //      } catch (Exception e) {
      //        e.printStackTrace();
      //      }
      Class<?> configurationClass = Class.forName("com.example.springboot.Configuration");
      Class<?> apiClientClass = Class.forName("com.example.springboot.ApiClient");

      Object configuration = configurationClass.getDeclaredConstructor().newInstance();
      Method setBasePath = configurationClass.getDeclaredMethod("setBasePath", String.class);
      setBasePath.invoke(configuration, "https://todoBasePath");
      Method setSelectedServerIndex =
          configurationClass.getDeclaredMethod("setSelectedServerIndex", int.class);
      setSelectedServerIndex.invoke(configuration, 0);
      Method setServers = configurationClass.getDeclaredMethod("setServers", List.class);
      setServers.invoke(configuration, List.of("todoServer0"));

      System.err.println(
          Arrays.stream(
                  configurationClass.getDeclaredConstructor().newInstance().getClass().getMethods())
              .collect(Collectors.toList()));
      System.err.println(((Configuration) configuration).getBaseAddress());
      Object apiClient =
          apiClientClass
              .getDeclaredConstructor(OkHttpClient.class, configurationClass)
              .newInstance(mockHttp, configuration);

      Method method = apiClientClass.getDeclaredMethod(methodName);
      Object objectResponse = method.invoke(apiClient);
      System.out.println("end callMethod");
      System.out.println(objectResponse);
      return objectResponse;

    } catch (Exception e) {
      e.printStackTrace();
      //      System.err.println();
    }
    return null;
  }
}
