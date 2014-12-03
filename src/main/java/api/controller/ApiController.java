package api.controller;

import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

import api.entity.Action;

@Slf4j
@Controller
public class ApiController {
  
  @RequestMapping(method=RequestMethod.GET, value="/actions", headers="Accept=application/json")
  public @ResponseBody List<Action> findActions(/*@RequestParam(value="query", required=true)String query*/) {
    log.debug("CALLED");
    List<Action> actions = new ArrayList();
    actions.add(new Action("1", "a1", "Action 1", "action_url", "no tags"));
    actions.add(new Action("2", "a2", "Action 2", "action_url", "no tags"));
    actions.add(new Action("3", "a3", "Action 3", "action_url", "no tags"));
    actions.add(new Action("4", "a4", "Action 4", "action_url", "no tags"));
    return actions;
  }
}
