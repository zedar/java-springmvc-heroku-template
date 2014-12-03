package unit.api.controller

import groovy.json.JsonSlurper

import org.springframework.test.web.servlet.MockMvc
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.http.HttpStatus.*

import org.springframework.beans.factory.annotation.Autowired

import spock.lang.Specification

import api.controller.ApiController
import api.service.ActionService

class ApiControllerSpec extends Specification {
  def apiController = new ApiController()
  def actionService = Mock(ActionService) {
    // if ActionService.find() method is called with query=timeout parameter simulate timeout exception
    find("query=timeout") >> { throw new Exception("Timeout") }
  }

  MockMvc mockMvc = standaloneSetup(apiController).build()

  def setup() {
    apiController.actionService = actionService
  }

  def "get list of actions"() {
    given: "empty query"
    String query = ""
    def json = new JsonSlurper()

    when: "REST /accounts url is hit"
    def resp = mockMvc.perform(get("/actions")).andReturn().response
    println "${resp.contentAsString}"
    def content = json.parseText(resp.contentAsString)

    then: "4 actions returned"
    resp.status == OK.value()
    resp.contentType.contains("application/json")

    content.size() == 4
  }

  def "timeout exception"() {
    given: "timeout query"
    String query = "query=timeout"
    
    when: "REST /accounts url is hit"
    def  resp = mockMvc.perform(get("/accounts?${query}")).andReturn().response

    then: "HTTP 404 is returned"
    resp.status == NOT_FOUND.value()
  }
}
