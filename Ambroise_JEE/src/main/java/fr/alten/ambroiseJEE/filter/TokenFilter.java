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
import fr.alten.ambroiseJEE.utils.TokenIgnore;

public class TokenFilter implements Filter {

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {

		final HttpServletRequest httpRequest = (HttpServletRequest) request;

		final String requestURI = httpRequest.getRequestURI();
		final String method = httpRequest.getMethod();

		// we check if the url don't end with /login. if it's the case, the filter don't
		// have to applied.
		// If the requested method is the HTTP OPTION method, we let it go throught the
		// filter to allow an normal HTTP communication
		if (!(requestURI.endsWith("/login") || requestURI.endsWith("/signout") || requestURI.endsWith("/admin/init")
				|| requestURI.startsWith("/test") || method.equals(HttpMethod.OPTIONS.toString()))) {
			try {
				final String token = httpRequest.getHeader("authorization");
				final String subject;
				final UserRole role;
				if (!TokenIgnore.fileIsPresent()) {
					// We try to validate the token. In our case, the subject is formed by mail|role
					final String[] tokenInfo = JWTokenUtility.validate(token).split("\\|");
					subject = tokenInfo[0];
					role = UserRole.valueOf(tokenInfo[1]);
				} else {
					subject = TokenIgnore.getTokenIgnoreMail();
					role = TokenIgnore.getTokenIgnoreUserRole();
				}
				// We put the decoded subject parameters in attribute to allow further use in
				// the chain
				httpRequest.setAttribute("mail", subject);
				httpRequest.setAttribute("role", role);

				chain.doFilter(httpRequest, response);

			} catch (final InvalidJwtException e) {
				((HttpServletResponse) response).sendError(401, "Invalid token or token is expired");
			}
		} else {
			chain.doFilter(request, response);
		}
	}
}