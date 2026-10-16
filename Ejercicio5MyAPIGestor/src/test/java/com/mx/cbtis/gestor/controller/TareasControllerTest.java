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

import com.mx.cbtis.gestor.modelo.Tareas;
import com.mx.cbtis.gestor.repository.TareasRepository;

@ExtendWith(MockitoExtension.class)
class TareasControllerTest {

	@Mock
	TareasRepository tareastable;

	@InjectMocks
	TareasController controller;

	private Tareas crearTarea(Integer id, String nombre, Integer idProyecto) {
		return new Tareas(id, nombre, "Descripcion", "2026-12-01", "Por hacer", "PENDIENTE", idProyecto);
	}

	@Test
	@DisplayName("UT-16 getTarea(Tareas): devuelve las tareas de un proyecto")
	void tareasPorProyecto() {
		when(tareastable.findByIdProyecto(5))
				.thenReturn(Arrays.asList(crearTarea(1, "T1", 5), crearTarea(2, "T2", 5)));

		Tareas filtro = new Tareas();
		filtro.setId_proyecto(5);

		List<Tareas> resultado = controller.getTarea(filtro);

		assertEquals(2, resultado.size());
		assertEquals(5, resultado.get(0).getId_proyecto());
	}

	@Test
	@DisplayName("UT-17 getTarea(Tareas): proyecto sin tareas devuelve lista vacia")
	void tareasPorProyectoSinResultados() {
		when(tareastable.findByIdProyecto(77)).thenReturn(Collections.emptyList());

		Tareas filtro = new Tareas();
		filtro.setId_proyecto(77);

		List<Tareas> resultado = controller.getTarea(filtro);

		assertEquals(0, resultado.size());
	}

	@Test
	@DisplayName("UT-18 getTarea(int): id existente devuelve la tarea")
	void buscarTareaExistente() {
		when(tareastable.findById(1)).thenReturn(Optional.of(crearTarea(1, "T1", 5)));

		Tareas resultado = controller.getTarea(1);

		assertEquals(1, resultado.getId_tarea());
		assertEquals("T1", resultado.getNombre());
	}

	@Test
	@DisplayName("UT-19 getTarea(int): id inexistente devuelve objeto vacio")
	void buscarTareaInexistente() {
		when(tareastable.findById(99)).thenReturn(Optional.empty());

		Tareas resultado = controller.getTarea(99);

		assertNull(resultado.getId_tarea());
		assertNull(resultado.getNombre());
	}

	@Test
	@DisplayName("UT-20 addTareas: guarda la tarea y devuelve 1")
	void agregarTarea() {
		Tareas nueva = crearTarea(null, "Nueva", 5);

		int flag = controller.addTareas(nueva);

		assertEquals(1, flag);
		verify(tareastable).save(nueva);
	}

	@Test
	@DisplayName("UT-21 updateTareas: actualiza la tarea y devuelve 1")
	void actualizarTarea() {
		Tareas existente = crearTarea(1, "T1", 5);

		int flag = controller.updateTareas(existente);

		assertEquals(1, flag);
		verify(tareastable).save(existente);
	}

	@Test
	@DisplayName("UT-22 deleteTareas: elimina la tarea y devuelve 1")
	void eliminarTarea() {
		Tareas existente = crearTarea(1, "T1", 5);

		int flag = controller.deleteTareas(existente);

		assertEquals(1, flag);
		verify(tareastable).delete(existente);
	}

	@Test
	@DisplayName("UT-23 getTareas: devuelve la lista completa de tareas")
	void listarTareas() {
		when(tareastable.findAll()).thenReturn(Arrays.asList(crearTarea(1, "T1", 5), crearTarea(2, "T2", 6)));

		List<Tareas> resultado = controller.getTareas();

		assertEquals(2, resultado.size());
	}

	@Test
	@DisplayName("UT-39 TareasController: add, update y delete con objeto nulo devuelven 0")
	void tareaNula_devuelveCero() {
		assertEquals(0, controller.addTareas(null));
		assertEquals(0, controller.updateTareas(null));
		assertEquals(0, controller.deleteTareas(null));
		verifyNoInteractions(tareastable);
	}
}

