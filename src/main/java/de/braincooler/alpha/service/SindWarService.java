package de.braincooler.alpha.service;

import de.braincooler.alpha.gwclient.GwWebClient;
import de.braincooler.alpha.persitence.Building;
import de.braincooler.alpha.persitence.BuildingRepository;
import de.braincooler.alpha.persitence.Sind;
import de.braincooler.alpha.persitence.WarRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class SindWarService {
    private static final Logger log = LoggerFactory.getLogger(SindWarService.class);
    private final GwWebClient gwWebClient;
    private final BuildingRepository buildingRepository;
    private final WarRepository warRepository;

    public List<Building> getWars(int sindId) {
        Set<Integer> wars = warRepository.getWars(sindId)
                .stream()
                .map(Sind::getId)
                .collect(Collectors.toSet());

        return buildingRepository.getWithWar(wars);
    }
}