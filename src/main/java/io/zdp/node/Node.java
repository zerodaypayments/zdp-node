package io.zdp.node;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Node {

	public static void main(String[] args) throws IOException {

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/io/zdp/node/spring-context.xml");

		ctx.start();
	}

}