package fr.alten.ambroiseJEE.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.alten.ambroiseJEE.AmbroiseJeeApplication;

/**
 * This filter allow to lock request when it's not coming from an local adress
 * @see AmbroiseJeeApplication#filterRegistrationBeanLocalhost() for pattern concerned by this filter. 
 * @author Andy Chabalier
 *
 */
public class LocalhostFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;

		if (httpRequest.getRemoteAddr().equals("0:0:0:0:0:0:0:1") || httpRequest.getRemoteAddr().equals("127.0.0.1")) {
			chain.doFilter(httpRequest, response);
		} else {
			((HttpServletResponse) response).sendError(404 ,"You don't have the right to make this request");
		}
	}
}
