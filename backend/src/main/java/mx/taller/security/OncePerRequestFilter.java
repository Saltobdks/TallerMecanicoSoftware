package mx.taller.security;

/** Alias local para el filtro de Spring; permite mantener la cadena JWT explícita. */
abstract class OncePerRequestFilter extends org.springframework.web.filter.OncePerRequestFilter {}
