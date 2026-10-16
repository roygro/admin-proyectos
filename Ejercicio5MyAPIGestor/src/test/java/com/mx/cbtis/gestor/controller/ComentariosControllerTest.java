package com.mx.cbtis.gestor.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mx.cbtis.gestor.modelo.Comentarios;
import com.mx.cbtis.gestor.repository.ComentariosRepository;

@ExtendWith(MockitoExtension.class)
class ComentariosControllerTest {

	@Mock
	ComentariosRepository comentariostable;

	@InjectMocks
	ComentariosController controller;

	private Comentarios crearComentario(Integer id, Integer idTarea, String texto) {
		return new Comentarios(id, "1", idTarea, texto, "2026-10-01 10:00:00");
	}

	@Test
	@DisplayName("UT-24 getComentario(Comentarios): devuelve los comentarios de una tarea")
	void comentariosPorTarea() {
		when(comentariostable.findByTarea(3))
				.thenReturn(Arrays.asList(crearComentario(1, 3, "Hola"), crearComentario(2, 3, "Listo")));

		Comentarios filtro = new Comentarios();
		filtro.setId_tarea(3);

		List<Comentarios> resultado = controller.getComentario(filtro);

		assertEquals(2, resultado.size());
		assertEquals(3, resultado.get(0).getId_tarea());
	}

	@Test
	@DisplayName("UT-25 getComentario(Comentarios): tarea sin comentarios devuelve lista vacia")
	void comentariosPorTareaSinResultados() {
		when(comentariostable.findByTarea(88)).thenReturn(Collections.emptyList());

		Comentarios filtro = new Comentarios();
		filtro.setId_tarea(88);

		List<Comentarios> resultado = controller.getComentario(filtro);

		assertEquals(0, resultado.size());
	}

	@Test
	@DisplayName("UT-26 getComentario(int): id existente devuelve el comentario")
	void buscarComentarioExistente() {
		when(comentariostable.findById(1)).thenReturn(Optional.of(crearComentario(1, 3, "Hola")));

		Comentarios resultado = controller.getComentario(1);

		assertEquals(1, resultado.getId_comentario());
		assertEquals("Hola", resultado.getTexto_comentario());
	}

	@Test
	@DisplayName("UT-27 getComentario(int): id inexistente devuelve objeto vacio")
	void buscarComentarioInexistente() {
		when(comentariostable.findById(99)).thenReturn(Optional.empty());

		Comentarios resultado = controller.getComentario(99);

		assertNull(resultado.getId_comentario());
		assertNull(resultado.getTexto_comentario());
	}

	@Test
	@DisplayName("UT-28 addComentario: guarda el comentario y devuelve 1")
	void agregarComentario() {
		Comentarios nuevo = crearComentario(null, 3, "Nuevo");

		int flag = controller.addComentario(nuevo);

		assertEquals(1, flag);
		verify(comentariostable).save(nuevo);
	}

	@Test
	@DisplayName("UT-29 updateComentario: actualiza el comentario y devuelve 1")
	void actualizarComentario() {
		Comentarios existente = crearComentario(1, 3, "Editado");

		int flag = controller.updateComentario(existente);

		assertEquals(1, flag);
		verify(comentariostable).save(existente);
	}

	@Test
	@DisplayName("UT-30 deleteComentario: elimina el comentario y devuelve 1")
	void eliminarComentario() {
		Comentarios existente = crearComentario(1, 3, "Hola");

		int flag = controller.deleteComentario(existente);

		assertEquals(1, flag);
		verify(comentariostable).delete(existente);
	}

	@Test
	@DisplayName("UT-31 getComentarios: devuelve la lista completa de comentarios")
	void listarComentarios() {
		when(comentariostable.findAll())
				.thenReturn(Arrays.asList(crearComentario(1, 3, "A"), crearComentario(2, 4, "B")));

		List<Comentarios> resultado = controller.getComentarios();

		assertEquals(2, resultado.size());
	}

	@Test
	@DisplayName("UT-40 ComentariosController: add, update y delete con objeto nulo devuelven 0")
	void comentarioNulo_devuelveCero() {
		assertEquals(0, controller.addComentario(null));
		assertEquals(0, controller.updateComentario(null));
		assertEquals(0, controller.deleteComentario(null));
		verifyNoInteractions(comentariostable);
	}
}
