package service;

import java.io.File;
import java.util.Scanner;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dao.UsuarioDAO;
import model.Usuario;
import spark.Request;
import spark.Response;

public class UsuarioService {
    private UsuarioDAO dao = new UsuarioDAO();
    private boolean statusDAO;

    public UsuarioService() {
        statusDAO = dao.conectar();
    }

    private String makeIndex() {
        String html = "";
        try {
            Scanner sc = new Scanner(new File("index.html"));
            while (sc.hasNext()) {
                html += sc.nextLine() + '\n';
            }
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        html = html.replaceFirst("~ERRO~", statusDAO ? "\"\"" : "Erro de conexão com banco de dados");
        return html;
    }

    public Object getIndex(Request request, Response response) {
        String html = makeIndex();
        html = html.replaceFirst("~FETCH~", "/usuarios");
        return html;
    }

    public Object postLista(Request request, Response response) {
        response.type("application/json");
        return jsonPadrao(dao.getUsuarios());
    }

    public Object getUsuario(Request request, Response response) {
        String html = makeIndex();
        html = html.replaceFirst("~FETCH~", "/usuario");
        html = html.replaceFirst("~FBODY~", "{\"codigo:\"" + request.params(":codigo") + "}");
        return html;
    }

    public Object postUsuario(Request request, Response response) {
        JsonElement el = JsonParser.parseString(request.body()).getAsJsonObject().get("codigo");
        if (el.getAsString().equals(""))
            return jsonPadrao(dao.getUsuarios());
        return jsonPadrao(dao.get(el.getAsInt()));
    }

    private String jsonPadrao(int tipo, String valor) {
        return String.format("{\"tipo\":%d,\"valor\":%s}", tipo, valor);
    }

    private String jsonPadrao(Object o) {
        return jsonPadrao(0, o.toString());
    }

    private String jsonPadrao(Object[] arr) {
        String r = "[";
        for (int i = 0; i < arr.length - 1; i++)
            r += arr[i].toString() + ",";
        r += arr[arr.length - 1].toString();
        r += "]";
        return jsonPadrao(1, r);
    }

    public Object postCriaUsuario(Request request, Response response) {
        JsonObject o = JsonParser.parseString(request.body()).getAsJsonObject();
        Usuario u = new Usuario(
                o.get("codigo").getAsInt(),
                o.get("login").getAsString(),
                o.get("senha").getAsString(),
                o.get("sexo").getAsString().charAt(0));
        return jsonPadrao(dao.inserirUsuario(u) ? "Sucesso" : "Erro");
    }

    public Object postAtualizaUsuario(Request request, Response response) {
        JsonObject o = JsonParser.parseString(request.body()).getAsJsonObject();
        Usuario u = new Usuario(
                o.get("codigo").getAsInt(),
                o.get("login").getAsString(),
                o.get("senha").getAsString(),
                o.get("sexo").getAsString().charAt(0));
        return jsonPadrao(dao.atualizarUsuario(u) ? "Sucesso" : "Erro");
    }

    public Object posDeletatUsuario(Request request, Response response) {
        JsonElement el = JsonParser.parseString(request.body()).getAsJsonObject().get("codigo");
        return jsonPadrao(0, dao.excluirUsuario(el.getAsInt()) ? "Excluido" : "Erro");
    }
}
