package org.serratec.resource;

import java.time.LocalDate;
import java.util.List;

import org.serratec.dto.StatusPedidoAlterarDTO;
import org.serratec.enums.StatusPedido;
import org.serratec.exceptions.PedidoException;
import org.serratec.model.Pedido;
import org.serratec.repository.PedidoRepository;
import org.serratec.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PedidoResource {

	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	EmailService emailService;
	
	@GetMapping("/pedido")
	public ResponseEntity<?> getPedidos() {
		List<Pedido> pedidos = pedidoRepository.findAll();

		return new ResponseEntity<>(pedidos, HttpStatus.OK);
	}

	@PutMapping("/pedido/statusPedido")
	public ResponseEntity<?> putStatus(@RequestBody StatusPedidoAlterarDTO dto){	
		
		
		
		Pedido pedido;
		try {
			
			pedido = dto.toPedido(pedidoRepository);
			pedidoRepository.save(pedido);
			
			if (pedido.getStatus().equals(StatusPedido.FINALIZADO))
				emailService.enviar("Pedido finalizado com sucesso!",
						"Data de Entrega: " + LocalDate.now().plusDays(15) + 
						"Produtos: " + pedido.getProdutos(), 
						pedido.getCliente().getEmail(), 
						"Capim Canela E-commerce");
				
				
			
			return new ResponseEntity<>("Pedido alterado com sucesso", HttpStatus.OK);
		} catch (PedidoException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}		
	}
}
