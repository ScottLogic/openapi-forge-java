## OpenAPI Forge - C#

This repository is the C# generator for the [OpenAPI Forge](https://github.com/ScottLogic/openapi-forge), see that repository for usage instructions:

https://github.com/ScottLogic/openapi-forge

## Development

### Running

To run this generator, you also need to have [OpenAPI Forge] installed, or the repository checked out. Assuming you have it installed as a global module, you can run this generator as follows:

```
$ openapi-forge forge
 \ https://petstore3.swagger.io/api/v3/openapi.json
 \ .
 \ -o api
```

This generates an API from the Pet Store swagger definition, using the generator within the current folder (`.`), outputting the results to the `api` folder.

### Testing

The standard test script is used to execute the BDD-style tests against this generator.

```
npm run test
```

The script expects that the openapi-forge project (which is where the BDD feature files are located) is checked out at the same folder-level as this project. You also need to have the .NET CLI installed globally, you can confirm this by running `dotnet` in your terminal window.

### Linting

Two scripts are available to help you find linting errors:

```
npm run lint:check:all
```

This runs eslint in check mode which will raise errors found but not try and fix them. This is also ran on a PR and a push to main. It will fail if any errors were found.

```
npm run lint:write:all
```

This runs eslint in write mode which will raise errors found and try to fix them.
