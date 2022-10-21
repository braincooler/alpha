package de.braincooler.alpha.gwclient;

import com.gargoylesoftware.css.parser.CSSErrorHandler;
import com.gargoylesoftware.css.parser.CSSException;
import com.gargoylesoftware.css.parser.CSSParseException;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class GwWebClient {
    private static final Logger log = LoggerFactory.getLogger(GwWebClient.class);

    @Value("${gw.user}")
    private String gwUser;
    @Value("${gw.password}")
    private String gwPassword;

    private WebClient webClient;

    public HtmlPage fetchSyndWarsPage(int syndId) {
        String url = String.format("http://www.gwars.io/syndicate.php?id=%d&page=politics", syndId);
        return getPage(url);
    }

    public SektorPage fetchBuildingTable(int x, int y, String type) {
        String sektorUrl = String.format("http://www.gwars.io/map.php?sx=%d&sy=%d&st=%s", x, y, type);

        HtmlPage htmlPage = getPage(sektorUrl);
        DomElement sektorNameElement = htmlPage.getFirstByXPath("//*[@id=\"mapcontents\"]/center/table/tbody/tr/td[1]/table/tbody/tr/td[2]");
        String sektorname = sektorNameElement.getVisibleText().substring(sektorNameElement.getVisibleText().indexOf("]") + 2);
        HtmlTable table = htmlPage.getFirstByXPath("//*[@id=\"mapcontents\"]/table[1]/tbody/tr/td/table[1]");

        return new SektorPage(sektorname, table.getRows());
    }

    public HtmlPage getPage(String url) {
        try {
            Thread.sleep(400);
            return webClient.getPage(url);
        } catch (ArrayIndexOutOfBoundsException ex) {
            log.error("<<< --- page not readable url={} --->>>", url);
        } catch (IOException | InterruptedException ex) {
            log.error("error loading object info, url={}", url);
        }
        return null;
    }

    @PostConstruct
    private void initWebClient() {
        this.webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER);
        this.webClient.getOptions().setJavaScriptEnabled(false);
        this.webClient.getOptions().setThrowExceptionOnScriptError(false);
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        this.webClient.getOptions().setCssEnabled(false);
        this.webClient.getOptions().setDownloadImages(false);

        webClient.setCssErrorHandler(new CSSErrorHandler() {
            @Override
            public void warning(CSSParseException e) throws CSSException {

            }

            @Override
            public void error(CSSParseException e) throws CSSException {

            }

            @Override
            public void fatalError(CSSParseException e) throws CSSException {

            }
        });

        try {
            final HtmlPage page1 = webClient.getPage("https://www.gwars.ru/login.php?");
            HtmlForm form = page1.getElementByName("myform");
            HtmlInput login = form.getInputByName("login");
            HtmlInput password = form.getInputByName("pass");
            HtmlSubmitInput button = form.getInputByValue("Войти");

            login.type(gwUser);
            password.type(gwPassword);

            button.click();
        } catch (IOException ex) {
            log.error("error initializing web client", ex);
        }
    }
}
