# Usage

## Modify Application.java

Modify the entry point of the program with your specific purpse. You ca see the example petstore swagger usage below. This example builds the necessary configuration around the given petstore api, constructs the API client and then fetches every `available` pet and prints them into stdout.

You can add this example into the commandLineRunner method between inside the return block.

```java
Configuration configuration = new Configuration();
configuration.basePath = "https://petstore3.swagger.io";
OkHttpClient client = new OkHttpClient();
ApiClientPet apiClientPet = new ApiClientPet(client, configuration);
HttpResponse<List<Pet>> pets = apiClientPet.findPetsByStatus("available");
System.out.println(pets.data.stream().map(x -> x.getName()).collect(Collectors.toList()));
```

You should also add the additional imports before the class statement.

```java
import java.util.List;
import java.util.stream.Collectors;
import okhttp3.OkHttpClient;

```

## Requirements

The generated project does not have any external dependencies besides Java. Make sure you have java (version >=17) in your system.

## Running

Unix:

```bash
./mvnw spring-boot:run
```

Windows:

```
.\mvnw.bat spring-boot:run
```
