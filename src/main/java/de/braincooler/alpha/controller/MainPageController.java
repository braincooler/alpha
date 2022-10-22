package de.braincooler.alpha.controller;

import de.braincooler.alpha.persitence.Building;
import de.braincooler.alpha.persitence.BuildingRepository;
import de.braincooler.alpha.persitence.WarRepository;
import de.braincooler.alpha.service.SindWarService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainPageController {
    private static final Logger log = LoggerFactory.getLogger(MainPageController.class);
    private static final int PAGE_SIZE = 20;

    private final SindWarService sindWarService;
    private final BuildingRepository buildingRepository;
    private final WarRepository warRepository;

    @GetMapping
    public String defaultPage() {
        return "redirect:1635/1?field=size&dir=asc1";
    }

    @GetMapping("/{sind}/{page-number}")
    public String mainPage(@PathVariable(name = "sind") final int sindId,
                           @PathVariable(name = "page-number") final int pageNumber,
                           @RequestParam(name = "field") final String field,
                           @RequestParam(name = "dir") final String direction,
                           Model model) {

        if (!buildingRepository.isReady() && !warRepository.isReady()) {
            return "loading";
        }

        List<Building> targets = sindWarService.getWars(sindId);
        SortUtil.sort(targets, field, direction);

        int totalPages = calculatePageCount(targets);

        int start = (pageNumber - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, targets.size());
        List<Building> buildings = new ArrayList<>();
        for (int i = start; i < end; i++) {
            buildings.add(targets.get(i));
        }

        model.addAttribute("buildings", buildings);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("sind", sindId);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", targets.size());
        // sorting parameters
        model.addAttribute("sortField", field);
        model.addAttribute("sortDir", direction);
        model.addAttribute("reverseSortDir", direction.equals("asc") ? "desc" : "asc");

        return "main";
    }

    private int calculatePageCount(List<Building> targets) {
        int rest = targets.size() % PAGE_SIZE;
        int pages = (targets.size() - rest) / PAGE_SIZE;
        pages = pages + (rest == 0 ? 0 : 1);
        return pages;
    }
}
