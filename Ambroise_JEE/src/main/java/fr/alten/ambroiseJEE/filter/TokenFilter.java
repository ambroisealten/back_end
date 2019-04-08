package fr.alten.ambroiseJEE.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jose4j.jwt.consumer.InvalidJwtException;
import org.springframework.http.HttpMethod;

import fr.alten.ambroiseJEE.security.JWTokenUtility;
import fr.alten.ambroiseJEE.security.UserRole;

public class TokenFilter implements Filter {
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;

		String token = httpRequest.getHeader("authorization");

		String requestURI = httpRequest.getRequestURI();
		String method = httpRequest.getMethod();
		
		// we check if the url don't end with /login. if it's the case, the filter don't have to applied. 
		//If the requested method is the HTTP OPTION method, we let it go throught the filter to allow an normal HTTP communication
		if (!(requestURI.endsWith("/login") || requestURI.endsWith("/async") || method.equals(HttpMethod.OPTIONS.toString()))) {
			try {
				//We try to validate the token. In our case, the subject is formed by mail|role
				String[] tokenInfo = JWTokenUtility.validate(token).split("\\|");
				String subject = tokenInfo[0];
				UserRole role = UserRole.valueOf(tokenInfo[1]);

				//We put the decoded subject parameters in attribute to allow further use in the chain
				httpRequest.setAttribute("mail", subject);
				httpRequest.setAttribute("role", role);

				chain.doFilter(httpRequest, response);

			} catch (InvalidJwtException e) {
				((HttpServletResponse) response).sendError(403, "Invalid token or token is expired");
			}
		} else {
			chain.doFilter(request, response);
		}
	}
}
