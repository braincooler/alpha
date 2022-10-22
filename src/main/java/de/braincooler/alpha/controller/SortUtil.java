package de.braincooler.alpha.controller;

import de.braincooler.alpha.persitence.Building;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortUtil {

    public static void sort(List<Building> source, String field, String direction) {

        switch (field) {
            case "tur" -> {
                source.sort(Comparator.comparing(building -> building.tur));
                if (direction.equals("desc")) {
                    Collections.reverse(source);
                }
            }
            case "sektor" -> {
                source.sort(Comparator.comparing(building -> building.getSektor().getName()));
                if (direction.equals("desc")) {
                    Collections.reverse(source);
                }
            }
            case "size" -> {
                if (direction.equals("asc")) {
                    source.sort(Comparator.comparingInt(Building::getSize));
                } else {
                    source.sort(Comparator.comparingInt(Building::getSize).reversed());
                }
            }
            case "control" -> {
                source.sort(Comparator.comparingInt(building -> building.getUnderControl().getId()));
                if (direction.equals("desc")) {
                    Collections.reverse(source);
                }
            }
        }
    }
}
