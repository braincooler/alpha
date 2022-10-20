package de.braincooler.alpha.controller;

import de.braincooler.alpha.GwWebClient;
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

import java.util.*;

@Controller
@RequiredArgsConstructor
public class MainPageController {
    private static final Logger log = LoggerFactory.getLogger(MainPageController.class);
    private final GwWebClient gwWebClient;
    private final WarRepository warRepository;
    private final BuildingRepository buildingRepository;
    private final SindWarService sindWarService;

    @GetMapping
    public String defaultPage(){
        return "redirect:1637/1?field=size&dir=asc1";
    }

    @GetMapping("/{sind}/{page-number}")
    public String mainPage(
            @PathVariable(name = "sind") final int sindId,
            @PathVariable(name = "page-number") final int pageNumber,
            @RequestParam(name = "field") final String field,
            @RequestParam(name = "dir") final String dir,
            Model model) {

        List<Building> targets = sindWarService.getWars(sindId);

        targets.removeIf(building ->
                Objects.equals(building.getUnderControl().getId(), building.getOwnerSind().getId()));

        if (field.equals("control")) {
            targets.sort(Comparator.comparingInt(building -> building.getUnderControl().getId()));
            if (dir.equals("desc")) {
                Collections.reverse(targets);
            }

        }

        if (field.equals("sektor")) {
            targets.sort(Comparator.comparing(building -> building.getSektor().getName()));
            if (dir.equals("desc")) {
                Collections.reverse(targets);
            }
        }

        if (field.equals("size")) {
            if (dir.equals("asc")) {
                targets.sort(Comparator.comparingInt(Building::getSize));
            } else {
                targets.sort(Comparator.comparingInt(Building::getSize).reversed());
            }
        }

        int pageSize = 20;
        int rest = targets.size() % pageSize;
        int pages = (targets.size() - rest) / pageSize;
        pages = pages + (rest == 0 ? 0 : 1);


        int start = (pageNumber - 1) * pageSize;
        int end = Math.min(start + pageSize, targets.size());
        List<Building> page = new ArrayList<>();
        for (int i = start; i < end; i++) {
            page.add(targets.get(i));
        }

        model.addAttribute("buildings", page);

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("sind", sindId);
        model.addAttribute("totalPages", pages);
        model.addAttribute("totalItems", targets.size());
        // sorting parameters
        model.addAttribute("sortField", field);
        model.addAttribute("sortDir", dir);
        model.addAttribute("reverseSortDir", dir.equals("asc") ? "desc" : "asc");

        return "main";
    }
}
