package de.braincooler.alpha.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainPageController {
    private static final Logger log = LoggerFactory.getLogger(MainPageController.class);

    @GetMapping
    public String getAll(Model model) {

        return "main";
    }
}
