package br.com.fiap.easypark.hateoas;

import br.com.fiap.easypark.controller.VagaController;
import br.com.fiap.easypark.dto.VagaOutDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class VagaModelAssembler implements RepresentationModelAssembler<VagaOutDto, EntityModel<VagaOutDto>> {

    @Override
    public EntityModel<VagaOutDto> toModel(VagaOutDto dto) {
        var self = linkTo(methodOn(VagaController.class).get(dto.id())).withSelfRel();
        var status = linkTo(methodOn(VagaController.class).status(dto.id())).withRel("status");


        EntityModel<VagaOutDto> model = EntityModel.of(dto).add(self, status);

        if (dto.nivelId() != null) {
            model.add(linkTo(methodOn(VagaController.class)
                    .listByEstacionamento(dto.id())).withRel("vagas_no_mesmo_estacionamento"));
        }
        if (dto.nivelId() != null) {

            model.add(linkTo(methodOn(VagaController.class)
                    .listByEstacionamento(dto.id())).withRel("nivel_vizinhos"));
        }

        return model;
    }
}
