package io.zdp.node;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import io.zdp.node.http.HttpServer;

public class Node {

	public static void main(String[] args) throws Exception {
/*
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/io/zdp/node/spring-context.xml");

		ctx.start();
		*/
		HttpServer http = new HttpServer();
		http.init();
	}

}