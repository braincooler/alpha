package de.braincooler.alpha.persitence;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import de.braincooler.alpha.gwclient.GwWebClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class WarRepository {
    private static final List<Integer> sind = List.of(1635, 1637);
    private static final Logger log = LoggerFactory.getLogger(WarRepository.class);

    private final GwWebClient gwWebClient;

    private final Map<Integer, Set<Sind>> wars = new HashMap<>();

    private boolean isReady = false;

    public boolean isReady(){
        return isReady;
    }
    public Set<Sind> getWars(int sindId) {
        return wars.get(sindId) == null ? new HashSet<>() : wars.get(sindId);
    }

    @Scheduled(fixedDelay = 65 * 60 * 1000)
    public void initWars() {
        log.info("init war list...");
        sind.forEach(sindId -> {
            Set<Sind> sindWars = new HashSet<>();
            HtmlPage htmlPage = gwWebClient.fetchSyndWarsPage(sindId);
            HtmlTable warTable = htmlPage.getFirstByXPath("/html/body/div[3]/table[3]");
            List<HtmlTableRow> rows = warTable.getRows();
            for (int i = 4; i < rows.size(); i++) {
                String visibleTextWithId
                        = rows.get(i)
                        .getCell(1)
                        .getFirstChild()
                        .getVisibleText();
                String visibleTextWithName
                        = rows.get(i)
                        .getCell(1)
                        .getChildNodes()
                        .get(2)
                        .getVisibleText();
                int parsedSindId = parseSindId(visibleTextWithId);
                sindWars.removeIf(s -> s.getId() == parsedSindId);
                sindWars.add(new Sind(parsedSindId, visibleTextWithName));
            }
            wars.put(sindId, sindWars);
        });
        log.info("init war list ready");
    }

    private int parseSindId(String visibleText) {
        return Integer.parseInt(visibleText.substring(1));
    }
}