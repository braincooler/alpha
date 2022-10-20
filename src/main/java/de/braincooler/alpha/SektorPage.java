package de.braincooler.alpha;

import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import de.braincooler.alpha.persitence.Sektor;
import de.braincooler.alpha.persitence.Sind;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SektorPage {
    public String name;
    public List<HtmlTableRow> rows;
}
