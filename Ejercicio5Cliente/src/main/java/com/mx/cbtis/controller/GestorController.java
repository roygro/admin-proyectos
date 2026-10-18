package com.mx.cbtis.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.mx.cbtis.modelo.Comentarios;
import com.mx.cbtis.modelo.Proyectos;
import com.mx.cbtis.modelo.Tareas;
import com.mx.cbtis.modelo.Usuario;

@Controller
public class GestorController {

	// CAMBIO: el RestTemplate ahora se inyecta (antes se creaba con new en cada metodo)
	private final RestTemplate template;

	public GestorController(RestTemplate template) {
		this.template = template;
	}

	// CAMBIO (manejo de errores): convierte un fallo al llamar a la API en una respuesta controlada.
	// - Error 4xx de la API (ej. 400, 404): se propaga el mismo codigo con un mensaje.
	// - API caida (conexion rechazada) o error 5xx: 503 Service Unavailable con mensaje.
	private ResponseEntity<Object> errorServicio(RestClientException e) {
		Map<String, String> cuerpo = new HashMap<String, String>();
		if (e instanceof HttpClientErrorException) {
			HttpClientErrorException ex = (HttpClientErrorException) e;
			cuerpo.put("mensaje", "El servidor rechazo la solicitud");
			return ResponseEntity.status(ex.getStatusCode()).body(cuerpo);
		}
		cuerpo.put("mensaje", "No se pudo conectar con el servidor");
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(cuerpo);
	}

	@GetMapping("/")
	public String getHome() {
		return "home";
	}

	@GetMapping("/tutorial")
	public String getTutorial() {
		return "tutorial";
	}

	@GetMapping("/manual")
	public String getManual() {
		return "manual";
	}

	@GetMapping("/login")
	public String getLogin() {
		return "login";
	}

	@GetMapping("/register")
	public String getRegister(Model modelo) {

		Usuario usuario = new Usuario();
		modelo.addAttribute("usuario", usuario);

		return "register";
	}

	@PostMapping("/saveUsuario")
	public ModelAndView setSaveUsuario(@ModelAttribute Usuario usuario) {

		// CAMBIO: valida los datos antes de llamar a la API
		if (usuario.getCorreo_electronico() == null || usuario.getCorreo_electronico().trim().isEmpty()
				|| usuario.getContrasena() == null || usuario.getContrasena().trim().isEmpty()) {
			return new ModelAndView("redirect:/register?error");
		}

		String urlservicebd = "http://localhost:8081/addUsuario";
		try {
			template.postForEntity(urlservicebd, usuario, Integer.class);
		} catch (RestClientException e) {
			return new ModelAndView("redirect:/register?error");
		}

		return new ModelAndView("redirect:/login");

	}

	@GetMapping("/inicio")
	public String getInicio(Model modelo) {

		Proyectos proyectos = new Proyectos();
		modelo.addAttribute("proyectos", proyectos);

		return "inicio";
	}

	@PostMapping("/saveProyecto")
	public ModelAndView setSaveProyecto(@ModelAttribute Proyectos proyecto) {

		String urlservicebd = "http://localhost:8081/addProyecto";
		ResponseEntity<Integer> response = template.postForEntity(urlservicebd,proyecto,Integer.class);

		return new ModelAndView("redirect:/inicio");

	}

	@PostMapping("/consultarProyectos")
	public ResponseEntity<Object> consultarProyectos(Model modelo) {

		String urlservicebd = "http://localhost:8081/buscarProyectos";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			Proyectos[]  response = template.postForObject(urlservicebd, headers, Proyectos[].class);
			List<Proyectos> proyectos = Arrays.asList(response);
			modelo.addAttribute("listaProyectos", proyectos);

			ResponseEntity<Object> objeto = new ResponseEntity<Object>(proyectos, HttpStatusCode.valueOf(200));

			return objeto;
		} catch (RestClientException e) {
			// CAMBIO: API caida o con error -> respuesta controlada (503)
			return errorServicio(e);
		}
	}

	@PostMapping("/eliminarProyecto")
	public ModelAndView eliminarProyecto(@RequestBody Proyectos proyecto) {
		System.out.println("El id del proyecto es: " + proyecto.getId_proyecto());
		String urlservicebd = "http://localhost:8081/deleteProyecto";
		HttpEntity<Proyectos> request = new HttpEntity<Proyectos>(proyecto);
		ResponseEntity<Integer> response = template.exchange(urlservicebd, HttpMethod.DELETE,request,Integer.class);

		return new ModelAndView("redirect:/inicio");
	}

	@PostMapping ("/actualizarProyecto")
	public ResponseEntity<Object> updateProyecto(@RequestBody Proyectos proyecto) {
		Map<String,Integer> params = new HashMap<String,Integer>();
		params.put("id_proyecto", proyecto.getId_proyecto());
		String urlservicebd = "http://localhost:8081/buscarProyecto?id_proyecto={id_proyecto}";
		try {
			Proyectos proyectos = template.getForObject(urlservicebd, Proyectos.class, params);

			// CAMBIO: si el proyecto no existe (la API responde 404 o un objeto vacio) -> 404 controlado
			if (proyectos == null || proyectos.getId_proyecto() == null) {
				Map<String, String> cuerpo = new HashMap<String, String>();
				cuerpo.put("mensaje", "El proyecto no existe");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cuerpo);
			}

			ResponseEntity<Object> objeto = new ResponseEntity<Object>(proyectos, HttpStatusCode.valueOf(200));

			return objeto;
		} catch (RestClientException e) {
			return errorServicio(e);
		}
	}


	@PostMapping("/verificarLogin")
	public ResponseEntity<Object> verificarUsuario(@RequestBody Usuario paramUser) {
		String urlservicebd = "http://localhost:8081/getUsuarioLogin";
		try {
			ResponseEntity<Usuario> response = template.postForEntity(urlservicebd,paramUser,Usuario.class);

			return new ResponseEntity<Object>(response.getBody(), response.getStatusCode());
		} catch (RestClientException e) {
			// CAMBIO: API caida -> 503 controlado
			return errorServicio(e);
		}

	}

	@PostMapping("/consultarUsuarios")
	public ResponseEntity<Object> consultarUsuarios(Model modelo) {

		String urlservicebd = "http://localhost:8081/buscarUsuarios";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Usuario[]  response = template.postForObject(urlservicebd, headers, Usuario[].class);
		List<Usuario> usuarios = Arrays.asList(response);
		modelo.addAttribute("listaUsuarios", usuarios);

		ResponseEntity<Object> objeto = new ResponseEntity<Object>(usuarios, HttpStatusCode.valueOf(200));

		return objeto;
	}

	@PostMapping("/obtenerTareas")
	public ResponseEntity<List<Tareas>> obtenerTareas(@RequestBody Tareas idProyecto, Model modelo) {
		String urlservicebd = "http://localhost:8081/getTarea";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Tareas[] response = template.postForObject(urlservicebd, idProyecto, Tareas[].class);
		List<Tareas> listaTareas = Arrays.asList(response);
		modelo.addAttribute("tareas", listaTareas);
		return ResponseEntity.ok(listaTareas);
	}

	@PostMapping("/obtenerComentarios")
	public ResponseEntity<List<Comentarios>> obtenerComentarios(@RequestBody Comentarios idComentario, Model modelo) {
		String urlservicebd = "http://localhost:8081/getComentario";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Comentarios[] response = template.postForObject(urlservicebd, idComentario, Comentarios[].class);
		List<Comentarios> listaComentarios = Arrays.asList(response);
		modelo.addAttribute("comentarios", listaComentarios);
		return ResponseEntity.ok(listaComentarios);
	}

	//Liliana
	@GetMapping("/proyecto")
	public String getProyecto(Model modelo) {
		Tareas tarea = new Tareas();
		modelo.addAttribute("tarea", tarea);

		Comentarios comentario = new Comentarios();
		modelo.addAttribute("comentarios", comentario);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String urlservicebdUsuario = "http://localhost:8081/buscarUsuarios";
		try {
			Usuario[] responseUsuario = template.postForObject(urlservicebdUsuario, headers, Usuario[].class);
			List<Usuario> usuarios = Arrays.asList(responseUsuario);
			modelo.addAttribute("listaUsuarios", usuarios);
		} catch (RestClientException e) {
			// CAMBIO: si la API no responde, la pagina carga con lista vacia y un mensaje (no pagina de error 500)
			modelo.addAttribute("listaUsuarios", Arrays.asList(new Usuario[0]));
			modelo.addAttribute("errorServidor", "No se pudo conectar con el servidor");
		}

		return "proyecto";
	}

	@GetMapping("/tarea")
	public String getTarea(Model modelo) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Tareas tareas = new Tareas();
		modelo.addAttribute("tareas", tareas);

		return "proyecto";
	}

	@PostMapping("/saveTarea")
	public ModelAndView setSaveTareas(@ModelAttribute Tareas tareas) {

		ModelAndView mav = new ModelAndView("redirect:/proyecto");
		mav.addObject("id_proyecto", tareas.getId_proyecto());

		// CAMBIO: valida la fecha antes de llamar a la API
		if (tareas.getFecha_vencimiento() == null
				|| tareas.getFecha_vencimiento().toString().trim().isEmpty()) {
			mav.addObject("error", "fecha");
			return mav;
		}

		String urlservicebd = "http://localhost:8081/addTareas";
		try {
			template.postForEntity(urlservicebd, tareas, Integer.class);
		} catch (RestClientException e) {
			// CAMBIO: API caida o con error -> redirige con error en vez de 500
			mav.addObject("error", "servidor");
		}

		return mav;
	}
	@PostMapping("/saveComentario")
	public ResponseEntity<Object> setSaveComentario(@RequestBody Comentarios comentarios) {

		String urlservicebd = "http://localhost:8081/addComentario";
		try {
			ResponseEntity<Integer> response = template.postForEntity(urlservicebd, comentarios, Integer.class);
			return new ResponseEntity<Object>(response.getBody(), response.getStatusCode());
		} catch (RestClientException e) {
			return errorServicio(e);
		}
	}


	@PostMapping("/actualizarTarea")
	public ResponseEntity<Object> updateTarea(@RequestBody  Tareas tarea) {
		Map<String,Integer> params = new HashMap<String,Integer>();
		params.put("id_tarea", tarea.getId_tarea());
		String urlservicebd = "http://localhost:8081/buscarTarea?id_tarea={id_tarea}";
		Tareas tareas = template.getForObject(urlservicebd, Tareas.class, params);

		ResponseEntity<Object> objeto = new ResponseEntity<Object>(tareas, HttpStatusCode.valueOf(200));

		return objeto;
	}

	@PostMapping("/eliminarTarea")
	public ModelAndView eliminarTarea(@RequestBody Tareas tareas) {
		System.out.println("El id d de la tarea es: " + tareas.getId_tarea());
		String urlservicebd = "http://localhost:8081/deleteTareas";
		HttpEntity<Tareas> request = new HttpEntity<Tareas>(tareas);
		ResponseEntity<Integer> response = template.exchange(urlservicebd, HttpMethod.DELETE,request,Integer.class);

		return new ModelAndView("redirect:/proyecto");
	}

	@PostMapping("/verificarTarea")
	public ResponseEntity<Object> verificarTarea(@RequestBody Tareas paramName){
		String urlservicebd = "http://localhost:8081/addTareas";
		try {
			ResponseEntity<Integer> response = template.postForEntity(urlservicebd, paramName, Integer.class);
			return new ResponseEntity<Object>(response.getBody(), response.getStatusCode());
		} catch (RestClientException e) {
			// CAMBIO: 400 de la API (ej. fecha invalida) se propaga; API caida -> 503
			return errorServicio(e);
		}
	}

	@PostMapping("/cambiosTareas")
	public ResponseEntity<Integer> cambiosTareas(@RequestBody Tareas paramDatos){
		String urlservicebd = "http://localhost:8081/addTareas";
		ResponseEntity<Integer> response = template.postForEntity(urlservicebd, paramDatos, Integer.class);
		return response;
	}

	@PostMapping("/consultarTareas")
	public ResponseEntity<Object> consultarTareas() {
		String urlservicebd = "http://localhost:8081/buscarTareas";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Tareas[]  response = template.postForObject(urlservicebd, headers, Tareas[].class);
		List<Tareas> tareas = Arrays.asList(response);
		ResponseEntity<Object> objeto = new ResponseEntity<Object>(tareas, HttpStatusCode.valueOf(200));

		return objeto;

	}

	@PostMapping("/consultarComentarios")
	public ResponseEntity<Object> consultarComentarios() {
		String urlservicebd = "http://localhost:8081/buscarComentarios";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Comentarios[]  response = template.postForObject(urlservicebd, headers, Comentarios[].class);
		List<Comentarios> comentarios = Arrays.asList(response);
		ResponseEntity<Object> objeto = new ResponseEntity<Object>(comentarios, HttpStatusCode.valueOf(200));

		return objeto;

	}
}