package de.braincooler.alpha.persitence;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import de.braincooler.alpha.gwclient.GwWebClient;
import de.braincooler.alpha.gwclient.SektorPage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BuildingRepository {
    private static final Logger log = LoggerFactory.getLogger(BuildingRepository.class);

    private final GwWebClient gwWebClient;

    private final List<Building> buildings = new ArrayList<>();

    private boolean isReady = false;

    public boolean isReady(){
        return isReady;
    }

    public List<Building> getWithWar(Set<Integer> warSindIds){
        return buildings.stream()
                .filter(building -> warSindIds.contains(building.getUnderControl().getId()))
                .collect(Collectors.toList());
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void initWars() {
        log.info("init building list...");
        for (int x = 47; x <= 53; x++) {
            for (int y = 47; y <= 53; y++) {
                SektorPage plantsSektorPage = gwWebClient.fetchBuildingTable(x, y, "plants");
                SektorPage techSektorPage = gwWebClient.fetchBuildingTable(x, y, "tech");
                extracted(x, y, plantsSektorPage, plantsSektorPage.getRows());
                extracted(x, y, plantsSektorPage, techSektorPage.getRows());
            }
        }

        isReady = true;
        log.info("init building ready... " + "Total: " + buildings.size());
    }

    private void extracted(int x, int y, SektorPage plantsSektorPage, List<HtmlTableRow> plantRows) {
        for (int i = 1; i < plantRows.size(); i++) {
            String classValue = plantRows.get(i)
                    .getCell(0)
                    .getAttribute("class");
            if (classValue.equals("greenlightbg")) {
                DomNodeList<DomNode> columns = plantRows.get(i).getChildNodes();

                DomNode buildingColumn = columns.get(0);
                int buildingLinkIndex = buildingColumn.getChildNodes().size() == 3 ? 1 : 0;
                HtmlAnchor buildingLink = (HtmlAnchor) buildingColumn.getChildNodes().get(buildingLinkIndex);
                String buildingName = buildingColumn.getChildNodes().get(buildingLinkIndex).getVisibleText();
                buildingName = buildingName.substring(buildingName.indexOf("]") + 1);
                int buildingId = parseBuildingId(buildingLink);
                String firstColumnVisibleText = buildingColumn.getVisibleText();
                int buildingSize = parseSize(firstColumnVisibleText);
                Integer sindId = null;
                if (buildingColumn.getChildNodes().size() == 3 &&
                        buildingColumn.getFirstChild() instanceof HtmlAnchor controlSindLink) {
                    sindId = parseSindId(controlSindLink);
                }

                DomNode ownerColumn = columns.get(1).getFirstChild().getFirstChild();
                Integer ownerSindId = null;
                if (ownerColumn.getChildNodes().size() == 2 &&
                        ownerColumn.getFirstChild() instanceof HtmlAnchor ownerSindLink) {
                    ownerSindId = parseSindId(ownerSindLink);
                }

                Building building = new Building();
                building.setId(buildingId);
                building.setSektor(new Sektor(x, y, plantsSektorPage.getName()));
                building.setOwnerSind(new Sind(ownerSindId, null));
                building.setUnderControl(new Sind(sindId, null));
                building.setSize(buildingSize);
                building.setUrl("https://www.gwars.io/object.php?id=" + buildingId);
                building.setName(buildingName);
                building.setTur(Objects.equals(ownerSindId, sindId));

                buildings.removeIf(b -> b.getId() == buildingId);
                buildings.add(building);
            }
        }
    }

    private int parseBuildingId(HtmlAnchor link) {
        String hrefAttribute = link.getHrefAttribute();
        int i = hrefAttribute.indexOf("=");
        return Integer.parseInt(hrefAttribute.substring(i + 1));
    }

    private int parseSize(String text) {
        int i = text.indexOf("(");
        return Integer.parseInt(text.substring(i + 1, text.length() - 1));
    }

    private int parseSindId(HtmlAnchor link) {
        String hrefAttribute = link.getHrefAttribute();
        int i = hrefAttribute.indexOf("=");
        return Integer.parseInt(hrefAttribute.substring(i + 1));
    }
}