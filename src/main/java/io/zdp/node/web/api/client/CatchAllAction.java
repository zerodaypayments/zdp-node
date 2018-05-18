package io.zdp.node.web.api.client;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.zdp.api.model.v1.BaseResponseObject;

@ControllerAdvice
public class CatchAllAction {

	public static class Error404 extends BaseResponseObject {

		private String url;

		private String uri;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		@Override
		public String getType() {
			return "page-not-found";
		}

	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public Error404 anything(HttpServletRequest req) {
		Error404 error = new Error404();
		error.setUri(req.getRequestURI());
		error.setUrl(req.getRequestURL().toString());
		return error;
	}
}
