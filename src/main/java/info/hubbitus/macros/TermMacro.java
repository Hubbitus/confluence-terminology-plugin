package info.hubbitus.macros;

import java.util.Map;
import java.util.Objects;

import javax.xml.stream.XMLStreamException;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Pavel Alexeev.
 * @since 2020-02-11 16:03.
 */
@Scanned
public class TermMacro implements Macro {
	/**
	 * The pageBuilderService is required for the Web Resource Plugin Module
	 * @link https://developer.atlassian.com/server/framework/atlassian-sdk/create-a-confluence-hello-world-macro/
	 */
	private PageBuilderService pageBuilderService;

	private PageManager pageManager;

	/**
	 * @link https://community.atlassian.com/t5/Confluence-questions/renderConfluenceMacro-xhtml-macro-Confluence-4/qaq-p/103884
	 */
	private XhtmlContent xhtmlContent;

	/**
	 * @link https://developer.atlassian.com/server/confluence/how-do-i-get-the-base-url-and-contextpath-of-a-confluence-installation/
	 */
	private SettingsManager settingsManager;

	@Autowired
	public TermMacro(@ComponentImport PageBuilderService pageBuilderService, @ComponentImport PageManager pageManager, @ComponentImport SettingsManager settingsManager, @ComponentImport XhtmlContent xhtmlContent) {
		this.pageBuilderService = pageBuilderService;
		this.pageManager = pageManager;
		this.settingsManager = settingsManager;
		this.xhtmlContent = xhtmlContent;
	}

	@Override
	public String execute(Map<String, String> map, String s, ConversionContext conversionContext) throws MacroExecutionException {
		pageBuilderService.assembler().resources().requireWebResource("info.hubbitus.terminology:terminology-resources");
		String name = map.get("Name");
		String termPageName = map.get("TermPage");
		String color = map.get("TooltipColor");

		Page page = pageManager.getPage(conversionContext.getSpaceKey(), termPageName);
		String pageContent = page.getBodyAsString();

		try {
			return "<a class='glossary-term' href='" + settingsManager.getGlobalSettings().getBaseUrl() + page.getUrlPath() + "' data-tooltip='" + GeneralUtil.escapeForHtmlAttribute(xhtmlContent.convertStorageToView(pageContent, conversionContext)) + "' data-tooltip-color=" + color + "><code>" + name + "</code></a>";
		} catch (XMLStreamException | XhtmlException e) {
			e.printStackTrace();
		}
		return "Error render macro Term";
	}

	@Override
	public Macro.BodyType getBodyType() {
		return Macro.BodyType.NONE;
	}

	@Override
	public Macro.OutputType getOutputType() {
		return Macro.OutputType.BLOCK;
	}
}

