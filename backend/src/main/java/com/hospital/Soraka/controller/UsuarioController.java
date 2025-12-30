package com.hospital.Soraka.controller;

import com.hospital.Soraka.service.UsuarioService;
import com.hospital.Soraka.dto.usuario.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public List<UsuarioResponseDTO> getUsuarios() {
        return usuarioService.getUsuarios();
    }

    @GetMapping("/usuarios/{id}")
    public UsuarioResponseDTO getUsuarioById(@PathVariable Long id){
        return usuarioService.getUsuarioById(id);
    }

    @PostMapping("/usuarios")
    public UsuarioResponseDTO createUsuario(@Valid @RequestBody UsuarioPostDTO usuarioDTO){
        return usuarioService.createUsuario(usuarioDTO);
    }

    @DeleteMapping("/usuarios/{id}")
    public void deleteUsuario(@PathVariable Long id){
        usuarioService.deleteUsuario(id);
    }

    @PatchMapping("/usuarios/{id}")
    public UsuarioResponseDTO patchUsuario(@Valid @RequestBody UsuarioPatchDTO usuarioDTO, @PathVariable Long id){
        return usuarioService.patchUsuario(id, usuarioDTO);
    }
}