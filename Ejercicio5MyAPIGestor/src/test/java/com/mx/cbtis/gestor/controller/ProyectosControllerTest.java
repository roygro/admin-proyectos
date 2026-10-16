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

import com.mx.cbtis.gestor.modelo.Proyectos;
import com.mx.cbtis.gestor.repository.ProtectosRepository;

@ExtendWith(MockitoExtension.class)
class ProyectosControllerTest {

	@Mock
	ProtectosRepository proyectostable;

	@InjectMocks
	ProyectosController controller;

	private Proyectos crearProyecto(Integer id, String nombre) {
		return new Proyectos(id, nombre, "Descripcion", "2026-01-01", "2026-12-31", "ACTIVO", "10000");
	}

	@Test
	@DisplayName("UT-09 getProyecto: id existente devuelve el proyecto")
	void buscarProyectoExistente() {
		when(proyectostable.findById(1)).thenReturn(Optional.of(crearProyecto(1, "Gestor")));

		Proyectos resultado = controller.getProyecto(1);

		assertEquals(1, resultado.getId_proyecto());
		assertEquals("Gestor", resultado.getNombre());
	}

	@Test
	@DisplayName("UT-10 getProyecto: id inexistente devuelve objeto vacio")
	void buscarProyectoInexistente() {
		when(proyectostable.findById(99)).thenReturn(Optional.empty());

		Proyectos resultado = controller.getProyecto(99);

		assertNull(resultado.getId_proyecto());
		assertNull(resultado.getNombre());
	}

	@Test
	@DisplayName("UT-11 addProyecto: guarda el proyecto y devuelve 1")
	void agregarProyecto() {
		Proyectos nuevo = crearProyecto(null, "Nuevo");

		int flag = controller.addProyecto(nuevo);

		assertEquals(1, flag);
		verify(proyectostable).save(nuevo);
	}

	@Test
	@DisplayName("UT-12 updateProyecto: actualiza el proyecto y devuelve 1")
	void actualizarProyecto() {
		Proyectos existente = crearProyecto(1, "Gestor");

		int flag = controller.updateProyecto(existente);

		assertEquals(1, flag);
		verify(proyectostable).save(existente);
	}

	@Test
	@DisplayName("UT-13 deleteProyecto: elimina el proyecto y devuelve 1")
	void eliminarProyecto() {
		Proyectos existente = crearProyecto(1, "Gestor");

		int flag = controller.deleteProyecto(existente);

		assertEquals(1, flag);
		verify(proyectostable).delete(existente);
	}

	@Test
	@DisplayName("UT-14 getProyectos: devuelve la lista completa de proyectos")
	void listarProyectos() {
		when(proyectostable.findAll()).thenReturn(Arrays.asList(crearProyecto(1, "A"), crearProyecto(2, "B")));

		List<Proyectos> resultado = controller.getProyectos();

		assertEquals(2, resultado.size());
		assertEquals("B", resultado.get(1).getNombre());
	}

	@Test
	@DisplayName("UT-38 ProyectosController: add, update y delete con objeto nulo devuelven 0")
	void proyectoNulo_devuelveCero() {
		assertEquals(0, controller.addProyecto(null));
		assertEquals(0, controller.updateProyecto(null));
		assertEquals(0, controller.deleteProyecto(null));
		verifyNoInteractions(proyectostable);
	}


}
