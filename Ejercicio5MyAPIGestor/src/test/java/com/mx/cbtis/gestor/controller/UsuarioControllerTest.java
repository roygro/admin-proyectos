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

import com.mx.cbtis.gestor.modelo.Usuario;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

	@Mock
	UsuarioRepository usuariotable;

	@InjectMocks
	UsuarioController controller;

	private Usuario crearUsuario(Integer id, String nombre) {
		return new Usuario(id, nombre, nombre.toLowerCase() + "@correo.com", "1234", "ADMIN", "img.png");
	}

	@Test
	@DisplayName("UT-01 getUsuarioLogin: credenciales correctas devuelve el usuario")
	void loginCredencialesValidas() {
		Usuario existente = crearUsuario(1, "Ana");
		when(usuariotable.findByCorreoPass("ana@correo.com", "1234")).thenReturn(Optional.of(existente));

		Usuario param = new Usuario();
		param.setCorreo_electronico("ana@correo.com");
		param.setContrasena("1234");

		Usuario resultado = controller.getUsuarioLogin(param);

		assertEquals(1, resultado.getId_usuario());
		assertEquals("Ana", resultado.getNombre());
	}

	@Test
	@DisplayName("UT-02 getUsuarioLogin: credenciales incorrectas devuelve nombre 000")
	void loginCredencialesInvalidas() {
		when(usuariotable.findByCorreoPass("x@correo.com", "mal")).thenReturn(Optional.empty());

		Usuario param = new Usuario();
		param.setCorreo_electronico("x@correo.com");
		param.setContrasena("mal");

		Usuario resultado = controller.getUsuarioLogin(param);

		assertEquals("000", resultado.getNombre());
		assertNull(resultado.getId_usuario());
	}

	@Test
	@DisplayName("UT-03 getUsuario: id existente devuelve el usuario")
	void buscarUsuarioExistente() {
		when(usuariotable.findById(1)).thenReturn(Optional.of(crearUsuario(1, "Ana")));

		Usuario resultado = controller.getUsuario(1);

		assertEquals(1, resultado.getId_usuario());
		assertEquals("Ana", resultado.getNombre());
	}

	@Test
	@DisplayName("UT-04 getUsuario: id inexistente devuelve objeto vacio")
	void buscarUsuarioInexistente() {
		when(usuariotable.findById(99)).thenReturn(Optional.empty());

		Usuario resultado = controller.getUsuario(99);

		assertNull(resultado.getId_usuario());
		assertNull(resultado.getNombre());
	}

	@Test
	@DisplayName("UT-05 addUsuario: guarda el usuario y devuelve 1")
	void agregarUsuario() {
		Usuario nuevo = crearUsuario(null, "Luis");

		int flag = controller.addUsuario(nuevo);

		assertEquals(1, flag);
		verify(usuariotable).save(nuevo);
	}

	@Test
	@DisplayName("UT-06 updateUsuario: actualiza el usuario y devuelve 1")
	void actualizarUsuario() {
		Usuario existente = crearUsuario(1, "Ana");

		int flag = controller.updateUsuario(existente);

		assertEquals(1, flag);
		verify(usuariotable).save(existente);
	}

	@Test
	@DisplayName("UT-07 deleteUsuario: elimina el usuario y devuelve 1")
	void eliminarUsuario() {
		Usuario existente = crearUsuario(1, "Ana");

		int flag = controller.deleteUsuario(existente);

		assertEquals(1, flag);
		verify(usuariotable).delete(existente);
	}

	@Test
	@DisplayName("UT-08 getUsuarios: devuelve la lista completa de usuarios")
	void listarUsuarios() {
		when(usuariotable.findAll()).thenReturn(Arrays.asList(crearUsuario(1, "Ana"), crearUsuario(2, "Luis")));

		List<Usuario> resultado = controller.getUsuarios();

		assertEquals(2, resultado.size());
		assertEquals("Luis", resultado.get(1).getNombre());
	}

	@Test
	@DisplayName("UT-37 UsuarioController: add, update y delete con objeto nulo devuelven 0")
	void usuarioNulo_devuelveCero() {
		assertEquals(0, controller.addUsuario(null));
		assertEquals(0, controller.updateUsuario(null));
		assertEquals(0, controller.deleteUsuario(null));
		verifyNoInteractions(usuariotable);
	}
}
