package info.hubbitus.macros;

import java.util.Map;
import javax.xml.stream.XMLStreamException;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
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

		// Links to different space separated by ":". F.e.: "TEST:DNS"
		String spaceKey = conversionContext.getSpaceKey(); // Current by default
		String[] parts = termPageName.split(":");
		if (parts.length > 1){
			spaceKey = parts[0];
			termPageName = parts[1];
		}

		Page page = pageManager.getPage(spaceKey, termPageName);
		String pageContent = page.getBodyAsString();

		ConversionContext pageContext = new DefaultConversionContext(page.toPageContext(), conversionContext.getOutputDeviceType());

		try {
			// GeneralUtil.escapeForHtmlAttribute() work incorrect there! It is replace " to \" (JavaScript type), but we need &quot; there!
			// See https://stackoverflow.com/questions/4015345/how-do-i-properly-escape-quotes-inside-html-attributes/4015380#4015380
			String tooltipContent = xhtmlContent.convertStorageToView(pageContent, pageContext)
				.replaceAll("⍞", "⍞⍞") // Allow nesting (see terminology.js corresponded part)
					.replaceAll("\"", "⍞");

			return "<a class='glossary-term' href=\"" + settingsManager.getGlobalSettings().getBaseUrl() + page.getUrlPath() + "\" data-tooltip=\"" + tooltipContent + "\" data-tooltip-color='" + color + "'><code>" + name + "</code></a>";
		} catch (XMLStreamException | XhtmlException e) {
			e.printStackTrace();
			return "Error render macro Term: " + e.getMessage();
		}
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

