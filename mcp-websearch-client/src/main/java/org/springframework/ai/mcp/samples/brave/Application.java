package org.springframework.ai.mcp.samples.brave;

import com.google.adk.agents.LlmAgent;
import com.google.adk.tools.GoogleSearchTool;

public class Application {

	public static void main(String[] args) {
		LlmAgent rootAgent = LlmAgent.builder()
				.name("search_assistant")
				.description("An assistant that can search the web.")
				.model("gemini-1.5-flash") // Or your preferred models
				.instruction("You are a helpful assistant. Answer user questions using Google Search when needed.")
				.tools(new GoogleSearchTool())
				.build();

		var response = rootAgent.chat("What is the weather in Amsterdam?");
		System.out.println(response);
	}
}