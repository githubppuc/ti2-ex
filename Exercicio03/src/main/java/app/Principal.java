package app;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import service.UsuarioService;

public class Principal {

	private static UsuarioService usuarioService = new UsuarioService();

	public static void main(String[] args) {
		port(4500);

		get("/", (request, response) -> {
			response.redirect("/usuarios");
			return "";
		});

		post("/usuarios", (request, response) -> usuarioService.postLista(request, response));
		get("/usuarios", (request, response) -> usuarioService.getIndex(request, response));

		post("/usuario", (request, response) -> usuarioService.postUsuario(request, response));
		get("/usuario/:codigo", (request, response) -> usuarioService.getUsuario(request, response));

		post("/usuario/create", (request, response) -> usuarioService.postCriaUsuario(request, response));
		post("/usuario/update", (request, response) -> usuarioService.postAtualizaUsuario(request, response));
		post("/usuario/delete", (request, response) -> usuarioService.posDeletatUsuario(request, response));
	}
}