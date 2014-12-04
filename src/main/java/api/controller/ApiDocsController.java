package api.controller;

import java.io.StringWriter;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/api-docs")
public class ApiDocsController {
  
  @RequestMapping(method=RequestMethod.GET, headers="Accept=application/json")
  public @ResponseBody String genDocs() throws IOException {
    StringWriter sw = new StringWriter();
    JsonGenerator g = new JsonFactory().createGenerator(sw);
    g.useDefaultPrettyPrinter();
    
    g.writeStartObject();
      g.writeStringField("swagger", "2.0");
      g.writeObjectFieldStart("info");
        g.writeStringField("title", "Java, SpringMVC, Jetty, Heroku API example");
        g.writeStringField("description", "Example of simple microservice for API");
        g.writeObjectFieldStart("contact");
          g.writeStringField("name", "zedar");
          g.writeStringField("url", "https://github.com/zedar");
        g.writeEndObject();
        g.writeObjectFieldStart("license");
          g.writeStringField("name", "Creative Commons 4.0 International");
          g.writeStringField("url", "http://creativecommons.org/licenses/by/4.0/");
        g.writeEndObject();
        g.writeStringField("version", "0.0.1");
      g.writeEndObject();
      g.writeStringField("host", "api-springmvc-jetty.herokuapp.com");
      g.writeStringField("basePath", "/api");
      g.writeArrayFieldStart("schemes");
        g.writeString("https");
      g.writeEndArray();
      g.writeObjectFieldStart("paths");
        g.writeObjectFieldStart("/actions");
          g.writeObjectFieldStart("get");
            g.writeArrayFieldStart("tags");
              g.writeString("action");
            g.writeEndArray();
            g.writeStringField("summary", "Find actions with optional query");
            g.writeArrayFieldStart("parameters");
              g.writeStartObject();
                g.writeStringField("name", "query");
                g.writeStringField("in", "query");
                g.writeStringField("type", "string");
              g.writeEndObject();
            g.writeEndArray();
            g.writeObjectFieldStart("responses");
              g.writeObjectFieldStart("200");
                g.writeStringField("description", "Response with list of found actions");
                g.writeObjectFieldStart("schema");
                  g.writeStringField("type", "array");
                  g.writeObjectFieldStart("items");
                    g.writeStringField("$ref", "#/definitions/Action");
                  g.writeEndObject();
                g.writeEndObject();
              g.writeEndObject();
              g.writeObjectFieldStart("default");
                g.writeStringField("description", "Unexpected error");
                g.writeObjectFieldStart("schema");
                  g.writeObjectFieldStart("items");
                    g.writeStringField("$ref", "#/definitions/Error");
                  g.writeEndObject();
                g.writeEndObject();
              g.writeEndObject();
            g.writeEndObject();
          g.writeEndObject();
        g.writeEndObject();
      g.writeEndObject();
      g.writeObjectFieldStart("definitions");
        g.writeObjectFieldStart("Action");
          g.writeArrayFieldStart("required");
            g.writeString("id");
            g.writeString("name");
          g.writeEndArray();
          g.writeObjectFieldStart("properties");
            g.writeObjectFieldStart("id");
              g.writeStringField("type", "string");
            g.writeEndObject();
            g.writeObjectFieldStart("name");
              g.writeStringField("type", "string");
            g.writeEndObject();
            g.writeObjectFieldStart("description");
              g.writeStringField("type", "string");
            g.writeEndObject();
            g.writeObjectFieldStart("url");
              g.writeStringField("type", "string");
            g.writeEndObject();
            g.writeObjectFieldStart("tags");
              g.writeStringField("type", "string");
            g.writeEndObject();
          g.writeEndObject();
        g.writeEndObject();
      g.writeEndObject();
    g.writeEndObject();

    g.close();
    String out = sw.toString();
    log.debug("API-DOCS:" + out);
    
    return out;
  }
}
