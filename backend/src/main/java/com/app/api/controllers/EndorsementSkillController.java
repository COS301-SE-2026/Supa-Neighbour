package com.app.api.controllers;

import com.app.api.models.EndorsementSkill;
import com.app.api.services.EndorsementSkillService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController 
@RequestMapping("/api/endorsement-skills")
public class EndorsementSkillController {

    private final EndorsementSkillService endorsementSkillService;

    public EndorsementSkillController(EndorsementSkillService endorsementSkillService) {
        this.endorsementSkillService=endorsementSkillService;
    }

    @GetMapping
    public List<EndorsementSkill> getSkills() {
        return endorsementSkillService.getAllSkills();
    }
}


