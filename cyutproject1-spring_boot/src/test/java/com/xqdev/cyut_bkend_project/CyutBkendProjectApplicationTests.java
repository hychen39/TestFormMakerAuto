package com.xqdev.cyut_bkend_project;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CyutBkendProjectApplicationTests {

	// @Autowired
	// private ItemService itemService;

	// @Test
	// void contextLoads() {
	// 	String markdownValue = itemService.findByQuestionNumber(1).getContent();
	// 	String htmlValue = convertMarkdownToHTML(markdownValue);

	// 	System.out.println("Markdown String:");
	// 	System.out.println(markdownValue);
	// 	System.out.println("HTML String:");
	// 	System.out.println(htmlValue);
	// }

	// @Test
	// void testFlexMark() {
	// 	MutableDataSet options = new MutableDataSet();
	// 	Parser parser = Parser.builder(options).build();
	// 	HtmlRenderer renderer = HtmlRenderer.builder(options).build();
	// 	Node document = parser.parse(itemService.findByQuestionNumber(1).getContent());
	// 	System.out.println(renderer.render(document));
	// }

	// public static String convertMarkdownToHTML(String markdown) {
	// 	Parser parser = Parser.builder().build();
	// 	Node document = parser.parse(markdown);
	// 	HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
	// 	return htmlRenderer.render(document);
	// }

}
