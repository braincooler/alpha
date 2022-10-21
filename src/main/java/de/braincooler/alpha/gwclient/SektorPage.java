package de.braincooler.alpha.gwclient;

import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
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
