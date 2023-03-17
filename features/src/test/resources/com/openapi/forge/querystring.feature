Feature: Querystring handling

  Scenario: Calling API methods with a querystring
    Given an API with the following specification
    """
    {
      "openapi":"3.0.2",
      "info" : {"title": "test", "version": "0.0.0"},
      "servers": [{ "url": "https://example.com/api/v3" }],
      "paths": {
        "/test/get": {
          "get": {
            "operationId": "sendString",
            "parameters": [
              {
                "name": "value",
                "in": "query",
                "schema": { "type": "string" }
              }
            ],
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
      }
    }
    """
    When calling the method sendString with parameters "cabbage"
    Then the requested URL should be https://example.com/api/v3/test/get?value=cabbage

  Scenario: Calling API methods with a querystring omitting optional params
    Given an API with the following specification
    """
    {
      "openapi":"3.0.2",
      "info" : {"title": "test", "version": "0.0.0"},
      "servers": [{ "url": "https://example.com/api/v3" }],
      "paths": {
        "/test/get": {
          "get": {
            "operationId": "sendString",
            "parameters": [
              {
                "name": "value",
                "in": "query",
                "schema": { "type": "string" }
              }
            ],
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
      }
    }
    """
    When calling the method sendString without params
    Then the requested URL should be https://example.com/api/v3/test/get

   Scenario: Calling API methods with required and optional params
    Given an API with the following specification
    """
    {
      "openapi":"3.0.2",
      "info" : {"title": "test", "version": "0.0.0"},
      "servers": [{ "url": "https://example.com/api/v3" }],
      "paths": {
        "/test/required": {
          "get": {
            "operationId": "sendString",
            "parameters": [
              {
                "name": "optionalValue",
                "in": "query",
                "schema": { "type": "string" }
              },
              {
                "name": "requiredValue",
                "in": "query",
                "schema": { "type": "string" },
                "required": true
              }
            ],
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
      }
    }
    """
    When calling the method sendString with parameters "one"
    Then the requested URL should be https://example.com/api/v3/test/required?requiredValue=one
    When calling the method sendString with parameters "one,two"
    Then the requested URL should be https://example.com/api/v3/test/required?requiredValue=one&optionalValue=two

  Scenario: Calling API methods with default values
    Given an API with the following specification
    """
    {
      "openapi":"3.0.2",
      "info" : {"title": "test", "version": "0.0.0"},
      "servers": [{ "url": "https://example.com/api/v3" }],
      "paths": {
         "/test/testDefaultParam": {
          "get": {
            "operationId": "testDefaultParam",
            "parameters": [
              {
                "name": "paramOne",
                "in": "query",
                "schema": { "type": "string" }
              },
              {
                "name": "paramTwo",
                "in": "query",
                "schema": {
                  "type": "string",
                  "default": "valTwo"
                }
              },
              {
                "name": "paramThree",
                "in": "query",
                "schema": {
                  "type": "number",
                  "default": 3.4
                }
              }
            ],
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
      }
    }
    """
    When calling the method testDefaultParam without params
    Then the requested URL should be https://example.com/api/v3/test/testDefaultParam?paramTwo=valTwo&paramThree=3.4
    When calling the method testDefaultParam with parameters "hello"
    Then the requested URL should be https://example.com/api/v3/test/testDefaultParam?paramTwo=valTwo&paramThree=3.4&paramOne=hello
    When calling the method testDefaultParam with parameters "hello,sizzle"
    Then the requested URL should be https://example.com/api/v3/test/testDefaultParam?paramTwo=sizzle&paramThree=3.4&paramOne=hello
    When calling the method testDefaultParam with parameters "hello,sizzle,56"
    Then the requested URL should be https://example.com/api/v3/test/testDefaultParam?paramTwo=sizzle&paramThree=56&paramOne=hello

  Scenario: Calling API methods with a query array parameter 
    Given an API with the following specification
    """
    {
      "openapi":"3.0.2",
      "info" : {"title": "test", "version": "0.0.0"},
      "servers": [{ "url": "https://example.com/api/v3" }],
      "paths": {
        "/test/get": {
          "get": {
            "operationId": "sendStringArray",
            "parameters": [
              {
                "name": "value",
                "in": "query",
                "schema": {
                   "type": "array", 
                   "items": { "type": "string" } 
                }
              }
            ],
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
      }
    }
    """
    When calling the method sendStringArray with array "cabbage,carrots"
    Then the requested URL should be https://example.com/api/v3/test/get?value=cabbage&value=carrots

  Scenario: Calling API methods with a query object parameter 
    Given an API with the following specification
    """
    {
      "openapi":"3.0.2",
      "info" : {"title": "test", "version": "0.0.0"},
      "servers": [{ "url": "https://example.com/api/v3" }],
      "paths": {
        "/test/values": {
          "get": {
            "operationId": "sendValueObject",
            "parameters": [
              {
                "name": "value",
                "in": "query",
                "schema": {                   
                  "type": "object",
                  "properties": {
                    "id": { "type": "integer" },
                    "type": { "type": "string" }
                  }
                }
              }
            ],
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
      }
    }
    """
    When calling the method sendValueObject with object {"id": 7, "type": "test"}
    Then the requested URL should be https://example.com/api/v3/test/values?id=7&type=test
