package com.app.api.controllers;

import com.app.api.models.EndorsementSkill;
import com.app.api.services.EndorsementSkillService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Constructs the controller with its service dependency.
 *
 * @param endorsementSkillService the service used to fetch skills
 */
@RestController 
@RequestMapping("/api/endorsement-skills")
public class EndorsementSkillController {

    private final EndorsementSkillService endorsementSkillService;

/**
* Lists every skill in the catalogue.
*
* @return all skills
*/
    public EndorsementSkillController(EndorsementSkillService endorsementSkillService) {
        this.endorsementSkillService=endorsementSkillService;
    }

/**
 * Lists every skill in the catalogue.
 *
 * @return all skills
 */
    @GetMapping
    public List<EndorsementSkill> getSkills() {
        return endorsementSkillService.getAllSkills();
    }
}


