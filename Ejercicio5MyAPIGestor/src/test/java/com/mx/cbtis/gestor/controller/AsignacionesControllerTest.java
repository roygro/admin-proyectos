package com.mx.cbtis.gestor.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mx.cbtis.gestor.modelo.Asignaciones;
import com.mx.cbtis.gestor.repository.AsignacionesRepository;

@ExtendWith(MockitoExtension.class)
class AsignacionesControllerTest {

	@Mock
	AsignacionesRepository asignacionestable;

	@InjectMocks
	AsignacionesController controller;

	private Asignaciones crearAsignacion(Integer id, Integer idUsuario, Integer idTarea) {
		return new Asignaciones(id, idUsuario, idTarea, "2026-10-01");
	}

	@Test
	@DisplayName("UT-32 getAsignacion: id existente devuelve la asignacion")
	void buscarAsignacionExistente() {
		when(asignacionestable.findById(1)).thenReturn(Optional.of(crearAsignacion(1, 2, 3)));

		Asignaciones resultado = controller.getAsignacion(1);

		assertEquals(1, resultado.getId_asignacion());
		assertEquals(2, resultado.getId_usuario());
		assertEquals(3, resultado.getId_tarea());
	}

	@Test
	@DisplayName("UT-33 getAsignacion: id inexistente devuelve objeto vacio")
	void buscarAsignacionInexistente() {
		when(asignacionestable.findById(99)).thenReturn(Optional.empty());

		Asignaciones resultado = controller.getAsignacion(99);

		assertNull(resultado.getId_asignacion());
		assertNull(resultado.getId_usuario());
	}

	@Test
	@DisplayName("UT-34 addAsignacion: guarda la asignacion y devuelve 1")
	void agregarAsignacion() {
		Asignaciones nueva = crearAsignacion(null, 2, 3);

		int flag = controller.addAsignacion(nueva);

		assertEquals(1, flag);
		verify(asignacionestable).save(nueva);
	}

	@Test
	@DisplayName("UT-35 updateAsignacion: actualiza la asignacion y devuelve 1")
	void actualizarAsignacion() {
		Asignaciones existente = crearAsignacion(1, 2, 4);

		int flag = controller.updateAsignacion(existente);

		assertEquals(1, flag);
		verify(asignacionestable).save(existente);
	}

	@Test
	@DisplayName("UT-36 deleteAsignacion: elimina la asignacion y devuelve 1")
	void eliminarAsignacion() {
		Asignaciones existente = crearAsignacion(1, 2, 3);

		int flag = controller.deleteAsignacion(existente);

		assertEquals(1, flag);
		verify(asignacionestable).delete(existente);
	}

	@Test
	@DisplayName("UT-37 getAsignaciones: devuelve la lista completa de asignaciones")
	void listarAsignaciones() {
		when(asignacionestable.findAll())
				.thenReturn(Arrays.asList(crearAsignacion(1, 2, 3), crearAsignacion(2, 2, 4)));

		List<Asignaciones> resultado = controller.getAsignaciones();

		assertEquals(2, resultado.size());
	}

	@Test
	@DisplayName("UT-41 AsignacionesController: add, update y delete con objeto nulo devuelven 0")
	void asignacionNula_devuelveCero() {
		assertEquals(0, controller.addAsignacion(null));
		assertEquals(0, controller.updateAsignacion(null));
		assertEquals(0, controller.deleteAsignacion(null));
		verifyNoInteractions(asignacionestable);
	}
}
