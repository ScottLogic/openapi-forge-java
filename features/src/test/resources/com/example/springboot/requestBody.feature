Feature: Handles requests with a body

  Scenario: Calling API methods with a request body
    Given an API with the following specification
    """
    {
      "openapi":"3.0.2",
      "info" : {"title": "test", "version": "0.0.0"},
      "servers": [{ "url": "https://example.com/api/v3" }],
      "paths": {
        "/test/testBody": {
          "get": {
            "operationId": "testBody",
            "requestBody": {
              "content": {
                "application/json": {
                  "schema": { "$ref": "#/components/schemas/ObjectResponse" }
                }
              }
            },
            "responses": {
              "200": {
                "description": "description",
                "content": {
                  "application/json": {
                    "schema": { "type": "string" }
                  }
                }
              }
            }
          }
        }
      },
      "components": {
        "schemas": {
          "ObjectResponse": {
            "type": "object",
            "properties": {
              "value": { "type": "string" }
            }
          }
        }
      }
    }
    """
    When calling the method testBody with object {"value":"test"}
    Then the requested URL should be https://example.com/api/v3/test/testBody
    And the request should have a body with value {"value":"test"}
